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
public class ProductCommentDTO implements Parcelable {
    private String content;
    private Timestamp created_time;
    private Integer likes;
    private DocumentReference parent_comment_id;
    private DocumentReference user_ref;

    protected ProductCommentDTO(Parcel in) {

        parent_comment_id = FirebaseFirestore.getInstance().document(in.readString());
        user_ref = FirebaseFirestore.getInstance().document(in.readString());

        content = in.readString();
        created_time = in.readParcelable(Timestamp.class.getClassLoader());
        if (in.readByte() == 0) {
            likes = null;
        } else {
            likes = in.readInt();
        }
    }

    public static final Creator<ProductCommentDTO> CREATOR = new Creator<ProductCommentDTO>() {
        @Override
        public ProductCommentDTO createFromParcel(Parcel in) {
            return new ProductCommentDTO(in);
        }

        @Override
        public ProductCommentDTO[] newArray(int size) {
            return new ProductCommentDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        String parent_comment_id_text = parent_comment_id.getPath();
        dest.writeString(parent_comment_id_text);

        String user_ref_text = user_ref.getPath();
        dest.writeString(user_ref_text);

        dest.writeString(content);
        dest.writeParcelable(created_time, flags);
        if (likes == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(likes);
        }
    }
}
