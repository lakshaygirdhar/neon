package com.scanlibrary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gaadi.neon.activity.GalleryActivity;
import com.gaadi.neon.activity.ReviewImageActivity;
import com.gaadi.neon.fragment.CameraFragment;
import com.gaadi.neon.fragment.NeutralFragment;
import com.gaadi.neon.util.CameraPreview;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.PhotoParams;

import java.io.File;
import java.util.ArrayList;

/**
 * @author lakshaygirdhar
 * @since 13-08-2016
 * @version 1.0
 *
 */
@SuppressWarnings("deprecation,unchecked")
public class CameraActivity extends AppCompatActivity implements IScanner, CameraFragment.PictureTakenListener {

    private static final String TAG = "CameraActivity";
    public static final int GALLERY_PICK = 99;
    private static final int REQUEST_REVIEW = 100;
    private Camera camera;
    private PhotoParams photoParams;
    public boolean readyToTakePicture;
    private CameraPreview cameraPreview;
    private ArrayList<FileInfo> imagesList = new ArrayList<>();
    private ArrayList<String> outputImages = new ArrayList<>();

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
            CameraFragment fragment = CameraFragment.getInstance(photoParams);
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
            camera.release();
            camera = null;
            cameraPreview = null;
        } catch (Exception e) {
             Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.IMAGES_SELECTED, imagesList);
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
            setResult(ScanConstants.MULTIPLE_CAPTURED, new Intent().putExtra(ScanConstants.CAMERA_IMAGES,infos));
            finish();
        } else {
            Toast.makeText(this, getString(R.string.click_photo), Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public void onScanFinish(Uri uri) {
        Intent intent = new Intent(this, ReviewImageActivity.class);
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
                imagesList = (ArrayList<FileInfo>) data.getSerializableExtra(GalleryActivity.GALLERY_SELECTED_PHOTOS);
            } else {
                if (requestCode == REQUEST_REVIEW) {
                    readyToTakePicture = true;
                    Intent intent = new Intent();
                    intent.putExtra(ScanConstants.CAPTURED_IMAGE_PATH,data.getStringExtra(Constants.IMAGE_PATH));
                    setResult(ScanConstants.SINGLE_CAPTURED,intent);
                    finish();
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