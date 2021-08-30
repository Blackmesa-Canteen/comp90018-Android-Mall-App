package com.comp90018.assignment2.db.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.comp90018.assignment2.dto.UserDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic atomic operations on database objects。
 * 主要是被service层调用
 */
public class UserRepository {

    private static UserRepository instance;
    private final FirebaseFirestore db;

    // default time out is 360 seconds
    private long timeoutPeriodMs = 360000;

    private static final int SUCCESSFUL = 0;
    private static final int RUNNING = 1;
    private static final int FAILED = -1;
    private static final String TAG = "DB_User_Repository";
    private static final String COLLECTION_NAME = "users";

    private UserRepository() {

        // [START get_firestore_instance]
        db = FirebaseFirestore.getInstance();
        // [END get_firestore_instance]
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }

        return instance;
    }

    /**
     * set database transaction timeout
     * @param timeoutPeriodMs ms
     */
    public void setTimeoutPeriodMs(long timeoutPeriodMs) {
        this.timeoutPeriodMs = timeoutPeriodMs;
    }


    /**
     * find userDTO from db with it's id
     * @param userId user's auto generated id
     * @return userDTO
     */
    public UserDTO findUserById(String userId) {
        DocumentReference userReference = db.collection(COLLECTION_NAME).document(userId);

        final UserDTO[] userDTO = {null};
        final int[] status = {RUNNING};

        Log.d(TAG, "kai克克:"+userId);

        Map<String, Object> data = new HashMap<>();
        data.put("capital", true);

        db.collection("users").document("BbJ")
                .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("ojbk");
                Log.e(TAG, "DocumentSnapshot successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error writing document", e);
                    }
                });

        Log.d(TAG, "ku克克:"+db.toString());

        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                System.out.println("成功读了");
                Log.d(TAG, "success克克");
                UserDTO userDTO1 = documentSnapshot.toObject(UserDTO.class);
                Log.d(TAG, userDTO1.toString());
            }
        });

        // callback of query
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                Log.d(TAG, "BANG DING克克");
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userDTO[0] = document.toObject(UserDTO.class);
                        Log.d(TAG, "欧克克");
                        status[0] = SUCCESSFUL;
                    } else {
                        Log.d(TAG, "No such user");
                        status[0] = FAILED;
                        Log.d(TAG, "没克克");
                    }
                } else {
                    Log.d(TAG, "findUserById failed with ", task.getException());
                    status[0] = FAILED;
                    Log.d(TAG, "失败异常克克");
                }
            }
        });

        // start time counting down
        long startTime =  System.currentTimeMillis();
        long nowTime = startTime;

        // busy waiting
//        while (status[0] == RUNNING) {
//            // check time out
//            nowTime = System.currentTimeMillis();
//            if(nowTime - startTime >= timeoutPeriodMs) {
//                status[0] = FAILED;
//                Log.e(TAG, "findUserById time out.");
//            }
//        }
//
//        // return successful result
//        if (status[0] == SUCCESSFUL) {
//            return userDTO[0];
//        }

        // failed, return null.
        return null;
    }

    /**
     * Find the user based on reference (some database objects stores user reference)
     * @param userReference reference
     * @return userDTO
     */
    public UserDTO findUserByReference(DocumentReference userReference) {
        final UserDTO[] userDTO = {null};
        final int[] status = {RUNNING};

        // callback of query
        userReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    userDTO[0] = document.toObject(UserDTO.class);
                    status[0] = SUCCESSFUL;
                } else {
                    Log.d(TAG, "No such user");
                    status[0] = FAILED;
                }
            } else {
                Log.d(TAG, "findUserByReference failed with ", task.getException());
                status[0] = FAILED;
            }
        });

        // start time counting down
        long startTime =  System.currentTimeMillis();
        long nowTime = startTime;

        // busy waiting
        while (status[0] == RUNNING) {
            // check time out
            nowTime = System.currentTimeMillis();
            if(nowTime - startTime >= timeoutPeriodMs) {
                status[0] = FAILED;
                Log.e(TAG, "findUserByReference time out.");
            }
        }

        // return successful result
        if (status[0] == SUCCESSFUL) {
            return userDTO[0];
        }

        // failed, return null.
        return null;
    }

    /**
     * find the user by email address
     * @param email
     * @return
     */
    public UserDTO findUserByEmail(String email) {
        return null;
    }

    /**
     *
     * @return
     */
    public List<UserDTO> findAllUsers() {
        return null;
    }

    /**
     *
     * @param userDTO
     */
    public void addNewUser(UserDTO userDTO) {

    }

    /**
     *
     * @param userId
     * @param newPassword
     */
    public void updateUserPassword(String userId, String newPassword) {

    }
}
