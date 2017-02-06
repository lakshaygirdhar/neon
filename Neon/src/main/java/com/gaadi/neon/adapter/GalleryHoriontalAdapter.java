package com.gaadi.neon.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gaadi.neon.interfaces.SetOnImageClickListener;
import com.gaadi.neon.util.FileInfo;
import com.scanlibrary.R;

import java.util.ArrayList;

/**
 * @author princebatra
 * @version 1.0
 * @since 6/2/17
 */
public class GalleryHoriontalAdapter extends RecyclerView.Adapter<GalleryHoriontalAdapter.ItemHolder> {

    protected Context context;
    private ArrayList<FileInfo> fileInfos;
    private LayoutInflater layoutInflater;
    SetOnImageClickListener listener;

    public GalleryHoriontalAdapter(Context _context, ArrayList<FileInfo> _fileInfos, SetOnImageClickListener _listener) {
        context = _context;
        fileInfos = _fileInfos;
        layoutInflater = LayoutInflater.from(context);
        listener = _listener;
    }


    @Override
    public GalleryHoriontalAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView itemCardView = (CardView) layoutInflater.inflate(R.layout.layout_cardview, parent, false);
        return new ItemHolder(itemCardView, this);
    }

    @Override
    public void onBindViewHolder(GalleryHoriontalAdapter.ItemHolder holder, int position) {
        FileInfo fileInfo = fileInfos.get(position);
        Glide.with(context).load(fileInfo.getFilePath())
                .thumbnail(0.1f)
                .crossFade()
                .placeholder(R.drawable.default_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return fileInfos.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private GalleryHoriontalAdapter parent;
        ImageView imageView;

        ItemHolder(CardView cardView, GalleryHoriontalAdapter parent) {
            super(cardView);
            itemView.setOnClickListener(this);
            this.parent = parent;
            imageView = (ImageView) cardView.findViewById(R.id.item_image);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(parent.fileInfos.get(getLayoutPosition()));
        }
    }

}
