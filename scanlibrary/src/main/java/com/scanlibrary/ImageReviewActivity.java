package com.scanlibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaadi.neon.Utils.FileInfo;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

/**
 * Created by lakshaygirdhar on 25/12/15.
 */
public class ImageReviewActivity extends AppCompatActivity implements View.OnClickListener, FragmentListener {



    private ImagesReviewViewPagerAdapter mPagerAdapter;
    //    private ArrayList<ImageTagsModel> mImageTags;
    private ArrayList<FileInfo> imagesList;

    private TextView mDoneButton;
    private TextView mTitle;
    private ViewPager mPager;
    private Toolbar toolbar;
    private boolean isViewDirty = false;
    private ImageView viewPagerRightBtn;
    private ImageView viewPagerLeftBtn;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private UpdateImageListReceiver mUpdateImageListReceiver;
//    private boolean singleTagSelection;

//    private HashSet<ImageTagsModel> alreadySelectedTags = new HashSet<>();

//    public void setSelectedTagModel(ImageTagsModel lastSelectedModel, ImageTagsModel currentSelectedModel) {
//        if (lastSelectedModel != null) {
//            alreadySelectedTags.remove(lastSelectedModel);
//        }
//        alreadySelectedTags.add(currentSelectedModel);
//    }
//
//    public boolean isAlreadySelectedTag(ImageTagsModel tagsModel) {
//        if (alreadySelectedTags.contains(tagsModel)) {
//            return true;
//        }
//        return false;
//    }
//
//    public HashSet<ImageTagsModel> getAlreadySelectedTags() {
//        return alreadySelectedTags;
//    }
//
//    public void setAlreadySelectedTags(HashSet<ImageTagsModel> alreadySelectedTags) {
//        this.alreadySelectedTags = alreadySelectedTags;
//    }
//
//    public boolean isSingleTagSelection() {
//        return singleTagSelection;
//    }
//
//    public void resetTagsSelection() {
//        alreadySelectedTags.clear();
//        mPagerAdapter.updatePagerItems(imagesList);
//        mPagerAdapter.notifyDataSetChanged();
//        mPager.setCurrentItem(0);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_imagereview);
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
//        mImageTags = (ArrayList<ImageTagsModel>) intent.getSerializableExtra(ScanConstants.IMAGE_TAGS_FOR_REVIEW);
        imagesList = (ArrayList<FileInfo>) intent.getSerializableExtra(ScanConstants.IMAGE_MODEL_FOR__REVIEW);
//        singleTagSelection = intent.getBooleanExtra(ScanConstants.SINGLE_TAG_SELECTION, false);
//        if (singleTagSelection) {
//            alreadySelectedTags = (HashSet<ImageTagsModel>) intent.getSerializableExtra(ScanConstants.ALREADY_SELECTED_TAGS);
//        }
        int position = intent.getIntExtra(ScanConstants.IMAGE_REVIEW_POSITION, 0);
        if (position == 0) {
            viewPagerLeftBtn.setVisibility(View.GONE);
        }
        if (position == imagesList.size() - 1) {
            viewPagerRightBtn.setVisibility(View.GONE);
        }
        mPagerAdapter = new ImagesReviewViewPagerAdapter(getSupportFragmentManager(), imagesList/*, mImageTags*/);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(position);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setArrowButton(position, imagesList);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanConstants.UPDATE_IMAGE_LIST);
        mUpdateImageListReceiver = new UpdateImageListReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateImageListReceiver,filter);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    ArrayList<ImageModel> tempImageList;

    public void makeUnchangedList(ArrayList<ImageModel> list) {
        tempImageList = new ArrayList<>();
        tempImageList.addAll(list);
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
            imagesList.remove(event.getPosition());
            mPagerAdapter.setPagerItems(imagesList);
            if (imagesList.size() == 0) {
                onBackPressed();
            }
            setArrowButton(mPager.getCurrentItem(), imagesList);
        } else if (event.getImageEventType() == ImageEditEvent.EVENT_ROTATE) {
            isViewDirty = true;

        } else if (event.getImageEventType() == ImageEditEvent.EVENT_REPLACED_BY_CAM) {
            isViewDirty = true;
        } else if (event.getImageEventType() == ImageEditEvent.EVENT_REPLACED_BY_GALLERY) {
            isViewDirty = true;
        } else if (event.getImageEventType() == ImageEditEvent.EVENT_TAG_CHANGED) {
            imagesList.set(event.getPosition(), event.getModel());
        }
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.image_review_toolbar_doneBtn) {
            Intent i = new Intent();
            i.putExtra(ScanConstants.IMAGE_MODEL_FOR__REVIEW, imagesList);
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
            if (position < imagesList.size() - 1) {
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
        i.putExtra(ScanConstants.IMAGE_MODEL_FOR__REVIEW, imagesList);
        setResult(RESULT_OK, i);
        //finish();
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mUpdateImageListReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ImageReview Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.scanlibrary/http/host/path")
        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ImageReview Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.scanlibrary/http/host/path")
        );
//        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_screen_slide, menu);

        menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                return true;

            case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    class UpdateImageListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ImageReviewActivity.this.setResult(ScanConstants.RESULT_FROM_IMAGE_REVIEW_ACTIVITY,intent);
            finish();
        }
    }
}
