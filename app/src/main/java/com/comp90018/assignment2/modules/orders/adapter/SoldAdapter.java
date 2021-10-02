package com.comp90018.assignment2.modules.orders.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.dto.OrderDTO;
import com.comp90018.assignment2.modules.product.activity.ProductDetailActivity;
import com.comp90018.assignment2.utils.Constants;
import com.comp90018.assignment2.utils.view.OvalImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SoldAdapter extends RecyclerView.Adapter{
    private Context context;
    private LayoutInflater layoutInflater;

    private final static String TAG = "SoldAdapter";

    private List<OrderDTO> orderDTOList;
    private Map<DocumentReference, ProductDTO> productDTOMap;
    private Map<DocumentReference, UserDTO> userDTOMap;
    private FirebaseStorage storage;

    public SoldAdapter(Context context, List<OrderDTO> orderDTOList) {
        this.context = context;
        this.orderDTOList = orderDTOList;
        this.productDTOMap = new HashMap<>();
        this.userDTOMap = new HashMap<>();
        storage = FirebaseStorage.getInstance();
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sold_pdt_list, parent, false);
        return new ItemProductInfoViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemProductInfoViewHolder viewHolder = (ItemProductInfoViewHolder) holder;
        viewHolder.setData(orderDTOList, productDTOMap, userDTOMap, position);
    }

    @Override
    public int getItemCount() { return orderDTOList.size(); }

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

    public synchronized void addNewProductDtoInMap(DocumentReference reference, ProductDTO productDTO) {
        productDTOMap.put(reference, productDTO);
    }


    /**
     * ViewHolder for items
     */
    private class ItemProductInfoViewHolder extends RecyclerView.ViewHolder {
        /// seller
        private Context context;

        private CircleImageView imgAvatar;
        private TextView textNickname;
        private TextView transaction_state;

        /// product image and name
        private OvalImageView ProductImage;
        private TextView ProductDescriptionCut;

        /// product price
        private TextView ProductPrice; //purchased_pdt_price;

        /// contact and detail
        private ImageView ContactImage; //purchased_contact_image;

        private CardView DetailCardView; //cv_pdt_detail_btn_profile;
        private Button DetailButton; //purchased_pdt_detail_btn;


        public ItemProductInfoViewHolder(Context context, View inflate) {
            super(inflate);
            this.context = context;

            imgAvatar = (CircleImageView) inflate.findViewById(R.id.sold_buyer_avatar);
            textNickname = (TextView) inflate.findViewById(R.id.sold_buyer_username);
            transaction_state = (TextView) inflate.findViewById(R.id.sold_transaction_state);

            //product image and name
            ProductImage = (OvalImageView) inflate.findViewById(R.id.sold_pdt_img);
            ProductDescriptionCut = (TextView) inflate.findViewById(R.id.sold_pdt_name);

            // product price
            ProductPrice = (TextView) inflate.findViewById(R.id.sold_pdt_price);

            // contact and detail
            ContactImage = (ImageView) inflate.findViewById(R.id.sold_contact_image);
            DetailButton = (Button) inflate.findViewById(R.id.sold_pdt_detail_btn);
        }

        /**
         * attach data to views
         *
         * @param orderDTOList dto list
         * @param userDTOMap user hashmap
         * @param position item number
         */
        public void setData(List<OrderDTO> orderDTOList, Map<DocumentReference, ProductDTO> productDTOMap,Map<DocumentReference, UserDTO> userDTOMap, final int position) {
            OrderDTO orderDTO = orderDTOList.get(position);
            String descriptionCut;

            transaction_state.setText(orderDTOList.get(position).getTracking_info());
            // TODO 我买到的页面 要显示卖家的姓名 而非 reference
            textNickname.setText(orderDTOList.get(position).getSeller_ref().toString());


            // set product card
            DocumentReference productDocReference = orderDTO.getProduct_ref();
            ProductDTO productDTO = null;
            if (productDTOMap.containsKey(productDocReference)){
                // get dto
                productDTO = productDTOMap.get(productDocReference);
                // set product img
                // if default img
                if (productDTO.getImage_address() == null
                        || productDTO.getImage_address().get(0) == null
                        || productDTO.getImage_address().get(0).equals("")
                        || productDTO.getImage_address().get(0).equals("default")
                        || productDTO.getImage_address().get(0).equals("gs://comp90018-mobile-caa7c.appspot.com/public/default.png")) {
                    ProductImage.setImageResource(R.drawable.default_image);
                } else {
                    // only show the first img
                    // storage Reference of firebase
                    StorageReference imgReference = storage.getReferenceFromUrl(productDTO.getImage_address().get(0));

                    // query image with the reference
                    Glide.with(context)
                            .load(imgReference)
                            .into(ProductImage);
                }
                // set product description

                if (productDTO.getDescription().length() > 33) {
                    descriptionCut = productDTO.getDescription().substring(0, 33) + "...";
                } else {
                    descriptionCut = productDTO.getDescription();
                }
                ProductDescriptionCut.setText(descriptionCut);

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
                if (productDTO.getCurrency() == Constants.AUS_DOLLAR) {
                    formattedPriceText = "$" + formattedPriceText;
                } else {
                    formattedPriceText = "$" + formattedPriceText;
                }
                ProductPrice.setText(formattedPriceText);
            }

            // set user card
            DocumentReference userDocReference = orderDTO.getBuyer_ref();
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
            } else {
                //rating.setRating(0);
            }

            // attach listeners
            ProductDTO finalProductDTO = productDTO;
            UserDTO finalUserDTO = userDTO;
            View.OnClickListener goToProductActivityListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("productDTO", finalProductDTO);
                    intent.putExtra("userDTO", finalUserDTO);
                    context.startActivity(intent);
                    //Log.d(TAG, "to detail activity: "+ descriptionCut);
                }
            };

            View.OnClickListener goToUserPageActivityListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "to user page:" + finalUserDTO.getEmail(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "to user page:" + finalUserDTO.getEmail());
                }
            };

            View.OnClickListener showPopup = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            };

            // wait for userDTO loaded, then attach listener
            if (finalUserDTO != null) {
                ProductImage.setOnClickListener(goToProductActivityListener);
                ProductDescriptionCut.setOnClickListener(goToProductActivityListener);
            }
            // handle product status



            switch (orderDTO.getStatus()) {
                case Constants.WAITING_DELIVERY:
                    // TODO:
                    transaction_state.setText("Waiting Delivery");
                    break;
                case Constants.DELIVERING:
                    // TODO:
                    transaction_state.setText("Delivering");
                    break;
                case Constants.SUCCESSFUL_NOT_COMMENT:
                    // TODO:
                    transaction_state.setText("Successful Not Comment");
                    break;
                case Constants.SUCCESSFUL_COMMENT:
                    // TODO:
                    transaction_state.setText("Successful Comment");
                    break;
                case Constants.CANCELED:
                    // TODO:
                    transaction_state.setText("Cancelled");
                    break;
                case Constants.ON_REFUND:
                    // TODO:
                    transaction_state.setText("On Refund");
                    break;
                case Constants.ON_REFUND_DELIVERING:
                    // TODO:
                    transaction_state.setText("On Refund Delivering");
                    break;
                case Constants.REFUNDED:
                    // TODO:
                    transaction_state.setText("Refunded");
                    break;
            }
        }
    }
}