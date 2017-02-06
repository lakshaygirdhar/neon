package com.gaadi.neon.activity.gallery;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gaadi.neon.adapter.GalleryHoriontalAdapter;
import com.gaadi.neon.adapter.GridFilesAdapter;
import com.gaadi.neon.interfaces.SetOnImageClickListener;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.FileInfo;
import com.scanlibrary.R;
import com.scanlibrary.databinding.ActivityGridFilesBinding;
import com.scanlibrary.databinding.HorizontalGalleryLayoutBinding;

import java.util.ArrayList;

/**
 * @author princebatra
 * @version 1.0
 * @since 6/2/17
 */
public class HorizontalFilesActivity extends NeonBaseGalleryActivity implements SetOnImageClickListener{

    HorizontalGalleryLayoutBinding binder;
    ArrayList<FileInfo> fileInfos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bindXml();
        setTitle("Files");
    }


    private void bindXml() {
        //binder = DataBindingUtil.setContentView(this, R.layout.horizontal_gallery_layout);
        binder = DataBindingUtil.inflate(getLayoutInflater(),R.layout.horizontal_gallery_layout,frameLayout,true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binder.galleryHorizontalRv.setLayoutManager(linearLayoutManager);
        fileInfos = getFileFromBucketId(null);
        if(fileInfos != null && fileInfos.size()>0) {
            GalleryHoriontalAdapter adapter = new GalleryHoriontalAdapter(this, fileInfos, this);
            binder.galleryHorizontalRv.setAdapter(adapter);
            onClick(fileInfos.get(0));
        }
    }


    @Override
    public void onClick(FileInfo fileInfo) {
        Glide.with(this).load(fileInfo.getFilePath())
                .placeholder(R.drawable.default_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binder.fullScreenImage);

    }
}
