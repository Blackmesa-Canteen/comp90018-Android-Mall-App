package com.comp90018.assignment2.modules.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.comp90018.assignment2.dto.ProductDTO;
import com.comp90018.assignment2.dto.UserDTO;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.Map;

public class ProductDetailAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private List<ProductDTO> productDTOList;
    private Map<DocumentReference, UserDTO> userDTOMap;

    private FirebaseStorage storage;


    @Override
    public int getCount() {
        return productDTOList.size();

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
