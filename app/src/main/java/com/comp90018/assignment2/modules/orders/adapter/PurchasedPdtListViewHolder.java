package com.comp90018.assignment2.modules.orders.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.assignment2.R;
import com.comp90018.assignment2.dto.OrderDTO;

import java.util.List;

public class PurchasedPdtListViewHolder extends RecyclerView.ViewHolder {
    private Context context;
    // TODO 闲鱼 我 买入或卖出列表 右上角那个"交易成功" 是订单状态还是 发货追踪 还是都不是？
    private TextView trackingInfo;
    // my purchased-pdt-list page displays the buyers username and avatar
    private TextView sellerUsername;

    public PurchasedPdtListViewHolder(Context context, View view) {
        super(view);
        this.context = context;

        trackingInfo = (TextView) view.findViewById(R.id.purchased_transaction_state);
        sellerUsername = (TextView) view.findViewById(R.id.purchased_seller_username);

    }

    public void setData(List<OrderDTO> orderDTOList, final int position) {
        // TODO 我买到的页面 右上角的"交易成功"如何判断？
        trackingInfo.setText(orderDTOList.get(position).getTracking_info());
        // TODO 我买到的页面 要显示卖家的姓名 而非 reference
        sellerUsername.setText(orderDTOList.get(position).getSeller_ref().toString());
    }
}
