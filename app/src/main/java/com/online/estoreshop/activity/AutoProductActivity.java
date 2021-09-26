package com.online.estoreshop.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.online.estoreshop.R;
import com.online.estoreshop.adapter.ViewProductAdapter;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityAutoProductBinding;
import com.online.estoreshop.interfaces.ClickedItem;
import com.online.estoreshop.interfaces.ClickedItem2;
import com.online.estoreshop.models.ProductDomain;
import com.online.estoreshop.utils.CommonUtils;
import com.online.estoreshop.utils.Constants;
import com.online.estoreshop.utils.ProductFragment;
import com.online.estoreshop.utils.SelectProductAdapter;
import com.online.estoreshop.utils.UrlConstants;
import com.online.estoreshop.volleyservice.VolleyInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.online.estoreshop.utils.UrlConstants.REQ_majorList3;

public class AutoProductActivity extends BaseActivity implements ClickedItem2, VolleyInterface, TextWatcher {

    ActivityAutoProductBinding binding;
    SelectProductAdapter adapter;
    ArrayList<ProductDomain> productList = new ArrayList<>();
    ArrayList<ProductDomain> selectedList = new ArrayList<>();
    ArrayList<String> selectedIdList = new ArrayList<>();

    String cat, subcat,subcatid;

    String shopId;
    String shopName;
    int clickedPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auto_product);

        mActivity = this;

        shopId = sharedPreferences.getString(Constants.SHOP_ID, "");
        shopName = sharedPreferences.getString(Constants.SHOP_NAME, "");

        cat = getIntent().getStringExtra("cat");
        subcat = getIntent().getStringExtra("subcat");
        subcatid = getIntent().getStringExtra("subcatid");

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedList.size() > 0) {
                    addProducts();
                }
            }
        });

        binding.edtSearch.addTextChangedListener(this);


        binding.tvCat.setText(cat + " > " + subcat);
        binding.titleImage.setText(shopName);

        setupRecyclerView();

        getProducts();

    }

    private void addProducts() {

        String url = UrlConstants.addProductFromMajor;
        HashMap<String, Object> params = new HashMap<>();

        final JSONArray jsonArray = new JSONArray();


        try {
            for (int i = 0; i < selectedList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("shop_id", sharedPreferences.getString(Constants.SHOP_ID, ""));
                jsonObject.put("major_id", selectedList.get(i).getMajor_id());
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

                Log.d("majorId-->", selectedList.get(i).getMajor_id());

                jsonArray.put(jsonObject);
            }

            CommonUtils.setProgressBar(mActivity);

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
                                    finish();
                                } else {
                                    Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void setupRecyclerView() {
        binding.recylerView.setHasFixedSize(true);
        binding.recylerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new SelectProductAdapter(mActivity, productList, this, selectedIdList);
        binding.recylerView.setAdapter(adapter);

        RecyclerView.ItemAnimator animator = binding.recylerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    private void getProducts() {
        HashMap<String, Object> params = new HashMap<>();

        params.put("listing_flag", "product");
        params.put("item_id", subcatid);

        String url = UrlConstants.majorList;
        volleyUtils.callApi(mActivity, this, url, REQ_majorList3, params, 1, true);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_majorList3) {
            productResponse(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void productResponse(JSONObject response) {

        try {

            productList.clear();
            if (response.getString("code").equals("1")) {

                JSONArray category = response.getJSONArray("product");

                for (int i = 0; i < category.length(); i++) {

                    JSONObject jsonObject = category.getJSONObject(i);
                    String product_id ="";
                    String major_id = jsonObject.getString("major_id");
                    String category_id = "";
                    String shop_id = sharedPreferences.getString(Constants.SHOP_ID, "");
                    String product_name = jsonObject.getString("product_name");
                    String orginal_price = jsonObject.getString("orginal_price");
                    String special_price = jsonObject.getString("special_price");
                    String discount = jsonObject.getString("discount");
                    String stock_availability = jsonObject.getString("stock_availability");
                    String unit = "Kg";
                    if (jsonObject.has("unit")) {
                        unit = jsonObject.getString("unit");
                    }
                    String product_image = jsonObject.getString("product_img");

                    productList.add(new ProductDomain(product_id, shop_id, category_id, product_name, orginal_price,
                            special_price, discount, unit, product_image, stock_availability, major_id));
                }

                if (productList.size() > 0) {
                    adapter.notifyDataSetChanged();
                    binding.recylerView.scheduleLayoutAnimation();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clicked(String type, Object object, final int position) {
        clickedPos = position;
        final ProductDomain productDomain = (ProductDomain) object;
        if (type.equals("check")) {

            if (selectedList.contains(productDomain)) {
                selectedList.remove(productDomain);
                selectedIdList.remove(productDomain.getMajor_id());
            } else {
                selectedList.add(productDomain);
                selectedIdList.add(productDomain.getMajor_id());
            }

            if (selectedList.size() > 0) {
                binding.bottomLayout.setVisibility(View.VISIBLE);
                binding.bottomLayout2.setVisibility(View.VISIBLE);
            } else {
                binding.bottomLayout.setVisibility(View.GONE);
                binding.bottomLayout2.setVisibility(View.GONE);
            }


            adapter.selectedList = selectedIdList;
            adapter.needChange = false;
            adapter.notifyDataSetChanged();

//            setText(selectedList.size() + "/" + productList.size() + " Selected");


//            dismiss();
//            optionListener.optionSelected3(productDomain);
        } else if (type.equals("update")) {
            final Dialog rankDialog;
            rankDialog = new Dialog(mActivity, R.style.AlertDialogCustom);
            rankDialog.setContentView(R.layout.edit_product);
            rankDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rankDialog.setCancelable(true);

            final EditText edtPrice = rankDialog.findViewById(R.id.edtPrice);
            final EditText edtspecialPrize = rankDialog.findViewById(R.id.edtspecialPrize);
            final EditText edtDiscount = rankDialog.findViewById(R.id.edtDiscount);

            edtPrice.setText(productDomain.getOrginal_price());
            edtspecialPrize.setText(productDomain.getSpecial_price());
            edtDiscount.setText(productDomain.getDiscount());

            TextView updateButton = rankDialog.findViewById(R.id.rank_dialog_button);
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sPrice = edtPrice.getText().toString();
                    String sSpecialPrice = edtspecialPrize.getText().toString();
                    String sDiscount = edtDiscount.getText().toString();

                    if (!sPrice.equals("") && !sSpecialPrice.equals("") && !sDiscount.equals("")) {
                        rankDialog.dismiss();
                        update(sPrice, sSpecialPrice, sDiscount);
                    } else {
                        Toast.makeText(mActivity, "Please enter values", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            rankDialog.show();
        }
    }

    private void update(String sPrice, String sSpecialPrice, String sDiscount) {
        productList.get(clickedPos).setOrginal_price(sPrice);
        productList.get(clickedPos).setSpecial_price(sSpecialPrice);
        productList.get(clickedPos).setDiscount(sDiscount);

        for (int i = 0; i < selectedList.size(); i++) {
            if (productList.get(clickedPos).getMajor_id().equals(selectedList.get(i).getMajor_id())) {
                selectedList.get(i).setOrginal_price(sPrice);
                selectedList.get(i).setSpecial_price(sSpecialPrice);
                selectedList.get(i).setDiscount(sDiscount);
                break;
            }
        }

        adapter.notifyDataSetChanged();
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
}