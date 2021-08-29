package com.comp90018.assignment2.dto;


import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * Data Model of SubCategory
 *
 * @author xiaotian
 */

@Data
@NoArgsConstructor
@ToString
public class SubCategoryDTO {
    private List<DocumentReference> category_ref;
    private String image_address;
    private String name;
}
