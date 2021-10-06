package com.comp90018.assignment2.modules.orders.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ziyuan Xu
 */
public class PictureCollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "PictureCollectionAdapter";
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<String> images;
    private FirebaseStorage firebaseStorage;

    public PictureCollectionAdapter(Context mContext, ArrayList<String> images) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        if (images.size() > 0) {
            this.images = images;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        firebaseStorage = FirebaseStorage.getInstance();
        return new PictureCollectionViewHolder(mLayoutInflater.inflate(R.layout.item_picture_gallery, null), mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PictureCollectionViewHolder pictureCollectionViewHolder = (PictureCollectionViewHolder) holder;
        pictureCollectionViewHolder.setData(images, position);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
    class PictureCollectionViewHolder extends RecyclerView.ViewHolder {
        private final Context mContext;
        private final ImageView pg_image;

        public PictureCollectionViewHolder(@NonNull View itemView, final Context mContext) {
            super(itemView);
            this.mContext = mContext;
            pg_image = itemView.findViewById(R.id.pg_image);
        }
        public void setData(ArrayList<String> images, final int position) {
            StorageReference imgReference = firebaseStorage.getReferenceFromUrl(images.get(position));
            Glide.with(mContext).load(imgReference).override(80, 80)
                    .centerCrop().into(pg_image);
        }
    }
}
