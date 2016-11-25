package com.gaadi.neon.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.scanlibrary.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lakshay
 * @since 13-02-2015.
 *
 */
public class Constants {

    public static final int TYPE_IMAGE = 1;
    public static final String APP_SHARED_PREFERENCE = "com.gcloud.gaadi.prefs";
    public static final String TAG = "Gallery";
    public static final String RESULT_IMAGES = "result_images";

    public static final String IMAGES_SELECTED = "imagesSelected";
    public static final String IMAGE_PATH = "image_path";
    public static final int REQUEST_PERMISSION_CAMERA = 104;
    public static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 105;
    public static final String FLASH_MODE="flashMode";
    public static String FLAG="Flag";

    public static File getMediaOutputFile(Context context, int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name));

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        if (type == TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else
            return null;
        return mediaFile;
    }
}
