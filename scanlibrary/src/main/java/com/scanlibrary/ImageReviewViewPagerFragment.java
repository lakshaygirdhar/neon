package com.scanlibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.imageuploadlib.Utils.FileInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by himanshu on 23/12/15.
 */
public class ImageReviewViewPagerFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ImageReviewViewPager";
    private static final int CROPPING_REQUEST_CODE = 3001;
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    private ImageView deleteBtn;
    private ImageView rotateBtn;
//    private TextView txtVwTagSpinner;
    private ImageView draweeView;

    private FileInfo imageModel;
//    private ArrayList<ImageTagsModel> imageTags;

//    private ImageTagsAdapter imageTagsAdapter;
//    private ImageTagsModel selectedTag;
    private int counterRotation = 0;
    private int screenWidth, screenHeight;
    private Context mContext;
    private ImageView cropBtn;
    private String imagePathForCropping;
    private String imagePathForCropped;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ImageReviewViewPagerFragment create(int pageNumber, FileInfo imageModel/*, ArrayList<ImageTagsModel> imageTags*/) {
        ImageReviewViewPagerFragment fragment = new ImageReviewViewPagerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putSerializable(ScanConstants.IMAGE_MODEL_FOR__REVIEW, imageModel);
//        args.putSerializable(ScanConstants.IMAGE_TAGS_FOR_REVIEW, imageTags);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageReviewViewPagerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_image_review_viewpager, container, false);
//        ButterKnife.bind(this, rootView);

        deleteBtn = (ImageView) rootView.findViewById(R.id.imagereview_deletebtn);
        cropBtn=(ImageView)rootView.findViewById(R.id.imagereview_cropbtn);
        rotateBtn = (ImageView) rootView.findViewById(R.id.imagereview_rotatebtn);
//        txtVwTagSpinner = (TextView) rootView.findViewById(R.id.imagereview_tag_spinner);
        draweeView = (ImageView) rootView.findViewById(R.id.imagereview_imageview);
        deleteBtn.setOnClickListener(this);
        rotateBtn.setOnClickListener(this);
        cropBtn.setOnClickListener(this);
//        txtVwTagSpinner.setOnClickListener(this);
        onLoad(savedInstanceState);
        // Set the title view to show the page number.
       /* ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                getString(R.string.title_template_step, mPageNumber + 1));*/

        return rootView;
    }

    public void onLoad(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        imageModel = (FileInfo) bundle.getSerializable(ScanConstants.IMAGE_MODEL_FOR__REVIEW);
        if (savedInstanceState != null) {
            Object o = bundle.getSerializable(ScanConstants.IMAGE_MODEL_FOR__REVIEW);
            if (o != null) {
                imageModel = (FileInfo) o;
            }
        }
//        imageTags = (ArrayList<ImageTagsModel>) bundle.getSerializable(ScanConstants.IMAGE_TAGS_FOR_REVIEW);
//        if (imageTags != null && imageTags.size() > 0) {
//            txtVwTagSpinner.setVisibility(View.VISIBLE);
//            if (imageModel.getTagsModel() != null) {
//                txtVwTagSpinner.setText(imageModel.getTagsModel().getTag_name());
//                selectedTag = imageModel.getTagsModel();
//            }
//            imageTagsAdapter = new ImageTagsAdapter(getActivity(), imageTags);
//        } else {
//            txtVwTagSpinner.setVisibility(View.GONE);
//        }
        //draweeView.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
        // draweeView.setImage(ImageSource.uri(imageModel.getImagePath()));


        Glide.with(mContext).load(imageModel.getFilePath())
                .placeholder(R.drawable.default_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(draweeView);
        // Glide.with(mContext).load("file://"+imageModel.getImagePath()).into(draweeView);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ScanConstants.IMAGE_MODEL_FOR__REVIEW, imageModel);
        super.onSaveInstanceState(outState);
    }

    public void showTagsDropDown() {
//        final ListPopupWindow listPopupWindow = new ListPopupWindow(getActivity());
//        listPopupWindow.setModal(true);
//        listPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
//                R.drawable.abc_popup_background_mtrl_mult,
//                null));
//        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
//        listPopupWindow.setAdapter(imageTagsAdapter);
//        listPopupWindow.setAnchorView(getView().findViewById(R.id.imagereview_tag_spinner));
//        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ImageTagsModel currentSelectedTag= (ImageTagsModel) parent.getAdapter().getItem(position);

//                if (((ImageReviewActivity) getActivity()).isSingleTagSelection() &&
//                        ((ImageReviewActivity) getActivity()).isAlreadySelectedTag(currentSelectedTag)) {
//                    Toast.makeText(getActivity(), "Already selected tag", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                ImageTagsModel lastSelectedModel = selectedTag;
//                selectedTag = currentSelectedTag;
//                ((ImageReviewActivity) getActivity()).setSelectedTagModel(lastSelectedModel, selectedTag);

//                txtVwTagSpinner.setText(selectedTag.getTag_name());
//                ImageTagsModel tagModel = new ImageTagsModel();
//                tagModel.setTagId(selectedTag.getTagId());
//                tagModel.setTag_name(selectedTag.getTag_name());
//                imageModel.setTagsModel(tagModel);
//                ImageEditEvent event = new ImageEditEvent();
//                event.setModel(imageModel);
//                ((FragmentListener) getActivity()).getFragmentChanges(event);
//                listPopupWindow.dismiss();
//            }
//        });
//        txtVwTagSpinner.post(new Runnable() {
//            @Override
//            public void run() {
//                listPopupWindow.show();
//            }
//        });
    }


    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    private File cropFilePath;

    @Override
    public void onClick(View v) {
        ImageEditEvent event = new ImageEditEvent();
        event.setModel(imageModel);
        if (v.getId() == R.id.imagereview_deletebtn) {
            event.setImageEventType(ImageEditEvent.EVENT_DELETE);
            event.setPosition(mPageNumber);
            warnDeleteDialog(event);
        } else if (v.getId() == R.id.imagereview_rotatebtn) {
            // draweeView.get
            //draweeView.setRotation(90.0f);

            rotateImage(imageModel.getFilePath());
            // getImageFromFrescoView(imageModel.getImagePath());
//            clearFrescoCache(Uri.fromFile(new File(imageModel.getImagePath())));
            //CommonUtils.setExifRotation(new File(imageModel.getImagePath()),0);
            //draweeView.setImageURI(Uri.fromFile(new File(imageModel.getImagePath())));
        } else if (v.getId() == R.id.imagereview_tag_spinner) {
//            showTagsDropDown();
        }
        else if(v.getId()==R.id.imagereview_cropbtn){
            //TODO PRINCE

            imagePathForCropping = imageModel.getFilePath();
            Intent intent = new Intent(getActivity(),ScanActivity.class);
            intent.putExtra(ScanConstants.IMAGE_FILE_FOR_CROPPING, new File(imageModel.getFilePath()));
            startActivityForResult(intent,CROPPING_REQUEST_CODE);
//            getActivity().getSupportFragmentManager().beginTransaction().add(ScanFragment.instantiate())

//            cropFilePath=Utils.getEmptyStoragePath(getActivity());
//            Uri inputUri=Uri.fromFile(new File(imageModel.getFilePath()));
//            Uri outputUri=Uri.fromFile(cropFilePath);
            //ToDO_PRINCE
//            Crop.of(inputUri, outputUri).start(getActivity(),ImageReviewViewPagerFragment.this);
        }
    }

    private void warnDeleteDialog(final ImageEditEvent event) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.remove_img_title);
        builder.setMessage(R.string.removeImage);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.okDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isAdded())
                    ((FragmentListener) getActivity()).getFragmentChanges(event);
            }
        });
        builder.setNegativeButton(R.string.cancelDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @SuppressWarnings({"deprecation"})
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @SuppressLint("NewApi")
    private Bitmap getBitmap(String path) {
        DisplayMetrics displaymetrics;
        displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
        screenHeight = displaymetrics.heightPixels;
        Log.e("inside of", "getBitmap = " + path);
        try {
            Bitmap b = null;
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            Matrix matrix = new Matrix();
            ExifInterface exifReader = new ExifInterface(path);
            int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            int rotate = 0;
            if (orientation == ExifInterface.ORIENTATION_NORMAL) {
                // Do nothing. The original image is fine.
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                rotate = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                rotate = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                rotate = 270;
            } else {
                Toast.makeText(getActivity(), "Not Able to rotate image due to missing orientation tag", Toast.LENGTH_SHORT).show();
                Log.e("ERROR", "Not Able to rotate image due to orientation tag=" + orientation);
                return null;
            }


//            draweeView.setRotation(draweeView.getRotation() + 90.0f);
//            ImageEditEvent event = new ImageEditEvent();
//            event.setModel(imageModel);
//            event.setImageEventType(ImageEditEvent.EVENT_ROTATE);
//            ((FragmentListener) getActivity()).getFragmentChanges(event);


//             matrix.postRotate(rotate);
            //Button btn_RotateImg = (Button) findViewById(R.id.btn_RotateImg);
           /* try {
                b = loadBitmap(path, rotate, screenWidth, screenHeight);

                //btn_RotateImg.setEnabled(true);
            } catch (OutOfMemoryError e) {
                // btn_RotateImg.setEnabled(false);
            }*/
            //System.gc();
            // return b;
        } catch (Exception e) {
            Log.e("my tag", e.getMessage(), e);
            // return null;
        }
        return null;
    }

    /*public Bitmap loadBitmap(String path, int orientation, final int targetWidth, final int targetHeight) {
        Bitmap bitmap = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int sourceWidth, sourceHeight;
            if (orientation == 90 || orientation == 270) {
                sourceWidth = options.outHeight;
                sourceHeight = options.outWidth;
            } else {
                sourceWidth = options.outWidth;
                sourceHeight = options.outHeight;
            }
            if (sourceWidth > targetWidth || sourceHeight > targetHeight) {
                float widthRatio = (float) sourceWidth / (float) targetWidth;
                float heightRatio = (float) sourceHeight / (float) targetHeight;
                float maxRatio = Math.max(widthRatio, heightRatio);
                options.inJustDecodeBounds = false;
                options.inSampleSize = (int) maxRatio;
                bitmap = BitmapFactory.decodeFile(path, options);
            } else {
                bitmap = BitmapFactory.decodeFile(path);
            }
            if (orientation > 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            sourceWidth = bitmap.getWidth();
            sourceHeight = bitmap.getHeight();
            if (sourceWidth != targetWidth || sourceHeight != targetHeight) {
                float widthRatio = (float) sourceWidth / (float) targetWidth;
                float heightRatio = (float) sourceHeight / (float) targetHeight;
                float maxRatio = Math.max(widthRatio, heightRatio);
                sourceWidth = (int) ((float) sourceWidth / maxRatio);
                sourceHeight = (int) ((float) sourceHeight / maxRatio);
                bitmap = Bitmap.createScaledBitmap(bitmap, sourceWidth, sourceHeight, true);
            }
        } catch (Exception e) {
        }

        return bitmap;
    }*/

    public void rotateImage(String path) {
        File file = new File(path);
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(file.getPath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if ((orientation == ExifInterface.ORIENTATION_NORMAL) | (orientation == 0)) {
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "" + ExifInterface.ORIENTATION_ROTATE_90);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "" + ExifInterface.ORIENTATION_ROTATE_180);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "" + ExifInterface.ORIENTATION_ROTATE_270);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "" + ExifInterface.ORIENTATION_NORMAL);
        }
        try {
            exifInterface.saveAttributes();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        getBitmap(path);
        draweeView.setRotation(draweeView.getRotation() + 90.0f);
        ImageEditEvent event = new ImageEditEvent();
        event.setModel(imageModel);
        event.setImageEventType(ImageEditEvent.EVENT_ROTATE);
        ((FragmentListener) getActivity()).getFragmentChanges(event);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        //TODO_PRINCE
        if (resultCode == ScanConstants.SINGLE_CAPTURED) {
            String imagePath = result.getStringExtra(ScanConstants.CAPTURED_IMAGE_PATH);
            Log.i(TAG,"onActivityResult "+imagePath);
            imageModel.setFilePath(imagePath);
            Intent intent = new Intent();
            intent.setAction(ScanConstants.UPDATE_IMAGE_LIST);
            intent.putExtra(ScanConstants.IMAGE_INDEX_SENT_FOR_CROPPING,mPageNumber);
            intent.putExtra(ScanConstants.IMAGE_RECEIVED_AFTER_CROPPING,imageModel);
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            Glide.with(mContext).load(imagePath)
                    .placeholder(R.drawable.default_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(draweeView);
        }
    }


}
