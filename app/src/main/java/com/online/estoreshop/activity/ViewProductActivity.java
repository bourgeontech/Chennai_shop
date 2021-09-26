package com.online.estoreshop.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.online.estoreshop.R;
import com.online.estoreshop.adapter.ProductAdapter;
import com.online.estoreshop.adapter.ViewProductAdapter;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityViewProductsBinding;
import com.online.estoreshop.interfaces.ClickedItem;
import com.online.estoreshop.models.CategoryDomain;
import com.online.estoreshop.models.ProductDomain;
import com.online.estoreshop.utils.CommonUtils;
import com.online.estoreshop.utils.Constants;
import com.online.estoreshop.utils.UrlConstants;
import com.online.estoreshop.volleyservice.VolleyInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.online.estoreshop.utils.UrlConstants.REQ_delete_sub_category;
import static com.online.estoreshop.utils.UrlConstants.REQ_productList;
import static com.online.estoreshop.utils.UrlConstants.REQ_subCategoryList;

public class ViewProductActivity extends BaseActivity implements View.OnClickListener, ClickedItem, TextWatcher, VolleyInterface {

    ActivityViewProductsBinding binding;
    String shopId;
    String shopName;
    ViewProductAdapter adapter;
    ArrayList<ProductDomain> productList = new ArrayList<>();


    String category_id;
    String category;
    String sub_category_id;
    String category_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_products);

        mActivity = this;

        shopId = sharedPreferences.getString(Constants.SHOP_ID, "");
        shopName = sharedPreferences.getString(Constants.SHOP_NAME, "");

        category_id = getIntent().getStringExtra("category_id");
        category = getIntent().getStringExtra("category");
        sub_category_id = getIntent().getStringExtra("sub_category_id");
        category_title = getIntent().getStringExtra("category_title");

        binding.back.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.tvCat.setOnClickListener(this);
        binding.tvCat2.setOnClickListener(this);
        binding.home.setOnClickListener(this);
        binding.edtSearch.addTextChangedListener(this);

        setupRecyclerView();


        binding.titleImage.setText(shopName);
        binding.tvCat.setText("Home");
        binding.tvCat2.setText(category);
        binding.tvCat3.setText(category_title);

    }

    @Override
    protected void onResume() {
        super.onResume();
        callService();
    }

    private void callService() {

        if (CommonUtils.checkConnectivity(mActivity)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("shop_id", shopId);
            params.put("category_ids", category_id);
            params.put("sub_category_ids", sub_category_id);
            String url = UrlConstants.productList;
            volleyUtils.callApi(mActivity, this, url, REQ_productList, params, 1, true);
        } else {
            Toast.makeText(mActivity, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }


    }

    private void deleteProduct(ProductDomain productDomain) {

        CommonUtils.setProgressBar(mActivity);

        final JSONObject params = new JSONObject();
        try {
            params.put("shop_id", shopId);
            params.put("category_id", productDomain.getCategory_id());
            params.put("product_id", productDomain.getProduct_id());

            Log.d("request-->", params.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://day2night.in/shop/deleteProduct.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        CommonUtils.cancelProgressBar();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response-->", obj.toString());
                            if (obj.getString("code").equals("1")) {
                                callService();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtils.cancelProgressBar();
                Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return params.toString().getBytes();
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }


    private void setupRecyclerView() {
        binding.recylerView.setHasFixedSize(true);
        binding.recylerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new ViewProductAdapter(mActivity, productList, this);
        binding.recylerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tvCat2: {
                Intent intent = new Intent();
                setResult(502, intent);
                finish();
                break;
            }
            case R.id.btnAdd: {
                Intent intent = new Intent(mActivity, AddProductActivity.class);
                intent.putExtra("category_id", category_id);
                intent.putExtra("sub_category_id", sub_category_id);
                startActivity(intent);
                break;
            }

            case R.id.tvCat: {
                Intent intent = new Intent();
                setResult(501, intent);
                finish();
                break;
            } case R.id.home: {
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void clicked(String type, Object object) {

        ProductDomain productDomain = (ProductDomain) object;
        if (type.equals("delete")) {
            showDeleteDialog(productDomain);
        } else {
            Intent intent = new Intent(mActivity, EditProductActivity.class);
            Gson gson = new Gson();
            intent.putExtra("productData", gson.toJson(productDomain));
            intent.putExtra("sub_category_id", sub_category_id);
            startActivity(intent);
        }
    }


    private void showDeleteDialog(final ProductDomain productDomain) {
        new AlertDialog.Builder(mActivity)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (CommonUtils.checkConnectivity(mActivity)) {
                            deleteProduct(productDomain);
                        } else {
                            Toast.makeText(mActivity, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        filter(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void filter(String toString) {

        ArrayList<ProductDomain> newList = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {

            if (productList.get(i).getProduct_name().toLowerCase().contains(toString.toLowerCase())) {
                newList.add(productList.get(i));
            }
        }
        adapter.productList = newList;
        adapter.notifyDataSetChanged();
        binding.recylerView.scheduleLayoutAnimation();

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_productList) {
            productListResponse(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void productListResponse(JSONObject response) {
        try {

            productList.clear();
            Log.d("response-->", response.toString());
            if (response.getString("code").equals("1")) {

                JSONArray category = response.getJSONArray("product");

                for (int i = 0; i < category.length(); i++) {

                    JSONObject jsonObject = category.getJSONObject(i);
                    String product_id = jsonObject.getString("product_id");
                    String shop_id = shopId;
                    String category_id = jsonObject.getString("category_id");
                    String product_name = jsonObject.getString("product_name");
                    String orginal_price = jsonObject.getString("orginal_price");
                    String special_price = jsonObject.getString("special_price");
                    String discount = jsonObject.getString("discount");
                    String stock_availability = jsonObject.getString("stock_availability");
                    String unit = "Kg";
                    if (jsonObject.has("unit")) {
                        unit = jsonObject.getString("unit");
                    }
                    String product_image = jsonObject.getString("product_image");

                    productList.add(new ProductDomain(product_id, shop_id, category_id,
                            product_name, orginal_price, special_price, discount, unit, product_image, stock_availability));
                }

                if (productList.size() == 0) {
                    binding.emptyLayout.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setText("No products under this category");
                } else {
                    binding.emptyLayout.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    binding.recylerView.scheduleLayoutAnimation();
                }

            } else {
                binding.emptyLayout.setVisibility(View.VISIBLE);
                binding.tvEmpty.setText(response.getString("status"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}