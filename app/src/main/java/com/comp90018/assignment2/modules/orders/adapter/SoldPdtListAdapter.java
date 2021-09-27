package com.comp90018.assignment2.modules.orders.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.OrderDTO;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class SoldPdtListAdapter extends RecyclerView.Adapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private FirebaseStorage storage;
    private List<OrderDTO> orderDTOList;

    public SoldPdtListAdapter(Context ctx, List<OrderDTO> orderDTOList) {
        this.context = ctx;
        this.orderDTOList = orderDTOList;
        storage = FirebaseStorage.getInstance();
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sold_pdt_list, parent, false);
        return new SoldPdtListViewHolder(context, view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SoldPdtListViewHolder soldPdtListViewHolder = (SoldPdtListViewHolder) holder;
        soldPdtListViewHolder.setData(orderDTOList, position);
    }

    @Override
    public int getItemCount() {
        return orderDTOList.size();
    }
}
