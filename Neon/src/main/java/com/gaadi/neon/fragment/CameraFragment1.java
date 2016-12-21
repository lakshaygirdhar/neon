package com.gaadi.neon.fragment;

/**
 * @author lakshaygirdhar
 * @version 1.0
 * @since 19/10/16
 *
 */

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.gaadi.neon.adapter.FlashModeRecyclerHorizontalAdapter;
import com.gaadi.neon.model.ImageTagModel;
import com.gaadi.neon.util.CameraPreview;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.DrawingView;
import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.NeonConstants;
import com.gaadi.neon.util.NeonUtils;
import com.gaadi.neon.util.PhotoParams;
import com.gaadi.neon.util.PrefsUtils;
import com.scanlibrary.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation,unchecked")
public class CameraFragment1 extends Fragment implements View.OnClickListener, View.OnTouchListener, Camera.PictureCallback {

    private static final String TAG = "CameraFragment1";
    private static final int REQUEST_REVIEW = 100;
    private PhotoParams mPhotoParams;
    private DrawingView drawingView;

    private ImageView currentFlashMode;
    private ArrayList<String> supportedFlashModes;

    private RecyclerView rcvFlash;
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private boolean readyToTakePicture;
    private FrameLayout mCameraLayout;
    private View fragmentView;
    private Activity mActivity;
    private PictureTakenListener mPictureTakenListener;
    private boolean permissionAlreadyRequested;

    private boolean useFrontFacingCamera;
    private boolean enableCapturedReview;
    private float mDist;
    private ImageView mSwitchCamera;
    private PhotoParams.CameraFacing cameraFacing;

    public void clickPicture()
    {
        if (readyToTakePicture) {
            if(mCamera != null) {
                mCamera.takePicture(null, null, this);
            }
            readyToTakePicture = false;
        }
    }

    public interface PictureTakenListener {
        void onPictureTaken(String filePath);
        void onPicturesFinalized(ArrayList<FileInfo> infos);
        void onPicturesFinalized(Map<ImageTagModel,List<FileInfo>> filesMap);
    }

    public static CameraFragment1 getInstance(PhotoParams photoParams) {
        CameraFragment1 fragment = new CameraFragment1();
        Bundle extras = new Bundle();
        extras.putSerializable(NeonConstants.PHOTO_PARAMS, photoParams);
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPictureTakenListener = (PictureTakenListener) activity;
    }

    public void startPreview(){
        mCamera.startPreview();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = LayoutInflater.from(getContext()).inflate(R.layout.camera_fragment_layout, container, false);
        mPhotoParams = (PhotoParams) getArguments().getSerializable(NeonConstants.PHOTO_PARAMS);
        mActivity = getActivity();
        if(mPhotoParams != null){

            initialize();

            customize();

        }
        else
        {
            Toast.makeText(getContext(), getString(R.string.pass_params), Toast.LENGTH_SHORT).show();
        }
        return fragmentView;
    }

    private void initialize()
    {
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);

        currentFlashMode = (ImageView) fragmentView.findViewById(R.id.currentFlashMode);
        rcvFlash = (RecyclerView) fragmentView.findViewById(R.id.flash_listview);
        rcvFlash.setLayoutManager(layoutManager);

        //View to add rectangle on tap to focus
        drawingView = new DrawingView(mActivity);

        mSwitchCamera = (ImageView) fragmentView.findViewById(R.id.switchCamera);

