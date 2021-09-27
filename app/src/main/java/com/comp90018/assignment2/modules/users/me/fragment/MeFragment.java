package com.comp90018.assignment2.modules.users.me.fragment;


import static com.comp90018.assignment2.utils.Constants.USERS_COLLECTION;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.base.BaseFragment;

import com.comp90018.assignment2.dto.UserDTO;

import com.comp90018.assignment2.modules.MainActivity;
import com.comp90018.assignment2.modules.orders.activity.PurchasedPdtListActivity;
import com.comp90018.assignment2.modules.orders.activity.SoldPdtListActivity;
import com.comp90018.assignment2.modules.users.authentication.activity.LoginActivity;

import com.comp90018.assignment2.modules.users.me.activity.EditProfileActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import cn.jpush.im.android.api.JMessageClient;

/**
 * Me fragment
 *
 * @author Zhonghui Jiang
 * @author Xiaotian Li
 */
public class MeFragment extends BaseFragment {

    private static final String TAG = "MeFragment[dev]";

    private de.hdodenhof.circleimageview.CircleImageView ci_user;
    private ImageButton ib_profile_arrow;

    private TextView tv_profile;
    private LinearLayout tv_published;
    private LinearLayout tv_sold;
    private LinearLayout tv_purchased;
    private TextView tv_favorate;
    private TextView tv_address;
    private TextView tv_payment;
    private TextView tv_update;
//    private TextView tv_feedback;
    private TextView tv_setting;
    private TextView tv_about;
    private TextView tv_follower_number;
    private TextView tv_following_number;

    private Button tv_logout;


    private TextView tv_nick_name;
    private TextView tv_loginID;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;


    @Override
    public View inflateView() {
        /*
         * 当你实际写出这个fragment的layout文件后，用View view = View.inflate(mContext, R.layout.fragment_XXX, null); 绑定
         * 然后该各fragment的各种view可以通过view.findViewById()来绑定
         * 然后return view.
         * */
        View view = View.inflate(activityContext, R.layout.fragment_me_layout, null);
        ci_user = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.ci_user);
        ib_profile_arrow = (ImageButton) view.findViewById(R.id.ib_profile_arrow);

        tv_profile = (TextView) view.findViewById(R.id.tv_profile);
        tv_published = (LinearLayout) view.findViewById(R.id.tv_published);
        tv_sold = (LinearLayout) view.findViewById(R.id.tv_sold);
        tv_purchased = (LinearLayout) view.findViewById(R.id.tv_purchased);
        tv_favorate = (TextView) view.findViewById(R.id.tv_favorate);
        tv_address = (TextView) view.findViewById(R.id.tv_address);
        tv_payment = (TextView) view.findViewById(R.id.tv_payment);
        tv_update = (TextView) view.findViewById(R.id.tv_update);
//        tv_feedback = (TextView) view.findViewById(R.id.tv_feedback);
        tv_setting = (TextView) view.findViewById(R.id.tv_setting);
        tv_about = (TextView) view.findViewById(R.id.tv_about);
        tv_logout = (Button) view.findViewById(R.id.tv_logout);
        tv_follower_number = (TextView) view.findViewById(R.id.tv_follower_number);
        tv_following_number = (TextView) view.findViewById(R.id.tv_following_number);


        tv_nick_name = (TextView) view.findViewById(R.id.tv_nick_name);
        tv_loginID = (TextView) view.findViewById(R.id.tv_loginID);

        setListeners();

