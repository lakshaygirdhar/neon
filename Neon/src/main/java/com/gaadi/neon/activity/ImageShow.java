package com.gaadi.neon.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.gaadi.neon.PhotosLibrary;
import com.gaadi.neon.adapter.ImagesFoldersAdapter;
import com.gaadi.neon.fragment.ImageShowFragment;
import com.gaadi.neon.interfaces.ICameraParam;
import com.gaadi.neon.interfaces.IGalleryParam;
import com.gaadi.neon.interfaces.INeutralParam;
import com.gaadi.neon.interfaces.IParam;
import com.gaadi.neon.model.PhotosMode;
import com.gaadi.neon.util.NeonException;
import com.gaadi.neon.util.SingletonClass;
import com.scanlibrary.R;
import com.scanlibrary.databinding.ActivityGridFoldersBinding;
import com.scanlibrary.databinding.ActivityImageShowBinding;

public class ImageShow extends NeonBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bindXml();
    }


    private void bindXml() {
        ActivityImageShowBinding binder = DataBindingUtil.setContentView(this, R.layout.activity_image_show);
        ImageShowFragment imageShowFragment = new ImageShowFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.imageShowFragmentContainer, imageShowFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            return goBackForImageCollection();
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean goBackForImageCollection() {
        try {
            IGalleryParam galleryParam = SingletonClass.getSingleonInstance().getGalleryParam();
            ICameraParam cameraParam = SingletonClass.getSingleonInstance().getCameraParam();
            if (galleryParam != null) {
                PhotosLibrary.collectPhotos(this, PhotosMode.setGalleryMode().setParams(galleryParam),SingletonClass.getSingleonInstance().getImageResultListener());
            } else {
                PhotosLibrary.collectPhotos(this, PhotosMode.setCameraMode().setParams(cameraParam),SingletonClass.getSingleonInstance().getImageResultListener());
            }
            finish();
        } catch (NeonException e) {
        }
        return true;
    }


    @Override
    public void onBackPressed() {
       goBackForImageCollection();
    }

}
