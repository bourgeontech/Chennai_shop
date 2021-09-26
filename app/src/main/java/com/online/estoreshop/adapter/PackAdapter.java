package com.online.estoreshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.online.estoreshop.R;
import com.online.estoreshop.activity.PackActivity;
import com.online.estoreshop.databinding.ItemNotificationsBinding;
import com.online.estoreshop.databinding.ItemPackBinding;
import com.online.estoreshop.interfaces.ClickedItem;
import com.online.estoreshop.models.PackDomain;

import java.util.ArrayList;

public class PackAdapter extends RecyclerView.Adapter<PackAdapter.ViewHolder> {

    Context context;
    ArrayList<PackDomain> packList;
    ClickedItem clickedItem;
    String pname;

    public PackAdapter(Context context, ArrayList<PackDomain> packList, String pname, ClickedItem clickedItem) {
        this.context = context;
        this.packList = packList;
        this.clickedItem = clickedItem;
        this.pname = pname;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPackBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_pack, parent, false);

        return new PackAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.binding.tvTitle.setText(pname);
        holder.binding.tvDescription.setText(packList.get(position).getDescription());
        holder.binding.tvPrice.setText("Org Price : " + packList.get(position).getOrginal_price() +
                ", Special Price : " + packList.get(position).getSpecial_price() + ", Discount : " + packList.get(position).getDiscount());
        holder.binding.tvQty.setText("Qty : " + packList.get(position).getQuantity() + packList.get(position).getUnit());

        holder.binding.imgDeletePack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedItem.clicked("delete",packList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (packList != null)
            return packList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemPackBinding binding;

        public ViewHolder(@NonNull ItemPackBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
