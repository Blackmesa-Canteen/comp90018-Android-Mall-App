package com.comp90018.assignment2.modules.publish.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * @author Ziyuan Xu
 */
public class PictureCollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "PictureCollectionAdapter";
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<LocalMedia> collection;

    public PictureCollectionAdapter(Context mContext, List<LocalMedia> collection) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        if (collection.size() > 0) {
            this.collection = collection;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PictureCollectionAdapter.PictureCollectionViewHolder(mLayoutInflater.inflate(R.layout.item_picture_gallery, null), mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PictureCollectionViewHolder pictureCollectionViewHolder = (PictureCollectionViewHolder) holder;
        pictureCollectionViewHolder.setData(collection, position);
    }

    @Override
    public int getItemCount() {
        return collection.size();
    }

    class PictureCollectionViewHolder extends RecyclerView.ViewHolder {
        private final Context mContext;
        private final ImageView pg_image;

        public PictureCollectionViewHolder(@NonNull View itemView, final Context mContext) {
            super(itemView);
            this.mContext = mContext;
            pg_image = itemView.findViewById(R.id.pg_image);
        }
        public void setData(List<LocalMedia> collection, final int position) {
            Glide.with(mContext).load(collection.get(position).getPath()).into(pg_image);
        }
    }
}
