package com.gaadi.neon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gaadi.neon.fragment.CameraFragment1;
import com.gaadi.neon.fragment.NeutralFragment;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.NeonConstants;
import com.gaadi.neon.util.PhotoParams;
import com.scanlibrary.R;

import java.io.File;
import java.util.ArrayList;
/**
 * @author lakshaygirdhar
 * @version 1.0
 * @since 8/9/16
 *
 */
@SuppressWarnings("deprecation,unchecked")
public class CameraActivity1 extends AppCompatActivity implements CameraFragment1.PictureTakenListener, View.OnClickListener
{
    public static final int GALLERY_PICK = 99;
    private static final String TAG = "CameraActivity1";
    private ArrayList<FileInfo> imagesList = new ArrayList<>();
    private ImageView buttonCapture;
    private ImageView buttonDone;
    private CameraFragment1 mFragment;
    private PhotoParams photoParams;
    private int maxNumberOfImages;
    private LinearLayout scrollView;
    private TextView tvImageName;
    private String imageName;
    private ImageView buttonGallery;

    @Override
    protected void onCreate(
            @Nullable
                    Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity_layout);

        buttonCapture = (ImageView) findViewById(R.id.buttonCapture);
        buttonGallery = (ImageView) findViewById(R.id.buttonGallery);
        buttonDone = (ImageView) findViewById(R.id.buttonDone);
        scrollView = (LinearLayout) findViewById(R.id.imageHolderView);
        tvImageName = (TextView) findViewById(R.id.tvImageName);

        photoParams = (PhotoParams) getIntent().getSerializableExtra(NeutralFragment.PHOTO_PARAMS);
        customize();

        buttonCapture.setOnClickListener(this);
        enableDoneButton(false);
        buttonGallery.setOnClickListener(this);

        //To make sure that name appears only after animation ends
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (maxNumberOfImages == 0) {
                    buttonDone.setVisibility(View.VISIBLE);
                    buttonDone.setOnClickListener(CameraActivity1.this);
                }
                if (photoParams.getImageName() != null && !"".equals(photoParams.getImageName())) {
                    tvImageName.setVisibility(View.VISIBLE);
                    tvImageName.setText(String.valueOf(photoParams.getImageName()));
                    tvImageName.setOnClickListener(CameraActivity1.this);
                }
            }
        }, 1000);

        mFragment = CameraFragment1.getInstance(photoParams);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content_frame, mFragment).commit();

        //for handling screen orientation
        if (savedInstanceState != null) {
            Log.e(Constants.TAG, "savedInstanceState not null");
            imagesList = (ArrayList<FileInfo>) savedInstanceState.getSerializable(Constants.IMAGES_SELECTED);
            addInScrollView(imagesList);
        }
    }

    private void customize()
    {
        if(photoParams.getTagEnabled()){

        }
        maxNumberOfImages = photoParams.getNoOfPhotos();
        imageName = photoParams.getImageName();

        boolean isGalleryEnabled = photoParams.isGalleryFromCameraEnabled();

        if (!isGalleryEnabled) {
            buttonGallery.setVisibility(View.GONE);
        }
    }

    private void enableDoneButton(boolean enable) {
        buttonCapture.setImageResource(enable ? R.drawable.camera_switch : R.drawable.ic_camera);
        buttonCapture.setTag(enable ? "done" : "capture");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.IMAGES_SELECTED, imagesList);
    }

    //updates the listview with the photos clicked by the camera
    private void updateCapturedPhotos(FileInfo fileInfo) {
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
            mFragment.startPreview();
            buttonCapture.setEnabled(true);
        }
    }

    private void updateView(boolean status) {
        if (!status) {
            buttonCapture.setVisibility(View.GONE);
        } else {
            buttonCapture.setVisibility(View.VISIBLE);
        }
        buttonDone.setVisibility(View.VISIBLE);
        tvImageName.setText(status ? imageName : "Press Done");
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
        scrollView.addView(createImageView(info));
        scrollView.setVisibility(View.VISIBLE);
    }

    private View createImageView(final FileInfo info) {
        final File file = new File(info.getFilePath());
        if (!file.exists())
            return null;
        final View outerView = View.inflate(this,R.layout.camera_priority_overlay,null);
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

    @Override
    public void onPictureTaken(String filePath)
    {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilePath(filePath);
        fileInfo.setFileName(filePath.substring(filePath.lastIndexOf("/") + 1));
        fileInfo.setSource(FileInfo.SOURCE.PHONE_CAMERA);
        imagesList.add(fileInfo);

        if(photoParams.isCameraHorizontalPreviewEnabled())
            updateCapturedPhotos(fileInfo);
    }

    @Override
    public void onPicturesFinalized(ArrayList<FileInfo> infos)
    {
        getSupportFragmentManager().popBackStackImmediate();
        if(infos.size() > 0)
        {
            setResult(RESULT_OK, new Intent().putExtra(NeonConstants.COLLECTED_IMAGES, infos));
            finish();
        }
        else
        {
            Toast.makeText(this, getString(R.string.click_photo), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void sendPictureForCropping(File file)
    {
        //        Intent intent = new Intent(this, ScanActivity.class);
        //        intent.putExtra(ScanConstants.IMAGE_FILE_FOR_CROPPING,file);
        //        startActivityForResult(intent,ScanActivity.REQUEST_REVIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode == GALLERY_PICK)
            {
                imagesList = (ArrayList<FileInfo>) data.getSerializableExtra(GalleryActivity.GALLERY_SELECTED_PHOTOS);
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.buttonCapture) {
            if (v.getTag().equals("capture")) {
                mFragment.clickPicture();
            } else if (v.getTag().equals("done")) {
                if (imagesList.size() > 0) {
                   onPicturesFinalized(imagesList);
                } else {
                    Toast.makeText(this, getString(R.string.please_select_atleast_one), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (v.getId() == R.id.buttonGallery) {
            Intent intent = new Intent(this, GalleryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(GalleryActivity.MAX_COUNT, maxNumberOfImages);
            intent.putExtra(Constants.PHOTO_PARAMS, photoParams);
            startActivityForResult(intent, GALLERY_PICK);

        } else if (v.getId() == R.id.buttonDone) {
            if (imagesList.size() == 0) {
                Toast.makeText(this, getString(R.string.no_images), Toast.LENGTH_SHORT).show();
            } else {
                buttonCapture.setTag("done");
                onClick(buttonCapture);
            }
        }
    }
}
