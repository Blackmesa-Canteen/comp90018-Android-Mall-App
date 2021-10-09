package com.comp90018.assignment2.dto;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
public class SubCategoryDTO implements Parcelable {
    private DocumentReference category_ref;
    private String image_address;
    private String name;
    private String subcategory_id;

    protected SubCategoryDTO(Parcel in) {
        category_ref = FirebaseFirestore.getInstance().document(in.readString());
        image_address = in.readString();
        name = in.readString();
    }

    public static final Creator<SubCategoryDTO> CREATOR = new Creator<SubCategoryDTO>() {
        @Override
        public SubCategoryDTO createFromParcel(Parcel in) {
            return new SubCategoryDTO(in);
        }

        @Override
        public SubCategoryDTO[] newArray(int size) {
            return new SubCategoryDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String category_ref_text = category_ref.getPath();
        dest.writeString(category_ref_text);
        dest.writeString(image_address);
        dest.writeString(name);
        dest.writeString(subcategory_id);
    }
    @Override
    public String toString() {
        return name;
    }
}
