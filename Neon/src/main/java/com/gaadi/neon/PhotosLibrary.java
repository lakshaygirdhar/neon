package com.gaadi.neon;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.gaadi.neon.activity.CameraActivity;
import com.gaadi.neon.activity.CameraActivity1;
import com.gaadi.neon.activity.GalleryActivity;
import com.gaadi.neon.activity.NeutralActivity;
import com.gaadi.neon.util.ApplicationController;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.NeonUtils;
import com.gaadi.neon.util.PhotoParams;
import com.scanlibrary.R;

/**
 * @author lakshaygirdhar
 * @since 13-08-2016
 *
 */
public class PhotosLibrary {

    public static void collectPhotos(Context context, PhotoParams params, int requestCode) {

        if(ApplicationController.selectedFiles != null && ApplicationController.selectedFiles.size()>0)
            ApplicationController.selectedFiles.clear();

        if(params != null)
        switch (params.getMode()) {
            case CAMERA_PRIORITY:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !NeonUtils.checkForPermission(context,
                                                         new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                         Constants.REQUEST_PERMISSION_CAMERA, "Camera and Storage")) {
                    return;
                }
                Intent newIntent = new Intent(context, CameraActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.putExtra("photoParams", params);
                ((Activity) context).startActivityForResult(newIntent, requestCode);
                ((Activity) context).overridePendingTransition(com.scanlibrary.R.anim.slide_in_bottom, com.scanlibrary.R.anim.do_nothing);
                break;

            case GALLERY_PRIORITY:
                Intent intent2  = new Intent(context, GalleryActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent2.putExtra("photoParams", params);
                ((Activity) context).startActivityForResult(intent2, requestCode);
                break;

            case NEUTRAL:
                Intent cameraNeutralIntent = new Intent(context, NeutralActivity.class);
                cameraNeutralIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                cameraNeutralIntent.putExtra("photoParams", params);
                ((Activity) context).startActivityForResult(cameraNeutralIntent, requestCode);
                break;

            case CAMERA_ONLY:
                Intent intent  = new Intent(context, CameraActivity1.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("photoParams", params);
                ((Activity) context).startActivityForResult(intent, requestCode);
                break;

            case GALLERY_ONLY:
                Intent intent1  = new Intent(context, GalleryActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("photoParams", params);
                ((Activity) context).startActivityForResult(intent1, requestCode);
                break;

            default:
                Toast.makeText(context, context.getString(R.string.invalid_mode),Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(context, context.getString(R.string.pass_valid_params), Toast.LENGTH_SHORT).show();
    }
}
