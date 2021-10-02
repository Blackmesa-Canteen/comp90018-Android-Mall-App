package com.comp90018.assignment2.modules.orders.activity;

import static com.comp90018.assignment2.utils.Constants.USERS_COLLECTION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.widget.TextView;
import com.google.firebase.Timestamp;
import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.databinding.ActivityOrderDetailBinding;
import com.comp90018.assignment2.databinding.ActivityProductDetailBinding;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.dto.OrderDTO;
import com.comp90018.assignment2.modules.product.activity.ProductDetailActivity;
import com.comp90018.assignment2.modules.users.me.activity.EditProfileActivity;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luck.picture.lib.tools.ToastUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import me.leefeng.promptlibrary.PromptDialog;

public class OrderDetailActivity extends AppCompatActivity {
    private static final String TAG = "[dev]OrderDetail";
    private ActivityOrderDetailBinding binding;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private UserDTO currentUserDTO;
    private Context context;
    private PromptDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new PromptDialog(this);





        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // retrieve bundle data from intent li xiaotian
        Intent intent = getIntent();
        ProductDTO productDTO = (ProductDTO) intent.getParcelableExtra("productDTO");
        UserDTO userDTO = (UserDTO) intent.getParcelableExtra("userDTO");
        OrderDTO orderDTO = (OrderDTO) intent.getParcelableExtra("orderDTO");
        switch (orderDTO.getStatus()) {
            case Constants.WAITING_DELIVERY:
                binding.orderDetailOrderStatus.setText("Waiting delivery");
                break;
            case Constants.DELIVERING:
                binding.orderDetailOrderStatus.setText("Delivering");
                break;
            case Constants.SUCCESSFUL_NOT_COMMENT:
                binding.orderDetailOrderStatus.setText("Successful not comment");
                break;
            case Constants.SUCCESSFUL_COMMENT:
                binding.orderDetailOrderStatus.setText("Successful comment");
                break;
            case Constants.CANCELED:
                binding.orderDetailOrderStatus.setText("Canceled");
                break;
            case Constants.ON_REFUND:
                binding.orderDetailOrderStatus.setText("On refund");
                break;
            case Constants.ON_REFUND_DELIVERING:
                binding.orderDetailOrderStatus.setText("On refund delivering");
                break;
            case Constants.REFUNDED:
                binding.orderDetailOrderStatus.setText("Refunded");
                break;
        }
        binding.orderDetailDeliveryStatus.setText(orderDTO.getTracking_info().toString());
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
            Glide.with(OrderDetailActivity.this)
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
        binding.orderDetailTotalPrice.setText(formattedPriceText);
        binding.orderDetailReceiverName.setText(userDTO.getNickname());
        binding.orderDetailReceiverEmail.setText(userDTO.getEmail());
        binding.orderDetailReceiverAddr.setText(userDTO.getLocation_text().toString());


        db.collection(USERS_COLLECTION)
                .document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot result = task.getResult();
                            currentUserDTO = result.toObject(UserDTO.class);

                            // show current user's avatar
                            binding.orderDetailSellerUsername.setText(currentUserDTO.getNickname());

                        } else {
                            Log.e(TAG, "Get current user dto error");
                        }
                    }
                });
        binding.orderDetailId.setText(orderDTO.getId());


        String format =  "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Timestamp time = orderDTO.getCreated_time();
        binding.orderDetailTransactionDate.setText(time.toDate().toString());





        binding.orderDetailBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
}