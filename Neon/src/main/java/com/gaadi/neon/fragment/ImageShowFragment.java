package com.gaadi.neon.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gaadi.neon.enumerations.ResponseCode;
import com.gaadi.neon.adapter.ImageShowAdapter;
import com.gaadi.neon.model.ImageTagModel;
import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.NeonImagesHandler;
import com.scanlibrary.R;
import com.scanlibrary.databinding.ImageShowLayoutBinding;

import java.util.List;

/**
 * @author princebatra
 * @version 1.0
 * @since 2/2/17
 */
public class ImageShowFragment extends Fragment {

    ImageShowAdapter adapter;
    ImageShowLayoutBinding binder;
    View.OnClickListener doneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (NeonImagesHandler.getSingleonInstance().validateNeonExit(getActivity())) {
                NeonImagesHandler.getSingleonInstance().sendImageCollectionAndFinish(getActivity(), ResponseCode.Success);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binder = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.image_show_layout, null, false);
        binder.btnDone.setOnClickListener(doneListener);
        return binder.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NeonImagesHandler.getSingleonInstance().getImagesCollection() == null ||
                NeonImagesHandler.getSingleonInstance().getImagesCollection().size() < 0) {
            return;
        }
        if (adapter == null) {
            adapter = new ImageShowAdapter(getActivity());
            binder.imageShowGrid.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }



}
