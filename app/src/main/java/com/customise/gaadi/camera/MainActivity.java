package com.customise.gaadi.camera;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gaadi.neon.Enumerations.CameraFacing;
import com.gaadi.neon.Enumerations.CameraOrientation;
import com.gaadi.neon.Enumerations.CameraType;
import com.gaadi.neon.Enumerations.GalleryType;
import com.gaadi.neon.PhotosLibrary;
import com.gaadi.neon.interfaces.ICameraParam;
import com.gaadi.neon.interfaces.IGalleryParam;
import com.gaadi.neon.interfaces.INeutralParam;
import com.gaadi.neon.interfaces.SetOnImageCollectionListener;
import com.gaadi.neon.model.ImageTagModel;
import com.gaadi.neon.model.PhotosMode;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.NeonConstants;
import com.gaadi.neon.util.NeonException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SetOnImageCollectionListener {

    private static final String TAG = "MainActivity";
    public static final int REQUEST_CODE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void cameraPriorityClicked(View view) {
        try {
            PhotosLibrary.collectPhotos(this, PhotosMode.setCameraMode().setParams(new ICameraParam() {
                @Override
                public CameraFacing getCameraFacing() {
                    return CameraFacing.front;
                }

                @Override
                public CameraOrientation getCameraOrientation() {
                    return CameraOrientation.landscape;
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
                    return 0;
                }

                @Override
                public boolean getTagEnabled() {
                    return true;
                }

                @Override
                public List<ImageTagModel> getImageTagsModel() {
                    ArrayList<ImageTagModel> list = new ArrayList<ImageTagModel>();
                    for (int i = 0; i < 2; i++) {
                        list.add(new ImageTagModel("Tag" + i, String.valueOf(i), true));
                    }
                    return list;
                }




            }),this);
        } catch (NullPointerException e) {

        } catch (NeonException e) {

        }

    }

    public void galleryPriorityClicked(View view) {
        try {
            PhotosLibrary.collectPhotos(this, PhotosMode.setGalleryMode().setParams(new IGalleryParam() {
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
                    return 0;
                }

                @Override
                public boolean getTagEnabled() {
                    return false;
                }

                @Override
                public List<ImageTagModel> getImageTagsModel() {
                    return null;
                }


            }),this);
        } catch (NeonException e) {
            e.printStackTrace();
        }
    }

    public void galleryOnlyGridClicked(View view) {
        try {
            PhotosLibrary.collectPhotos(this, PhotosMode.setGalleryMode().setParams(new IGalleryParam() {
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
                    return false;
                }

                @Override
                public boolean isRestrictedExtensionJpgPngEnabled() {
                    return true;
                }

                @Override
                public int getNumberOfPhotos() {
                    return 0;
                }

                @Override
                public boolean getTagEnabled() {
                    return false;
                }

                @Override
                public List<ImageTagModel> getImageTagsModel() {
                    return null;
                }


            }),this);
        } catch (NeonException e) {
            e.printStackTrace();
        }
    }

    public void cameraOnlyClicked(View view) {
        try {
            PhotosLibrary.collectPhotos(this, PhotosMode.setCameraMode().setParams(new ICameraParam() {
                @Override
                public CameraFacing getCameraFacing() {
                    return CameraFacing.front;
                }

                @Override
                public CameraOrientation getCameraOrientation() {
                    return CameraOrientation.landscape;
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
                    return false;
                }

                @Override
                public int getNumberOfPhotos() {
                    return 0;
                }

                @Override
                public boolean getTagEnabled() {
                    return true;
                }

                @Override
                public List<ImageTagModel> getImageTagsModel() {
                    ArrayList<ImageTagModel> list = new ArrayList<ImageTagModel>();
                    for (int i = 0; i < 2; i++) {
                        list.add(new ImageTagModel("Tag" + i, String.valueOf(i), true));
                    }
                    return list;
                }




            }),this);
        } catch (NullPointerException e) {

        } catch (NeonException e) {

        }

    }

    public void galleryOnlyGridFilesClicked(View view) {
        try {
            PhotosLibrary.collectPhotos(this, PhotosMode.setGalleryMode().setParams(new IGalleryParam() {
                @Override
                public boolean selectVideos() {
                    return false;
                }

                @Override
                public GalleryType getGalleryViewType() {
                    return GalleryType.grid_files;
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
                    return 0;
                }

                @Override
                public boolean getTagEnabled() {
                    return false;
                }

                @Override
                public List<ImageTagModel> getImageTagsModel() {
                    return null;
                }


            }),this);
        } catch (NeonException e) {
            e.printStackTrace();
        }
    }


    public void neutralClicked(View view){
        try {
            PhotosLibrary.collectPhotos(this,PhotosMode.setNeutralMode().setParams(new INeutralParam() {
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
                    return 0;
                }

                @Override
                public boolean getTagEnabled() {
                    return true;
                }

                @Override
                public List<ImageTagModel> getImageTagsModel() {
                    ArrayList<ImageTagModel> list = new ArrayList<ImageTagModel>();
                    for (int i = 0; i < 2; i++) {
                        list.add(new ImageTagModel("Tag" + i, String.valueOf(i), true));
                    }
                    return list;
                }
            }),this);
        } catch (NeonException e) {
            e.printStackTrace();
        }
    }



    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode + " responseCode : " + resultCode);


        List<FileInfo> fileInfoList = (List<FileInfo>) data.getSerializableExtra(Constants.RESULT_IMAGES);
        HashMap<String, List<FileInfo>> hashMap = (HashMap<String, List<FileInfo>>) data.getSerializableExtra(Constants.RESULT_IMAGES);


        if (hashMap != null) {
            Toast.makeText(this, "Got hash map with tags", Toast.LENGTH_SHORT).show();
        }
        if (fileInfoList != null) {
            Toast.makeText(this, "Got file list without tags", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void imageCollection(HashMap<String, List<FileInfo>> imageTagsCollection) {
        if(imageTagsCollection != null && imageTagsCollection.size()>0){
            Toast.makeText(this,"Got Tags collection with size " + imageTagsCollection.size(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void imageCollection(List<FileInfo> imageCollection) {
        if(imageCollection != null && imageCollection.size()>0){
            Toast.makeText(this,"Got collection with size " + imageCollection.size(),Toast.LENGTH_SHORT).show();
        }
    }
}
