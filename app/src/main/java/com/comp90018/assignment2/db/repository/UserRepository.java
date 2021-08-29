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

import java.util.List;

/**
 * Basic atomic operations on database objects。
 * 主要是被service层调用
 */
public class UserRepository {

    private static UserRepository instance;
    private final FirebaseFirestore db;

    // default time out is 3 seconds
    private long timeoutPeriodMs = 3000;

    private static int SUCCESSFUL = 0;
    private static int RUNNING = 1;
    private static int FAILED = -1;
    private static String TAG = "DB_User_Repository";
    private static String COLLECTION_NAME = "users";

    private UserRepository() {
        // protect the constructor

        // [START get_firestore_instance]
        db = FirebaseFirestore.getInstance();
        // [END get_firestore_instance]

        // [START set_firestore_settings]
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        // [END set_firestore_settings]
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
     * @return
     */
    public UserDTO findUserById(String userId) {
        DocumentReference userReference = db.collection(COLLECTION_NAME).document(userId);

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
                Log.d(TAG, "get failed with ", task.getException());
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
                Log.e(TAG, "find userById time out.");
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
