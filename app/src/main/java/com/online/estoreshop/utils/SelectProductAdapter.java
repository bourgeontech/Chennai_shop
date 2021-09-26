package com.online.estoreshop.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.Base64;
import android.util.Log;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.online.estoreshop.R;
import com.online.estoreshop.databinding.ItemSelectCatBinding;
import com.online.estoreshop.databinding.ItemSelectProdBinding;
import com.online.estoreshop.interfaces.ClickedItem;
import com.online.estoreshop.interfaces.ClickedItem2;
import com.online.estoreshop.models.CategoryDomain;
import com.online.estoreshop.models.ProductDomain;

import java.util.ArrayList;

public class SelectProductAdapter extends RecyclerView.Adapter<SelectProductAdapter.ViewHolder> {

    Context context;
    public ArrayList<ProductDomain> productList;
    ClickedItem2 clickedItem;
    public ArrayList<String> selectedList = new ArrayList<>();
    public boolean needChange = true;

    public SelectProductAdapter(Context context, ArrayList<ProductDomain> productList, ClickedItem2 clickedItem, ArrayList<String> selectedList) {
        this.context = context;
        this.productList = productList;
        this.clickedItem = clickedItem;
        this.selectedList = selectedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSelectProdBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_select_prod, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final boolean[] ischecked = {false};

        if (selectedList.contains(productList.get(position).getMajor_id())) {
            holder.binding.checkbox.setImageResource(R.drawable.checkbox_filled);
        } else {
            holder.binding.checkbox.setImageResource(R.drawable.checkbox_empty);
        }


        Log.d("Category_image-->", productList.get(position).getProduct_image());

            try {
                byte[] decodedString = Base64.decode(productList.get(position).getProduct_image(), Base64.NO_WRAP);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(50));

                Glide.with(context)
                        .load(decodedByte)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .apply(requestOptions)
                        .dontAnimate()
                        .placeholder(R.drawable.curved_white_yellow_border)
                        .into(holder.binding.productImage);

            } catch (Exception e) {
                e.printStackTrace();
            }



        holder.binding.productName.setText(productList.get(position).getProduct_name());
        holder.binding.tvOriginalPrice.setText("Rs " + productList.get(position).getOrginal_price());
        holder.binding.tvSpecialPrice.setText("Rs " + productList.get(position).getSpecial_price());
        holder.binding.tvDiscount.setText("Save " + productList.get(position).getDiscount() + "%");


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                    holder.binding.checkbox.setImageResource(R.drawable.checkbox_empty);
                //                    holder.binding.checkbox.setImageResource(R.drawable.checkbox_filled);
                ischecked[0] = !ischecked[0];
                clickedItem.clicked("check", productList.get(position), position);
//                clickedItem.clicked("click", productList.get(position));
            }
        });
        holder.binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedItem.clicked("update", productList.get(position), position);
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

        ItemSelectProdBinding binding;

        public ViewHolder(@NonNull ItemSelectProdBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.tvOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}