        mSwitchCamera.setOnClickListener(this);
        fragmentView.setOnTouchListener(this);
    }

    private void customize()
    {
        PhotoParams.CameraOrientation orientation = mPhotoParams.getOrientation();
        cameraFacing = mPhotoParams.getCameraFace();
        setOrientation(mActivity, orientation);

        if(!mPhotoParams.isFlashOptionsEnabled())
            fragmentView.findViewById(R.id.llFlash).setVisibility(View.INVISIBLE);

        enableCapturedReview = mPhotoParams.isEnableCapturedReview();

        if(mPhotoParams.isCameraFaceSwitchEnabled())
        {
            if(NeonUtils.isFrontCameraAvailable() != Camera.CameraInfo.CAMERA_FACING_FRONT)
            {
                mSwitchCamera.setVisibility(View.GONE);
                useFrontFacingCamera = false;
            }
        } else {
            mSwitchCamera.setVisibility(View.GONE);
            useFrontFacingCamera = false;
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            mCamera.setPreviewCallback(null);
            mCameraPreview.getHolder().removeCallback(mCameraPreview);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            mCameraPreview = null;
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewGroup.LayoutParams layoutParamsDrawing
                = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                             ViewGroup.LayoutParams.FILL_PARENT);

        getActivity().addContentView(drawingView, layoutParamsDrawing);
    }

    private void setFlashLayoutAndMode() {
        currentFlashMode.setOnClickListener(this);
        String flashMode = PrefsUtils.getStringSharedPreference(getActivity(), Constants.FLASH_MODE, "");
        if (flashMode.equals("")) {
            currentFlashMode.setImageResource(R.drawable.flash_off);
        } else {
            if (supportedFlashModes != null && supportedFlashModes.size() > 0) {
                if (supportedFlashModes.contains(flashMode)) {
                    setFlash(flashMode);
                } else {
                    setFlash(supportedFlashModes.get(0));
                }
            }
        }
    }

    public void setFlash(String mode) {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFlashMode(mode);

        if ("off".equals(mode)) {
            currentFlashMode.setImageResource(R.drawable.flash_off);
        } else if ("on".equals(mode)) {
            currentFlashMode.setImageResource(R.drawable.flash_on);
        } else if ("auto".equals(mode)) {
            currentFlashMode.setImageResource(R.drawable.flash_auto);
        } else if ("red-eye".equals(mode)) {
            currentFlashMode.setImageResource(R.drawable.flash_red_eye);
        } else if ("torch".equals(mode)) {
            currentFlashMode.setImageResource(R.drawable.flash_torch);
        }
        else{
            currentFlashMode.setImageResource(R.drawable.flash_off);
        }
        PrefsUtils.setStringSharedPreference(getActivity(), Constants.FLASH_MODE, mode);
        mCamera.setParameters(parameters);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_REVIEW) {
                String capturedFilePath = "";
                mPictureTakenListener.onPictureTaken(capturedFilePath);
            }
        } else {
             if (requestCode != 101) {
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
                        && !NeonUtils.checkForPermission(mActivity,
                                                           new String[]{Manifest.permission.CAMERA,
                                                                   Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                           Constants.REQUEST_PERMISSION_CAMERA, "Camera and Storage")) {
                    permissionAlreadyRequested = true;
                    return;
                }
                if (cameraFacing == PhotoParams.CameraFacing.FRONT && NeonUtils.isFrontCameraAvailable() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    Log.d(TAG, "onResume: open front");
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                } else {
                    mCamera = Camera.open();
                }

                //To set hardware camera rotation
                setCameraRotation();
                Camera.Parameters parameters = mCamera.getParameters();
                createSupportedFlashList(parameters);

                setFlashLayoutAndMode();

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
            } catch (Exception e) {
                Log.e("Camera Open Exception", "" + e.getMessage());
            }

        } else {
            Log.e(TAG, "camera not null");
        }
    }

    private void createSupportedFlashList(Camera.Parameters parameters) {
        supportedFlashModes = (ArrayList<String>) parameters.getSupportedFlashModes();
        if (supportedFlashModes == null) {
            currentFlashMode.setVisibility(View.GONE);
            rcvFlash.setVisibility(View.GONE);
        } else {
            currentFlashMode.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.switchCamera) {
            int cameraFacing = initCameraId();
            if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                stopCamera();
                useFrontFacingCamera = true;
                startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
            } else {
                stopCamera();
                useFrontFacingCamera = false;
                startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
        } else if (v.getId() == R.id.currentFlashMode) {
            if(rcvFlash.getVisibility()==View.GONE)
                createFlashModesDropDown();
            else
                rcvFlash.setVisibility(View.GONE);
        }
    }

    private void createFlashModesDropDown() {
        FlashModeRecyclerHorizontalAdapter flashModeAdapter = new FlashModeRecyclerHorizontalAdapter(getActivity(), supportedFlashModes);
        rcvFlash.setAdapter(flashModeAdapter);
        rcvFlash.setVisibility(View.VISIBLE);
        flashModeAdapter.setOnItemClickListener(new FlashModeRecyclerHorizontalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                setFlash(supportedFlashModes.get(position));
                rcvFlash.setVisibility(View.GONE);
            }
        });
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

            final Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);

            try {
                mCamera.autoFocus(null);

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

    private class ImagePostProcessing extends AsyncTask<Void, Void, File>
    {

        private Context context;
        private byte[] data;
        private ProgressDialog progressDialog;

        ImagePostProcessing(Context context, byte[] data) {
            this.context = context;
            this.data = data;
        }

        @Override
        protected File doInBackground(Void... params) {
            File pictureFile = Constants.getMediaOutputFile(getActivity(),Constants.TYPE_IMAGE);

            if(pictureFile == null)
                return null;

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                Bitmap bm;

                // COnverting ByteArray to Bitmap - >Rotate and Convert back to Data
                if (data != null) {
                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;
                    bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        // Notice that width and height are reversed
                        Bitmap scaled = Bitmap.createScaledBitmap(bm,screenWidth,screenHeight,true);
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
                        bm = Bitmap.createScaledBitmap(bm, screenWidth, screenHeight, true);
                    }
                } else {
                    return null;
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
                    mPictureTakenListener.onPictureTaken(file.getAbsolutePath());
                    readyToTakePicture = true;
                    return;
                }
                mCamera.startPreview();
            } else {
                Toast.makeText(context, getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
                readyToTakePicture = true;
                mCamera.startPreview();
            }
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




    private Rect calculateTapArea(float x, float y, float coefficient) {
        int FOCUS_AREA_SIZE = 200;
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
        Log.d(TAG, "handleFocus: " + event);
        //        int pointerId = event.getPointerId(0);
        //        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        //        float x = event.getX(pointerIndex);
        //        float y = event.getY(pointerIndex);

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


            } catch (Exception e) {
                Log.e("Camera Open Exception", "" + e.getMessage());
            }
        } else {
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
