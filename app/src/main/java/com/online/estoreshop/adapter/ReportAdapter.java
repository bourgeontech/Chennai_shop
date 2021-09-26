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
import com.online.estoreshop.databinding.ItemOrdersBinding;
import com.online.estoreshop.databinding.ItemReportBinding;
import com.online.estoreshop.models.ReportDomain;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    Context context;
    ArrayList<ReportDomain> reportList;

    public ReportAdapter(Context context, ArrayList<ReportDomain> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReportBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_report, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.tvNum.setText((position + 1) + "");
        holder.binding.tvCustomer.setText(reportList.get(position).getCustomer_name());

        int quantity = Integer.parseInt(reportList.get(position).getQuantity());
        double price = Double.parseDouble(reportList.get(position).getPrice());

        holder.binding.tvPrice.setText((quantity * price) + "");
        holder.binding.tvProduct.setText(reportList.get(position).getProduct_name());
        holder.binding.tvQuantity.setText(reportList.get(position).getQuantity());
    }

    @Override
    public int getItemCount() {
        if (reportList != null)
            return reportList.size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemReportBinding binding;

        public ViewHolder(@NonNull ItemReportBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
