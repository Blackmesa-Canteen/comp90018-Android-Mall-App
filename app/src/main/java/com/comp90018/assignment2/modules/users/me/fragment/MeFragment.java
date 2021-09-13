package com.comp90018.assignment2.modules.users.me.fragment;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.base.BaseFragment;
import com.comp90018.assignment2.db.service.UserService;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 *
 * @author you
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
    FirebaseFirestore db;



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

        return view;







        
    }

    @Override
    public void loadData() {
        /* 实际上，这个方法会从网上请求数据，然后你要把数据在这个方法里装到对应的view里 */
        imageView.setImageResource(R.drawable.profile);

    }
}
