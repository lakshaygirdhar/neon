package com.gaadi.neon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaadi.neon.adapter.ImagesReviewViewPagerAdapter;
import com.gaadi.neon.events.ImageEditEvent;

import com.gaadi.neon.interfaces.FragmentListener;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.FileInfo;
import com.scanlibrary.R;

import java.util.ArrayList;

public class ImageReviewActivity extends AppCompatActivity implements View.OnClickListener, FragmentListener {

    private ImagesReviewViewPagerAdapter mPagerAdapter;
    private ArrayList<FileInfo> gallaryItemsFiles;

    private TextView mDoneButton;
    private TextView mTitle;
    private ViewPager mPager;
    private Toolbar toolbar;
    private boolean isViewDirty = false;
    private ImageView viewPagerRightBtn;
    private ImageView viewPagerLeftBtn;
    private boolean singleTagSelection;

    public boolean isSingleTagSelection() {
        return singleTagSelection;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_review);
        toolbar = (Toolbar) findViewById(R.id.image_review_toolbar);
        setSupportActionBar(toolbar);
        mDoneButton = (TextView) findViewById(R.id.image_review_toolbar_doneBtn);
        mDoneButton.setOnClickListener(this);
        mPager = (ViewPager) findViewById(R.id.pager);
        viewPagerLeftBtn = (ImageView) findViewById(R.id.view_pager_leftbtn);
        viewPagerRightBtn = (ImageView) findViewById(R.id.view_pager_rightbtn);
        viewPagerRightBtn.setOnClickListener(this);
        viewPagerLeftBtn.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.image_review_toolbar_title);
        getSupportActionBar().setTitle("Image Review");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        gallaryItemsFiles = (ArrayList<FileInfo>) intent.getSerializableExtra(Constants.IMAGE_MODEL_FOR__REVIEW);

        singleTagSelection = intent.getBooleanExtra(Constants.SINGLE_TAG_SELECTION, false);
        int position = intent.getIntExtra(Constants.IMAGE_REVIEW_POSITION, 0);
        if (position == 0) {
            viewPagerLeftBtn.setVisibility(View.GONE);
        }
        if (position == gallaryItemsFiles.size() - 1) {
            viewPagerRightBtn.setVisibility(View.GONE);
        }
        mPagerAdapter = new ImagesReviewViewPagerAdapter(getSupportFragmentManager(), gallaryItemsFiles);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(position);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setArrowButton(position, gallaryItemsFiles);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }

    private void setArrowButton(int position, ArrayList<FileInfo> imagesTempList) {
        if (position == 0 || imagesTempList.size() == 1) {
            viewPagerLeftBtn.setVisibility(View.GONE);
        } else {
            viewPagerLeftBtn.setVisibility(View.VISIBLE);
        }
        if (position == imagesTempList.size() - 1 || imagesTempList.size() == 1) {
            viewPagerRightBtn.setVisibility(View.GONE);
        } else {
            viewPagerRightBtn.setVisibility(View.VISIBLE);
        }
    }

    public void getFragmentChanges(ImageEditEvent event) {
        if (event.getImageEventType() == ImageEditEvent.EVENT_DELETE) {
            isViewDirty = true;
            gallaryItemsFiles.remove(event.getPosition());
            mPagerAdapter.setPagerItems(gallaryItemsFiles);
            if (gallaryItemsFiles.size() == 0) {
                onBackPressed();
            }
            setArrowButton(mPager.getCurrentItem(), gallaryItemsFiles);
        } else if (event.getImageEventType() == ImageEditEvent.EVENT_ROTATE) {
            isViewDirty = true;

        } else if (event.getImageEventType() == ImageEditEvent.EVENT_REPLACED_BY_CAM) {
            isViewDirty = true;
        } else if (event.getImageEventType() == ImageEditEvent.EVENT_REPLACED_BY_GALLERY) {
            isViewDirty = true;
        } else if (event.getImageEventType() == ImageEditEvent.EVENT_TAG_CHANGED) {
            gallaryItemsFiles.set(event.getPosition(), event.getModel());
        }
    }


    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.image_review_toolbar_doneBtn) {
            Intent i = new Intent();
            i.putExtra(Constants.IMAGE_MODEL_FOR__REVIEW, gallaryItemsFiles);
            setResult(RESULT_OK, i);
            finish();
        } else if (id == R.id.view_pager_leftbtn) {
            int position = mPager.getCurrentItem();
            if (position > 0) {
                position--;
                mPager.setCurrentItem(position);
            }


        } else if (id == R.id.view_pager_rightbtn) {
            int position = mPager.getCurrentItem();
            if (position < gallaryItemsFiles.size() - 1) {
                position++;
                mPager.setCurrentItem(position);
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_pager, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        if (item.getItemId() == R.id.menu_done) {
            Intent i = new Intent();
            i.putExtra(Constants.IMAGE_MODEL_FOR__REVIEW, gallaryItemsFiles);
            setResult(RESULT_OK, i);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
       /* if(isViewDirty){
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            imagePipeline.clearCaches();
        }*/
        Intent i = new Intent();
        i.putExtra(Constants.IMAGE_MODEL_FOR__REVIEW, gallaryItemsFiles);
        setResult(RESULT_OK, i);
        //finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}
