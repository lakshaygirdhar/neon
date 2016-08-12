package com.scanlibrary;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gaadi.neon.Utils.FileInfo;

import java.util.ArrayList;

/**
 * Created by lakshaygirdhar on 27/12/15.
 */
public class ImagesReviewViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_PAGES = 20;
    ArrayList<FileInfo> imageList;
    ArrayList<ImageReviewViewPagerFragment> fragmentList;
//    ArrayList<ImageTagsModel> imageTags;
    FragmentManager mFragmentManager;

    public ImagesReviewViewPagerAdapter(FragmentManager fm, ArrayList<FileInfo> imageList/*,ArrayList<ImageTagsModel> imageMap*/) {
        super(fm);
        mFragmentManager=fm;
        this.imageList=imageList;
//        this.imageTags = imageMap;
        updatePagerItems(imageList);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }
    public int getItemPosition(Object item) {
        ImageReviewViewPagerFragment fragment = (ImageReviewViewPagerFragment)item;

        int position = fragmentList.indexOf(fragment);

        if (position >= 0) {
            return position;
        } else {
            return POSITION_NONE;
        }
    }

    public void updatePagerItems(ArrayList<FileInfo> pagerItems){

        if(fragmentList!=null)
            fragmentList.clear();
        else
            fragmentList=new ArrayList<>();
        for (int i = 0; i < pagerItems.size(); i++) {
            fragmentList.add(ImageReviewViewPagerFragment.create(i,imageList.get(i)/*,imageTags*/));
        }
    }

    public void setPagerItems(ArrayList<FileInfo> pagerItems) {
        this.imageList=pagerItems;
        if (fragmentList != null)
            for (int i = 0; i < imageList.size(); i++) {
                mFragmentManager.beginTransaction().remove(fragmentList.get(i)).commit();
            }
        updatePagerItems(pagerItems);
        notifyDataSetChanged();
    }
   /* @Override
    public float getPageWidth(int position) {
        return 0.9f;
    }*/

    @Override
    public int getCount() {
        return fragmentList.size();
    }

}
