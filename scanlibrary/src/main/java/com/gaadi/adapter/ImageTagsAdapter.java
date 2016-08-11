package com.gaadi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaadi.model.ImageTagsModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by alokmishra on 27/11/15.
 */

public class ImageTagsAdapter extends ArrayAdapter<ImageTagsModel> {

    private final LayoutInflater mInflater;
    Context context;
    List<ImageTagsModel> mItems;
    ArrayList<Integer> mCurrentList;
    private ImageTagsModelsHolder mHolder;
    public ImageTagsAdapter(Context context, List<ImageTagsModel> rowItem) {
        super(context,android.R.layout.simple_spinner_dropdown_item);
        this.context = context;
        this.mItems = rowItem;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setDropDownViewResource(com.gaadi.R.layout.color_layout_row);

    }

    @Override
    public int getCount() {

        return mItems.size();
    }

    @Override
    public ImageTagsModel getItem(int position) {

        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {

        return mItems.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(com.gaadi.R.layout.color_layout_row, parent, false);

            mHolder = new ImageTagsModelsHolder();
            mHolder.text = (TextView) convertView.findViewById(com.gaadi.R.id.colorValue);
            mHolder.text.setTextAppearance(context, com.gaadi.R.style.textStyleHeading2);
            mHolder.iv_color = (ImageView) convertView.findViewById(com.gaadi.R.id.color);

            convertView.setTag(mHolder);

        } else {
            mHolder = (ImageTagsModelsHolder) convertView.getTag();

        }
        mHolder.text.setText(mItems.get(position).getTag_name());


        return convertView;

    }
    public ArrayList<Integer> getSelectedList(){
        return this.mCurrentList;
    }
    public void setSelectedArrayList(ArrayList<Integer> list){

        this.mCurrentList = list;
    }
    private class ImageTagsModelsHolder {
        TextView text;
        ImageView iv_color;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(com.gaadi.R.layout.color_layout_row, parent, false);

            mHolder = new ImageTagsModelsHolder();
            mHolder.text = (TextView) convertView.findViewById(com.gaadi.R.id.colorValue);
            //mHolder.text.setTextAppearance(context, R.style.textStyleHeading2);
            mHolder.iv_color = (ImageView) convertView.findViewById(com.gaadi.R.id.color);

            convertView.setTag(mHolder);

        } else {
            mHolder = (ImageTagsModelsHolder) convertView.getTag();

        }
        mHolder.text.setText(mItems.get(position).getTag_name());


        return convertView;

    }


}
