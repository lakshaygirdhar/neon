package com.gaadi.neon.activity.gallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gaadi.neon.Enumerations.CameraFacing;
import com.gaadi.neon.Enumerations.CameraOrientation;
import com.gaadi.neon.Enumerations.CameraType;
import com.gaadi.neon.PhotosLibrary;
import com.gaadi.neon.adapter.ImagesFoldersAdapter;
import com.gaadi.neon.interfaces.ICameraParam;
import com.gaadi.neon.interfaces.SetOnPermissionResultListener;
import com.gaadi.neon.model.ImageTagModel;
import com.gaadi.neon.model.PhotosMode;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.ManifestPermission;
import com.gaadi.neon.util.NeonException;
import com.gaadi.neon.util.PermissionType;
import com.gaadi.neon.util.SingletonClass;
import com.scanlibrary.R;
import com.scanlibrary.databinding.ActivityGridFoldersBinding;

import java.util.List;

public class GridFoldersActivity extends NeonBaseGalleryActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindXml();
        setTitle(R.string.gallery);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        MenuItem textViewCamera = menu.findItem(R.id.menu_camera);
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
           onBackPressed();
            return true;
        }else if (id == R.id.menu_camera) {
            performCameraOperation();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(SingletonClass.getSingleonInstance().isNeutralEnabled()){
            super.onBackPressed();
        }else{
            if (SingletonClass.getSingleonInstance().getImagesCollection() != null &&
                    SingletonClass.getSingleonInstance().getImagesCollection().size() > 0) {
                new AlertDialog.Builder(this).setTitle("All Images will be lost. Do you sure want to go back?")
                        .setCancelable(true).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SingletonClass.getSingleonInstance().scheduleSinletonClearance();
                        finish();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }else{
                super.onBackPressed();
            }
        }
    }


    private void performCameraOperation() {

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


    private void bindXml() {
        try {
            askForPermissionIfNeeded(PermissionType.write_external_storage, new SetOnPermissionResultListener() {
                @Override
                public void onResult(boolean permissionGranted) {
                    if(permissionGranted){
                        ActivityGridFoldersBinding binder = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_grid_folders,frameLayout,true);
                        ImagesFoldersAdapter adapter = new ImagesFoldersAdapter(GridFoldersActivity.this, getImageBuckets());
                        binder.gvFolders.setAdapter(adapter);
                    }else{
                        Toast.makeText(GridFoldersActivity.this,"Permission not granted",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (ManifestPermission manifestPermission) {
            manifestPermission.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Constants.destroyPreviousActivity && requestCode == Constants.destroyPreviousActivity){
            finish();
        }
    }
}
