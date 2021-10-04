package com.comp90018.assignment2.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.FirebaseFirestore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
public class CategoryDTO implements Parcelable {
    private String name;
    private String category_id;
    protected CategoryDTO(Parcel in) {
        name = in.readString();
    }

    public static final Creator<CategoryDTO> CREATOR = new Creator<CategoryDTO>() {
        @Override
        public CategoryDTO createFromParcel(Parcel in) {
            return new CategoryDTO(in);
        }

        @Override
        public CategoryDTO[] newArray(int size) {
            return new CategoryDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(category_id);
    }
    @Override
    public String toString() {
        return name;
    }
}
