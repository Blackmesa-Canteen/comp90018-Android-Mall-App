package com.comp90018.assignment2.modules.categories.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.SubCategoryDTO;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class CategoryRightAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<SubCategoryDTO> subcategories;
    private final LayoutInflater mLayoutInflater;
    private FirebaseStorage storage;

    public CategoryRightAdapter(Context mContext, ArrayList<SubCategoryDTO> subcategories) {
        this.mContext = mContext;
        storage = FirebaseStorage.getInstance();
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
        //TODO: add Layout to organize subcategories
        SubcategoryViewHolder subcategoryViewHolder = (SubcategoryViewHolder) holder;
        subcategoryViewHolder.setData(subcategories.get(0));
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
            sct_image = (ImageView) itemView.findViewById(R.id.sct_image);
            sct_title = (TextView)itemView.findViewById(R.id.sct_title);
        }

        public void setData(SubCategoryDTO subcategory) {
            StorageReference subcategoryReference = storage.getReferenceFromUrl(subcategory.getImage_address());
            Glide.with(mContext)
                    .load(subcategoryReference)
                    .into(sct_image);
            sct_title.setText(subcategory.getName());
        }
    }
}
