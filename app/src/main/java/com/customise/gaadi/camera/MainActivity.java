package com.customise.gaadi.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.gaadi.neon.PhotosLibrary;
import com.gaadi.neon.model.ImageTagModel;
import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.NeonConstants;
import com.gaadi.neon.util.PhotoParams;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private static final String TAG = "MainActivity";
    public static final int REQUEST_CODE = 2004;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bClickMe).setOnClickListener(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode + " responseCode : " + resultCode);
        if(requestCode == REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                Map<ImageTagModel, List<FileInfo>> images =
                        (Map<ImageTagModel, List<FileInfo>>) data.getSerializableExtra(NeonConstants.COLLECTED_IMAGES);
                for(Map.Entry<ImageTagModel, List<FileInfo>> entry : images.entrySet())
                {
                    Log.d(TAG, "onActivityResult: " + entry.getKey().getTagName());

                    for(FileInfo fileInfo : entry.getValue())
                    {
                        if(fileInfo.getFilePath() != null)
                        {
                            Log.d(TAG, "onActivityResult: " + fileInfo.getFilePath());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.bClickMe:
                PhotoParams params = new PhotoParams();
                params.setMode(PhotoParams.MODE.CAMERA_PRIORITY);
                params.setTagEnabled(true);
                //                ArrayList<ImageTagModel> imageTagModels = new ArrayList<>();
                //                ImageTagModel imageTagModel = new ImageTagModel();
                //                imageTagModel.setTagName("First");
                //                imageTagModels.add(imageTagModel);
                //
                //                ImageTagModel imageTagModel1 = new ImageTagModel();
                //                imageTagModel1.setTagName("Second");
                //                imageTagModel1.setMandatory(true);
                //                imageTagModels.add(imageTagModel1);
                //
                //                ImageTagModel imageTagModel2 = new ImageTagModel();
                //                imageTagModel2.setTagName("Third");
                //                imageTagModels.add(imageTagModel2);
                //
                //                params.setImageTags(imageTagModels);

                params.setImageName("My Image");
                params.setNoOfPhotos(1);
                params.setCameraFace(PhotoParams.CameraFacing.BACK);
                //        params.setMode(PhotoParams.MODE.NEUTRAL);
                params.setOrientation(PhotoParams.CameraOrientation.LANDSCAPE);
                //        params.setNoOfPhotos(2);
                //        params.setEnableCapturedReview(true);
                //        params.setEnableExtraBrightness(false);
                //        params.setRestrictedExtensionEnabled(true);
                //        params.setCameraFace(PhotoParams.CameraFacing.BACK);
                //        params.setGalleryFromCameraEnabled(false);
                PhotosLibrary.collectPhotos(this, params, REQUEST_CODE);
                break;
        }
    }
}
