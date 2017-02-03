package com.gaadi.neon.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gaadi.neon.fragment.ImageReviewViewPagerFragment;
import com.gaadi.neon.util.FileInfo;

import java.util.ArrayList;

/**
 * @author dipanshugarg
 * @version 1.0
 * @since 25/1/17
 */
public class ImagesReviewViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_PAGES = 20;
    ArrayList<FileInfo> imageList;
    ArrayList<ImageReviewViewPagerFragment> fragmentList;
    //ArrayList<ImageTagsModel> imageTags;
    FragmentManager mFragmentManager;

    public ImagesReviewViewPagerAdapter(FragmentManager fm,ArrayList<FileInfo> imageList) {
        super(fm);
        mFragmentManager=fm;
        this.imageList=imageList;
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
            fragmentList.add(ImageReviewViewPagerFragment.create(i,imageList.get(i)));
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
