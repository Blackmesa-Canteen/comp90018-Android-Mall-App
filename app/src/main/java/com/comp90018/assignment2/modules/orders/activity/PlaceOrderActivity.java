package com.comp90018.assignment2.modules.orders.activity;

import static com.comp90018.assignment2.utils.Constants.ORDERS_COLLECTION;
import static com.comp90018.assignment2.utils.Constants.PRODUCT_COLLECTION;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.comp90018.assignment2.dto.OrderDTO;
import com.comp90018.assignment2.modules.product.activity.ProductDetailActivity;
import com.comp90018.assignment2.modules.publish.activity.PublishProductActivity;
import com.google.firebase.Timestamp;
import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityPlaceOrderBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import lombok.Builder;

public class PlaceOrderActivity extends AppCompatActivity {
    private static final String TAG = "[dev]PlaceOrder";
    private ActivityPlaceOrderBinding binding;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        binding = ActivityPlaceOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        ProductDTO productDTO = (ProductDTO) intent.getParcelableExtra("productDTO");
        UserDTO userDTO = (UserDTO) intent.getParcelableExtra("userDTO");
        UserDTO currentUserDTO = (UserDTO) intent.getParcelableExtra("currentUserDTO");
        if (productDTO.getImage_address() == null
                || productDTO.getImage_address().get(0) == null
                || productDTO.getImage_address().get(0).equals("")
                || productDTO.getImage_address().get(0).equals("default")
                || productDTO.getImage_address().get(0).equals("gs://comp90018-mobile-caa7c.appspot.com/public/default.png")) {
            binding.placeOrderPdtImgOvalImageView.setImageResource(R.drawable.default_image);
        } else {
            // only show the first img
            // storage Reference of firebase
            StorageReference imgReference = storage.getReferenceFromUrl(productDTO.getImage_address().get(0));

            // query image with the reference
            Glide.with(PlaceOrderActivity.this)
                    .load(imgReference)
                    .into(binding.placeOrderPdtImgOvalImageView);
        }
        String descriptionCut;
        if (productDTO.getDescription().length() > 33) {
            descriptionCut = productDTO.getDescription().substring(0, 33) + "...";
        } else {
            descriptionCut = productDTO.getDescription();
        }
        binding.placeOrderPdtName.setText(descriptionCut);
        binding.placeOrderReceiverName.setText(currentUserDTO.getNickname());
        binding.placeOrderReceiverEmail.setText(currentUserDTO.getEmail());
        double price = productDTO.getPrice();
        String formattedPriceText;

        // Use BigDecimal to round, reserving one decimal place
        price = new BigDecimal(price).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (price < 1000) {
            formattedPriceText = String.valueOf(price);
        } else {
            // e.g 8750 to 8.8k
            int numThousands = new BigDecimal((int) (price / 1000)).setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
            formattedPriceText = String.valueOf(numThousands) + "k";
        }
        // currency info
        if (productDTO.getCurrency() == Constants.AUS_DOLLAR) {
            formattedPriceText = "$" + formattedPriceText;
        } else {
            formattedPriceText = "$" + formattedPriceText;
        }
        binding.placeOrderPdtPrice.setText(formattedPriceText);
        binding.placeOrderCheckoutPrice.setText(formattedPriceText);


        //create order
        binding.placeOrderPdtDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.placeOrderReceiverAddr.setHint("insert receiver's address");
                String address = binding.placeOrderReceiverAddr.getText().toString();
                String addressRegex = "^[a-zA-Z0-9 !?,.;@\"'()-]{0,40}$";
                // No password regex, because it is login
                if (!address.matches(addressRegex)) {
                    new AlertDialog.Builder(PlaceOrderActivity.this).setMessage("address should be letters, numbers, common symbols and space").setPositiveButton("ok", null).show();
                    return;
                }
                DocumentReference buyerRef = db.collection(Constants.USERS_COLLECTION).document(currentUserDTO.getId());
                DocumentReference productRef = db.collection(PRODUCT_COLLECTION).document(productDTO.getId());
                DocumentReference sellertRef = db.collection(Constants.USERS_COLLECTION).document(userDTO.getId());
                String seller_address = userDTO.getLocation_text().toString();
                Integer star_number = productDTO.getStar_number().intValue();
                String track_id = getTrackId();
                OrderDTO newOrderDTO = OrderDTO.builder()
                        .address(address)
                        .buyer_ref(buyerRef)
                        .created_time(Timestamp.now())
                        .product_ref(productRef)
                        .seller_address(seller_address)
                        .seller_ref(sellertRef)
                        .star_number(star_number)
                        .status(0)
                        .tracking_id(track_id)
                        .tracking_info("sent")
                        .build();
                db.collection(PRODUCT_COLLECTION)
                        .document(productDTO.getId())
                        .update("status", Constants.SOLD_OUT);
                publish(newOrderDTO);
            }
        });

        binding.placeOrderBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void publish(OrderDTO orderDTO){
        ProgressDialog publishProgressDialog = new ProgressDialog(PlaceOrderActivity.this);
        publishProgressDialog.setTitle("Creating Order...");
        publishProgressDialog.setMessage("Please wait");
        db.collection(Constants.ORDERS_COLLECTION)
                .add(orderDTO)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Order created with ID: "+ documentReference.getId());
                    db.collection(Constants.ORDERS_COLLECTION)
                            .document(documentReference.getId())
                            .update("id", documentReference.getId());
                    // finish the activity
                    publishProgressDialog.dismiss();
                    finish();
                    Intent publishProductIntent = new Intent(PlaceOrderActivity.this, PurchasedActivity.class);
                    startActivity(publishProductIntent);
                }).addOnFailureListener(e -> {
            publishProgressDialog.dismiss();
            Log.w(TAG, "Create order:failed", e);
            new AlertDialog.Builder(PlaceOrderActivity.this)
                    .setTitle("Sorry")
                    .setMessage("Database network issue, please try again later")
                    .setPositiveButton("Ok", null).show();
        });
    }

    private String getTrackId(){
        List<String> list = Arrays.asList("SF","YT","ZT");

        int index = (int) (Math.random()* list.size());
        String value = list.get(index);
        Random random = new Random();
        for (int i = 0; i < 13; i++) {
            value += String.valueOf(random.nextInt(10));
        }

        return value;
    }
}