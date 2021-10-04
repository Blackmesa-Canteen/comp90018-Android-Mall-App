package com.comp90018.assignment2.modules.users.favorite.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityFavoriteProductBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.users.favorite.adapter.RvItemFavoriteProductAdapter;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import me.leefeng.promptlibrary.PromptDialog;

/**
 * favorite pdts:
 * <p>
 * require input current UserDTO with DATA_A
 *
 * @author xiaotian li
 */
public class FavoriteProductActivity extends AppCompatActivity {

    private final static String TAG = "FollAct[dev]";
    private ActivityFavoriteProductBinding binding;

    private UserDTO currentUserDTO;

    private List<ProductDTO> favoriteProductList;

    private RvItemFavoriteProductAdapter adapter;

    private PromptDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set up view binding
        // init view binding
        binding = ActivityFavoriteProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // attach to layout file
        setContentView(view);

        // get current User DTO from bundle
        currentUserDTO = getIntent().getParcelableExtra(Constants.DATA_A);

        if (currentUserDTO == null) {
            Log.w(TAG, "empty inputing current user dto");
            finish();
        }

        favoriteProductList = new ArrayList<>();
        dialog = new PromptDialog(this);

        // set up exit
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // get all favorite products
        List<DocumentReference> favorite_product_refs = currentUserDTO.getFavorite_refs();

        if (favorite_product_refs == null || favorite_product_refs.size() == 0) {
            binding.textNothing.setVisibility(View.VISIBLE);
            binding.rvProducts.setVisibility(View.GONE);
        } else {
            binding.rvProducts.setVisibility(View.VISIBLE);

            adapter = new RvItemFavoriteProductAdapter(R.layout.item_rv_favorite_product,
                    favoriteProductList,
                    currentUserDTO,
                    this
            );

            binding.rvProducts.setAdapter(adapter);
            binding.rvProducts.setLayoutManager(new LinearLayoutManager(this));

            // query db to fill adapter
            for (DocumentReference pdtRef : favorite_product_refs) {
                pdtRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            ProductDTO productDTO = task.getResult().toObject(ProductDTO.class);

                            // check the productDTO status
                            // only shows published or sold out product
                            if (productDTO != null
                                    && (productDTO.getStatus() == Constants.PUBLISHED
                                    || productDTO.getStatus() == Constants.SOLD_OUT)) {

                                favoriteProductList.add(productDTO);
                                int currentSize = favoriteProductList.size();
                                adapter.notifyItemInserted(currentSize - 1);
                            }

                        } else {
                            dialog.showWarn("Please try again");
                            Log.w(TAG, "db query failed: " + task.getException());
                        }
                    }
                });
            }
        }
    }
}