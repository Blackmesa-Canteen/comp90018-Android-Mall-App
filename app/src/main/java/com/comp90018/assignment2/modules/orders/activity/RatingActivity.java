package com.comp90018.assignment2.modules.orders.activity;

import static com.comp90018.assignment2.utils.Constants.ORDERS_COLLECTION;
import static com.comp90018.assignment2.utils.Constants.USERS_COLLECTION;
import java.math.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;

import com.comp90018.assignment2.databinding.ActivityRatingBinding;
import com.comp90018.assignment2.dto.OrderDTO;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
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
        DocumentReference sellerDocReference = orderDTO.getSeller_ref(); //get buyer info
        DocumentReference currentUserReference = db.collection(USERS_COLLECTION).document(firebaseAuth.getCurrentUser().getUid());//get current user info
        DocumentReference buyerDocReference = orderDTO.getBuyer_ref();
        String seller_id = sellerDocReference.getId();//get buyer id
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
                float rating=ratingBar.getRating();//get current star rating
                orderDTO.setStatus(3);  //set the status successfully comment
                number_of_comment_order=sellerDTO.getSold_number();
                current_rating= userDTO.getStar_number();

                //calculate the average rating
                star_number=((rating+4.0)/2);

                //update status and star number
                db.collection(ORDERS_COLLECTION)
                        .document(orderDTO.getId())
                        .update("status", Constants.SUCCESSFUL_COMMENT);
                db.collection(USERS_COLLECTION)
                        .document(userDTO.getId())
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
