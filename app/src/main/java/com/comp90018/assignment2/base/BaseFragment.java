package com.comp90018.assignment2.base;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Base for all fragments on the MainActivity
 * @author Xiaotian
 */
public abstract class BaseFragment extends Fragment {

    /**
     * This is the activity that the base fragment belongs to
     */
    public Context activityContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // allocate the activity when the fragment is created
        activityContext = getActivity();
    }

    /**
     * will be used automatically for child fragments to attach and inflate views
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // when creating the view, inflate it
        return inflateView();
    }

    /**
     * will be used automatically to load data.
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // once the activity is generated, load data
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * inflate layout files and bind views.
     * e.g
     *  (override)
     *  public View initView() {
     *         View view = View.inflate(mContext, R.layout.fragment_new_post, null);
     *         new_post_item = (ListView) view.findViewById(R.id.lv_new_post_item);
     *         return view;
     *     }
     * @return the inflated view
     *
     */
    public abstract View inflateView();

    /**
     * load data for the fragment from Internet,
     * then render the data to views.
     *
     * e.g.
     *  (override)
     *  public void getDataFromNet() {
     *         OkHttpUtils
     *                 .get()
     *                 .url(Constants.HOT_POST_URL)
     *                 .id(100)
     *                 .build()
     *                 .execute(new MyStringCallback());
     *     }
     */
    public abstract void loadData();

}
