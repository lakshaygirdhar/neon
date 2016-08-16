package com.gaadi.neon.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.gaadi.neon.util.ApplicationController;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.PhotoParams;
import com.scanlibrary.CameraItemsFragment;
import com.scanlibrary.R;

/**
 *  @author lakshaygirdhar
 *  @version 1.0
 *  @since 13-08-2016
 *
 */

public class NeutralActivity extends FragmentActivity
{
    private CameraItemsFragment cameraItemsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_neutral);
        PhotoParams params = (PhotoParams) getIntent().getSerializableExtra(CameraItemsFragment.PHOTO_PARAMS);
        cameraItemsFragment = CameraItemsFragment.newInstance(this, params, null,
                R.drawable.image_load_default_big, R.drawable.image_load_default_small);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.photoFragment, cameraItemsFragment).commit();
    }

    @Override
    public void onBackPressed() {

        if (ApplicationController.selectedFiles != null) {
            ApplicationController.selectedFiles.clear();
        }

        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
