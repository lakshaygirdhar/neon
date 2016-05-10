package com.scanlibrary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.imageuploadlib.Utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Prince Midha on 05/05/2016.
 */
public class Utils {

    private static final String TAG = "scanlibrary:Utils";
    private Utils() {

    }

    public static Uri getUri(Context context, Bitmap bitmap) {
        Uri pictureFileUri = null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File pictureFile = Constants.getMediaOutputFile(Constants.TYPE_IMAGE);
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
        File file = Constants.getMediaOutputFile(Constants.TYPE_IMAGE); // the File to save to
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
}