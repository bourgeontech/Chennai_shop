package com.online.estoreshop.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.online.estoreshop.R;
import com.online.estoreshop.databinding.ItemProductBinding;
import com.online.estoreshop.databinding.ItemViewProductBinding;
import com.online.estoreshop.interfaces.ClickedItem;
import com.online.estoreshop.models.CategoryDomain;
import com.online.estoreshop.models.ProductDomain;

import java.util.ArrayList;

public class ViewProductAdapter extends RecyclerView.Adapter<ViewProductAdapter.ViewHolder> {

    Context context;
    public ArrayList<ProductDomain> productList;
    ClickedItem clickedItem;
    public ArrayList<String> selectedList = new ArrayList<>();

    public ViewProductAdapter(Context context, ArrayList<ProductDomain> productList, ClickedItem clickedItem) {
        this.context = context;
        this.productList = productList;
        this.clickedItem = clickedItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemViewProductBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_view_product, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final boolean[] ischecked = {false};

        try {
            byte[] decodedString = Base64.decode(productList.get(position).getProduct_image(), Base64.NO_WRAP);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(40));

            Glide.with(context)
                    .load(decodedByte)
                    .centerCrop()
                    .apply(requestOptions)
                    .placeholder(0)
                    .into(holder.binding.productImage);

        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.binding.productName.setText(productList.get(position).getProduct_name());
        holder.binding.tvOriginalPrice.setText("Rs " + productList.get(position).getOrginal_price());
        holder.binding.tvSpecialPrice.setText("Rs " + productList.get(position).getSpecial_price());

        if (!productList.get(position).getDiscount().equals("")) {
            holder.binding.tvDiscount.setText("Save " + productList.get(position).getDiscount() + "%");
        } else {
            holder.binding.tvDiscount.setText("Save 0%");
        }

        holder.binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedItem.clicked("update", productList.get(position));
            }
        });

        holder.binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedItem.clicked("delete", productList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        if (productList != null)
            return productList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemViewProductBinding binding;

        public ViewHolder(@NonNull ItemViewProductBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.tvOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        }
    }
}
