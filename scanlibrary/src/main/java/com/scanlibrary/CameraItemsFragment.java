package com.scanlibrary;

//import android.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imageuploadlib.Activity.CameraActivity;
import com.imageuploadlib.Activity.GalleryActivity;
import com.imageuploadlib.Adapters.PhotosGridAdapter;
import com.imageuploadlib.Interfaces.UpdateSelection;
import com.imageuploadlib.Utils.ApplicationController;
import com.imageuploadlib.Utils.CommonUtils;
import com.imageuploadlib.Utils.Constants;
import com.imageuploadlib.Utils.FileInfo;
import com.imageuploadlib.Utils.PhotoParams;

import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;

/**
 * Created by Lakshay on 13-02-2015.
 */
public class CameraItemsFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemLongClickListener, UpdateSelection, AdapterView.OnItemClickListener {

    private static final String TAG = "CameraFragment";

    public static final int CODE_CAMERA = 148;
    public static final int CODE_GALLERY = 256;
    private static final int OPEN_IMAGE_VIEW_PAGER_SCREEN = 102;

    private static final String UPLOADED_IMAGEGS = "alreadyUploaded";
    private static final String SELECTED_IMAGES = "alreadySelected";
    public static final String IMG_LOAD_DEF_BIG = "IMG_LOAD_DEF_BIG";
    public static final String IMG_LOAD_DEF_SMALL = "IMG_LOAD_DEF_SMALL";
    private int maxPhotos = 20;
    public static final String ADD_PHOTOS = "addPhotos";
    public static final String PHOTO_PARAMS = "photoParams";
    TextView tvCount;
    public static final String APP_SHARED_PREFERENCE = "com.gcloud.gaadi.prefs";
    public boolean COACH_MARKS_DISABLE = false;

    DynamicGridView gvPhotos;
    PhotosGridAdapter photosGridAdapter;

    PhotoParams params;
    public static int loadDefImgBig, loadDefImgSmall;

    private ImagesHandler imagesHandler;
    RelativeLayout coachMarksLayout;

    private ArrayList<FileInfo> deletedImages;
    private ArrayList<FileInfo> cameraItemsFiles;
    private Context context;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ImageReviewActivity.class);
//        intent.putExtra(Constants.IMAGE_TAGS_FOR_REVIEW, mImageTags);
        intent.putExtra(ScanConstants.IMAGE_MODEL_FOR__REVIEW, cameraItemsFiles);
        intent.putExtra(ScanConstants.IMAGE_REVIEW_POSITION, position);
//        intent.putExtra(Constants.SINGLE_TAG_SELECTION,singleTagSelection);
//        intent.putExtra(Constants.ALREADY_SELECTED_TAGS,alreadySelectedTags);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, OPEN_IMAGE_VIEW_PAGER_SCREEN);
    }


    public interface ImagesHandler {
        public void outputImages(ArrayList<FileInfo> files, ArrayList<FileInfo> deletedImages);

        public void dragImagesHandler(int first, int second);

        public void gaHandler(String screen, String category, String action, String label, ArrayList<FileInfo> images);
    }

    public static CameraItemsFragment newInstance(Context context, PhotoParams params,
                                                  ImagesHandler imagesHandler,
                                                  ArrayList<?> uploadedImages,
                                                  int loadDefaultResBig,
                                                  int loadDefaultResSmall) {
        CameraItemsFragment fragment = new CameraItemsFragment();
        fragment.imagesHandler = imagesHandler;
        Bundle bundle = new Bundle();
        bundle.putSerializable(PHOTO_PARAMS, params);
        bundle.putSerializable(SELECTED_IMAGES, uploadedImages);
        bundle.putInt(IMG_LOAD_DEF_BIG, loadDefaultResBig);
        bundle.putInt(IMG_LOAD_DEF_SMALL, loadDefaultResSmall);
        fragment.setArguments(bundle);
        fragment.context = context;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.camera_fragment, container, false);

        deletedImages = new ArrayList<FileInfo>();
        cameraItemsFiles = new ArrayList<FileInfo>();
        rootView.findViewById(R.id.addPhotoCamera).setOnClickListener(this);
        rootView.findViewById(R.id.addPhotoGallary).setOnClickListener(this);
        rootView.findViewById(R.id.done).setOnClickListener(this);
        rootView.findViewById(R.id.ivBack).setOnClickListener(this);
        tvCount = (TextView) rootView.findViewById(R.id.photosCount);

        coachMarksLayout = (RelativeLayout) rootView.findViewById(R.id.coachMarksImage);
        coachMarksLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coachMarksLayout.setVisibility(View.GONE);

                SharedPreferences prefs = getActivity().getSharedPreferences(APP_SHARED_PREFERENCE, Context.MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = prefs.edit();
                prefEditor.putBoolean("SHOW_COACH_MARKS", false);
                prefEditor.commit();


            }
        });


        params = (PhotoParams) getArguments().getSerializable(PHOTO_PARAMS);
