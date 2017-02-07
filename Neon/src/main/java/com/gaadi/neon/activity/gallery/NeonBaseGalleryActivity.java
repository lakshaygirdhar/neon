package com.gaadi.neon.activity.gallery;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.gaadi.neon.Enumerations.GalleryType;
import com.gaadi.neon.activity.BaseActivity;
import com.gaadi.neon.activity.GalleryActivity;
import com.gaadi.neon.activity.NeonBaseActivity;
import com.gaadi.neon.interfaces.IGalleryParam;
import com.gaadi.neon.interfaces.SetOnPermissionResultListener;
import com.gaadi.neon.model.BucketModel;
import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.ManifestPermission;
import com.gaadi.neon.util.PermissionType;
import com.gaadi.neon.util.SingletonClass;
import com.scanlibrary.R;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author princebatra
 * @version 1.0
 * @since 25/1/17
 */
public abstract class NeonBaseGalleryActivity extends NeonBaseActivity {

    private Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    ArrayList<BucketModel> buckets;

    protected ArrayList<BucketModel> getImageBuckets() {
        buckets = new ArrayList<>();

        String[] PROJECTION_BUCKET = {MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.BUCKET_ID, MediaStore.Images.ImageColumns.DATA};

        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";

        Cursor mCursor;
        if (SingletonClass.getSingleonInstance().getGalleryParam() != null && SingletonClass.getSingleonInstance().getGalleryParam().isRestrictedExtensionJpgPngEnabled()) {
            mCursor = getContentResolver().query(uri, PROJECTION_BUCKET, MediaStore.Images.Media.MIME_TYPE + " in (?, ?)", new String[]{"image/jpeg", "image/png"}, orderBy);
        } else {
            mCursor = getContentResolver().query(uri, PROJECTION_BUCKET, null, null, orderBy);
        }
        if (mCursor == null) {
            Toast.makeText(this, getString(R.string.gallery_error), Toast.LENGTH_SHORT).show();
            finish();
            return null;
        }
        mCursor.moveToFirst();


        if(mCursor.getCount() > 0){
            do {
                String bucketId = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));

                int index = getBucketIndexWithId(bucketId);
                if (index == -1) {
                    BucketModel bucketModel = new BucketModel();
                    bucketModel.setBucketId(bucketId);
                    bucketModel.setBucketName(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
                    bucketModel.setFileCount(1);
                    bucketModel.setBucketCoverImagePath(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                    buckets.add(bucketModel);
                } else {
                    buckets.get(index).setFileCount(buckets.get(index).getFileCount() + 1);
                }
            }while (mCursor.moveToNext());
        }
        mCursor.close();

        return buckets;
    }

    /**Pass bucketId if need all images from all buckets*/
    protected ArrayList<FileInfo> getFileFromBucketId(String bucketId) {
        ArrayList<FileInfo> fileInfos = new ArrayList<>();

        String[] PROJECTION_FILES = {MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.BUCKET_ID, MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DATE_TAKEN};

        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";

        String selection = MediaStore.Images.Media.BUCKET_ID + " =? and " + MediaStore.Images.Media.SIZE + " >?";
        String[] selectionArgs = new String[]{bucketId, String.valueOf(0)};
        if(bucketId == null){
            selection = null;
            selectionArgs = null;
        }
        Cursor mCursor = getContentResolver().query(uri, PROJECTION_FILES, selection, selectionArgs, orderBy);
        if (mCursor == null) {
            Toast.makeText(this, getString(R.string.gallery_error), Toast.LENGTH_SHORT).show();
            finish();
            return null;
        }
        mCursor.moveToFirst();

        if(mCursor.getCount()>0){
            do{
                FileInfo singleFileInfo = new FileInfo();
                singleFileInfo.setSource(FileInfo.SOURCE.PHONE_GALLERY);
                singleFileInfo.setFilePath(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                singleFileInfo.setDateTimeTaken(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN)));
                singleFileInfo.setType(FileInfo.FILE_TYPE.IMAGE);
                fileInfos.add(singleFileInfo);
            }while (mCursor.moveToNext());
        }
        mCursor.close();

        return fileInfos;
    }


    private int getBucketIndexWithId(String id) {
        if (buckets == null || buckets.size() <= 0) {
            return -1;
        }

        for (int i = 0; i < buckets.size(); i++) {
            if (buckets.get(i).getBucketId().equals(id)) {
                return i;
            }
        }
        return -1;
    }


}
