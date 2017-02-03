package com.gaadi.neon.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gaadi.neon.util.SingletonClass;
import com.scanlibrary.R;

/**
 * @author princebatra
 * @version 1.0
 * @since 2/2/17
 */
public class ImageShowAdapter extends BaseAdapter {

    private Context context;

    public ImageShowAdapter(Context activity) {
        context = activity;
    }

    @Override
    public int getCount() {
        return SingletonClass.getSingleonInstance().getImagesCollection().size();
    }

    @Override
    public Object getItem(int position) {
        return SingletonClass.getSingleonInstance().getImagesCollection().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PhotosHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.display_images, null);
            holder = new PhotosHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.ivImageDisplay);
            holder.removeImage = (ImageView) convertView.findViewById(R.id.ivRemoveImage);
            holder.tvProfile = (TextView) convertView.findViewById(R.id.tvProfilePhoto);
            convertView.setTag(holder);
        } else {
            holder = (PhotosHolder) convertView.getTag();
        }

        if (!SingletonClass.getSingleonInstance().getGenericParam().getTagEnabled()) {
            holder.tvProfile.setVisibility(View.GONE);
        } else {
            holder.tvProfile.setVisibility(View.VISIBLE);
        }

        if (SingletonClass.getSingleonInstance().getImagesCollection().get(position).getFileTag() != null) {
            holder.tvProfile.setText(SingletonClass.getSingleonInstance().getImagesCollection().get(position).getFileTag().getTagName());
        } else {
            holder.tvProfile.setText("Select Tag");
        }

        holder.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SingletonClass.getSingleonInstance().removeFromCollection(position)) {
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(context,"Failed to delete.Please try again later.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked position " + position, Toast.LENGTH_SHORT).show();
            }
        });

        Glide.with(context).load(SingletonClass.getSingleonInstance().getImagesCollection().get(position).getFilePath())
                .crossFade()
                .placeholder(R.drawable.default_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);

        return convertView;
    }

    private class PhotosHolder {
        ImageView image;
        ImageView removeImage;
        TextView tvProfile;
    }
}
