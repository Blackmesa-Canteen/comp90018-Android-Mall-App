package com.comp90018.assignment2.modules.orders.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.comp90018.assignment2.dto.CategoryDTO;

import java.util.ArrayList;
import java.util.List;

public class CategoryArrayAdapter extends ArrayAdapter<CategoryDTO> {
    Context context;
    private List<CategoryDTO> categories = new ArrayList<>();
    boolean isFirstTime;
    LayoutInflater mInflater;
    String firstElement;
    String[] objects;

    public CategoryArrayAdapter(@NonNull Context context, int resource, List<CategoryDTO> categories, String defaultTextForSpinner) {
        super(context, resource);
        this.context = context;
        this.categories = categories;
        mInflater = LayoutInflater.from(context);
        this.isFirstTime = true;
        objects = new String[this.categories.size()];
        setDefaultText(defaultTextForSpinner);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        for (int i = 0; i < categories.size(); i++) {
            objects[i] = categories.get(i).getName();
        }
        if (isFirstTime) {
            objects[0] = firstElement;
            isFirstTime = false;
        }
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        notifyDataSetChanged();
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        TextView label = (TextView) row.findViewById(android.R.id.text1);
        label.setText(objects[position]);
        return row;
    }

    public void setDefaultText(String defaultText) {
        this.firstElement = objects[0];
        objects[0] = defaultText;
    }

    @Override
    public int getCount() {
        return objects.length;
    }

    @Override
    public CategoryDTO getItem(int i) {
        return categories.get(i);
    }

}
