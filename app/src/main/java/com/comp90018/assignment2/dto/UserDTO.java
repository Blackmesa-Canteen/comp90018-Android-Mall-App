package com.comp90018.assignment2.dto;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
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
public class UserDTO implements Parcelable {

    private String avatar_address;
    private Timestamp created_time;
    private String description;
    private String email;

    private List<DocumentReference> favorite_refs;
    private List<DocumentReference> follower_refs;
    private List<DocumentReference> following_refs;

    private Integer gender;
    private String location_text;
    private String nickname;
    private String password;
    private String payment_info;
    private Integer sold_number;
    private Double star_number;

    protected UserDTO(Parcel in) {


        List<String> tempStringReferenceTextList = null;

        favorite_refs = new ArrayList<>();
        tempStringReferenceTextList = in.createStringArrayList();
        for (String path : tempStringReferenceTextList) {
            favorite_refs.add(FirebaseFirestore.getInstance().document(path));
        }
        tempStringReferenceTextList = null;

        follower_refs = new ArrayList<>();
        tempStringReferenceTextList = in.createStringArrayList();
        for (String path : tempStringReferenceTextList) {
            follower_refs.add(FirebaseFirestore.getInstance().document(path));
        }
        tempStringReferenceTextList = null;

        following_refs = new ArrayList<>();
        tempStringReferenceTextList = in.createStringArrayList();
        for (String path : tempStringReferenceTextList) {
            following_refs.add(FirebaseFirestore.getInstance().document(path));
        }
        tempStringReferenceTextList = null;

        avatar_address = in.readString();
        created_time = in.readParcelable(Timestamp.class.getClassLoader());
        description = in.readString();
        email = in.readString();
        if (in.readByte() == 0) {
            gender = null;
        } else {
            gender = in.readInt();
        }
        location_text = in.readString();
        nickname = in.readString();
        password = in.readString();
        payment_info = in.readString();
        if (in.readByte() == 0) {
            sold_number = null;
        } else {
            sold_number = in.readInt();
        }
        if (in.readByte() == 0) {
            star_number = null;
        } else {
            star_number = in.readDouble();
        }
    }

    public static final Creator<UserDTO> CREATOR = new Creator<UserDTO>() {
        @Override
        public UserDTO createFromParcel(Parcel in) {
            return new UserDTO(in);
        }

        @Override
        public UserDTO[] newArray(int size) {
            return new UserDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        List<String> favorite_refs_text_list = new ArrayList<>();
        for (DocumentReference documentReference : favorite_refs) {
            favorite_refs_text_list.add(documentReference.getPath());
        }
        dest.writeStringList(favorite_refs_text_list);

        List<String> follower_refs_text_list = new ArrayList<>();
        for (DocumentReference documentReference : follower_refs) {
            follower_refs_text_list.add(documentReference.getPath());
        }
        dest.writeStringList(follower_refs_text_list);

        List<String> following_refs_text_list = new ArrayList<>();
        for (DocumentReference documentReference : following_refs) {
            following_refs_text_list.add(documentReference.getPath());
        }
        dest.writeStringList(following_refs_text_list);

        dest.writeString(avatar_address);
        dest.writeParcelable(created_time, flags);
        dest.writeString(description);
        dest.writeString(email);
        if (gender == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(gender);
        }
        dest.writeString(location_text);
        dest.writeString(nickname);
        dest.writeString(password);
        dest.writeString(payment_info);
        if (sold_number == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(sold_number);
        }
        if (star_number == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(star_number);
        }
    }
}
