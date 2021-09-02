package com.comp90018.assignment2.modules.categories.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.base.BaseFragment;

public class CategoriesFragment extends BaseFragment {

    RecyclerView categoryContent;
    ListView categoryList;

    @Override
    public View inflateView() {
        /*
         * 当你实际写出这个fragment的layout文件后，用View view = View.inflate(mContext, R.layout.fragment_XXX, null); 绑定
         * 然后该各fragment的各种view可以通过view.findViewById()来绑定
         * 然后return view.
         * */
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.fragment_categories, null);
        categoryList = view.findViewById(R.id.ct_left);
        categoryContent = view.findViewById(R.id.ct_right);
//        imageView = new ImageView(activityContext);
//        return imageView;
        return view;
    }

    @Override
    public void loadData() {
        /* 实际上，这个方法会从网上请求数据，然后你要把数据在这个方法里装到对应的view里 */

    }
}
