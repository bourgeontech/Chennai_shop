package com.online.estoreshop.activity;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import static com.online.estoreshop.utils.UrlConstants.REQ_CATEGORY_LIST;
import static com.online.estoreshop.utils.UrlConstants.REQ_DELETE_CATEGORY;
import static com.online.estoreshop.utils.UrlConstants.REQ_delete_sub_category;
import static com.online.estoreshop.utils.UrlConstants.REQ_subCategoryList;

public class SubcategoryActivity extends BaseActivity implements View.OnClickListener, ClickedItem, TextWatcher, VolleyInterface {

    ActivityManageItemBinding binding;
    ProductAdapter adapter;
    ArrayList<CategoryDomain> productList = new ArrayList<>();

    String shopId;
    String shopName;

    ArrayList<String> selectedList = new ArrayList<>();

    String category_id;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_item);

        category_id = getIntent().getStringExtra("category_id");
        category = getIntent().getStringExtra("category");

        shopId = sharedPreferences.getString(Constants.SHOP_ID, "");
        shopName = sharedPreferences.getString(Constants.SHOP_NAME, "");

        mActivity = this;

        liseteners();

        setupRecyclerView();

        binding.add.setVisibility(View.GONE);
        binding.home.setVisibility(View.VISIBLE);
        binding.titleImage.setText(shopName);
        binding.status.setText(category);
        binding.edtSearch.setHint("Search subcategories");
        binding.tvTxt.setText("Subcategories");

    }

    @Override
    protected void onResume() {
        super.onResume();
        callService();
    }

    private void liseteners() {

        binding.btnAdd.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.status.setOnClickListener(this);
        binding.home.setOnClickListener(this);
        binding.edtSearch.addTextChangedListener(this);
    }

    private void callService() {

        if (CommonUtils.checkConnectivity(mActivity)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("shop_id", shopId);
            params.put("category_ids", category_id);
            String url = UrlConstants.subCategoryList;
            volleyUtils.callApi(mActivity, this, url, REQ_subCategoryList, params, 1, true);
        } else {
            Toast.makeText(mActivity, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }


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
                Intent intent2 = new Intent(mActivity, AddSubcategoryActivity.class);
                intent2.putExtra("category_id", category_id);
//                intent2.putExtra("category_id", category_id);
                startActivity(intent2);
                break;
            case R.id.back:
            case R.id.status:
                finish();
                break;
            case R.id.home:
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
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

            Intent intent = new Intent(mActivity, ViewProductActivity.class);
            intent.putExtra("sub_category_id", productDomain.getCategory_id());
            intent.putExtra("category_id", category_id);
            intent.putExtra("category", category);
            intent.putExtra("category_title", productDomain.getCategory_title());
            startActivityForResult(intent, 501);
        } else {
//          final CharSequence[] items = {"DELETE", "EDIT"};
            final CharSequence[] items = {"EDIT"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("DELETE")) {
                        deleteCategory(productDomain.getCategory_id());
                    } else if (items[item].equals("EDIT")) {
                        Intent intent = new Intent(mActivity, EditSubcategoryActivity.class);
                        Gson gson = new Gson();
                        intent.putExtra("category_id", category_id);
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

        if (CommonUtils.checkConnectivity(mActivity)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("shop_id", shopId);
            params.put("sub_category_id", category_id);
            String url = UrlConstants.delete_sub_category;
            volleyUtils.callApi(mActivity, this, url, REQ_delete_sub_category, params, 1, true);
        } else {
            Toast.makeText(mActivity, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("controls-->", "here" + requestCode + " " + resultCode);
        if (resultCode == 501) {
            Log.d("controls-->", "here1");
            finish();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuinfo) {
        super.onCreateContextMenu(menu, view, menuinfo);
        menu.setHeaderTitle("Choose");
        menu.add(menu.FIRST, Menu.NONE, 0, "DELETE CATEGORY");

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

        if (newList.size() > 0) {
            binding.emptyLayout.setVisibility(View.GONE);
            binding.recylerView.setVisibility(View.VISIBLE);
            adapter.productList = newList;
            adapter.notifyDataSetChanged();
            binding.recylerView.scheduleLayoutAnimation();
        } else {
            binding.emptyLayout.setVisibility(View.VISIBLE);
            binding.recylerView.setVisibility(View.GONE);
            binding.tvEmpty.setText("No subcategories");
        }

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_subCategoryList) {
            categoryListResponse(response);
        } else if (requestcode == REQ_delete_sub_category) {
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
                Toast.makeText(SubcategoryActivity.this, response.getString("status"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void categoryListResponse(JSONObject response) {
        try {
            productList.clear();
            if (response.getString("code").equals("1")) {

                JSONArray category = response.getJSONArray("sub_category");

                for (int i = 0; i < category.length(); i++) {

                    String catid = category_id;
                    JSONObject jsonObject = category.getJSONObject(i);
                    String category_id = jsonObject.getString("sub_category_id");
                    String category_title = jsonObject.getString("sub_category_title");
                    String category_description = jsonObject.getString("sub_category_description");
                    String category_image = jsonObject.getString("sub_category_image");

                    productList.add(new CategoryDomain(catid, category_id, category_title, category_description, category_image));

                }

                if (productList.size() == 0) {
                    binding.emptyLayout.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setText("No subcategories found");
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