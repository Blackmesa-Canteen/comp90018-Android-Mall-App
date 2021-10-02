package com.comp90018.assignment2.modules.users.favorite.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.utils.view.OvalImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * @author xiaotian li
 */
public class RvItemFavoriteProductItemImageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private final static String TAG = "imgAdapter[dev]";
    private Context context;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    public RvItemFavoriteProductItemImageAdapter(int layoutResId, @Nullable List<String> data, Context context) {
        super(layoutResId, data);
        this.context = context;

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String imagePath) {
        if (imagePath != null && imagePath.length() != 0) {
            StorageReference imgReference = storage.getReferenceFromUrl(imagePath);
            Glide.with(context)
                    .load(imgReference)
                    .into((OvalImageView) helper.getView(R.id.iv));
        } else {
            Drawable default_img = ContextCompat.getDrawable(context, R.drawable.default_image);
            helper.setImageDrawable(R.id.iv_product_first, default_img);
        }
    }
}
