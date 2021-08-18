package com.comp90018.assignment2.dto;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * Data Model of User
 *
 * @author xiaotian
 */
@Data
@NoArgsConstructor
@ToString
public class UserDTO {

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
}
