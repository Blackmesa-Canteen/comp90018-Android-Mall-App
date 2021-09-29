package com.comp90018.assignment2.modules.orders.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.product.activity.ProductDetailActivity;
import com.comp90018.assignment2.utils.Constants;
import com.comp90018.assignment2.utils.view.OvalImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.math.BigDecimal;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * adapter for published activity
 *
 * @author Xiaotian Li
 */
public class RvPublishedProductAdapter extends BaseQuickAdapter<ProductDTO, BaseViewHolder> {

    private final Context context;
    private final static String TAG = "PubedAda[dev]";
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final ProgressDialog progressDialog;

    // set it with setter after query callback
    private UserDTO currentUserDTO = null;

    public RvPublishedProductAdapter(int layoutResId, @Nullable List<ProductDTO> data, Context context) {
        super(layoutResId, data);
        this.context = context;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait");
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ProductDTO productDTO) {

        // get this dto's position
        int itemPosition = getItemPosition(productDTO);

        // get three buttons
        Button buttonR = (Button)helper.getView(R.id.btn_r);
        Button buttonL = (Button)helper.getView(R.id.btn_l);
        Button buttonS = (Button)helper.getView(R.id.btn_s);

        // attach the whole item view event
        RelativeLayout itemRoot = (RelativeLayout)helper.getView(R.id.rl_root);
        // go to product detials
        itemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserDTO != null) {
                    // go to product details activity
                    Intent goToPdtDetailsIntent = new Intent(context, ProductDetailActivity.class);
                    goToPdtDetailsIntent.putExtra("productDTO", productDTO);
                    goToPdtDetailsIntent.putExtra("userDTO", currentUserDTO);
                    context.startActivity(goToPdtDetailsIntent);
                }
            }
        });

        // handle different product
        switch (productDTO.getStatus()) {
            case Constants.PUBLISHED:

                // handle static resource logic
                attachStaticDataToViews(helper, productDTO);
                // handle different views in different status
                showViewsForPublished(helper, buttonR, buttonL, buttonS);

                // go to product edit page
                // TODO go to product edit activity
                buttonR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Go to product edit", Toast.LENGTH_SHORT).show();

                        // do not need to refresh, because
                        // it goes to another activity, so onResume will be called
                    }
                });

                // pend the product selling
                buttonL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productDTO.setStatus(Constants.UNDERCARRIAGE);
                        // change status
                        progressDialog.show();
                        db.collection(Constants.PRODUCT_COLLECTION)
                                .document(productDTO.getId())
                                .set(productDTO)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {

                                            // refresh this item view
                                            notifyItemChanged(itemPosition);
                                        } else {
                                            Log.w(TAG, "update product status err: " + task.getException());
                                        }
                                    }
                                });
                    }
                });

                // remove this item
                buttonS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productDTO.setStatus(Constants.REMOVED);
                        // change status
                        progressDialog.show();
                        db.collection(Constants.PRODUCT_COLLECTION)
                                .document(productDTO.getId())
                                .set(productDTO)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            // refresh this item view
                                            notifyItemChanged(itemPosition);
                                        } else {
                                            Log.w(TAG, "update product status err: " + task.getException());
                                        }
                                    }
                                });
                    }
                });

                break;

            case Constants.UNDERCARRIAGE:
                // handle static resource logic
                attachStaticDataToViews(helper, productDTO);
                // handle different views in different status
                showViewsForPending(helper, buttonR, buttonL, buttonS);

                // go to product edit page
                // TODO go to product edit activity
                buttonR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Go to product edit", Toast.LENGTH_SHORT).show();

                        // do not need to refresh, because
                        // it goes to another activity, so onResume will be called
                    }
                });

                // republish the product selling
                buttonL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productDTO.setStatus(Constants.PUBLISHED);
                        // change status
                        progressDialog.show();
                        db.collection(Constants.PRODUCT_COLLECTION)
                                .document(productDTO.getId())
                                .set(productDTO)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {

                                            // refresh this item view
                                            notifyItemChanged(itemPosition);
                                        } else {
                                            Log.w(TAG, "update product status err: " + task.getException());
                                        }
                                    }
                                });
                    }
                });

                // remove this item
                buttonS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productDTO.setStatus(Constants.REMOVED);
                        // change status
                        progressDialog.show();
                        db.collection(Constants.PRODUCT_COLLECTION)
                                .document(productDTO.getId())
                                .set(productDTO)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            // refresh this item view
                                            notifyItemChanged(itemPosition);
                                        } else {
                                            Log.w(TAG, "update product status err: " + task.getException());
                                        }
                                    }
                                });
                    }
                });
                break;

            case Constants.SOLD_OUT:
                // handle static resource logic
                attachStaticDataToViews(helper, productDTO);
                // handle different views in different status
                showViewsForSoldOut(helper, buttonR, buttonL, buttonS);

                // go to product edit page
                // TODO go to order details activity
                buttonR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Go to order details", Toast.LENGTH_SHORT).show();
                    }
                });

                // gone button does nothing
                buttonL.setOnClickListener(null);
                buttonS.setOnClickListener(null);

                break;

            default:
                // handle static resource logic
                attachStaticDataToViews(helper, productDTO);
                showViewsForRemoved(helper, buttonR, buttonL, buttonS);

                // gone button does nothing
                buttonR.setOnClickListener(null);
                buttonL.setOnClickListener(null);
                buttonS.setOnClickListener(null);
                break;
        }
    }

    private void showViewsForPublished(@NonNull BaseViewHolder helper, Button buttonR, Button buttonL, Button buttonS) {
        // show status
        helper.setText(R.id.text_product_status,
                "On Sell...");
        helper.setTextColor(R.id.text_product_status,
                ContextCompat.getColor(context, R.color.blue));

        // handle dynamic button logic
        buttonR.setVisibility(View.VISIBLE);
        buttonR.setText("Edit");
        buttonL.setVisibility(View.VISIBLE);
        buttonL.setText("Pend");
        buttonS.setVisibility(View.VISIBLE);
        buttonS.setText("Remove");
    }

    private void showViewsForPending(@NonNull BaseViewHolder helper, Button buttonR, Button buttonL, Button buttonS) {
        // show status
        helper.setText(R.id.text_product_status,
                "Pending...");
        helper.setTextColor(R.id.text_product_status,
                ContextCompat.getColor(context, R.color.yellow_dark));

        // handle dynamic button logic
        buttonR.setVisibility(View.VISIBLE);
        buttonR.setText("Edit");
        buttonL.setVisibility(View.VISIBLE);
        buttonL.setText("Republish");
        buttonS.setVisibility(View.VISIBLE);
        buttonS.setText("Remove");
    }

    private void showViewsForRemoved(@NonNull BaseViewHolder helper, Button buttonR, Button buttonL, Button buttonS) {
        // show status
        helper.setText(R.id.text_product_status,
                "Removed.");

        helper.setTextColor(R.id.text_product_status,
                ContextCompat.getColor(context, R.color.grey1));

        // handle dynamic button logic
        buttonR.setVisibility(View.GONE);
        buttonL.setVisibility(View.GONE);
        buttonS.setVisibility(View.GONE);
    }

    private void showViewsForSoldOut(@NonNull BaseViewHolder helper, Button buttonR, Button buttonL, Button buttonS) {
        // show status
        helper.setText(R.id.text_product_status,
                "Sold out.");
        helper.setTextColor(R.id.text_product_status,
                ContextCompat.getColor(context, R.color.green_dark));

        // handle dynamic button logic
        buttonR.setVisibility(View.VISIBLE);
        buttonR.setText("Details");
        buttonL.setVisibility(View.GONE);
        buttonS.setVisibility(View.GONE);
    }

    private void attachStaticDataToViews(@NonNull BaseViewHolder helper, ProductDTO productDTO) {
        // set up image
        List<String> image_address = productDTO.getImage_address();
        if (image_address != null && image_address.get(0) != null) {
            // storage Reference of firebase
            StorageReference imgReference = storage.getReferenceFromUrl(image_address.get(0));
            // query image with the reference, then show avatar
            Glide.with(context)
                    .load(imgReference)
                    .into((OvalImageView) helper.getView(R.id.iv_product_first));
        } else {
            Drawable default_img = ContextCompat.getDrawable(context, R.drawable.default_image);
            helper.setImageDrawable(R.id.iv_product_first, default_img);
        }

        // set description
        String descriptionCut;
        if (productDTO.getDescription().length() > 20) {
            descriptionCut = productDTO.getDescription().substring(0, 20) + "...";
        } else {
            descriptionCut = productDTO.getDescription();
        }
        helper.setText(R.id.text_product_desc_cut, descriptionCut);

        // set price
        // set Price
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
        if (productDTO.getCurrency().equals(Constants.AUS_DOLLAR)) {
            formattedPriceText = "$" + formattedPriceText;
        } else {
            formattedPriceText = "$" + formattedPriceText;
        }
        helper.setText(R.id.text_item_price, formattedPriceText);
    }

    public void setCurrentUserDTO(UserDTO currentUserDTO) {
        this.currentUserDTO = currentUserDTO;
    }
}
