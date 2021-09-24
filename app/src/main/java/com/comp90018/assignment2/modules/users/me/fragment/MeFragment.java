package com.comp90018.assignment2.modules.users.me.fragment;


import static com.comp90018.assignment2.config.VideoDataSource.TAG;
import static com.comp90018.assignment2.utils.Constants.USERS_COLLECTION;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.base.BaseFragment;

import com.comp90018.assignment2.db.service.UserService;
import com.comp90018.assignment2.dto.UserDTO;
import com.comp90018.assignment2.modules.users.authentication.activity.EditProfileActivity;
import com.comp90018.assignment2.modules.users.authentication.activity.LoginActivity;
import com.comp90018.assignment2.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.comp90018.assignment2.modules.users.authentication.activity.EditProfileActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
/**
 *
 * @author Zhonghui Jiang
 */
public class MeFragment extends BaseFragment {

    /**
     * 缺省占位
     */
    private de.hdodenhof.circleimageview.CircleImageView ci_user;
    private ImageButton ib_profile_arrow;

    private TextView tv_profile;
    private TextView tv_published;
    private TextView tv_sold;
    private TextView tv_purchased;
    private TextView tv_favorate;
    private TextView tv_address;
    private TextView tv_payment;
    private TextView tv_update;
    private TextView tv_feedback;
    private TextView tv_setting;
    private TextView tv_about;
    private TextView tv_follower_number;
    private TextView tv_following_number;

    private Button tv_logout;


    private TextView tv_nick_name;
    private TextView tv_loginID;






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
        tv_published = (TextView) view.findViewById(R.id.tv_published);
        tv_sold = (TextView) view.findViewById(R.id.tv_sold);
        tv_purchased = (TextView) view.findViewById(R.id.tv_purchased);
        tv_favorate = (TextView) view.findViewById(R.id.tv_favorate);
        tv_address = (TextView) view.findViewById(R.id.tv_address);
        tv_payment = (TextView) view.findViewById(R.id.tv_payment);
        tv_update = (TextView) view.findViewById(R.id.tv_update);
        tv_feedback = (TextView) view.findViewById(R.id.tv_feedback);
        tv_setting = (TextView) view.findViewById(R.id.tv_setting);
        tv_about = (TextView) view.findViewById(R.id.tv_about);
        tv_logout=(Button) view.findViewById(R.id.tv_logout);
        tv_follower_number=(TextView) view.findViewById(R.id.tv_follower_number);
        tv_following_number=(TextView) view.findViewById(R.id.tv_following_number);


        tv_nick_name = (TextView) view.findViewById(R.id.tv_nick_name);
        tv_loginID =(TextView) view.findViewById(R.id.tv_loginID);

        setListeners();

        return view;
    }

    private void setListeners(){
        onClick onClick= new onClick();
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
        tv_feedback.setOnClickListener(onClick);
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
            switch (v.getId()){


                case R.id.ib_profile_arrow:  //点击profile或者箭头跳转用户个人信息

                case R.id.tv_profile :

                    Toast.makeText(activityContext, "个人信息页面", Toast.LENGTH_SHORT).show();

                    break;
                case R.id.tv_published:

                    Toast.makeText(activityContext, "商品发布详情", Toast.LENGTH_SHORT).show();

                    break;
                case R.id.tv_sold:
                    Toast.makeText(activityContext, "已卖出商品", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tv_purchased:
                    Toast.makeText(activityContext, "已买到商品", Toast.LENGTH_SHORT).show();
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
                case R.id.tv_feedback:
                    Toast.makeText(activityContext, "反馈", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tv_about:
                    Toast.makeText(activityContext, "关于", Toast.LENGTH_SHORT).show();
                    break;

//                case R.id.tv_update:
//                    intent = new Intent(activityContext, EditProfileActivity.class);
//                    startActivity(intent);
//
//                    break;
                case R.id.tv_logout:
                    intent = new Intent(activityContext, LoginActivity.class);
                    startActivity(intent);


                    break;
            }


        }

    }

    @Override
    public void loadData() {
        /* 实际上，这个方法会从网上请求数据，然后你要把数据在这个方法里装到对应的view里 */
//        imageView.setImageResource(R.drawable.profile);
        FirebaseAuth firebaseAuth;
        FirebaseFirestore db;
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        String loginID = firebaseAuth.getCurrentUser().getEmail();
        String userID=firebaseAuth.getCurrentUser().getUid();
        Uri avatar=firebaseAuth.getCurrentUser().getPhotoUrl();


        DocumentReference userReference = db.collection(USERS_COLLECTION).document(userID);
        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                String follower = String.valueOf(userDTO.getFollower_refs().size());
                String following = String.valueOf(userDTO.getFollowing_refs().size());

                tv_loginID.setText("Login ID: " + loginID);

                ci_user.setImageURI(avatar);
                tv_nick_name.setText(userDTO.getNickname());
                tv_follower_number.setText(follower);
                tv_following_number.setText(following);


            }
        });


















    }





}
