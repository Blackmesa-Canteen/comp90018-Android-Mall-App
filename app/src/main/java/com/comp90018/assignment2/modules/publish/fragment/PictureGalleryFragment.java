package com.comp90018.assignment2.modules.publish.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.base.BaseFragment;
import com.comp90018.assignment2.config.GlideEngine;
import com.comp90018.assignment2.modules.publish.adapter.PictureCollectionAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * @author Ziyuan Xu
 */
public class PictureGalleryFragment extends BaseFragment {
    //    private String userId;
    private final static String TAG = "PictureGalleryFragment";
    private ImageView pf_add;
    private RecyclerView pf_collection;

    @Override
    public View inflateView() {
        View view = View.inflate(activityContext, R.layout.fragment_picture_gallery, null);
        pf_collection = view.findViewById(R.id.pf_collection);
        pf_add = view.findViewById(R.id.pf_add);
        pf_add.setOnClickListener(view1 -> PictureSelector.create(PictureGalleryFragment.this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(4)
                .minSelectNum(1)
                .imageSpanCount(4)
                .selectionMode(PictureConfig.MULTIPLE)
                .isPreviewImage(true)
                .isCompress(true)
                .forResult(Constants.REQUEST_CODE_A));
        return view;
    }

    @Override
    public void loadData() {
        // no need to query database
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.REQUEST_CODE_A:
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                PictureCollectionAdapter pictureCollectionAdapter = new PictureCollectionAdapter(activityContext,
                        selectList);
                pf_collection.setAdapter(pictureCollectionAdapter);
                GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
                pf_collection.setLayoutManager(manager);
                break;
            default:
                break;
        }
    }

}
