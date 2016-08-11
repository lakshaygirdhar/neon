package com.gaadi.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.gaadi.fragment.CameraItemsFragment;
import com.gaadi.util.ApplicationController;
import com.gaadi.util.Constants;
import com.gaadi.util.FileInfo;
import com.gaadi.util.PhotoParams;

import java.util.ArrayList;

public class NeutralActivity extends FragmentActivity implements CameraItemsFragment.ImagesHandler {

    //    public static final String KEY_ARRAYLIST_IMAGES = "images";
    //    public static final String KEY_ARRAYLIST_DELETED_IMAGES = "deletedImages";
    private CameraItemsFragment cameraItemsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.gaadi.R.layout.activity_neutral);

        PhotoParams params = (PhotoParams) getIntent().getSerializableExtra(CameraItemsFragment.PHOTO_PARAMS);

        cameraItemsFragment = CameraItemsFragment.newInstance(this, params, this, null,
                                                              com.gaadi.R.drawable.image_load_default_big, com.gaadi.R.drawable.image_load_default_small);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(com.gaadi.R.id.photoFragment, cameraItemsFragment).commit();
    }

    @Override
    public void onBackPressed() {

        if (ApplicationController.selectedFiles != null) {
            ApplicationController.selectedFiles.clear();
        }

        super.onBackPressed();
    }

//    @Override
//    public void outputImages(ArrayList<FileInfo> files, ArrayList<FileInfo> deletedImages) {
//        Intent intent = new Intent();
//        Bundle args = new Bundle();
//        args.putSerializable(KEY_ARRAYLIST_IMAGES, files);
//        if (deletedImages != null) {
//            args.putSerializable(KEY_ARRAYLIST_DELETED_IMAGES, deletedImages);
//        }
//        intent.putExtras(args);
//        setResult(RESULT_OK, intent);
//        finish();
//    }

    @Override
    public void dragImagesHandler(int first, int second) {
      /*  for (int i = 0; i < ApplicationController.orderImages.size(); i++) {
            Log.e(TAG, "Order : " + ApplicationController.orderImages.get(i));
        }*/
    }

    @Override
    public void gaHandler(String screen, String category, String action, String label, ArrayList<FileInfo> images) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 1
                    && (grantResults[0] != PackageManager.PERMISSION_GRANTED
                            || grantResults[1] != PackageManager.PERMISSION_GRANTED)) {
                return;
            }
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraItemsFragment.onActivityResult(requestCode,
                        RESULT_OK, null);
            }
        } else if (requestCode == Constants.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraItemsFragment.onActivityResult(requestCode,
                        RESULT_OK, null);
            }
        }
    }
}
