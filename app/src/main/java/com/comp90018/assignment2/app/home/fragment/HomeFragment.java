package com.comp90018.assignment2.app.home.fragment;

import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.base.BaseFragment;

/**
 *
 * @author you
 */
public class HomeFragment extends BaseFragment {

    /**
     * 缺省占位
     */
    private ImageView imageView;

    @Override
    public View inflateView() {

        /*
        * 当你实际写出这个fragment的layout文件后，用View view = View.inflate(mContext, R.layout.fragment_XXX, null); 绑定
        * 然后该各fragment的各种view可以通过view.findViewById()来绑定
        * 然后return view.
        * */
        imageView = new ImageView(activityContext);

        return imageView;
    }

    @Override
    public void loadData() {
        /* 实际上，这个方法会从网上请求数据，然后你要把数据在这个方法里装到对应的view里 */
        imageView.setImageResource(R.drawable.shop);
    }
}
