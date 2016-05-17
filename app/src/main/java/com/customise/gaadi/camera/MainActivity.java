package com.customise.gaadi.camera;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.imageuploadlib.Utils.FileInfo;
import com.imageuploadlib.Utils.PhotoParams;
import com.scanlibrary.PhotosLibrary;
import com.scanlibrary.ScanConstants;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int REQUEST_CODE = 2004;
    ArrayList<FileInfo> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PhotoParams params = new PhotoParams();
        params.setMode(PhotoParams.MODE.CAMERA_PRIORITY);
        params.setOrientation(PhotoParams.CameraOrientation.LANDSCAPE);
        params.setNoOfPhotos(2);
        params.setEnableCapturedReview(true);
        params.setEnableExtraBrightness(false);
        params.setRestrictedExtensionEnabled(true);
        params.setCameraFace(PhotoParams.CameraFacing.FRONT);
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
                images = (ArrayList<FileInfo>) data.getSerializableExtra(ScanConstants.CAMERA_IMAGES);
                Log.i(TAG,"Selected images info "+images.size());
            }
        }
    }
}
