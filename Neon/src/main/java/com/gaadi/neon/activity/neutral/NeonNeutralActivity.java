package com.gaadi.neon.activity.neutral;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;

import com.gaadi.neon.enumerations.CameraFacing;
import com.gaadi.neon.enumerations.CameraOrientation;
import com.gaadi.neon.enumerations.CameraType;
import com.gaadi.neon.enumerations.GalleryType;
import com.gaadi.neon.PhotosLibrary;
import com.gaadi.neon.fragment.ImageShowFragment;
import com.gaadi.neon.interfaces.ICameraParam;
import com.gaadi.neon.interfaces.IGalleryParam;
import com.gaadi.neon.model.ImageTagModel;
import com.gaadi.neon.model.PhotosMode;
import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.NeonException;
import com.gaadi.neon.util.NeonImagesHandler;
import com.scanlibrary.R;
import com.scanlibrary.databinding.NeutralActivityLayoutBinding;

import java.util.ArrayList;
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
        bindXml();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (NeonImagesHandler.getSingleonInstance().getImagesCollection() == null ||
                NeonImagesHandler.getSingleonInstance().getImagesCollection().size() <= 0) {
            setTitle(R.string.photos);
        } else {
            setTitle(getString(R.string.photos_count,NeonImagesHandler.getSingleonInstance().getImagesCollection().size()));
        }
    }

    private void bindXml() {
        NeutralActivityLayoutBinding binder = DataBindingUtil.inflate(getLayoutInflater(), R.layout.neutral_activity_layout, frameLayout, true);

        binder.setHandlers(this);

        ImageShowFragment imageShowFragment = new ImageShowFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.imageShowFragmentContainer, imageShowFragment).commit();
    }

    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.addPhotoCamera) {
            try {
                PhotosLibrary.collectPhotos(this, PhotosMode.setCameraMode().setParams(new ICameraParam() {
                    @Override
                    public CameraFacing getCameraFacing() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().getCameraFacing();
                    }

                    @Override
                    public CameraOrientation getCameraOrientation() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().getCameraOrientation();
                    }

                    @Override
                    public boolean getFlashEnabled() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().getFlashEnabled();
                    }

                    @Override
                    public boolean getCameraSwitchingEnabled() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().getCameraSwitchingEnabled();
                    }

                    @Override
                    public boolean getVideoCaptureEnabled() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().getVideoCaptureEnabled();
                    }

                    @Override
                    public CameraType getCameraViewType() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().getCameraViewType();
                    }

                    @Override
                    public boolean cameraToGallerySwitchEnabled() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().cameraToGallerySwitchEnabled();
                    }

                    @Override
                    public int getNumberOfPhotos() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().getNumberOfPhotos();
                    }

                    @Override
                    public boolean getTagEnabled() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().getTagEnabled();
                    }

                    @Override
                    public List<ImageTagModel> getImageTagsModel() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().getImageTagsModel();
                    }

                    @Override
                    public ArrayList<FileInfo> getAlreadyAddedImages() {
                        return null;
                    }
                }), NeonImagesHandler.getSingleonInstance().getImageResultListener());
            } catch (NeonException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.addPhotoGallary) {
            try {
                PhotosLibrary.collectPhotos(this, PhotosMode.setGalleryMode().setParams(new IGalleryParam() {
                    @Override
                    public boolean selectVideos() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().selectVideos();
                    }

                    @Override
                    public GalleryType getGalleryViewType() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().getGalleryViewType();
                    }

                    @Override
                    public boolean enableFolderStructure() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().enableFolderStructure();
                    }

                    @Override
                    public boolean galleryToCameraSwitchEnabled() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().galleryToCameraSwitchEnabled();
                    }

                    @Override
                    public boolean isRestrictedExtensionJpgPngEnabled() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().isRestrictedExtensionJpgPngEnabled();
                    }

                    @Override
                    public int getNumberOfPhotos() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().getNumberOfPhotos();
                    }

                    @Override
                    public boolean getTagEnabled() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().getTagEnabled();
                    }

                    @Override
                    public List<ImageTagModel> getImageTagsModel() {
                        return NeonImagesHandler.getSingleonInstance().getNeutralParam().getImageTagsModel();
                    }

                    @Override
                    public ArrayList<FileInfo> getAlreadyAddedImages() {
                        return null;
                    }
                }), NeonImagesHandler.getSingleonInstance().getImageResultListener());
            } catch (NeonException e) {
                e.printStackTrace();
            }
        } else if (id == android.R.id.home) {
            onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        NeonImagesHandler.getSingleonInstance().showBackOperationAlertIfNeeded(this);
    }

}
