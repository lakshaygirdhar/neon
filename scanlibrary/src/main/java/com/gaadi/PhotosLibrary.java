package com.gaadi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.gaadi.activity.NeutralActivity;
import com.gaadi.util.CommonUtils;
import com.gaadi.util.Constants;
import com.gaadi.util.PhotoParams;
import com.gaadi.scanning.ScanActivity;

/**
 * Created by Lakshay on 21-05-2015.
 *
 */
public class PhotosLibrary {


    public static void collectPhotos(Context context, PhotoParams params, int requestCode) {

        switch (params.getMode()) {
            case CAMERA_PRIORITY:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !CommonUtils.checkForPermission(context,
                        new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.REQUEST_PERMISSION_CAMERA, "Camera and Storage")) {
                    return;
                }
                Intent newIntent = new Intent(context, ScanActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.putExtra("photoParams", params);
                ((Activity) context).startActivityForResult(newIntent, requestCode);
                ((Activity) context).overridePendingTransition(com.imageuploadlib.R.anim.slide_in_bottom, com.imageuploadlib.R.anim.do_nothing);
                break;

            case GALLERY_PRIORITY:
                break;

            case NEUTRAL:
                Intent cameraNeutralIntent = new Intent(context, NeutralActivity.class);
                cameraNeutralIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                cameraNeutralIntent.putExtra("photoParams", params);
                ((Activity) context).startActivityForResult(cameraNeutralIntent, requestCode);
                break;
        }
    }

//    public static Fragment getPhotosFragment(PhotoParams params) {
//        Fragment fragment = null;
//        switch (params.getMode()) {
//            case CAMERA_PRIORITY:
//                fragment = CameraPriorityFragment.getInstance(params);
//                break;
//            case GALLERY_PRIORITY:
//                break;
//            case NEUTRAL:
//                break;
//        }
//        return fragment;
//    }
}
