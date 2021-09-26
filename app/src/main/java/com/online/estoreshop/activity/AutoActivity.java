package com.online.estoreshop.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
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
import com.online.estoreshop.R;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityAutoBinding;
import com.online.estoreshop.models.CategoryDomain;
import com.online.estoreshop.models.ProductDomain;
import com.online.estoreshop.utils.CategoryFragment;
import com.online.estoreshop.utils.CommonUtils;
import com.online.estoreshop.utils.Constants;
import com.online.estoreshop.utils.ProductFragment;
import com.online.estoreshop.utils.RegisterSuccessFragment;
import com.online.estoreshop.utils.SubCategoryFragment;
import com.online.estoreshop.utils.UrlConstants;
import com.online.estoreshop.volleyservice.VolleyInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.online.estoreshop.utils.UrlConstants.REQ_majorList;
import static com.online.estoreshop.utils.UrlConstants.REQ_majorList2;
import static com.online.estoreshop.utils.UrlConstants.REQ_majorList3;

public class AutoActivity extends BaseActivity implements VolleyInterface, CategoryFragment.OptionListener, SubCategoryFragment.OptionListener2, ProductFragment.OptionListener3 {

    ActivityAutoBinding binding;
    CategoryFragment categoryFragment;
    SubCategoryFragment subCategoryFragment;
    ProductFragment productFragment;
    ArrayList<CategoryDomain> catList = new ArrayList<>();
    ArrayList<CategoryDomain> subcatList = new ArrayList<>();
    String catid = "";
    String subcatid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auto);

        mActivity = this;

        getCategory();

        binding.selectCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory();

            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cat = binding.cat.getText().toString();
                String subcat = binding.subcat.getText().toString();
                if (!cat.equals("") && !subcat.equals("")) {
                    Intent intent = new Intent(mActivity, AutoProductActivity.class);
                    intent.putExtra("cat", cat);
                    intent.putExtra("subcat", subcat);
                    intent.putExtra("subcatid", subcatid);
                    startActivity(intent);
//                    getProducts(subcat);
                } else {
                    Toast.makeText(mActivity, "Please select category and subcategory", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.selectSubcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cat.getText().toString().equals("")) {
                    Toast.makeText(mActivity, "Please select category", Toast.LENGTH_SHORT).show();
                } else {
                    getSubcategory();
                }
            }
        });

    }

    private void getProducts(String subcat) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("listing_flag", "product");
        params.put("item", subcat);

        String url = UrlConstants.majorList;
        volleyUtils.callApi(mActivity, this, url, REQ_majorList3, params, 1, true);
    }

    private void getSubcategory() {

        HashMap<String, Object> params = new HashMap<>();

        params.put("listing_flag", "sub_category");
        params.put("item_id", catid);

        String url = UrlConstants.majorList;
        volleyUtils.callApi(mActivity, this, url, REQ_majorList2, params, 1, true);
    }

    private void selectCategory() {
        if (catList.size() > 0) {
            categoryFragment = new CategoryFragment(catList);
            categoryFragment.setOptionListener(this);
            categoryFragment.setCancelable(true);
            categoryFragment.show(getSupportFragmentManager(), categoryFragment.getTag());
        } else {
            Toast.makeText(mActivity, "Category list is loading... Please wait", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCategory() {
        HashMap<String, Object> params = new HashMap<>();

        params.put("listing_flag", "category");
        params.put("item_id", "");

        String url = UrlConstants.majorList;
        volleyUtils.callApi(mActivity, this, url, REQ_majorList, params, 1, true);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_majorList) {
            getCategoryResponse(response);
        } else if (requestcode == REQ_majorList2) {
            getSubCategoryResponse(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    @Override
    public void optionSelected(CategoryDomain categoryDomain) {
        binding.cat.setText(categoryDomain.getCategory_title());
        catid = categoryDomain.getCategory_id();
        binding.subcat.setText("");
    }

    private void getCategoryResponse(JSONObject response) {
        try {
            catList.clear();
            String code = response.getString("code");
            if (code.equals("1")) {
                JSONArray category = response.getJSONArray("category");

                for (int i = 0; i < category.length(); i++) {

                    JSONObject jsonObject = category.getJSONObject(i);
                    String category_id = jsonObject.getString("category_id");
                    String category_title = jsonObject.getString("category_name");
                    String category_description = "";
                    String category_image = jsonObject.getString("category_img");

                    catList.add(new CategoryDomain(category_id, category_title, category_description, category_image));

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSubCategoryResponse(JSONObject response) {
        try {

            subcatList.clear();
            String code = response.getString("code");
            if (code.equals("1")) {
                JSONArray sub_category = response.getJSONArray("sub_category");

                for (int i = 0; i < sub_category.length(); i++) {

                    JSONObject jsonObject = sub_category.getJSONObject(i);
                    String category_id = "";
                    category_id = jsonObject.getString("sub_category_id");
                    String category_title = jsonObject.getString("sub_category_name");
                    String category_description = "";
                    String category_image = jsonObject.getString("sub_category_img");

                    subcatList.add(new CategoryDomain(category_id, category_title, category_description, category_image));

                }
                if (subcatList.size() > 0) {
                    subCategoryFragment = new SubCategoryFragment(subcatList);
                    subCategoryFragment.setOptionListener2(this);
                    subCategoryFragment.setCancelable(true);
                    subCategoryFragment.show(getSupportFragmentManager(), subCategoryFragment.getTag());
                } else {
                    Toast.makeText(mActivity, "Category list is loading... Please wait", Toast.LENGTH_SHORT).show();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void optionSelected2(CategoryDomain categoryDomain) {
        subcatid = categoryDomain.getCategory_id();
        binding.subcat.setText(categoryDomain.getCategory_title());
    }

    @Override
    public void optionSelected3(ArrayList<ProductDomain> selectedList) {

        String url = UrlConstants.addProductFromMajor;
        HashMap<String, Object> params = new HashMap<>();

        final JSONArray jsonArray = new JSONArray();


        try {
            for (int i = 0; i < selectedList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("shop_id", sharedPreferences.getString(Constants.SHOP_ID, ""));
                jsonObject.put("major_id", selectedList.get(i).getShop_id());
                jsonObject.put("product_name", selectedList.get(i).getProduct_name());
                jsonObject.put("orginal_price", selectedList.get(i).getOrginal_price());
                jsonObject.put("special_price", selectedList.get(i).getSpecial_price());
                jsonObject.put("discount", selectedList.get(i).getDiscount());
                jsonObject.put("unit", selectedList.get(i).getUnit());
                jsonObject.put("product_image", selectedList.get(i).getProduct_image());
                jsonObject.put("stock_availability", "1");
                jsonObject.put("show_for_sale", "1");
//                jsonObject.put("unit", cartList.get(i).uni());
//                jsonObject.put("unit", "Kg");

                jsonArray.put(jsonObject);
            }

            final JSONObject mainObj = new JSONObject();
            mainObj.put("", jsonArray);

            Log.d("jsonArray-->", jsonArray.toString());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            CommonUtils.cancelProgressBar();

                            try {
                                JSONObject response = new JSONObject(s);
                                Log.d("response-->", response.toString());
                                String code = response.getString("code");
                                if (code.equals("1")) {
                                    Toast.makeText(mActivity, "Products added successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
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
                public byte[] getBody() {
                    return jsonArray.toString().getBytes();
                }
            };


            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(stringRequest);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}