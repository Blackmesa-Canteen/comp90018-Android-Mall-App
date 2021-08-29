package com.comp90018.assignment2.dto;


import com.firebase.geofire.GeoLocation;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * Data Model of Product
 *
 * @author jing
 */

@Data
@NoArgsConstructor
@ToString
public class ProductDTO {
    private String brand;
    private List<DocumentReference> category_ref;
    private Integer currency;
    private String description;
    private Integer favorite_number;
    private String image_address;
    private GeoLocation location_coordinate;
    private String location_text;
    private List<DocumentReference> owner_ref;
    private Integer price;
    private Timestamp publish_time;
    private Integer quality;
    private Integer status;
    private List<DocumentReference> sub_category_ref;
    private String view_number;

}
