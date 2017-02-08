package com.gaadi.neon.activity.gallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gaadi.neon.Enumerations.CameraFacing;
import com.gaadi.neon.Enumerations.CameraOrientation;
import com.gaadi.neon.Enumerations.CameraType;
import com.gaadi.neon.PhotosLibrary;
import com.gaadi.neon.activity.ImageShow;
import com.gaadi.neon.adapter.GalleryHoriontalAdapter;
import com.gaadi.neon.adapter.GridFilesAdapter;
import com.gaadi.neon.interfaces.ICameraParam;
import com.gaadi.neon.interfaces.SetOnImageClickListener;
import com.gaadi.neon.interfaces.SetOnPermissionResultListener;
import com.gaadi.neon.model.ImageTagModel;
import com.gaadi.neon.model.PhotosMode;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.ManifestPermission;
import com.gaadi.neon.util.NeonException;
import com.gaadi.neon.util.PermissionType;
import com.gaadi.neon.util.SingletonClass;
import com.scanlibrary.R;
import com.scanlibrary.databinding.ActivityGridFilesBinding;
import com.scanlibrary.databinding.HorizontalGalleryLayoutBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author princebatra
 * @version 1.0
 * @since 6/2/17
 */
public class HorizontalFilesActivity extends NeonBaseGalleryActivity implements SetOnImageClickListener{

    HorizontalGalleryLayoutBinding binder;
    ArrayList<FileInfo> fileInfos;
    MenuItem textViewDone,menuItemCamera;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bindXml();
        String title = getIntent().getStringExtra(Constants.BucketName);
        if (title == null || title.length() <= 0) {
            title = "Files";
        }
        setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done_file, menu);
        textViewDone = menu.findItem(R.id.menu_next);
        menuItemCamera = menu.findItem(R.id.menuCamera);
        menuItemCamera.setVisible(SingletonClass.getSingleonInstance().getGalleryParam().galleryToCameraSwitchEnabled());
        textViewDone.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.menu_next) {
            if (SingletonClass.getSingleonInstance().getImagesCollection() == null ||
                    SingletonClass.getSingleonInstance().getImagesCollection().size() <= 0) {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }else {
               /* if(SingletonClass.getSingleonInstance().getImagesCollection() == null ||
                        SingletonClass.getSingleonInstance().getImagesCollection().size()<=0){
                    Toast.makeText(this,"No image selected",Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }*/
                if (!SingletonClass.getSingleonInstance().isNeutralEnabled()) {
                    Intent intent = new Intent(this, ImageShow.class);
                    startActivity(intent);
                    setResult(Constants.destroyPreviousActivity);
                    finish();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }else if (id == R.id.menuCamera) {
            performCameraOperation();
            setResult(Constants.destroyPreviousActivity);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(SingletonClass.getSingleonInstance().isNeutralEnabled()){
            super.onBackPressed();
        }else{
            if(!SingletonClass.getSingleonInstance().getGalleryParam().enableFolderStructure()){
                SingletonClass.getSingleonInstance().showBackOperationAlertIfNeeded(this);
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

                @Override
                public ArrayList<FileInfo> getAlreadyAddedImages() {
                    return null;
                }

            };
        }
        try {
            PhotosLibrary.collectPhotos(this, PhotosMode.setCameraMode().setParams(cameraParam),SingletonClass.getSingleonInstance().getImageResultListener());
        } catch (NeonException e) {
            e.printStackTrace();
        }
    }


    private void bindXml() {
        try {
            askForPermissionIfNeeded(PermissionType.write_external_storage, new SetOnPermissionResultListener() {
                @Override
                public void onResult(boolean permissionGranted) {
                    if(permissionGranted){
                        binder = DataBindingUtil.inflate(getLayoutInflater(),R.layout.horizontal_gallery_layout,frameLayout,true);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HorizontalFilesActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        binder.galleryHorizontalRv.setLayoutManager(linearLayoutManager);
                        fileInfos = getFileFromBucketId(getIntent().getStringExtra(Constants.BucketId));
                        if(fileInfos != null && fileInfos.size()>0) {
                            GalleryHoriontalAdapter adapter = new GalleryHoriontalAdapter(HorizontalFilesActivity.this, fileInfos, HorizontalFilesActivity.this);
                            binder.galleryHorizontalRv.setAdapter(adapter);
                            onClick(fileInfos.get(0));
                        }
                    }else{
                        Toast.makeText(HorizontalFilesActivity.this,"Permission not granted",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (ManifestPermission manifestPermission) {
            manifestPermission.printStackTrace();
        }


    }


    @Override
    public void onClick(FileInfo fileInfo) {
        Glide.with(this).load(fileInfo.getFilePath())
                .placeholder(R.drawable.default_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binder.fullScreenImage);

    }
}
