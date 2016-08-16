package com.gaadi.neon.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gaadi.neon.activity.GalleryActivity;
import com.gaadi.neon.util.CameraPreview;
import com.gaadi.neon.util.CommonUtils;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.DrawingView;
import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.PhotoParams;
import com.scanlibrary.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lakshay on 31-08-2015.
 */
public class CameraPriorityFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, Camera.PictureCallback {

    private static final String TAG = "CameraPriorityFragment";
	private static final int REQUEST_REVIEW = 100;
    private Activity mActivity;
    private ProgressBar progressBar;
    private PhotoParams mPhotoParams;
    private String imageName;
    private int maxNumberOfImages;
    private DrawingView drawingView;
    private ImageView buttonCapture, buttonGallery, buttonDone;
    private TextView tvImageName;

    private LinearLayout scrollView;
    private ArrayList<FileInfo> imagesList = new ArrayList<>();
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private boolean readyToTakePicture = false;
    private FrameLayout mCameraLayout;
	private String capturedFilePath = "";
    private View fragmentView;
	private PictureTakenListener mPictureTakenListener;
    private boolean permissionAlreadyRequested = false;
    private int FOCUS_AREA_SIZE = 200;
    public static final int GALLERY_PICK = 99;
    private ArrayList<String> outputImages = new ArrayList<>();
    /*
    * @Prince- adding flash buttons.
    * */

    private ImageView mFlashON, mFlashOff, mFlashAuto, mFlashTorch, mSwitchCamera;
    private LinearLayout mFlashlayout;
    private boolean useFrontFacingCamera;
    private ImageView mFlash;
    private boolean enableCapturedReview;
    private float mDist;
    private PhotoParams.CameraFacing cameraFacing;
    private boolean isGalleryEnabled;


    public interface PictureTakenListener {
        public void onPictureTaken(String filePath);
        public void onGalleryPicsCollected(ArrayList<FileInfo> infos);
        public void onPicturesCompleted();
        public void sendPictureForCropping(File file);
    }

