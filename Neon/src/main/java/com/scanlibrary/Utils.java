package com.scanlibrary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;
import android.util.Log;

import com.gaadi.neon.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by Prince Midha on 05/05/2016.
 *
 */
public class Utils {

    private static final String TAG = "scanlibrary:Utils";
    public static final String FOLDER_NAME = "Gaadi Evaluator";
    private Utils() {

    }

    public static Uri getUri(Context context, Bitmap bitmap) {
        Uri pictureFileUri = null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File pictureFile = Constants.getMediaOutputFile(context,Constants.TYPE_IMAGE);
        Log.e(TAG, pictureFile.getAbsolutePath());
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(bytes.toByteArray());
            fos.close();
            pictureFileUri = Uri.parse("file://" + pictureFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pictureFileUri;
    }

    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }

    public static Uri insertCroppedBitmap(Context context, Bitmap pictureBitmap) {

        OutputStream fOut = null;
        File file = Constants.getMediaOutputFile(context,Constants.TYPE_IMAGE); // the File to save to
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close(); // do not forget to close the stream
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse("file://" + file.getAbsolutePath());
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                uri));
//        try {
//            uri = MediaStore.Images.Media.insertImage(context.getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        return uri;
    }

    public static File getEmptyStoragePath(Context ctx) {
        File mediaFile = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String selectedPath = null;
        ArrayList<String> list = (ArrayList) getSdCardPaths(ctx, true);
        for (String path : list) {

            long freeBytes = new File(path).getFreeSpace();
            if (freeBytes > 5120) {
                selectedPath = path;
                break;
            }
        }
        File externalDir = new File(selectedPath ,
                FOLDER_NAME);
        if (!externalDir.exists()) {
            if (!externalDir.mkdir()) {
                //Toast.makeText(ctx,"FAILED externalDir.mkdir() TO CREATE DIRECTORY",Toast.LENGTH_SHORT).show();
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
            else{
                //Toast.makeText(ctx,"SUCCESS to create folder",Toast.LENGTH_SHORT).show();
            }
        }

        mediaFile = new File(externalDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    public static List<String> getSdCardPaths(final Context context, final boolean includePrimaryExternalStorage) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString());
        final List<String> result = new ArrayList<>();
        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdir()){
                Log.e("CommonUtils","Pictures Directory not found");
            }
            else{
                result.add(mediaStorageDir.getAbsolutePath());
            }
        }
        else{
            result.add(mediaStorageDir.getAbsolutePath());
        }
        final File[] externalCacheDirs = ContextCompat.getExternalFilesDirs(context, null);
        if (externalCacheDirs == null || externalCacheDirs.length == 0)
            return null;
        if (externalCacheDirs.length == 1) {
            if (externalCacheDirs[0] == null)
                return null;
            final String storageState = EnvironmentCompat.getStorageState(externalCacheDirs[0]);
            if (!Environment.MEDIA_MOUNTED.equals(storageState))
                return null;
            if (!includePrimaryExternalStorage && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Environment.isExternalStorageEmulated())
                return null;
        }

        if (includePrimaryExternalStorage || externalCacheDirs.length == 1)
        {
            result.add(externalCacheDirs[0].getAbsolutePath());
            //result.add(getRootOfInnerSdCardFolder(externalCacheDirs[0]));
        }
        for (int i = 1; i < externalCacheDirs.length; ++i) {
            final File file = externalCacheDirs[i];
            if (file == null)
                continue;
            final String storageState = EnvironmentCompat.getStorageState(file);
            if (Environment.MEDIA_MOUNTED.equals(storageState)) {
                result.add(externalCacheDirs[i].getAbsolutePath());
                //  result.add(getRootOfInnerSdCardFolder(externalCacheDirs[i]));
            }
        }
        if (result.isEmpty())
            return null;
        return result;
    }
}