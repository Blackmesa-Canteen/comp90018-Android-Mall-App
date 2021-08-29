package com.comp90018.assignment2.dto;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * Data Model of Order
 *
 * @author jing
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDTO {
    private String address;
    private DocumentReference buyer_ref;
    private Timestamp created_time;
    private DocumentReference product_ref;
    private DocumentReference seller_ref;
    private Integer status;
    private String tracking_id;
    private String tracking_info;

}