    public static CameraPriorityFragment getInstance(PhotoParams photoParams) {
        CameraPriorityFragment fragment = new CameraPriorityFragment();
        Bundle extras = new Bundle();
        extras.putSerializable(Constants.PHOTO_PARAMS, photoParams);
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mPictureTakenListener = (PictureTakenListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentView = LayoutInflater.from(mActivity).inflate(R.layout.camera_priority_fragment, null);
        progressBar = (ProgressBar) fragmentView.findViewById(R.id.progressBar);

        mPhotoParams = (PhotoParams) getArguments().getSerializable(Constants.PHOTO_PARAMS);
        imageName = mPhotoParams.getImageName();
        maxNumberOfImages = mPhotoParams.getNoOfPhotos();
        enableCapturedReview = mPhotoParams.getEnableCapturedReview();
        PhotoParams.CameraOrientation orientation = mPhotoParams.getOrientation();
        cameraFacing = mPhotoParams.getCameraFace();
        isGalleryEnabled = mPhotoParams.isGalleryFromCameraEnabled();

        //View to add rectangle on tap to focus
        drawingView = new DrawingView(mActivity);

        setOrientation(mActivity, orientation);

        buttonCapture = (ImageView) fragmentView.findViewById(R.id.buttonCapture);
        buttonGallery = (ImageView) fragmentView.findViewById(R.id.buttonGallery);
        buttonDone = (ImageView) fragmentView.findViewById(R.id.buttonDone);
        tvImageName = (TextView) fragmentView.findViewById(R.id.imageName);

        mFlash = (ImageButton)fragmentView.findViewById(R.id.flash);
        mFlashlayout = (LinearLayout)fragmentView.findViewById(R.id.flashLayout);
        mFlashAuto = (ImageButton) fragmentView.findViewById(R.id.auto);
        mFlashON = (ImageButton) fragmentView.findViewById(R.id.on);
        mFlashOff = (ImageButton) fragmentView.findViewById(R.id.off);
        mFlashTorch = (ImageButton) fragmentView.findViewById(R.id.torch);

        mSwitchCamera = (ImageButton) fragmentView.findViewById(R.id.switchCamera);
        if(CommonUtils.isFrontCameraAvailable() != Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mSwitchCamera.setVisibility(View.GONE);
            useFrontFacingCamera = false;
        }
        if (!isGalleryEnabled) {
            buttonGallery.setVisibility(View.GONE);
        }

        mFlashTorch.setOnClickListener(this);
        mFlashON.setOnClickListener(this);
        mFlashOff.setOnClickListener(this);
        mFlashAuto.setOnClickListener(this);
        mSwitchCamera.setOnClickListener(this);

        scrollView = (LinearLayout) fragmentView.findViewById(R.id.imageHolderView);

        //for handling screen orientation
        if (savedInstanceState != null) {
            Log.e(Constants.TAG, "savedInstanceState not null");
            imagesList = (ArrayList<FileInfo>) savedInstanceState.getSerializable(Constants.IMAGES_SELECTED);
            addInScrollView(imagesList);
        }
        fragmentView.setOnTouchListener(this);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewGroup.LayoutParams layoutParamsDrawing
                = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);

        mActivity.addContentView(drawingView, layoutParamsDrawing);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == GALLERY_PICK) {
//                if (maxNumberOfImages == 0) {
                imagesList = (ArrayList<FileInfo>) data.getSerializableExtra(GalleryActivity.GALLERY_SELECTED_PHOTOS);
                if (imagesList != null && imagesList.size()>0) {
                    buttonCapture.setTag("done");
                    onClick(buttonCapture);
                    if(enableCapturedReview) {
                        mPictureTakenListener.onGalleryPicsCollected(imagesList);
                        imagesList.clear();
                    }
                }
            } else if (requestCode == REQUEST_REVIEW) {
                mPictureTakenListener.onPictureTaken(capturedFilePath);
            }
        } else {
            if (requestCode == REQUEST_REVIEW) {
                readyToTakePicture = true;
                buttonCapture.setEnabled(true);
            } else if (requestCode == GALLERY_PICK) {
                return;
            } else if (requestCode != 101) {
                mActivity.setResult(resultCode);
                mActivity.finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCamera == null) {
            try {
                if (!permissionAlreadyRequested && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !CommonUtils.checkForPermission(mActivity,
                                                           new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                           Constants.REQUEST_PERMISSION_CAMERA, "Camera and Storage")) {
                    permissionAlreadyRequested = true;
                    return;
                }
                if (cameraFacing == PhotoParams.CameraFacing.FRONT) {
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    mSwitchCamera.setVisibility(View.GONE);
                    mFlashlayout.setVisibility(View.GONE);
                    mFlash.setVisibility(View.GONE);
                } else {
                    mCamera = Camera.open();
                }

                //To set hardware camera rotation
                setCameraRotation();

                mCameraPreview = new CameraPreview(mActivity, mCamera);
                mCameraPreview.setReadyListener(new CameraPreview.ReadyToTakePicture() {
                    @Override
                    public void readyToTakePicture(boolean ready) {
                        readyToTakePicture = ready;
                    }
                });

                mCameraPreview.setOnTouchListener(this);

                mCameraLayout = (FrameLayout) fragmentView.findViewById(R.id.camera_preview);
                mCameraLayout.addView(mCameraPreview);

                //set the screen layout to fullscreen
                mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);

                buttonCapture.setOnClickListener(this);
                enableDoneButton(false);
                buttonGallery.setOnClickListener(this);

            } catch (Exception e) {
                Log.e("Camera Open Exception", "" + e.getMessage());
            }

            //To make sure that name appears only after animation ends
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (maxNumberOfImages == 0) {
                        buttonDone.setVisibility(View.VISIBLE);
                        buttonDone.setOnClickListener(CameraPriorityFragment.this);
                    }
                    if (mPhotoParams.getImageName() != null && !"".equals(mPhotoParams.getImageName())) {
                        tvImageName.setVisibility(View.VISIBLE);
                        tvImageName.setText(mPhotoParams.getImageName() + "");
                        tvImageName.setOnClickListener(CameraPriorityFragment.this);
                    }
                }
            }, 1000);

        } else {
            enableDoneButton(false);
            Log.e(TAG, "camera not null");
        }
    }

    @Override
    public void onClick(View v) {

        //Toast.makeText(this, (v.getId()==R.id.bDone ? "done": "capture"), Toast.LENGTH_SHORT).show();
        if (v.getId() == R.id.buttonCapture) {
            if (v.getTag().equals("capture")) {
                if (readyToTakePicture) {
                    if(mCamera != null) {
                        mCamera.takePicture(null, null, this);
                    }
                    readyToTakePicture = false;
                    //llActionsCamera.setEnabled(false);
//                    buttonCapture.setEnabled(false);
                    if (maxNumberOfImages == 1)
                        buttonGallery.setEnabled(false);
                    if (maxNumberOfImages > 1 || maxNumberOfImages == 0) {
//                        buttonDone.setVisibility(View.VISIBLE);
                        buttonDone.setOnClickListener(this);
                    }
                }
            } else if (v.getTag().equals("done")) {
                //imagesList = imagesAdapter.getImageInfoArrayList();
//                Log.e("CameraPriorityActivity", "onclick " + croppedImageCount);
                if (imagesList.size() > 0) {
//                    croppedImages.add(cropImage(imagesList.get(croppedImageCount)));
//                    for (FileInfo info : imagesList) {
//                        outputImages.add(info.getFilePath());
//                    }
                    mPictureTakenListener.onGalleryPicsCollected(imagesList);

//                    mActivity.setResult(mActivity.RESULT_OK, new Intent().putStringArrayListExtra(Constants.RESULT_IMAGES, outputImages));
//                    mActivity.finish();
                } else {
                    Toast.makeText(mActivity, "Please click atleast one photo", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (v.getId() == R.id.buttonGallery) {
            //finish();
            Intent intent = new Intent(mActivity, GalleryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(GalleryActivity.MAX_COUNT, maxNumberOfImages);
            intent.putExtra(Constants.PHOTO_PARAMS, mPhotoParams);
            startActivityForResult(intent, GALLERY_PICK);

        } else if (v.getId() == R.id.imageName) {
//            if (imagesList.size() == maxNumberOfImages)
//                onClick(buttonCapture);
        } else if (v.getId() == R.id.buttonDone) {
//            if (enableCapturedReview ) {
//                mPictureTakenListener.onPicturesCompleted();
//                return;
//            }
            if (imagesList.size() == 0) {
                Toast.makeText(mActivity, "No Images Clicked", Toast.LENGTH_SHORT).show();
            } else {
                buttonCapture.setTag("done");
                onClick(buttonCapture);
            }
        } else if (v.getId() == R.id.auto) {
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            mCamera.setParameters(params);
        } else if (v.getId() == R.id.on) {
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            mCamera.setParameters(params);

        } else if (v.getId() == R.id.off) {
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(params);

        } else if (v.getId() == R.id.torch) {
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(params);
        } else if (v.getId() == R.id.flash) {
            mFlashlayout.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.switchCamera) {
            int cameraFacing = initCameraId();
            if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                stopCamera();
                useFrontFacingCamera = true;
                startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
                mFlashlayout.setVisibility(View.GONE);
                mFlash.setVisibility(View.GONE);
            } else {
                stopCamera();
                useFrontFacingCamera = false;
                startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                mFlashlayout.setVisibility(View.VISIBLE);
                mFlash.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mCamera != null) {

            Camera.Parameters params = mCamera.getParameters();
            int action = event.getAction();


            if (event.getPointerCount() > 1) {
// handle multi-touch events
                if (action == MotionEvent.ACTION_POINTER_DOWN) {
                    mDist = getFingerSpacing(event);
                } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                    mCamera.cancelAutoFocus();
                    handleZoom(event, params);
                }
            } else {
// handle single touch events
                if (action == MotionEvent.ACTION_UP) {
                    handleFocus(event, params);
                }
            }
            if (event.getPointerCount() > 1){
                return true;
            }

//            Camera camera = mCamera.getCamera();
//            mCamera.cancelAutoFocus();
            final Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);

//            Camera.Parameters parameters = mCamera.getParameters();
//            if (parameters.getFocusMode() != Camera.Parameters.FOCUS_MODE_AUTO) {
//                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//            }
//            if (parameters.getMaxNumFocusAreas() > 0) {
//                List<Camera.Area> mylist = new ArrayList<Camera.Area>();
//                mylist.add(new Camera.Area(focusRect, 1000));
//                parameters.setFocusAreas(mylist);
//            }

            try {
                mCamera.autoFocus(null);
//                mCamera.cancelAutoFocus();
//                mCamera.setParameters(parameters);
//                mCamera.startPreview();
//                mCamera.autoFocus(new Camera.AutoFocusCallback() {
//                    @Override
//                    public void onAutoFocus(boolean success, Camera camera) {
////                        if (camera.getParameters().getFocusMode() != Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
////                            Camera.Parameters parameters = camera.getParameters();
////                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
////                            if (parameters.getMaxNumFocusAreas() > 0) {
////                                parameters.setFocusAreas(null);
////                            }
////                            camera.setParameters(parameters);
////                            camera.startPreview();   //causing crash here
////                        }
//                    }
//                });

                drawingView.setHaveTouch(true, focusRect);
                drawingView.invalidate();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawingView.setHaveTouch(false, focusRect);
                        drawingView.invalidate();
                    }
                }, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        new ImagePostProcessing(mActivity, data).execute();
    }

    private class ImagePostProcessing extends AsyncTask<Void, Void, File> {

        private Context context;
        private byte[] data;
        private ProgressDialog progressDialog;

        public ImagePostProcessing(Context context, byte[] data) {
            this.context = context;
            this.data = data;
        }

        @Override
        protected File doInBackground(Void... params) {
            File pictureFile = Constants.getMediaOutputFile(Constants.TYPE_IMAGE);
            Log.e(TAG, pictureFile.getAbsolutePath());
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return null;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
//                fos.write(data);
//                fos.close();
                Bitmap bm=null;

                // COnverting ByteArray to Bitmap - >Rotate and Convert back to Data
                if (data != null) {
                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;
                    bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        // Notice that width and height are reversed
                        Bitmap scaled = Bitmap.createScaledBitmap(bm,screenHeight,screenWidth,true);
                        int w = scaled.getWidth();
                        int h = scaled.getHeight();
                        // Setting post rotate to 90
                        Matrix mtx = new Matrix();
                        int cameraId;
                        if (cameraFacing == PhotoParams.CameraFacing.FRONT) {
                            cameraId = getBackFacingCameraId();
                        } else {
                            cameraId = initCameraId();
                        }
                        int CameraEyeValue = setPhotoOrientation(getActivity(),cameraId); // CameraID = 1 : front 0:back
                        if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) { // As Front camera is Mirrored so Fliping the Orientation
                            if (CameraEyeValue == 270) {
                                mtx.postRotate(90);
                            } else if (CameraEyeValue == 90) {
                                mtx.postRotate(270);
                            }
                        }else{
                            mtx.postRotate(CameraEyeValue); // CameraEyeValue is default to Display Rotation
                        }

                        bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
                    }else{// LANDSCAPE MODE
                        //No need to reverse width and height
                        Bitmap scaled = Bitmap.createScaledBitmap(bm, screenWidth, screenHeight, true);
                        bm=scaled;
                    }
                }
                // COnverting the Die photo to Bitmap



                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                fos.write(byteArray);
                //fos.write(data);
                fos.close();
                Uri pictureFileUri = Uri.parse("file://" + pictureFile.getAbsolutePath());
                mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        pictureFileUri));

//                int rotation = getExifRotation(pictureFile);
//                Log.e(Constants.TAG, "Rotation : " + rotation);
//                if(rotation > 0){
//                    Matrix matrix = new Matrix();
//                    matrix.preRotate(rotation);
////                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getPath());
//                    Log.e(TAG, "bitmap size: " + bitmap.getByteCount());
//                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                }

            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            mCamera.startPreview();
            return pictureFile;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, null, "Saving Picture", true);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (progressDialog != null)
                progressDialog.dismiss();
            if (file != null) {
                if(!enableCapturedReview || mPhotoParams.getMode() == PhotoParams.MODE.NEUTRAL) {
                    updateCapturedPhotos(file);
                    return;
                }
                mPictureTakenListener.sendPictureForCropping(file);
//                capturedFilePath = file.getPath();
//                Intent intent = new Intent(mActivity, ReviewImageActivity.class);
//                intent.putExtra(Constants.IMAGE_NAME, mPhotoParams.getImageName());
//                intent.putExtra(Constants.IMAGE_PATH, file.getPath());
//                mActivity.startActivityForResult(intent, REQUEST_REVIEW);
                mCamera.startPreview();
            } else {
                Toast.makeText(context, "Camera Error. Kindly try again", Toast.LENGTH_SHORT).show();
                readyToTakePicture = true;
                buttonCapture.setEnabled(true);
                mCamera.startPreview();
            }
        }

    }

    //updates the listview with the photos clicked by the camera
    private void updateCapturedPhotos(File pictureFile) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilePath(pictureFile.getAbsolutePath());
        fileInfo.setFileName(pictureFile.getAbsolutePath().substring(pictureFile.getAbsolutePath().lastIndexOf("/") + 1));
        fileInfo.setSource(FileInfo.SOURCE.PHONE_CAMERA);
        imagesList.add(fileInfo);
        if (maxNumberOfImages == 1) {
            buttonCapture.setTag("done");
            onClick(buttonCapture);
        } else {
            Log.e(Constants.TAG, "updateCapturedPhotos");
            if (imagesList.size() >= 1)
                scrollView.setVisibility(View.VISIBLE);
            else
                scrollView.setVisibility(View.GONE);
            addInScrollView(fileInfo);

            if (maxNumberOfImages > 0) {
                updateView(imagesList.size() < maxNumberOfImages);
            }
            mCamera.startPreview();
            readyToTakePicture = true;
            buttonCapture.setEnabled(true);
        }
    }


    private void setCameraRotation() {
        //STEP #1: Get rotation degrees
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; //Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; //Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;//Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;//Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;

        //STEP #2: Set the 'rotation' parameter
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(rotate);
        mCamera.setParameters(params);
    }

    //It is called when configuration(orientation) of screen changes
    private void addInScrollView(ArrayList<FileInfo> infos) {
        if (infos != null && infos.size() > 0) {
            for (FileInfo info : infos) {
                scrollView.addView(createImageView(info));
            }
            scrollView.setVisibility(View.VISIBLE);
        }
        Log.e(Constants.TAG, "Add multiple items in scroll ");
    }

    private void addInScrollView(FileInfo info) {
        Log.e(Constants.TAG, " add in scroll View ");
        scrollView.addView(createImageView(info));
        scrollView.setVisibility(View.VISIBLE);
    }

    private View createImageView(final FileInfo info) {
        final File file = new File(info.getFilePath());
        if (!file.exists())
            return null;
        final View outerView = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.camera_priority_overlay, null);
        outerView.findViewById(R.id.ivRemoveImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.removeView(outerView);
                imagesList.remove(info);
                if (maxNumberOfImages > 0)
                    updateView(imagesList.size() < maxNumberOfImages);
                if (imagesList.size() < 1) {
                    buttonDone.setVisibility(View.GONE);
                    scrollView.setVisibility(View.GONE);
                }
            }
        });

        Glide.with(this).load("file://" + info.getFilePath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .centerCrop()
                .placeholder(R.drawable.image_load_default_small)
                .into((ImageView) outerView.findViewById(R.id.ivCaptured));/**/
        return outerView;
    }

    private void setOrientation(Activity activity, PhotoParams.CameraOrientation orientation) {
        if (orientation != null) {
            if (orientation.equals(PhotoParams.CameraOrientation.LANDSCAPE)) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (orientation.equals(PhotoParams.CameraOrientation.PORTRAIT)) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            Log.e(Constants.TAG, "No orientation set");
        }
    }

    private void updateView(boolean status) {
//        enableDoneButton(!status);
        if (!status) {
            buttonCapture.setVisibility(View.GONE);
        } else {
            buttonCapture.setVisibility(View.VISIBLE);
        }
        buttonDone.setVisibility(View.VISIBLE);
        tvImageName.setText(status ? imageName : "Press Done");
    }

    private void enableDoneButton(boolean enable) {
        buttonCapture.setImageResource(enable ? R.drawable.camera_ok : R.drawable.camera_click);
        buttonCapture.setTag(enable ? "done" : "capture");
    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
        int areaSize = Float.valueOf(FOCUS_AREA_SIZE * coefficient).intValue();

        int left = clamp((int) x - areaSize / 2, 0, mCameraPreview.getWidth() - areaSize);
        int top = clamp((int) y - areaSize / 2, 0, mCameraPreview.getHeight() - areaSize);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
//        matrix.mapRect(rectF);

        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
// Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
// currently set to auto-focus on single touch
                }
            });
        }
    }

    /** Determine the space between the first two fingers */
    private float getFingerSpacing(MotionEvent event) {
// ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    public void stopCamera () {
        try {
            if (null == mCamera) {
                return;
            }
            mCamera.setPreviewCallback(null);
            mCameraPreview.getHolder().removeCallback(mCameraPreview);
            mCamera.stopPreview();
            mCameraLayout.removeAllViews();
            mCamera.release();
            mCamera = null;
            mCameraPreview = null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopCamera();
    }

    private void startCamera(int cameraFacing) {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(cameraFacing);


                //To set hardware camera rotation
                setCameraRotation();

                mCameraPreview = new CameraPreview(mActivity, mCamera);
                mCameraPreview.setReadyListener(new CameraPreview.ReadyToTakePicture() {
                    @Override
                    public void readyToTakePicture(boolean ready) {
                        readyToTakePicture = ready;
                    }
                });

                mCameraPreview.setOnTouchListener(this);

                mCameraLayout = (FrameLayout) fragmentView.findViewById(R.id.camera_preview);
                mCameraLayout.addView(mCameraPreview);

                //set the screen layout to fullscreen
                mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);

                buttonCapture.setOnClickListener(this);
                enableDoneButton(false);
                buttonGallery.setOnClickListener(this);

            } catch (Exception e) {
                Log.e("Camera Open Exception", "" + e.getMessage());
            }
        } else {
            enableDoneButton(false);
            Log.e(TAG, "camera not null");
        }
    }

    private int initCameraId() {
        int count = Camera.getNumberOfCameras();
        int result = -1;

        if (count > 0) {
            result = 0; // if we have a camera, default to this one

            Camera.CameraInfo info = new Camera.CameraInfo();

            for (int i = 0; i < count; i++) {
                Camera.getCameraInfo(i, info);

                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK
                        && !useFrontFacingCamera) {
                    result = i;
                    break;
                }
                else if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT
                        && useFrontFacingCamera) {
                    result = i;
                    break;
                }
            }
        }

        return result;
    }

    public int setPhotoOrientation(Activity activity, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        // do something for phones running an SDK before lollipop
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

    private int getBackFacingCameraId() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

}