//        ArrayList<?> alreadySelected = (ArrayList<?>) getArguments().getSerializable(SELECTED_IMAGES);

//        addToSelectedFiles(alreadySelected);
        loadDefImgBig = getArguments().getInt(IMG_LOAD_DEF_BIG);
        loadDefImgSmall = getArguments().getInt(IMG_LOAD_DEF_SMALL);
//        ArrayList<String> uploadedImages = getArguments().getStringArrayList(UPLOADED_IMAGEGS);
//        if (alreadySelected != null && alreadySelected.size() > 0) {
//            ArrayList<FileInfo> uploadedFiles = (ArrayList<FileInfo>) createInfos(alreadySelected);
//            cameraItemsFiles.addAll(uploadedFiles);
//            ApplicationController.selectedImages.addAll(uploadedFiles);
//        }
        setUpPhotosGrid(rootView);
        if (params.getNoOfPhotos() > 0)
            maxPhotos = params.getNoOfPhotos();

        return rootView;
    }

    private void addToSelectedFiles(ArrayList<?> selectedFiles) {
        if (selectedFiles != null && selectedFiles.size() > 0) {
            if (selectedFiles.get(0) instanceof FileInfo) {
                for (Object fileInfo : selectedFiles) {
                    FileInfo fileInfo1 = (FileInfo) fileInfo;
                    ApplicationController.selectedFiles.add(fileInfo1.getFilePath());
                }
            } else {
                ArrayList<String> selectedFiles1 = (ArrayList<String>) selectedFiles;
                ApplicationController.selectedFiles.addAll(selectedFiles1);
            }
        }
    }

    private ArrayList<?> createInfos(ArrayList<?> uploadedImages) {
        if (uploadedImages != null && uploadedImages.get(0) instanceof FileInfo)
            return uploadedImages;
        ArrayList<FileInfo> files = new ArrayList<FileInfo>();
        for (int i = 0; i < uploadedImages.size(); i++) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFilePath((String) uploadedImages.get(i));
            fileInfo.setType(FileInfo.FILE_TYPE.IMAGE);
            fileInfo.setSelected(true);
            fileInfo.setFromServer(true);
            files.add(fileInfo);
        }
        return files;
    }

    @Override
    public void onResume() {
        super.onResume();
        tvCount.setText(cameraItemsFiles.size() + "");
        SharedPreferences prefs = getActivity().getSharedPreferences(APP_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        boolean coachMarksEnable = prefs.getBoolean("SHOW_COACH_MARKS", true);
        if (photosGridAdapter.getCount() >= 2 && coachMarksEnable) {
            coachMarksLayout.setVisibility(View.VISIBLE);
        } else {
            coachMarksLayout.setVisibility(View.GONE);
        }
    }

    private void setUpPhotosGrid(View rootView) {
        gvPhotos = (DynamicGridView) rootView.findViewById(R.id.gvPhotos);
        photosGridAdapter = new PhotosGridAdapter(getActivity(),
                cameraItemsFiles, 2, this,
                loadDefImgBig, loadDefImgSmall);


        gvPhotos.setAdapter(photosGridAdapter);
        gvPhotos.setOnItemLongClickListener(this);
        gvPhotos.setOnItemClickListener(this);;
        gvPhotos.setOnDropListener(new DynamicGridView.OnDropListener() {
            @Override
            public void onActionDrop() {
                gvPhotos.stopEditMode();
            }
        });
        gvPhotos.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {

                if (newPosition < cameraItemsFiles.size()) {
                    FileInfo old = cameraItemsFiles.remove(oldPosition);
                    cameraItemsFiles.add(newPosition, old);
                }
                imagesHandler.dragImagesHandler(oldPosition, newPosition);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraItemsFiles.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    ArrayList<FileInfo> cameraList;
    ArrayList<FileInfo> galleryList;
        switch (requestCode) {
            case CODE_CAMERA:
                if (data != null) {
                    cameraList = (ArrayList<FileInfo>) data.getSerializableExtra(ScanConstants.CAMERA_IMAGES);
                    updateGrid(cameraList, ADD_PHOTOS);
                    imagesHandler.gaHandler(Constants.SCREEN_CAMERA_ITEMS, Constants.CATEGORY_CAMERA, Constants.IMAGE_CAPTURE, cameraList.size() + "", cameraList);
                }
                break;

            case CODE_GALLERY:
                if (data != null) {
                    galleryList = (ArrayList<FileInfo>) data.getSerializableExtra(GalleryActivity.GALLERY_SELECTED_PHOTOS);
                    setSource(galleryList, FileInfo.SOURCE.PHONE_GALLERY);
                    checkForDeletedFiles(cameraItemsFiles);
                    updateGrid(galleryList, ADD_PHOTOS);
                    imagesHandler.gaHandler(Constants.SCREEN_CAMERA_ITEMS, Constants.CATEGORY_GALLERY, Constants.TAKE_FROM_GALLERY, galleryList.size() + "", galleryList);
                }
                break;

            case Constants.REQUEST_PERMISSION_CAMERA:
                onClick(getView().findViewById(R.id.addPhotoCamera));
                break;

            case Constants.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE:
                onClick(getView().findViewById(R.id.addPhotoGallary));
                break;
            case OPEN_IMAGE_VIEW_PAGER_SCREEN:
                if (resultCode == ScanConstants.RESULT_FROM_IMAGE_REVIEW_ACTIVITY) {
                    if (null != data) {
                        int index = data.getIntExtra(ScanConstants.IMAGE_INDEX_SENT_FOR_CROPPING,0);
                        cameraItemsFiles.set(index,(FileInfo) data.getSerializableExtra(ScanConstants.IMAGE_RECEIVED_AFTER_CROPPING));
                        photosGridAdapter.set(cameraItemsFiles);
                        photosGridAdapter.notifyDataSetChanged();
                        gvPhotos.invalidate();
                    }
                }
                break;
        }
    }

    //To remove files from main adapter which were unselected from the gallery folder.
    private void checkForDeletedFiles(ArrayList<FileInfo> cameraItemsFiles) {

        ArrayList<FileInfo> deleteFiles = new ArrayList<>();
        for (FileInfo fileInfo : cameraItemsFiles) {
            if (fileInfo.getSource() == FileInfo.SOURCE.PHONE_GALLERY) {
                if ((ApplicationController.selectedFiles != null) && !ApplicationController.selectedFiles.contains(fileInfo.getFilePath())) {
                    deleteFiles.add(fileInfo);
                }
            }
        }
        cameraItemsFiles.removeAll(deleteFiles);
    }

    //To set the source of the image
    private void setSource(ArrayList<FileInfo> list1, FileInfo.SOURCE phoneGallery) {
        for (FileInfo fileInfo : list1) {
            fileInfo.setSource(phoneGallery);
        }
    }

    private void updateGrid(ArrayList<FileInfo> listAdd, String action) {
        if (action.equals(ADD_PHOTOS)) {
            CommonUtils.removeFileInfo(listAdd, cameraItemsFiles);
            cameraItemsFiles.addAll(listAdd);
            photosGridAdapter.set(cameraItemsFiles);
            photosGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addPhotoCamera) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !CommonUtils.checkForPermission(context,
                    new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.REQUEST_PERMISSION_CAMERA, "Camera and Storage")) {
                return;
            }
            if (cameraItemsFiles.size() >= maxPhotos) {
                Toast.makeText(getActivity().getApplicationContext(), "Maximum photos can be : " + maxPhotos, Toast.LENGTH_SHORT).show();
                return;
            }
//            imagesHandler.gaHandler(Constants.SCREEN_CAMERA_ITEMS, Constants.CATEGORY_CAMERA, Constants.ACTION_CLICK, Constants.TAKE_PHOTO, 0L);
            Intent intent = new Intent(getActivity(), ScanActivity.class);
            intent.putExtra(GalleryActivity.MAX_COUNT, maxPhotos - cameraItemsFiles.size());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(PHOTO_PARAMS, params);
            startActivityForResult(intent, CODE_CAMERA);
        } else if (v.getId() == R.id.addPhotoGallary) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !CommonUtils.checkForPermission(context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE, "Storage")) {
                return;
            }
            if (cameraItemsFiles.size() >= maxPhotos) {
                Toast.makeText(getActivity().getApplicationContext(), "Maximum photos can be : " + maxPhotos, Toast.LENGTH_SHORT).show();
                return;
            }
