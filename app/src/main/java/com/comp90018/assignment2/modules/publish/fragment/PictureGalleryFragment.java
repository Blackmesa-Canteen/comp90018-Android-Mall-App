package com.comp90018.assignment2.modules.publish.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.List;

import lombok.Getter;

/**
 * @author Ziyuan Xu
 */
@Getter
public class PictureGalleryFragment extends BaseFragment {
    //    private String userId;
    private final static String TAG = "PictureGalleryFragment";
    private ImageView pf_add;
    private RecyclerView pf_collection;
    private List<LocalMedia> selectLists;
//    private FirebaseStorage firebaseStorage;

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
                selectLists = PictureSelector.obtainMultipleResult(data);
                PictureCollectionAdapter pictureCollectionAdapter = new PictureCollectionAdapter(activityContext,
                        selectLists);
                pf_collection.setAdapter(pictureCollectionAdapter);
                GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
                pf_collection.setLayoutManager(manager);
                break;
            default:
                break;
        }
    }

}
