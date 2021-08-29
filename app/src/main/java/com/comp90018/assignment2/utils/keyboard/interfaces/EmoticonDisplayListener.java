package com.comp90018.assignment2.utils.keyboard.interfaces;

import android.view.ViewGroup;

import com.comp90018.assignment2.utils.keyboard.adpater.EmoticonsAdapter;

public interface EmoticonDisplayListener<T> {

    void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, T t, boolean isDelBtn);
}
