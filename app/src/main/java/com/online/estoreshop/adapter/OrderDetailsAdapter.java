package com.online.estoreshop.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.online.estoreshop.R;
import com.online.estoreshop.databinding.ItemOrderDetailsBinding;
import com.online.estoreshop.databinding.ItemOrdersBinding;
import com.online.estoreshop.models.OrderDetailsDomain;

import java.util.ArrayList;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {

    Context context;
    ArrayList<OrderDetailsDomain> orderDetailsList;

    public OrderDetailsAdapter(Context context, ArrayList<OrderDetailsDomain> orderDetailsList) {
        this.context = context;
        this.orderDetailsList = orderDetailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderDetailsBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_order_details, parent, false);

        return new OrderDetailsAdapter.ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.d("position", position + " " + orderDetailsList.size());
        if (position + 1 == orderDetailsList.size()) {
            holder.binding.seperator.setVisibility(View.GONE);
        } else {
            holder.binding.seperator.setVisibility(View.VISIBLE);
        }

//        holder.binding.tvSlno.setText((position + 1) + "");
        holder.binding.tvItem.setText(orderDetailsList.get(position).getProduct_name());
        holder.binding.tvQty.setText(orderDetailsList.get(position).getQuantity());
        holder.binding.tvPrice.setText("₹ "+orderDetailsList.get(position).getPrice());
        holder.binding.tvTotal.setText("₹ "+Double.parseDouble(orderDetailsList.get(position).getPrice())*Double.parseDouble(orderDetailsList.get(position).getQuantity()));
//        holder.binding.tvDisc.setText("Discription");


    }

    @Override
    public int getItemCount() {
        if (orderDetailsList != null)
            return orderDetailsList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemOrderDetailsBinding binding;

        public ViewHolder(@NonNull ItemOrderDetailsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
