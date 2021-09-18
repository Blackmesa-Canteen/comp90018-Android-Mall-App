package com.comp90018.assignment2.modules.product;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.search.adapter.SearchResultRvAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.comp90018.assignment2.utils.view.OvalImageView;
import com.donkingliang.labels.LabelsView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDetailAdapter extends RecyclerView.Adapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> productDTOList;
    private FirebaseStorage storage;

    public ProductDetailAdapter(Context ctx, List<String> productDTOList) {

        this.context = ctx;
        this.productDTOList = productDTOList;
        storage = FirebaseStorage.getInstance();
        layoutInflater = LayoutInflater.from(context);

    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // only one type of view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_images, parent, false);
        return new ProductDetailAdapter.ItemProductInfoViewHolder(context, view);
    }

    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ProductDetailAdapter.ItemProductInfoViewHolder viewHolder = (ProductDetailAdapter.ItemProductInfoViewHolder) holder;
        viewHolder.setData(productDTOList, position);
    }

    public int getItemCount() {
        return productDTOList.size();

    }

    /**
     * ViewHolder for items
     */
    private class ItemProductInfoViewHolder extends RecyclerView.ViewHolder {

        private Context context;

        private OvalImageView imgProductImage;


        public ItemProductInfoViewHolder(Context context, View inflate) {
            super(inflate);
            this.context = context;

            // bind views
            imgProductImage = (OvalImageView) inflate.findViewById(R.id.item_image);

        }
            public void setData(List<String> productDTOList, final int position) {

                StorageReference imgReference = storage.getReferenceFromUrl(productDTOList.get(position));

            // query image with the reference
            Glide.with(context)
                    .load(imgReference)
                    .into(imgProductImage);
        }
    }
}

