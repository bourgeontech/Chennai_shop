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
import android.view.ContextMenu;
import android.view.Menu;
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
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityManageItemBinding;
import com.online.estoreshop.interfaces.ClickedItem;
import com.online.estoreshop.models.CategoryDomain;
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

import static com.online.estoreshop.utils.UrlConstants.REQ_CATEGORY_LIST;
import static com.online.estoreshop.utils.UrlConstants.REQ_DELETE_CATEGORY;

public class ManageItemActivity extends BaseActivity implements View.OnClickListener, ClickedItem, TextWatcher, VolleyInterface {

    ActivityManageItemBinding binding;
    ProductAdapter adapter;
    ArrayList<CategoryDomain> productList = new ArrayList<>();

    String shopId;
    String shopName;

    ArrayList<String> selectedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_item);

        shopId = sharedPreferences.getString(Constants.SHOP_ID, "");
        shopName = sharedPreferences.getString(Constants.SHOP_NAME, "");

        mActivity = this;

        liseteners();

        setupRecyclerView();

//        callService();

        binding.titleImage.setText(shopName);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonUtils.checkConnectivity(this)) {
            callService();
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void liseteners() {

        binding.btnAdd.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.add.setOnClickListener(this);
        binding.edtSearch.addTextChangedListener(this);
    }

    private void callService() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("shop_id", shopId);
        String url = UrlConstants.CATEGORY_LIST;
        volleyUtils.callApi(mActivity, this, url, REQ_CATEGORY_LIST, params, 1, true);

    }

    private void setupRecyclerView() {
        binding.recylerView.setHasFixedSize(true);
        binding.recylerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new ProductAdapter(mActivity, productList, this);
        binding.recylerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                startActivity(new Intent(mActivity, AddcategoryActivity.class));
                break;
            case R.id.back:
                finish();
                break; case R.id.add:
                startActivity(new Intent(mActivity, AutoActivity.class));
                break;
        }
    }

    private String getcatIds() {

        return android.text.TextUtils.join(",", selectedList);
    }

    @Override
    public void clicked(String type, Object object) {

        final CategoryDomain productDomain = (CategoryDomain) object;
        if (type.equals("check")) {
            checkedOnProduct(productDomain);
        } else if (type.equals("click")) {

            Intent intent = new Intent(mActivity, SubcategoryActivity.class);
            intent.putExtra("category_id", productDomain.getCategory_id());
            intent.putExtra("category", productDomain.getCategory_title());
            startActivity(intent);

        } else {

//            final CharSequence[] items = {"DELETE", "EDIT"};
            final CharSequence[] items = {"EDIT"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                    if (items[item].equals("DELETE")) {
                        deleteCategory(productDomain.getCategory_id());
                    } else if (items[item].equals("EDIT")) {
                        Intent intent = new Intent(mActivity, EditcategoryActivity.class);
                        Gson gson = new Gson();
                        intent.putExtra("productData", gson.toJson(productDomain));
                        startActivity(intent);
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void deleteCategory(String category_id) {


        HashMap<String, Object> params = new HashMap<>();
        params.put("shop_id", shopId);
        params.put("category_id", category_id);
        String url = UrlConstants.DELETE_CATEGORY;
        volleyUtils.callApi(mActivity, this, url, REQ_DELETE_CATEGORY, params, 1, true);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuinfo) {
        super.onCreateContextMenu(menu, view, menuinfo);
        menu.setHeaderTitle("Choose");
        menu.add(menu.FIRST, Menu.NONE, 0, "DELETE CATEGORY");
        menu.add(menu.FIRST, Menu.NONE, 1, "EDIT");

    }

    private void checkedOnProduct(CategoryDomain productDomain) {

        if (selectedList.contains(productDomain.getCategory_id())) {
            selectedList.remove(productDomain.getCategory_id());
        } else {
            selectedList.add(productDomain.getCategory_id());
        }

        adapter.selectedList = selectedList;

        if (selectedList.size() > 0) {
            binding.btnNext.setVisibility(View.VISIBLE);
        } else {
            binding.btnNext.setVisibility(View.GONE);
        }

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

        ArrayList<CategoryDomain> newList = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {

            if (productList.get(i).getCategory_title().toLowerCase().contains(toString.toLowerCase())) {
                newList.add(productList.get(i));
            }
        }

        adapter.productList = newList;
        adapter.notifyDataSetChanged();
        binding.recylerView.scheduleLayoutAnimation();

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_CATEGORY_LIST) {
            categoryListResponse(response);
        } else if (requestcode == REQ_DELETE_CATEGORY) {
            deleteCategoryResponse(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void deleteCategoryResponse(JSONObject response) {
        try {
            Log.d("response-->", response.toString());
            if (response.getString("code").equals("1")) {
                callService();
            } else {
                Toast.makeText(ManageItemActivity.this, response.getString("status"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void categoryListResponse(JSONObject response) {
        try {
            productList.clear();
            if (response.getString("code").equals("1")) {

                JSONArray category = response.getJSONArray("category");

                for (int i = 0; i < category.length(); i++) {

                    JSONObject jsonObject = category.getJSONObject(i);
                    String category_id = jsonObject.getString("category_id");
                    String category_title = jsonObject.getString("category_title");
                    String category_description = jsonObject.getString("category_description");
                    String category_image = jsonObject.getString("category_image");

                    productList.add(new CategoryDomain(category_id, category_title, category_description, category_image));

                }

                if (productList.size()==0){
                    binding.emptyLayout.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setText("No categories found.");
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