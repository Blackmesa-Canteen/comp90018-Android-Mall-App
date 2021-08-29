package com.comp90018.assignment2.dto;


import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@ToString
public class SubCategoryDTO {
    private DocumentReference category_ref;
    private String image_address;
    private String name;
}
