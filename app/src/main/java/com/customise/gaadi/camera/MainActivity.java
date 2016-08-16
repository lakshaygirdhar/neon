package com.customise.gaadi.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.PhotoParams;
import com.gaadi.neon.PhotosLibrary;
import com.scanlibrary.ScanConstants;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int REQUEST_CODE = 2004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PhotoParams params = new PhotoParams();
        params.setMode(PhotoParams.MODE.NEUTRAL);
        params.setOrientation(PhotoParams.CameraOrientation.PORTRAIT);
        params.setNoOfPhotos(2);
        params.setEnableCapturedReview(true);
        params.setEnableExtraBrightness(false);
        params.setRestrictedExtensionEnabled(true);
        params.setCameraFace(PhotoParams.CameraFacing.BACK);
        params.setGalleryFromCameraEnabled(false);
        PhotosLibrary.collectPhotos(this,params,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == ScanConstants.SINGLE_CAPTURED) {
                String imagePath = data.getStringExtra(ScanConstants.CAPTURED_IMAGE_PATH);
                Log.i(TAG , "Captured ImagePath "+imagePath);
            } else if (resultCode == ScanConstants.MULTIPLE_CAPTURED) {
                ArrayList<FileInfo> images = (ArrayList<FileInfo>) data.getSerializableExtra(ScanConstants.CAMERA_IMAGES);
                Log.i(TAG,"Selected images info "+ images.size());
            }
        }
    }
}
