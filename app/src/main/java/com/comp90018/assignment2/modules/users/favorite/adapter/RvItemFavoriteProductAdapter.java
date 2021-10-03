package com.comp90018.assignment2.modules.users.favorite.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.product.activity.ProductDetailActivity;
import com.comp90018.assignment2.modules.users.favorite.activity.FavoriteProductActivity;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author xiaotian li
 */
public class RvItemFavoriteProductAdapter extends BaseQuickAdapter<ProductDTO, BaseViewHolder> {
    private final static String TAG = "itemAdapter[dev]";
    private Context context;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    private UserDTO currentUserDTO;

    public RvItemFavoriteProductAdapter(int layoutResId, @Nullable List<ProductDTO> data, UserDTO currentUserDTO, Context context) {
        super(layoutResId, data);
        this.context = context;
        this.currentUserDTO = currentUserDTO;

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ProductDTO productDTO) {

        // render product info
        helper.setText(R.id.text_product_desc, productDTO.getDescription());
        helper.setText(R.id.text_item_price, "$" + productDTO.getPrice());

        helper.setVisible(R.id.text_sold_out, productDTO.getStatus() == Constants.SOLD_OUT);

        // rend product images
        List<String> image_address_list = productDTO.getImage_address();
        RecyclerView view = (RecyclerView) helper.getView(R.id.rv_product_imgs);
        RvItemFavoriteProductItemImageAdapter adapter =
                new RvItemFavoriteProductItemImageAdapter(
                        R.layout.item_rv_img_favorite_product,
                        image_address_list,
                        context
                );
        view.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        view.setLayoutManager(layoutManager);

        // handle button logic
        Button cancelBtn = (Button) helper.getView(R.id.cancel_button);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentUserId = currentUserDTO.getId();
                String productId = productDTO.getId();

                DocumentReference pdtRef =
                        db.collection(Constants.PRODUCT_COLLECTION)
                                .document(productId);

                currentUserDTO.getFavorite_refs().remove(pdtRef);

                db.collection(Constants.USERS_COLLECTION)
                        .document(currentUserId)
                        .set(currentUserDTO);

                cancelBtn.setText("Unfavorited");
                cancelBtn.setEnabled(false);
            }
        });

        // query user info
        DocumentReference owner_ref = productDTO.getOwner_ref();
        owner_ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    UserDTO userDTO = task.getResult().toObject(UserDTO.class);
                    // TODO : may cause exception?
                    helper.setText(R.id.text_nickname, userDTO.getNickname());
                    // load avatar
                    StorageReference imgReference = storage.getReferenceFromUrl(userDTO.getAvatar_address());
                    // query image with the reference, then show avatar
                    Glide.with(context)
                            .load(imgReference)
                            .into((CircleImageView) helper.getView(R.id.iv_avatar));

                    // set click event jump to user detail page
                    helper.getView(R.id.ll_user_title).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO go to user details page
                            Toast.makeText(context, "go to user detail", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // click event jump to product detail activity
                    View.OnClickListener goToPdtDetailEvent = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // go to product details activity
                            if (productDTO.getStatus() == Constants.PUBLISHED) {
                                Intent goToPdtDetailIntent = new Intent(context, ProductDetailActivity.class);
                                goToPdtDetailIntent.putExtra("productDTO", productDTO);
                                goToPdtDetailIntent.putExtra("userDTO", userDTO);
                                context.startActivity(goToPdtDetailIntent);
                            } else {
                                Toast.makeText(context, "This item is sold out.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    helper.getView(R.id.text_product_desc).setOnClickListener(goToPdtDetailEvent);
                    helper.getView(R.id.text_item_price).setOnClickListener(goToPdtDetailEvent);
                    helper.getView(R.id.rv_product_imgs).setOnClickListener(goToPdtDetailEvent);

                } else {
                    Log.w(TAG, "query db failed", task.getException());
                }
            }
        });
    }
}
