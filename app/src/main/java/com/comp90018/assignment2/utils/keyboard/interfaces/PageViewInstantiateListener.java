package com.comp90018.assignment2.utils.keyboard.interfaces;

import android.view.View;
import android.view.ViewGroup;

import com.comp90018.assignment2.utils.keyboard.data.PageEntity;

public interface PageViewInstantiateListener<T extends PageEntity> {

    View instantiateItem(ViewGroup container, int position, T pageEntity);
}
