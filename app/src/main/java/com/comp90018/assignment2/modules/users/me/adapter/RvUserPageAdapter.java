package com.comp90018.assignment2.modules.users.me.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.NonNull;

import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.comp90018.assignment2.modules.product.activity.ProductDetailActivity;
import com.bumptech.glide.Glide;
import com.comp90018.assignment2.utils.Constants;
import com.comp90018.assignment2.utils.view.OvalImageView;
import com.donkingliang.labels.LabelsView;
import com.google.firebase.storage.StorageReference;

import java.math.BigDecimal;
import java.util.ArrayList;


/**
 * @author Zhonghui Jiang
 */

public class RvUserPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "RvUserPageAdapter[dev]";
    private Context context;
    private LayoutInflater layoutInflater;

    private List<ProductDTO> productDTOList;
    private Map<DocumentReference, UserDTO> userDTOMap;

    private FirebaseStorage storage;
    private FirebaseFirestore db;


    private UserDTO currentUserDTO = null;

    public RvUserPageAdapter(@Nullable List<ProductDTO> productDTOList, Context context) {
        storage = FirebaseStorage.getInstance();
        this.context = context;
        this.productDTOList = productDTOList;
        this.userDTOMap = new HashMap<>();
        layoutInflater = LayoutInflater.from(context);
        db = FirebaseFirestore.getInstance();
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userpage_rv, parent, false);
        return new RvUserPageAdapter.ItemProductInfoViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        RvUserPageAdapter.ItemProductInfoViewHolder viewHolder = (RvUserPageAdapter.ItemProductInfoViewHolder) holder;
        viewHolder.setData(productDTOList, userDTOMap, position);
    }

    @Override
    public int getItemCount() {
        return productDTOList.size();
    }




