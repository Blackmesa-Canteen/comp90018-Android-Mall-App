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
 * Data Model of ProductComment
 *
 * @author jing
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductCommentDTO {
    private String content;
    private Timestamp created_time;
    private Integer likes;
    private DocumentReference parent_comment_id;
    private DocumentReference user_ref;

}
