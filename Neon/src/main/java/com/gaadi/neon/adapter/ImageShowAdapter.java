package com.gaadi.neon.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gaadi.neon.activity.ImageReviewActivity;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.NeonImagesHandler;
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
        if(NeonImagesHandler.getSingleonInstance().getImagesCollection() != null) {
            return NeonImagesHandler.getSingleonInstance().getImagesCollection().size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return NeonImagesHandler.getSingleonInstance().getImagesCollection().get(position);
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

        if (!NeonImagesHandler.getSingleonInstance().getGenericParam().getTagEnabled()) {
            holder.tvProfile.setVisibility(View.GONE);
        } else {
            holder.tvProfile.setVisibility(View.VISIBLE);
        }

        if (NeonImagesHandler.getSingleonInstance().getImagesCollection().get(position).getFileTag() != null) {
            holder.tvProfile.setText(NeonImagesHandler.getSingleonInstance().getImagesCollection().get(position).getFileTag().getTagName());
        } else {
            holder.tvProfile.setText("Select Tag");
        }

        holder.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NeonImagesHandler.getSingleonInstance().removeFromCollection(position)) {
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(context,"Failed to delete.Please try again later.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewPagerIntent = new Intent(context,ImageReviewActivity.class);
                viewPagerIntent.putExtra(Constants.IMAGE_REVIEW_POSITION,position);
                context.startActivity(viewPagerIntent);
            }
        });

        Glide.with(context).load(NeonImagesHandler.getSingleonInstance().getImagesCollection().get(position).getFilePath())
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
