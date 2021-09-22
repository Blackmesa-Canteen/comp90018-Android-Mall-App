package com.comp90018.assignment2.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO implements Parcelable {
    List<String> member_ids;
    String order_id;
    String product_id;

    protected ConversationDTO(Parcel in) {
        member_ids = in.createStringArrayList();
        order_id = in.readString();
        product_id = in.readString();
    }

    public static final Creator<ConversationDTO> CREATOR = new Creator<ConversationDTO>() {
        @Override
        public ConversationDTO createFromParcel(Parcel in) {
            return new ConversationDTO(in);
        }

        @Override
        public ConversationDTO[] newArray(int size) {
            return new ConversationDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(member_ids);
        dest.writeString(order_id);
        dest.writeString(product_id);
    }
}
