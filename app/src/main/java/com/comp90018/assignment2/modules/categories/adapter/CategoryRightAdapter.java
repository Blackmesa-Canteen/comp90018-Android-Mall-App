package com.comp90018.assignment2.modules.categories.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.SubCategoryDTO;
import com.comp90018.assignment2.modules.search.activity.SearchResultActivity;
import com.comp90018.assignment2.utils.Constants;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

/**
 * @author Ziyuan Xu
 */
public class CategoryRightAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    FirebaseFirestore db;
    private Context mContext;
    private ArrayList<SubCategoryDTO> subcategories;
    private final LayoutInflater mLayoutInflater;
    private FirebaseStorage storage;
    private final static String TAG = "CategoryRightAdapter";

    public CategoryRightAdapter(Context mContext, ArrayList<SubCategoryDTO> subcategories) {
        this.mContext = mContext;
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        mLayoutInflater = LayoutInflater.from(mContext);
        if (subcategories.size() > 0) {
            this.subcategories = subcategories;
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubcategoryViewHolder(mLayoutInflater.inflate(R.layout.item_subcategories_item, null), mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SubcategoryViewHolder subcategoryViewHolder = (SubcategoryViewHolder) holder;
        subcategoryViewHolder.setData(subcategories, position);
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

        public void setData(ArrayList<SubCategoryDTO> subcategories, final int position) {
            SubCategoryDTO subcategory = subcategories.get(position);
            StorageReference subcategoryReference = storage.getReferenceFromUrl(subcategory.getImage_address());
            Glide.with(mContext)
                    .load(subcategoryReference)
                    .into(sct_image);
            sct_title.setText(subcategory.getName());
            itemView.setOnClickListener(v -> {
                DocumentReference ref = db.document("sub_categories/"+subcategory.getSubcategory_id());
                db.collection(Constants.PRODUCT_COLLECTION)
                        .whereEqualTo("sub_category_ref", ref)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                ArrayList<ProductDTO> productDTOList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    ProductDTO product = document.toObject(ProductDTO.class);
                                    productDTOList.add(product);
                                }
                                // start result activity
                                Intent goToSearchResultIntent = new Intent(mContext, SearchResultActivity.class);
                                goToSearchResultIntent.putParcelableArrayListExtra("productDTOList", productDTOList);
                                mContext.startActivity(goToSearchResultIntent);
                            } else {
                                new AlertDialog.Builder(mContext).setMessage("Enter failed, please try again later.").setPositiveButton("ok", null).show();
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        });
            });
        }
    }
}
