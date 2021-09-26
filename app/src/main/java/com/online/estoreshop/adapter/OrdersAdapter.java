package com.online.estoreshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.online.estoreshop.R;
import com.online.estoreshop.activity.OrderDetailsActivity;
import com.online.estoreshop.activity.OtpActivity;
import com.online.estoreshop.activity.RegisterActivity;
import com.online.estoreshop.databinding.ItemOrdersBinding;
import com.online.estoreshop.databinding.ItemProductBinding;
import com.online.estoreshop.models.OrderDomain;
import com.online.estoreshop.models.PaymentsDomain;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    Activity activity;
    Context context;
    ArrayList<OrderDomain> orderList;
    ArrayList<PaymentsDomain> paymentList;
    String type;

    public OrdersAdapter(Activity activity,Context context, ArrayList<OrderDomain> orderList, ArrayList<PaymentsDomain> paymentList, String type) {
        this.context = context;
        this.orderList = orderList;
        this.paymentList = paymentList;
        this.type = type;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrdersBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_orders, parent, false);

        return new OrdersAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.binding.btnSeeList.setText(orderList.get(position).getPayable_amount()+"/-");
        holder.binding.tvName.setText(orderList.get(position).getCustomer_name()+"-"+orderList.get(position).getOrder_id());
        holder.binding.tvDate.setText("Order Time : " + orderList.get(position).getOrder_date() + " - " + orderList.get(position).getOrder_time());
        holder.binding.tvLocation.setText(orderList.get(position).getLandmark() + ", " + orderList.get(position).getAddress());
            String payment_status=paymentList.get(position).getPayment_status();
         if (payment_status.equals("Payment Received")){
            holder.binding.tvPhone.setTextColor(Color.parseColor("#3a873a"));
         }else {
              holder.binding.tvPhone.setTextColor(Color.parseColor("#ff0000"));
         }
        holder.binding.tvPhone.setText(payment_status);

        holder.binding.tvPhone.setText( paymentList.get(position).getPayment_status());
         String status=orderList.get(position).getDelivery_status().toLowerCase();
        //Toast.makeText(context, "status:"+status    , Toast.LENGTH_SHORT).show();
        if (status.equals("delivered")){
            holder.binding.status.setImageResource(R.drawable.circle_green);
            holder.binding.pickedup.setText("Delivered");
            holder.binding.pickedup.setVisibility(View.VISIBLE);
        }
        else if(status.equals("picked-up")) {
            holder.binding.status.setImageResource(R.drawable.circle_yellow);
            holder.binding.pickedup.setText("Rider Picked Up");
            holder.binding.pickedup.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.binding.status.setImageResource(R.drawable.circle_red);
        }

//        holder.binding.btnSeeList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, OrderDetailsActivity.class);
//                Gson gson = new Gson();
//                Gson pgson = new Gson();
//                intent.putExtra("orderData", gson.toJson(orderList.get(position)));
//                intent.putExtra("paymentData", pgson.toJson(paymentList.get(position)));
//                intent.putExtra("type", type);
//                context.startActivity(intent);
//            }
//        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                Gson gson = new Gson();
                Gson pgson = new Gson();
                intent.putExtra("orderData", gson.toJson(orderList.get(position)));
                intent.putExtra("paymentData", pgson.toJson(paymentList.get(position)));
                intent.putExtra("type", type);
                context.startActivity(intent);
                activity.finish();

            }
        });


    }

    @Override
    public int getItemCount() {
        if (orderList != null)
            return orderList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemOrdersBinding binding;

        public ViewHolder(@NonNull ItemOrdersBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
