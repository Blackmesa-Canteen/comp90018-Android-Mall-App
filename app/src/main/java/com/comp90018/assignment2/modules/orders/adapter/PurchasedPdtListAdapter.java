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

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PurchasedPdtListAdapter extends RecyclerView.Adapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private FirebaseStorage storage;
    private List<OrderDTO> orderDTOList;

    public PurchasedPdtListAdapter(Context ctx, List<OrderDTO> orderDTOList) {
        this.context = ctx;
        this.orderDTOList = orderDTOList;
        storage = FirebaseStorage.getInstance();
        layoutInflater = LayoutInflater.from(context);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // only one type of view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchased_pdt_list, parent, false);
        return new PurchasedPdtListViewHolder(context, view);
    }

    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        PurchasedPdtListViewHolder viewHolder = (PurchasedPdtListViewHolder) holder;
        viewHolder.setData(orderDTOList, position);
    }

    public int getItemCount() {
        return orderDTOList.size();
    }

}
