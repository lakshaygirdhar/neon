package com.gaadi.neon.activity.neutral;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;

import com.gaadi.neon.Enumerations.CameraFacing;
import com.gaadi.neon.Enumerations.CameraOrientation;
import com.gaadi.neon.Enumerations.CameraType;
import com.gaadi.neon.Enumerations.GalleryType;
import com.gaadi.neon.PhotosLibrary;
import com.gaadi.neon.fragment.ImageShowFragment;
import com.gaadi.neon.interfaces.ICameraParam;
import com.gaadi.neon.interfaces.IGalleryParam;
import com.gaadi.neon.model.ImageTagModel;
import com.gaadi.neon.model.PhotosMode;
import com.gaadi.neon.util.NeonException;
import com.gaadi.neon.util.SingletonClass;
import com.scanlibrary.R;
import com.scanlibrary.databinding.NeutralActivityLayoutBinding;

import java.util.List;

/**
 * @author princebatra
 * @version 1.0
 * @since 3/2/17
 */
public class NeonNeutralActivity extends NeonBaseNeutralActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bindXml();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(SingletonClass.getSingleonInstance().getImagesCollection() == null ||
                SingletonClass.getSingleonInstance().getImagesCollection().size()<=0){
            setTitle("Photos");
        }else{
            setTitle("Photos ( "  + SingletonClass.getSingleonInstance().getImagesCollection().size() + " )");
        }
    }

    private void bindXml() {
        //NeutralActivityLayoutBinding binder = DataBindingUtil.setContentView(this, R.layout.neutral_activity_layout);
        NeutralActivityLayoutBinding binder = DataBindingUtil.inflate(getLayoutInflater(), R.layout.neutral_activity_layout,frameLayout,true);

        binder.setHandlers(this);

        ImageShowFragment imageShowFragment = new ImageShowFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.imageShowFragmentContainer, imageShowFragment).commit();
    }

    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.addPhotoCamera){
            try {
                PhotosLibrary.collectPhotos(this,PhotosMode.setCameraMode().setParams(new ICameraParam() {
                    @Override
                    public CameraFacing getCameraFacing() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().getCameraFacing();
                    }

                    @Override
                    public CameraOrientation getCameraOrientation() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().getCameraOrientation();
                    }

                    @Override
                    public boolean getFlashEnabled() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().getFlashEnabled();
                    }

                    @Override
                    public boolean getCameraSwitchingEnabled() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().getCameraSwitchingEnabled();
                    }

                    @Override
                    public boolean getVideoCaptureEnabled() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().getVideoCaptureEnabled();
                    }

                    @Override
                    public CameraType getCameraViewType() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().getCameraViewType();
                    }

                    @Override
                    public boolean cameraToGallerySwitchEnabled() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().cameraToGallerySwitchEnabled();
                    }

                    @Override
                    public int getNumberOfPhotos() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().getNumberOfPhotos();
                    }

                    @Override
                    public boolean getTagEnabled() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().getTagEnabled();
                    }

                    @Override
                    public List<ImageTagModel> getImageTagsModel() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().getImageTagsModel();
                    }
                }),SingletonClass.getSingleonInstance().getImageResultListener());
            } catch (NeonException e) {
                e.printStackTrace();
            }
        }else if(id == R.id.addPhotoGallary){
            try {
                PhotosLibrary.collectPhotos(this,PhotosMode.setGalleryMode().setParams(new IGalleryParam() {
                    @Override
                    public boolean selectVideos() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().selectVideos();
                    }

                    @Override
                    public GalleryType getGalleryViewType() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().getGalleryViewType();
                    }

                    @Override
                    public boolean enableFolderStructure() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().enableFolderStructure();
                    }

                    @Override
                    public boolean galleryToCameraSwitchEnabled() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().galleryToCameraSwitchEnabled();
                    }

                    @Override
                    public boolean isRestrictedExtensionJpgPngEnabled() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().isRestrictedExtensionJpgPngEnabled();
                    }

                    @Override
                    public int getNumberOfPhotos() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().getNumberOfPhotos();
                    }

                    @Override
                    public boolean getTagEnabled() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().getTagEnabled();
                    }

                    @Override
                    public List<ImageTagModel> getImageTagsModel() {
                        return SingletonClass.getSingleonInstance().getNeutralParam().getImageTagsModel();
                    }
                }),SingletonClass.getSingleonInstance().getImageResultListener());
            } catch (NeonException e) {
                e.printStackTrace();
            }
        }else if(id == android.R.id.home){
            onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
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