//            imagesHandler.gaHandler(Constants.SCREEN_CAMERA_ITEMS, Constants.CATEGORY_GALLERY, Constants.ACTION_CLICK, Constants.TAKE_FROM_GALLERY, 0L);
            Intent intent1 = new Intent(getActivity(), GalleryActivity.class);
            intent1.putExtra(PHOTO_PARAMS, params);
            //intent1.putExtra(GalleryActivity.MAX_COUNT, maxPhotos-cameraItemsFiles.size());
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent1, CODE_GALLERY);

        } else if (v.getId() == R.id.done) {
//            if (cameraItemsFiles.size() > maxPhotos) {
//                Toast.makeText(getActivity().getApplicationContext(), "Maximum photos can be : " + maxPhotos, Toast.LENGTH_SHORT).show();
//                return;
//            }
//            imagesHandler.outputImages(cameraItemsFiles, deletedImages);

            getActivity().setResult(ScanConstants.MULTIPLE_CAPTURED, new Intent().putExtra(ScanConstants.CAMERA_IMAGES,cameraItemsFiles));
            getActivity().finish();
        } else if (v.getId() == R.id.ivBack) {
            try {
                if (ApplicationController.selectedFiles != null)
                    ApplicationController.selectedFiles.clear();

            } catch (Exception e) {


            } finally {

                getActivity().finish();
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        gvPhotos.startEditMode(position);
        return true;
    }


    @Override
    public void updateSelected(String imagePath, Boolean selected) {
        if (!selected) {
            for (FileInfo fileInfo1 : cameraItemsFiles) {
                if (fileInfo1.getFilePath().equals(imagePath)) {
                    cameraItemsFiles.remove(fileInfo1);
                    if (fileInfo1.getFromServer())
                        deletedImages.add(fileInfo1);
                    if (ApplicationController.selectedFiles != null)
                        ApplicationController.selectedFiles.remove(imagePath);
//                    ApplicationController.selectedFilesMark.remove(imagePath);
                    break;
                }
            }

            tvCount.setText(cameraItemsFiles.size() + "");
        }
    }
}

