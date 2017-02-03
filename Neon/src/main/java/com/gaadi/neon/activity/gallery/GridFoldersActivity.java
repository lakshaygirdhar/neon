package com.gaadi.neon.activity.gallery;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.gaadi.neon.Enumerations.CameraFacing;
import com.gaadi.neon.Enumerations.CameraOrientation;
import com.gaadi.neon.Enumerations.CameraType;
import com.gaadi.neon.PhotosLibrary;
import com.gaadi.neon.adapter.ImagesFoldersAdapter;
import com.gaadi.neon.interfaces.ICameraParam;
import com.gaadi.neon.model.ImageTagModel;
import com.gaadi.neon.model.PhotosMode;
import com.gaadi.neon.util.NeonException;
import com.gaadi.neon.util.SingletonClass;
import com.scanlibrary.R;
import com.scanlibrary.databinding.ActivityGridFoldersBinding;

import java.util.List;

public class GridFoldersActivity extends NeonBaseGalleryActivity {

    private MenuItem textViewCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bindXml();
        setTitle(R.string.gallery);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        textViewCamera = menu.findItem(R.id.menu_camera);
        if(SingletonClass.getSingleonInstance().getGalleryParam().galleryToCameraSwitchEnabled()) {
            textViewCamera.setVisible(true);
        }else{
            textViewCamera.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }else if (id == R.id.menu_camera) {
            ICameraParam cameraParam = SingletonClass.getSingleonInstance().getCameraParam();
            if(cameraParam == null){
                cameraParam = new ICameraParam() {
                    @Override
                    public CameraFacing getCameraFacing() {
                        return CameraFacing.front;
                    }

                    @Override
                    public CameraOrientation getCameraOrientation() {
                        return CameraOrientation.portrait;
                    }

                    @Override
                    public boolean getFlashEnabled() {
                        return true;
                    }

                    @Override
                    public boolean getCameraSwitchingEnabled() {
                        return true;
                    }

                    @Override
                    public boolean getVideoCaptureEnabled() {
                        return false;
                    }

                    @Override
                    public CameraType getCameraViewType() {
                        return CameraType.normal_camera;
                    }

                    @Override
                    public boolean cameraToGallerySwitchEnabled() {
                        return true;
                    }

                    @Override
                    public int getNumberOfPhotos() {
                        return SingletonClass.getSingleonInstance().getGalleryParam().getNumberOfPhotos();
                    }

                    @Override
                    public boolean getTagEnabled() {
                        return SingletonClass.getSingleonInstance().getGalleryParam().getTagEnabled();
                    }

                    @Override
                    public List<ImageTagModel> getImageTagsModel() {
                        return SingletonClass.getSingleonInstance().getGalleryParam().getImageTagsModel();
                    }

                };
            }
            try {
                PhotosLibrary.collectPhotos(this, PhotosMode.setCameraMode().setParams(cameraParam),SingletonClass.getSingleonInstance().getImageResultListener());
            } catch (NeonException e) {
                e.printStackTrace();
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void bindXml() {
        ActivityGridFoldersBinding binder = DataBindingUtil.setContentView(this, R.layout.activity_grid_folders);
        ImagesFoldersAdapter adapter = new ImagesFoldersAdapter(this, getImageBuckets());
        binder.gvFolders.setAdapter(adapter);
    }


}
