package com.comp90018.assignment2.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Parcelable DTO that mapped from the database object
 * make it parcelable to let it be able to bundled between activities
 *
 * @author jing
 * @author Xiaotian Li
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDTO implements Parcelable {

    private String address;
    private DocumentReference buyer_ref;
    private Timestamp created_time;
    private DocumentReference product_ref;
    private DocumentReference seller_ref;
    private Integer status;
    private String tracking_id;
    private String tracking_info;

    protected OrderDTO(Parcel in) {

        buyer_ref = FirebaseFirestore.getInstance().document(in.readString());
        product_ref = FirebaseFirestore.getInstance().document(in.readString());
        seller_ref = FirebaseFirestore.getInstance().document(in.readString());

        address = in.readString();
        created_time = in.readParcelable(Timestamp.class.getClassLoader());
        if (in.readByte() == 0) {
            status = null;
        } else {
            status = in.readInt();
        }
        tracking_id = in.readString();
        tracking_info = in.readString();
    }

    public static final Creator<OrderDTO> CREATOR = new Creator<OrderDTO>() {
        @Override
        public OrderDTO createFromParcel(Parcel in) {
            return new OrderDTO(in);
        }

        @Override
        public OrderDTO[] newArray(int size) {
            return new OrderDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        String buyer_ref_text = buyer_ref.getPath();
        dest.writeString(buyer_ref_text);

        String product_ref_text = product_ref.getPath();
        dest.writeString(product_ref_text);

        String seller_ref_text = seller_ref.getPath();
        dest.writeString(seller_ref_text);

        dest.writeString(address);
        dest.writeParcelable(created_time, flags);
        if (status == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(status);
        }
        dest.writeString(tracking_id);
        dest.writeString(tracking_info);
    }
}
