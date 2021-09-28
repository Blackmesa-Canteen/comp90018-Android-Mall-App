package com.comp90018.assignment2.modules.orders.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.utils.Constants;
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
        // handle different product
        switch (productDTO.getStatus()) {
            case Constants.PUBLISHED:

                // handle static resource logic
                attachStaticDataToViews(helper, productDTO);
                // show status
                helper.setText(R.id.text_product_status,
                        "On Sell...");
                helper.setTextColor(R.id.text_product_status,
                        ContextCompat.getColor(context, R.color.blue));

                // handle dynamic button logic
                Button buttonR = (Button)helper.getView(R.id.btn_r);
                Button buttonL = (Button)helper.getView(R.id.btn_l);
                Button buttonS = (Button)helper.getView(R.id.btn_s);
                buttonR.setText("Edit");
                buttonL.setText("Pend");
                buttonS.setText("Remove");

                // go to product edit page
                // TODO go to product edit activity
                buttonR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });

                // pend the product selling
                buttonL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                // remove this item
                buttonS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                break;

            case Constants.UNDERCARRIAGE:
                // handle static resource logic
                attachStaticDataToViews(helper, productDTO);
                // show status
                helper.setText(R.id.text_product_status,
                        "Pending...");

                helper.setTextColor(R.id.text_product_status,
                        ContextCompat.getColor(context, R.color.yellow_dark));
                break;

            case Constants.SOLD_OUT:
                // handle static resource logic
                attachStaticDataToViews(helper, productDTO);
                // show status
                helper.setText(R.id.text_product_status,
                        "Sold out.");

                helper.setTextColor(R.id.text_product_status,
                        ContextCompat.getColor(context, R.color.green_dark));
                break;

            default:
                // handle static resource logic
                attachStaticDataToViews(helper, productDTO);
                // show status
                helper.setText(R.id.text_product_status,
                        "Invalid.");

                helper.setTextColor(R.id.text_product_status,
                        ContextCompat.getColor(context, R.color.grey1));

                break;
        }
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
                    .into((CircleImageView) helper.getView(R.id.iv_product_first));
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
}
