package com.comp90018.assignment2.modules.categories.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.CategoryDTO;
import com.comp90018.assignment2.dto.SubCategoryDTO;

import java.util.ArrayList;

public class CategoryRightAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<SubCategoryDTO> subcategories;
    private final LayoutInflater mLayoutInflater;

    public CategoryRightAdapter(Context mContext, ArrayList<SubCategoryDTO> subcategories) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        if (subcategories.size() > 0) {
            this.subcategories = subcategories;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubcategoryViewHolder(mLayoutInflater.inflate(R.layout.item_subcategories_item, null), mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SubcategoryViewHolder subcategoryViewHolder = (SubcategoryViewHolder) holder;
        subcategoryViewHolder.setData(subcategories.get(position - 1), position - 1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return subcategories.size();
    }

    class SubcategoryViewHolder extends RecyclerView.ViewHolder  {
        private Context mContext;
        private TextView sct_title;
        private ImageView sct_image;

        public SubcategoryViewHolder(@NonNull View itemView,  final Context mContext) {
            super(itemView);
            this.mContext = mContext;
//            sct_image = (ImageView) itemView.findViewById(R.id.sct_image)
            sct_title = (TextView) itemView.findViewById(R.id.sct_title);
        }

        public void setData(SubCategoryDTO subcategory, final int position) {
//            Glide.with(mContext)
//                    .load()
//                    .into(sct_image);
            sct_title.setText(subcategory.getName());
        }
    }
}
