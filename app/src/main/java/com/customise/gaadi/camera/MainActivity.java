package com.customise.gaadi.camera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.imageuploadlib.Utils.PhotoParams;
import com.scanlibrary.PhotosLibrary;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PhotoParams params = new PhotoParams();
        params.setMode(PhotoParams.MODE.NEUTRAL);
        params.setOrientation(PhotoParams.CameraOrientation.LANDSCAPE);
        params.setNoOfPhotos(2);
        params.setEnableCapturedReview(false);
        params.setEnableExtraBrightness(false);
        params.setEnableRestrictedExtension(true);
        PhotosLibrary.collectPhotos(this, params);
    }
}
