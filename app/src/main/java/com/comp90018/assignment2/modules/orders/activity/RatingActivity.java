package com.comp90018.assignment2.modules.orders.activity;

import static android.content.ContentValues.TAG;
import static com.comp90018.assignment2.utils.Constants.ORDERS_COLLECTION;
import static com.comp90018.assignment2.utils.Constants.PRODUCT_COLLECTION;
import static com.comp90018.assignment2.utils.Constants.SUCCESSFUL_COMMENT;
import static com.comp90018.assignment2.utils.Constants.USERS_COLLECTION;
import java.math.*;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;

import com.comp90018.assignment2.databinding.ActivityRatingBinding;
import com.comp90018.assignment2.dto.OrderDTO;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.users.me.activity.EditProfileActivity;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;


/**
 * @author Zhonghui Jiang
 */

public class RatingActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private double current_rating;
    private double star_number;
    private int number_of_comment_order;

    List<OrderDTO> orderDTOList;

    private ActivityRatingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        ratingBar=findViewById(R.id.ratingbar);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        binding = ActivityRatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent intent = getIntent();
        ProductDTO productDTO = (ProductDTO) intent.getParcelableExtra("productDTO");
        UserDTO sellerDTO = (UserDTO) intent.getParcelableExtra("sellerDTO");
        OrderDTO orderDTO = (OrderDTO) intent.getParcelableExtra("orderDTO");
        UserDTO buyerDTO = (UserDTO) intent.getParcelableExtra("buyerDTO");
        UserDTO userDTO = (UserDTO) intent.getParcelableExtra("userDTO");
        DocumentReference sellerDocReference = orderDTO.getSeller_ref();
        DocumentReference currentUserReference = db.collection(USERS_COLLECTION).document(firebaseAuth.getCurrentUser().getUid());//get current user info
        DocumentReference buyerDocReference = orderDTO.getBuyer_ref();
        String seller_id = sellerDocReference.getId();
        String current_user_id = currentUserReference.getId(); //get current user id
        String buyer_id  = buyerDocReference.getId();
        ProductDTO finalProductDTO = productDTO;
        UserDTO finalUserDTO = userDTO;
        UserDTO finalBuyerDTO = buyerDTO;
        UserDTO finalSellerDTO = sellerDTO;



        //click ratingbar
        binding.ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {


            @Override

            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                Toast.makeText(RatingActivity.this, "Rate" + rating , Toast.LENGTH_SHORT).show();
//                update order star number
                orderDTO.setStar_number((int) rating);
                db.collection(ORDERS_COLLECTION)
                        .document(orderDTO.getId())
                        .update("star_number",rating);



            }

        });


        binding.orderDetailBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("orderDTO", orderDTO);
                finish();
            }
        });



        binding.btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number_of_comment_order+=1;
                orderDTO.setStatus(3);  //set the status successfully comment
                float rating=orderDTO.getStar_number();
                current_rating= sellerDTO.getStar_number();
                orderDTOList = new ArrayList<>();
                //update order status
                db.collection(ORDERS_COLLECTION)
                        .document(orderDTO.getId())
                        .update("status", Constants.SUCCESSFUL_COMMENT);
                Log.d("RatingActivity", "onClick: "+number_of_comment_order);
                Log.d("RatingActivity", "onClick: "+rating);
                Log.d("RatingActivity", "onClick: "+current_rating);


//                db.collection(Constants.ORDERS_COLLECTION)
//                        .whereEqualTo("status", SUCCESSFUL_COMMENT)
//                        .whereEqualTo("seller_ref", sellerDocReference)
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    orderDTOList = new ArrayList<>();
//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        OrderDTO orderDTO = document.toObject(OrderDTO.class);
//                                        orderDTOList.add(orderDTO);
//                                    }
//                                }
//                            }
//                        });
//
//                number_of_comment_order=orderDTOList.size();



                //calculate the average rating
                star_number=((rating+current_rating)/((number_of_comment_order+1)));
                //update star number
                db.collection(USERS_COLLECTION)
                        .document(sellerDTO.getId())
                        .update("star_number",star_number );






                //after submitting, jump to detail page
                Intent intent = new Intent(RatingActivity.this, OrderDetailActivity.class);
                intent.putExtra("productDTO", finalProductDTO);
                intent.putExtra("buyerDTO", finalBuyerDTO);
                intent.putExtra("sellerDTO", finalSellerDTO);
                intent.putExtra("orderDTO", orderDTO);
                intent.putExtra("userDTO", finalUserDTO);
                startActivity(intent);


            }
        });
        binding.orderDetailDeliveryTrackingId.setText(orderDTO.getTracking_id().toString());
        if (productDTO.getImage_address() == null
                || productDTO.getImage_address().get(0) == null
                || productDTO.getImage_address().get(0).equals("")
                || productDTO.getImage_address().get(0).equals("default")
                || productDTO.getImage_address().get(0).equals("gs://comp90018-mobile-caa7c.appspot.com/public/default.png")) {
            binding.orderDetailPdtImg.setImageResource(R.drawable.default_image);
        } else {
            // only show the first img
            // storage Reference of firebase
            StorageReference imgReference = storage.getReferenceFromUrl(productDTO.getImage_address().get(0));

            // query image with the reference
            Glide.with(RatingActivity.this)
                    .load(imgReference)
                    .into(binding.orderDetailPdtImg);
        }
        String descriptionCut;
        if (productDTO.getDescription().length() > 33) {
            descriptionCut = productDTO.getDescription().substring(0, 33) + "...";
        } else {
            descriptionCut = productDTO.getDescription();
        }
        binding.orderDetailPdtName.setText(descriptionCut);

    }
    @Override
    public void onResume() {
        super.onResume();

    }
}