//    protected void convert(@NonNull RecyclerView.ViewHolder helper, ProductDTO productDTO) {
//
//
//        // attach the whole item view event
//        LinearLayout item = (LinearLayout) helper.(R.id.item);
//        // go to product details
//        item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (currentUserDTO != null) {
//                    // go to product details activity
//                    Intent goToPdtDetailsIntent = new Intent(context, ProductDetailActivity.class);
//                    goToPdtDetailsIntent.putExtra("productDTO", productDTO);
//                    goToPdtDetailsIntent.putExtra("userDTO", currentUserDTO);
//                    context.startActivity(goToPdtDetailsIntent);
//                }
//            }
//        });
//    }


        private class ItemProductInfoViewHolder extends RecyclerView.ViewHolder {

            private Context context;

            private OvalImageView imgProductImage;
            private TextView textProductDescriptionCut;
            private LinearLayout llLabels; // contains imgProductImage and textProductDescriptionCut
            private LabelsView labels;
            private LinearLayout llPricingInfo; // contains price and like
            private TextView textProductPrice;
            private TextView textLikes;


            public ItemProductInfoViewHolder(Context context, View inflate) {
                super(inflate);
                this.context = context;
                // bind views
                imgProductImage = (OvalImageView) inflate.findViewById(R.id.item_image);
                textProductDescriptionCut = (TextView) inflate.findViewById(R.id.item_description);
                llLabels = (LinearLayout) inflate.findViewById(R.id.item_labels);
                labels = (LabelsView) inflate.findViewById(R.id.item_label);
                llPricingInfo = (LinearLayout) inflate.findViewById(R.id.item_price_label);
                textProductPrice = (TextView) inflate.findViewById(R.id.item_price);
                textLikes = (TextView) inflate.findViewById(R.id.item_likes);

//                inflate.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(context, "关于", Toast.LENGTH_SHORT).show();
//                    }
//                });




            }

            public void setData(List<ProductDTO> productDTOList, Map<DocumentReference, UserDTO> userDTOMap, final int position) {
                ProductDTO productDTO = productDTOList.get(position);
                // set product img
                // if default img
                if (productDTO.getImage_address() == null
                        || productDTO.getImage_address().get(0) == null
                        || productDTO.getImage_address().get(0).equals("")
                        || productDTO.getImage_address().get(0).equals("default")
                        || productDTO.getImage_address().get(0).equals("gs://comp90018-mobile-caa7c.appspot.com/public/default.png")) {
                    imgProductImage.setImageResource(R.drawable.default_image);
                } else {
                    // only show the first img
                    // storage Reference of firebase
                    StorageReference imgReference = storage.getReferenceFromUrl(productDTO.getImage_address().get(0));

                    // query image with the reference
                    Glide.with(context)
                            .load(imgReference)
                            .into(imgProductImage);
                }

                // set product description
                String descriptionCut;
                if (productDTO.getDescription().length() > 33) {
                    descriptionCut = productDTO.getDescription().substring(0, 33) + "...";
                } else {
                    descriptionCut = productDTO.getDescription();
                }

                textProductDescriptionCut.setText(descriptionCut);
                //System.out.println("textProductDescriptionCut: "+textProductDescriptionCut);
                // set labels: brand and quality
                ArrayList<String> labelStrings = new ArrayList<>();

                String brandNameCut;
                if (productDTO.getBrand().length() > 10) {
                    brandNameCut = productDTO.getBrand().substring(0, 10) + "...";
                } else {
                    brandNameCut = productDTO.getBrand();
                }

                String qualityText;
                int qualityStatus = productDTO.getQuality();
                if (qualityStatus == Constants.HEAVILY_USED) {
                    qualityText = "Heavily Used";

                } else if (qualityStatus == Constants.WELL_USED) {
                    qualityText = "Well Used";

                } else if (qualityStatus == Constants.AVERAGE_CONDITION) {
                    qualityText = "average";

                } else if (qualityStatus == Constants.SLIGHTLY_USED) {
                    qualityText = "Slightly Used";

                } else if (qualityStatus == Constants.EXCELLENT) {
                    qualityText = "EXCELLENT";

                } else {
                    qualityText = "average";
                }

                labelStrings.add(brandNameCut);
                labelStrings.add(qualityText);
                labels.setLabels(labelStrings);

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
                textProductPrice.setText(formattedPriceText);

                // set likes
                int likes = productDTO.getFavorite_number();
                String formattedLikesText;
                if (likes < 1000) {
                    formattedLikesText = String.valueOf(likes);
                } else {
                    // e.g 8750 to 8.8k
                    int numThousands = new BigDecimal((int) (likes / 1000)).setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
                    formattedLikesText = String.valueOf(numThousands) + "k";
                }
                formattedLikesText = formattedLikesText + " likes";
                textLikes.setText(formattedLikesText);

                // set user card
                DocumentReference userDocReference = productDTO.getOwner_ref();

                UserDTO userDTO = null;
                if (userDTOMap.containsKey(userDocReference)) {
                    // get dto
                    userDTO = userDTOMap.get(userDocReference);


                }



                // toast listeners
                UserDTO finalUserDTO = userDTO;
                View.OnClickListener goToProductActivityListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ProductDetailActivity.class);
                        intent.putExtra("productDTO", productDTO);
                        intent.putExtra("userDTO", finalUserDTO);
                        context.startActivity(intent);
                        Log.d(TAG, "to detail activity: "+ descriptionCut);
                    }
                };

                View.OnClickListener goToUserPageActivityListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "clicked user:" + finalUserDTO.getEmail(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Clicked u:" + finalUserDTO.getEmail());
                    }
                };

                // wait for userDTO loaded, then attach listener
                if (finalUserDTO != null) {
                    imgProductImage.setOnClickListener(goToProductActivityListener);
                    textProductDescriptionCut.setOnClickListener(goToProductActivityListener);
                    llLabels.setOnClickListener(goToProductActivityListener);
                    llPricingInfo.setOnClickListener(goToProductActivityListener);

                }
            }
        }


        public void setCurrentUserDTO(UserDTO currentUserDTO) {
            this.currentUserDTO = currentUserDTO;
        };

}
