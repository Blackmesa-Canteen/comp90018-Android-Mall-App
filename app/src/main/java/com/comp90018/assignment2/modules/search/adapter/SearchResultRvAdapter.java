package com.comp90018.assignment2.modules.search.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.product.activity.ProductDetailActivity;
import com.comp90018.assignment2.utils.Constants;
import com.comp90018.assignment2.utils.view.OvalImageView;
import com.donkingliang.labels.LabelsView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for items of recycler view in search result activity
 * @author Xiaotian Li
 */
public class SearchResultRvAdapter extends RecyclerView.Adapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private final static String TAG = "SearchResultRvAdapter";

    private List<ProductDTO> productDTOList;
    private Map<DocumentReference, UserDTO> userDTOMap;

    private FirebaseStorage storage;

    public SearchResultRvAdapter(Context context, List<ProductDTO> productDTOList) {
        this.context = context;
        this.productDTOList = productDTOList;
        this.userDTOMap = new HashMap<>();
        storage = FirebaseStorage.getInstance();
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // only one type of view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view_search, parent, false);
        return new ItemProductInfoViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ItemProductInfoViewHolder viewHolder = (ItemProductInfoViewHolder) holder;
        viewHolder.setData(productDTOList, userDTOMap, position);
    }

    @Override
    public int getItemCount() {
        return productDTOList.size();
    }

    /**
     * update UserDtoMap. will be called in callback.
     * synchronized to prevent callback conflicts
     *
     * @param reference
     * @param userDTO
     */
    public synchronized void addNewUserDtoInMap(DocumentReference reference, UserDTO userDTO) {
        userDTOMap.put(reference, userDTO);
    }


    /**
     * ViewHolder for items
     */
    private class ItemProductInfoViewHolder extends RecyclerView.ViewHolder {

        private Context context;

        private OvalImageView imgProductImage;
        private TextView textProductDescriptionCut;
        private LinearLayout llLabels;
        private LabelsView labels;
        private LinearLayout llPricingInfo;
        private TextView textProductPrice;
        private TextView textLikes;
        private LinearLayout llUserProfile;
        private CircleImageView imgAvatar;
        private TextView textNickname;
        private RatingBar rating;

        public ItemProductInfoViewHolder(Context context, View inflate) {
            super(inflate);
            this.context = context;

            // bind views
            imgProductImage = (OvalImageView)inflate.findViewById( R.id.item_image);
            textProductDescriptionCut = (TextView)inflate.findViewById( R.id.item_description);
            llLabels = (LinearLayout)inflate.findViewById( R.id.item_labels);
            labels = (LabelsView)inflate.findViewById( R.id.item_label);
            llPricingInfo = (LinearLayout)inflate.findViewById( R.id.item_price_label);
            textProductPrice = (TextView)inflate.findViewById( R.id.item_price);
            textLikes = (TextView)inflate.findViewById( R.id.item_likes);
            llUserProfile = (LinearLayout)inflate.findViewById( R.id.item_user_profile);
            imgAvatar = (CircleImageView)inflate.findViewById( R.id.item_img_avatar);
            textNickname = (TextView)inflate.findViewById( R.id.seller_nickname);
            rating = (RatingBar)inflate.findViewById( R.id.item_rating);
        }

        /**
         * attach data to views
         *
         * @param productDTOList dto list
         * @param userDTOMap user hashmap
         * @param position item number
         */
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

                // set labels: brand and quality
                ArrayList<String> labelStrings = new ArrayList<>();

                String brandNameCut;
                if (productDTO.getBrand().length() > 10) {
                    brandNameCut = productDTO.getBrand().substring(0,10) + "...";
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

                    // set avatar
                    // only show the first img
                    // storage Reference of firebase
                    if (userDTO.getAvatar_address() == null
                            || userDTO.getAvatar_address().length() == 0
                            || userDTO.getAvatar_address().equals("default")
                            || userDTO.getAvatar_address().equals("gs://comp90018-mobile-caa7c.appspot.com/users/default_avatar.jpg")) {

                        imgAvatar.setImageResource(R.drawable.default_avatar);
                    } else {
                        StorageReference imgReference = storage.getReferenceFromUrl(userDTO.getAvatar_address());

                        // query image with the reference
                        Glide.with(context)
                                .load(imgReference)
                                .into(imgAvatar);
                    }

                    // set nickname
                    String nickname;
                    if (userDTO.getNickname().length() > 10) {
                        nickname = userDTO.getNickname().substring(0,10) + "...";
                    } else {
                        nickname = userDTO.getNickname();
                    }
                    textNickname.setText(nickname);

                    // set stars
                    rating.setRating(userDTO.getStar_number().floatValue());

                } else {
                    rating.setRating(0);
                }

                // attach listeners
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
                        Toast.makeText(context, "to user page:" + finalUserDTO.getEmail(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "to user page:" + finalUserDTO.getEmail());
                    }
                };

                // wait for userDTO loaded, then attach listener
                if (finalUserDTO != null) {
                    imgProductImage.setOnClickListener(goToProductActivityListener);
                    textProductDescriptionCut.setOnClickListener(goToProductActivityListener);
                    llLabels.setOnClickListener(goToProductActivityListener);
                    llPricingInfo.setOnClickListener(goToProductActivityListener);

                    llUserProfile.setOnClickListener(goToUserPageActivityListener);
                }
        }
    }
}
