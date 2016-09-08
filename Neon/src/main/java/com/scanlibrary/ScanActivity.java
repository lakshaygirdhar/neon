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
 * @version 1.0
 * @since 13-08-2016
 */
@SuppressWarnings("deprecation,unchecked")
public class ScanActivity extends AppCompatActivity implements IScanner
{

    private static final String TAG = "ScanActivity";
    public static final int REQUEST_REVIEW = 100;

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(0, com.scanlibrary.R.anim.slide_out_bottom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_priority_items);

        scanFragmentForCropping((File) getIntent().getSerializableExtra(ScanConstants.IMAGE_FILE_FOR_CROPPING));
    }

    private void scanFragmentForCropping(File file)
    {
        ScanFragment fragment = new ScanFragment();
        Bundle bundle = new Bundle();
        PhotoParams photoParams = new PhotoParams();
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
    public void onBitmapSelect(Uri uri)
    {

    }

    @Override
    public void onScanFinish(Uri uri)
    {
        Intent intent = new Intent(this, ReviewImageActivity.class);
        intent.putExtra(Constants.IMAGE_PATH, uri.toString());
        startActivityForResult(intent, REQUEST_REVIEW);
    }

    public native Bitmap getScannedBitmap(Bitmap bitmap, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4);

    public native Bitmap getGrayBitmap(Bitmap bitmap);

    public native Bitmap getMagicColorBitmap(Bitmap bitmap);

    public native Bitmap getBWBitmap(Bitmap bitmap);

    public native float[] getPoints(Bitmap bitmap);

    static
    {
        System.loadLibrary("opencv_java");
        System.loadLibrary("Scanner");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_REVIEW)
        {

            Intent intent = new Intent();
            intent.putExtra(ScanConstants.CAPTURED_IMAGE_PATH, data.getStringExtra(Constants.IMAGE_PATH));
            setResult(ScanConstants.SINGLE_CAPTURED, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}