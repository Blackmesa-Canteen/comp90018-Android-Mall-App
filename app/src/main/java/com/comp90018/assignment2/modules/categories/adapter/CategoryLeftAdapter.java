package com.comp90018.assignment2.modules.categories.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.comp90018.assignment2.R;

public class CategoryLeftAdapter extends BaseAdapter {
    private int mSelect = 0;
    private Context mContext;
    private String[] categories;

    public CategoryLeftAdapter(Context mContext, String[] categories) {
        this.mContext = mContext;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public Object getItem(int i) {
        return categories[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_categories_item, null);
            holder = new ViewHolder();
            holder.ct_title = convertView.findViewById(R.id.ct_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ct_title.setText(categories[i]);
        if (mSelect == i) {
//            convertView.setBackgroundResource(R.drawable.item_category_item_);  //选中项背景
            holder.ct_title.setTextColor(Color.parseColor("#fd3f3f"));
        } else {
//            convertView.setBackgroundResource(R.drawable.bg2);  //其他项背景
            holder.ct_title.setTextColor(Color.parseColor("#323437"));
        }
        return convertView;

    }

    public void changeSelected(int i) {
        if (i != mSelect) {
            mSelect = i;
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        private TextView ct_title;
    }
}