        // setup services
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        return view;
    }

    private void setListeners() {
        onClick onClick = new onClick();
        ib_profile_arrow.setOnClickListener(onClick);
        tv_profile.setOnClickListener(onClick);
        tv_published.setOnClickListener(onClick);
        tv_sold.setOnClickListener(onClick);
        tv_purchased.setOnClickListener(onClick);
        tv_update.setOnClickListener(onClick);
        tv_logout.setOnClickListener(onClick);
        tv_favorate.setOnClickListener(onClick);
        tv_address.setOnClickListener(onClick);
        tv_payment.setOnClickListener(onClick);
//        tv_feedback.setOnClickListener(onClick);
        tv_setting.setOnClickListener(onClick);
        tv_about.setOnClickListener(onClick);
        tv_follower_number.setOnClickListener(onClick);
        tv_following_number.setOnClickListener(onClick);


    }


    public void onActivityCreated(Bundle savedInstanceStade) {

        super.onActivityCreated(savedInstanceStade);
        tv_update = (TextView) getActivity().findViewById(R.id.tv_update);
        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ToEditProfileActivity = new Intent(activityContext, EditProfileActivity.class);
                startActivity(ToEditProfileActivity);

            }
        });

    }


    //点击跳转activity
    private class onClick implements View.OnClickListener {
        @Override

        public void onClick(View v) {

            Intent intent = null;
            switch (v.getId()) {


                case R.id.ib_profile_arrow:  //点击profile或者箭头跳转用户个人信息

                case R.id.tv_profile:
                    Toast.makeText(activityContext, "用户个人信息", Toast.LENGTH_SHORT).show();

                    break;

//                    intent = new Intent(activityContext, profileActivity.class);
//                    startActivity(intent);
//                    break;
                case R.id.tv_published:

                    Toast.makeText(activityContext, "商品发布详情", Toast.LENGTH_SHORT).show();

                    break;
                case R.id.tv_sold:
                    intent = new Intent(activityContext, SoldPdtListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.tv_purchased:
                    intent = new Intent(activityContext, PurchasedPdtListActivity.class);
                    startActivity(intent);
                    break;
//                点击publish，sold，purchased 分别跳转相应界面
                case R.id.tv_favorate:
                    Toast.makeText(activityContext, "收藏商品", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tv_address:
                    Toast.makeText(activityContext, "送货地址", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tv_payment:
                    Toast.makeText(activityContext, "支付信息", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tv_setting:
                    Toast.makeText(activityContext, "设置", Toast.LENGTH_SHORT).show();
                    break;
//                case R.id.tv_feedback:
//                    Toast.makeText(activityContext, "反馈", Toast.LENGTH_SHORT).show();
//                    break;
                case R.id.tv_about:
                    Toast.makeText(activityContext, "关于", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.tv_update:
                    intent = new Intent(activityContext, EditProfileActivity.class);
                    startActivity(intent);

                    break;
                case R.id.tv_logout:

                    if (firebaseAuth.getCurrentUser() != null && JMessageClient.getMyInfo() != null) {

                        Log.d("Authentication_status", "firebaseAccount:" + firebaseAuth.getCurrentUser().getUid());
                        Log.d("Authentication_status", "JMessageAccount:" + JMessageClient.getMyInfo().getUserName());

                        firebaseAuth.signOut();
                        JMessageClient.logout();
                    }

                    Intent loginIntent = new Intent(activityContext, LoginActivity.class);
                    startActivity(loginIntent);
                    // jump to home if not login.
                    ((MainActivity) activityContext).getBinding().radioGroupMain.check(R.id.button_main_home);
                    break;
            }
        }
    }

    @Override
    public void loadData() {

        String loginID = firebaseAuth.getCurrentUser().getEmail();
        String userID = firebaseAuth.getCurrentUser().getUid();

        tv_loginID.setText("User ID: " + loginID);

        DocumentReference userReference = db.collection(USERS_COLLECTION).document(userID);
        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                // handle db query result
                handleQueryResult(documentSnapshot);
            }
        });

        // IMPORTANT: listen to recent data update
        userReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed: " + error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());

                    // handle db query result
                    handleQueryResult(snapshot);
                    
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    /**
     * attach db's query result to views
     * @param documentSnapshot firebase query result
     */
    private void handleQueryResult(DocumentSnapshot documentSnapshot) {
        UserDTO currentUserDto = documentSnapshot.toObject(UserDTO.class);
        String follower = String.valueOf(currentUserDto.getFollower_refs().size());
        String following = String.valueOf(currentUserDto.getFollowing_refs().size());

        // show current user's avatar
        if (currentUserDto == null
                || currentUserDto.getAvatar_address() == null
                || currentUserDto.getAvatar_address().equals("")
                || currentUserDto.getAvatar_address().equals("default")
                || currentUserDto.getAvatar_address().equals("gs://comp90018-mobile-caa7c.appspot.com/public/default.png")) {
            ci_user.setImageResource(R.drawable.default_avatar);
        } else {
            StorageReference imgReference = firebaseStorage.getReferenceFromUrl(currentUserDto.getAvatar_address());
            Glide.with(activityContext)
                    .load(imgReference)
                    .into(ci_user);
        }
        tv_nick_name.setText(currentUserDto.getNickname());
        tv_follower_number.setText(follower);
        tv_following_number.setText(following);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            // equivalent to onResume
            loadData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
