package com.gaadi.neon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gaadi.neon.activity.camera.NormalCameraActivityNeon;
import com.gaadi.neon.activity.gallery.GridFilesActivity;
import com.gaadi.neon.activity.gallery.GridFoldersActivity;
import com.gaadi.neon.activity.neutral.NeonNeutralActivity;
import com.gaadi.neon.interfaces.ICameraParam;
import com.gaadi.neon.interfaces.IGalleryParam;
import com.gaadi.neon.interfaces.INeutralParam;
import com.gaadi.neon.interfaces.SetOnImageCollectionListener;
import com.gaadi.neon.model.PhotosMode;
import com.gaadi.neon.util.NeonException;
import com.gaadi.neon.util.SingletonClass;

/**
 * @author lakshaygirdhar
 * @since 13-08-2016
 */
public class PhotosLibrary {


    /*public static void collectPhotos(Context context, PhotoParams params, int requestCode) {

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
                Intent newIntent = new Intent(context, CameraActivity1.class);
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
                Intent cameraNeutralIntent = new Intent(context, LNeutralActivity.class);
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
*/


    public static void collectPhotos(Context activity, PhotosMode photosMode, SetOnImageCollectionListener listener) throws NullPointerException, NeonException {
        SingletonClass.getSingleonInstance().setImageResultListener(listener);
        validate(activity, photosMode,listener);
        if (photosMode.getParams() instanceof INeutralParam) {
            startNeutralActivity(activity, photosMode);
        } else if (photosMode.getParams() instanceof ICameraParam) {
            startCameraActivity(activity, photosMode);
        } else if (photosMode.getParams() instanceof IGalleryParam) {
            startGalleryActivity(activity, photosMode);
        }
    }

    private static void validate(Context activity, PhotosMode photosMode,SetOnImageCollectionListener listener) throws NullPointerException, NeonException {
        if (activity == null) {
            throw new NullPointerException("Activity instance cannot be null");
        } else if (photosMode == null) {
            throw new NullPointerException("PhotosMode instance cannot be null");
        } else if ((photosMode.getParams().getTagEnabled()) &&
                (photosMode.getParams().getImageTagsModel() == null || photosMode.getParams().getImageTagsModel().size() <= 0)) {
            throw new NeonException("Tags enabled but list is empty or null");
        }else if(listener == null){
            throw new NullPointerException("'SetOnImageCollectionListener' cannot be null");
        }
    }

    private static void startCameraActivity(Context activity, PhotosMode photosMode) {
        ICameraParam cameraParams = (ICameraParam) photosMode.getParams();
        SingletonClass.getSingleonInstance().setCameraParam(cameraParams);

        switch (cameraParams.getCameraViewType()) {

            case normal_camera:
                Intent intent = new Intent(activity, NormalCameraActivityNeon.class);
                activity.startActivity(intent);
                break;

        }
    }

    private static void startGalleryActivity(Context activity, PhotosMode photosMode) {
        IGalleryParam galleryParams = (IGalleryParam) photosMode.getParams();
        SingletonClass.getSingleonInstance().setGalleryParam(galleryParams);

        switch (galleryParams.getGalleryViewType()) {

            case grid_folders:
                Intent gridGalleryFolderIntent = new Intent(activity, GridFoldersActivity.class);
                activity.startActivity(gridGalleryFolderIntent);
                break;

            case grid_files:
                Intent gridGalleryFileIntent = new Intent(activity, GridFilesActivity.class);
                activity.startActivity(gridGalleryFileIntent);
                break;

            case horizontal_scroll:
                Toast.makeText(activity, "Not yet implemented", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private static void startNeutralActivity(Context activity, PhotosMode photosMode) {
        SingletonClass.getSingleonInstance().setNeutralEnabled(true);

        INeutralParam neutralParamParams = (INeutralParam) photosMode.getParams();
        SingletonClass.getSingleonInstance().setNeutralParam(neutralParamParams);

        Intent neutralIntent = new Intent(activity, NeonNeutralActivity.class);
        activity.startActivity(neutralIntent);

    }


}
