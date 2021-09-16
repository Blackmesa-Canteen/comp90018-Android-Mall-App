package com.comp90018.assignment2.modules.messages.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.comp90018.assignment2.R;
import com.comp90018.assignment2.modules.messages.bean.KeyboardMoreItemBean;

import java.util.List;

/**
 * adapter for keyboard's more button
 *
 * @author xiaotian li
 *
 */
public class KeyboardMoreItemAdapter extends BaseQuickAdapter<KeyboardMoreItemBean, BaseViewHolder> {
    public KeyboardMoreItemAdapter(int layoutResId, @Nullable List<KeyboardMoreItemBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, KeyboardMoreItemBean item) {
        baseViewHolder.setImageResource(R.id.iv, item.getImageId());
        baseViewHolder.setText(R.id.tv, item.getTitle());
    }
}
