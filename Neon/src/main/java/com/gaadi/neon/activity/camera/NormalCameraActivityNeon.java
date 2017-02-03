package com.gaadi.neon.activity.camera;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gaadi.neon.Enumerations.GalleryType;
import com.gaadi.neon.PhotosLibrary;
import com.gaadi.neon.activity.ImageShow;
import com.gaadi.neon.fragment.CameraFragment1;
import com.gaadi.neon.interfaces.ICameraParam;
import com.gaadi.neon.interfaces.IGalleryParam;
import com.gaadi.neon.model.ImageTagModel;
import com.gaadi.neon.model.PhotosMode;
import com.gaadi.neon.util.AnimationUtils;
import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.NeonException;
import com.gaadi.neon.util.SingletonClass;
import com.scanlibrary.R;
import com.scanlibrary.databinding.NormalCameraActivityLayoutBinding;

import java.util.List;

/**
 * @author princebatra
 * @version 1.0
 * @since 25/1/17
 */
public class NormalCameraActivityNeon extends NeonBaseCameraActivity implements CameraFragment1.SetOnPictureTaken {

    private TextView tvImageName, tvTag, tvNext, tvPrevious;
    private ImageView buttonGallery;
    ICameraParam cameraParams;
    RelativeLayout tagsLayout;
    List<ImageTagModel> tagModels;
    int currentTag;
    NormalCameraActivityLayoutBinding binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindXml();
        cameraParams = SingletonClass.getSingleonInstance().getCameraParam();
        customize();
        CameraFragment1 fragment = new CameraFragment1();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private void bindXml() {
        binder = DataBindingUtil.setContentView(this, R.layout.normal_camera_activity_layout);
        tvImageName = binder.tvImageName;
        tvTag = binder.tvTag;
        tvNext = binder.tvSkip;
        tvPrevious = binder.tvPrev;
        buttonGallery = binder.buttonGallery;
        tagsLayout = binder.rlTags;
        binder.setHandlers(this);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonDone) {
            if (finishValidation()) {
                if (!SingletonClass.getSingleonInstance().isNeutralEnabled()) {
                    Intent intent = new Intent(this, ImageShow.class);
                    startActivity(intent);
                    finish();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        } else if (id == R.id.buttonGallery) {
            try {
                IGalleryParam galleryParam = SingletonClass.getSingleonInstance().getGalleryParam();
                if (galleryParam == null) {
                    galleryParam = new IGalleryParam() {
                        @Override
                        public boolean selectVideos() {
                            return false;
                        }

                        @Override
                        public GalleryType getGalleryViewType() {
                            return GalleryType.grid_folders;
                        }

                        @Override
                        public boolean galleryToCameraSwitchEnabled() {
                            return true;
                        }

                        @Override
                        public boolean isRestrictedExtensionJpgPngEnabled() {
                            return true;
                        }

                        @Override
                        public int getNumberOfPhotos() {
                            return SingletonClass.getSingleonInstance().getCameraParam().getNumberOfPhotos();
                        }

                        @Override
                        public boolean getTagEnabled() {
                            return SingletonClass.getSingleonInstance().getCameraParam().getTagEnabled();
                        }

                        @Override
                        public List<ImageTagModel> getImageTagsModel() {
                            return SingletonClass.getSingleonInstance().getCameraParam().getImageTagsModel();
                        }

                    };
                }
                PhotosLibrary.collectPhotos(this, PhotosMode.setGalleryMode().setParams(galleryParam),SingletonClass.getSingleonInstance().getImageResultListener());
                finish();
            } catch (NeonException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.tvSkip) {
            if (currentTag == tagModels.size() - 1) {
                onClick(binder.buttonDone);
            } else {
                setTag(getNextTag(), true);
            }
        } else if (id == R.id.tvPrev) {
            setTag(getPreviousTag(), false);
        }
    }

    private boolean finishValidation() {
        if (SingletonClass.getSingleonInstance().getCameraParam().getTagEnabled()) {
            for (int i = 0; i < tagModels.size(); i++) {
                if (tagModels.get(i).isMandatory() &&
                        !SingletonClass.getSingleonInstance().checkImagesAvailableForTag(tagModels.get(i))) {
                    Toast.makeText(this, String.format(getString(R.string.tag_mandatory_error), tagModels.get(i).getTagName()),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } else {
            if (SingletonClass.getSingleonInstance().getImagesCollection() == null ||
                    SingletonClass.getSingleonInstance().getImagesCollection().size() <= 0) {
                Toast.makeText(this, "No Image Captured", Toast.LENGTH_SHORT).show();
                return false;
            } else if (SingletonClass.getSingleonInstance().getImagesCollection().size() <
                    SingletonClass.getSingleonInstance().getCameraParam().getNumberOfPhotos()) {
                Toast.makeText(this, SingletonClass.getSingleonInstance().getCameraParam().getNumberOfPhotos() -
                        SingletonClass.getSingleonInstance().getImagesCollection().size() + " more image required", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public ImageTagModel getNextTag() {
        if (tagModels.get(currentTag).isMandatory() &&
                !SingletonClass.getSingleonInstance().checkImagesAvailableForTag(tagModels.get(currentTag))) {
            Toast.makeText(this, String.format(getString(R.string.tag_mandatory_error), tagModels.get(currentTag).getTagName()),
                    Toast.LENGTH_SHORT).show();
        } else {
            currentTag++;
        }

        if (currentTag == tagModels.size() - 1) {
            tvNext.setText(getString(R.string.finish));
        }
        if (currentTag > 0) {
            tvPrevious.setVisibility(View.VISIBLE);
        }

        return tagModels.get(currentTag);
    }

    public ImageTagModel getPreviousTag() {
        if (currentTag > 0) {
            currentTag--;
        }
        if (currentTag != tagModels.size() - 1) {
            tvNext.setText(getString(R.string.next));
        }
        if (currentTag == 0) {
            tvPrevious.setVisibility(View.GONE);
        }
        return tagModels.get(currentTag);
    }

    public void setTag(ImageTagModel imageTagModel, boolean rightToLeft) {
        tvTag.setText(imageTagModel.getTagName());
        if (rightToLeft) {
            AnimationUtils.translateOnXAxis(tvTag, 200, 0);
        } else {
            AnimationUtils.translateOnXAxis(tvTag, -200, 0);
        }

    }

    private void customize() {
        if (cameraParams.getTagEnabled()) {
            tvImageName.setVisibility(View.GONE);
            tagsLayout.setVisibility(View.VISIBLE);
            tagModels = cameraParams.getImageTagsModel();

            setTag(tagModels.get(currentTag), true);
        } else {
            tagsLayout.setVisibility(View.GONE);
            findViewById(R.id.rlTags).setVisibility(View.GONE);
        }

        buttonGallery.setVisibility(cameraParams.cameraToGallerySwitchEnabled() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPictureTaken(String filePath) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilePath(filePath);
        fileInfo.setFileName(filePath.substring(filePath.lastIndexOf("/") + 1));
        fileInfo.setSource(FileInfo.SOURCE.PHONE_CAMERA);
        if (cameraParams.getTagEnabled()) {
            fileInfo.setFileTag(tagModels.get(currentTag));
        }
        SingletonClass.getSingleonInstance().putInImageCollection(fileInfo);
    }

}
