package com.scanlibrary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.gaadi.neon.activity.GalleryActivity;
import com.gaadi.neon.activity.ReviewImageActivity;
import com.gaadi.neon.fragment.NeutralFragment;
import com.gaadi.neon.fragment.CameraPriorityFragment;
import com.gaadi.neon.util.CameraPreview;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.DrawingView;
import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.PhotoParams;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lakshaygirdhar
 * @since 13-08-2016
 * @version 1.0
 *
 */
public class CameraActivity extends AppCompatActivity implements IScanner, View.OnTouchListener, CameraPriorityFragment.PictureTakenListener {

    private static final String TAG = "ScanActivity";
    public static final int GALLERY_PICK = 99;
    private static final int REQUEST_REVIEW = 100;
    private Camera camera;
    private PhotoParams photoParams;
    public boolean readyToTakePicture;
    private CameraPreview cameraPreview;

    //Things to be restored on config change
    private ArrayList<FileInfo> imagesList = new ArrayList<>();

    private int FOCUS_AREA_SIZE = 200;
    private DrawingView drawingView;
    private FrameLayout camera_lLayout;
    private ArrayList<String> outputImages = new ArrayList<>();
    private CameraPriorityFragment fragment;


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, com.scanlibrary.R.anim.slide_out_bottom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_priority_items);

        photoParams = (PhotoParams) getIntent().getSerializableExtra(NeutralFragment.PHOTO_PARAMS);
        if (null != photoParams) {
            fragment = CameraPriorityFragment.getInstance(photoParams);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else {
            scanFragmentForCropping((File)getIntent().getSerializableExtra(ScanConstants.IMAGE_FILE_FOR_CROPPING));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            camera.setPreviewCallback(null);
            cameraPreview.getHolder().removeCallback(cameraPreview);
            camera.stopPreview();
            camera_lLayout.removeAllViews();
            camera.release();
            camera = null;
            cameraPreview = null;
        } catch (Exception e) {
            // Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(Constants.IMAGES_SELECTED, imagesList);
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (camera != null) {
//            Camera camera = mCamera.getCamera();
            camera.cancelAutoFocus();
            final Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);

            Camera.Parameters parameters = camera.getParameters();
            if (! parameters.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> mylist = new ArrayList<Camera.Area>();
                mylist.add(new Camera.Area(focusRect, 1000));
                parameters.setFocusAreas(mylist);
            }

            try {
                camera.cancelAutoFocus();
                camera.setParameters(parameters);
                camera.startPreview();
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
//                        if (camera.getParameters().getFocusMode() != Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
//                            Camera.Parameters parameters = camera.getParameters();
//                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//                            if (parameters.getMaxNumFocusAreas() > 0) {
//                                parameters.setFocusAreas(null);
//                            }
//                            camera.setParameters(parameters);
//                            camera.startPreview();   //causing crash here
//                        }
                    }
                });

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

    private Rect calculateTapArea(float x, float y, float coefficient) {
        int areaSize = Float.valueOf(FOCUS_AREA_SIZE * coefficient).intValue();

        int left = clamp((int) x - areaSize / 2, 0, cameraPreview.getWidth() - areaSize);
        int top = clamp((int) y - areaSize / 2, 0, cameraPreview.getHeight() - areaSize);

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

    private void focusOnTouch(MotionEvent event) {
        if (camera != null) {

            Camera.Parameters parameters = camera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0) {
                Log.i(TAG, "fancy !");
                Rect rect = calculateFocusArea(event.getX(), event.getY());

                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);

                camera.setParameters(parameters);
                camera.autoFocus(mAutoFocusTakePictureCallback);
            } else {
                camera.autoFocus(mAutoFocusTakePictureCallback);
            }
        }
    }

    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // do something...
                Log.i("tap_to_focus", "success!");
            } else {
                // do something...
                Log.i("tap_to_focus", "fail!");
            }
        }
    };

    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / cameraPreview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / cameraPreview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
            if (touchCoordinateInCameraReper > 0) {
                result = 1000 - focusAreaSize / 2;
            } else {
                result = -1000 + focusAreaSize / 2;
            }
        } else {
            result = touchCoordinateInCameraReper - focusAreaSize / 2;
        }
        return result;
    }

    private int getExifRotation(File pictureFile) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(pictureFile.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.e(Constants.TAG, "Exif orientation : " + orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    return 90;
                default:
                    return 0;
                // etc.
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "Exception Message : " + e.getMessage());
        }
        return 0;
    }

    @Override
    public void onPictureTaken(String filePath) {
        outputImages.clear();
        outputImages.add(filePath);
        setResult(RESULT_OK, new Intent().putStringArrayListExtra(Constants.RESULT_IMAGES, outputImages));
        finish();
    }

    @Override
    public void onGalleryPicsCollected(ArrayList<FileInfo> infos) {
        getSupportFragmentManager().popBackStackImmediate();
        if(infos.size()>0) {
            ArrayList<String> images = new ArrayList<>();
            images.clear();
            for (FileInfo info : infos) {
                images.add(info.getFilePath());
            }
            setResult(ScanConstants.MULTIPLE_CAPTURED, new Intent().putExtra(ScanConstants.CAMERA_IMAGES,infos));
            finish();
        } else {
            Toast.makeText(this, "Please click atleast one photo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void sendPictureForCropping(File file) {
        scanFragmentForCropping(file);
    }

    private void scanFragmentForCropping(File file) {
        ScanFragment fragment = new ScanFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ScanConstants.SELECTED_BITMAP, Uri.fromFile(file));
        bundle.putSerializable(Constants.PHOTO_PARAMS, photoParams);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(ScanFragment.class.toString());
        fragmentTransaction.commit();
    }

    @Override
    public void onBitmapSelect(Uri uri) {
//        ScanFragment fragment = new ScanFragment();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(ScanConstants.SELECTED_BITMAP, uri);
//        fragment.setArguments(bundle);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.content, fragment);
//        fragmentTransaction.addToBackStack(ScanFragment.class.toString());
//        fragmentTransaction.commit();
    }

    @Override
    public void onScanFinish(Uri uri) {
//        ResultFragment fragment = new ResultFragment();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(ScanConstants.SCANNED_RESULT, uri);
//        fragment.setArguments(bundle);
//        android.app.FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.content_frame, fragment);
//        fragmentTransaction.addToBackStack(ResultFragment.class.toString());
//        fragmentTransaction.commit();
        Intent intent = new Intent(this, ReviewImageActivity.class);
//        intent.putExtra(Constants.IMAGE_NAME, mPhotoParams.getImageName());
        intent.putExtra(Constants.IMAGE_PATH, uri.toString());
        startActivityForResult(intent, REQUEST_REVIEW);
    }

    public native Bitmap getScannedBitmap(Bitmap bitmap, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4);

    public native Bitmap getGrayBitmap(Bitmap bitmap);

    public native Bitmap getMagicColorBitmap(Bitmap bitmap);

    public native Bitmap getBWBitmap(Bitmap bitmap);

    public native float[] getPoints(Bitmap bitmap);

    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("Scanner");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == GALLERY_PICK) {
//                if (maxNumberOfImages == 0) {
                imagesList = (ArrayList<FileInfo>) data.getSerializableExtra(GalleryActivity.GALLERY_SELECTED_PHOTOS);
                if (imagesList != null && imagesList.size() > 0) {
                    Log.i(TAG,"iamgeList from gallery "+imagesList);
                } else if (requestCode == REQUEST_REVIEW) {
//                    mPictureTakenListener.onPictureTaken(capturedFilePath);
                }
            } else {
                if (requestCode == REQUEST_REVIEW) {
                    readyToTakePicture = true;

                    Intent intent = new Intent();
                    intent.putExtra(ScanConstants.CAPTURED_IMAGE_PATH,data.getStringExtra(Constants.IMAGE_PATH));
                    setResult(ScanConstants.SINGLE_CAPTURED,intent);
                    finish();
                } else if (requestCode == GALLERY_PICK) {
                    return;
                } else if (requestCode != 101) {
//                mActivity.setResult(resultCode);
//                mActivity.finish();
                }
            }
        }
        else if (resultCode == RESULT_CANCELED) {
            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack(ScanFragment.class.toString(),FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if (null == photoParams) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}