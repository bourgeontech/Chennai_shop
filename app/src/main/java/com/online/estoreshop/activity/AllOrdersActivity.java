package com.online.estoreshop.activity;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.online.estoreshop.R;
import com.online.estoreshop.adapter.OrdersAdapter;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityAllOrdersBinding;
import com.online.estoreshop.models.OrderDomain;
import com.online.estoreshop.models.PaymentsDomain;
import com.online.estoreshop.utils.CommonUtils;
import com.online.estoreshop.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class AllOrdersActivity extends BaseActivity {

    ActivityAllOrdersBinding binding;
    OrdersAdapter adapter;
    ArrayList<OrderDomain> orderList = new ArrayList<>();
    ArrayList<PaymentsDomain> paymentList = new ArrayList<>();
    String shopId;
    String shopName;
    String paymentmethod;
    String type;
    final Calendar myCalendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_orders);

        shopId = sharedPreferences.getString(Constants.SHOP_ID, "");
        shopName = sharedPreferences.getString(Constants.SHOP_NAME, "");

        type = getIntent().getStringExtra("type");

        mActivity = this;

        setupRecyclerview();

        switch (type) {
            case "Undelivered":
                binding.titleImage.setText("Undelivered Orders");
                binding.selectDateLayout.setVisibility(View.VISIBLE);
                break;
            case "Total":
                binding.titleImage.setText("All Orders");
                binding.selectDateLayout.setVisibility(View.GONE);
                break;
            case "Delivered":
                binding.titleImage.setText("Delivered Orders");
                binding.selectDateLayout.setVisibility(View.VISIBLE);
                break;
        }

        binding.tvDate.setText(CommonUtils.getTodayDate2());

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(mActivity, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonUtils.checkConnectivity(this)) {
            getAllOrders("");
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat df = new SimpleDateFormat("dd EEEE, MMMM yyyy", Locale.US);
            binding.tvDate.setText(df.format(myCalendar.getTime()));

            String myFormat = "dd-MM-yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            if (CommonUtils.checkConnectivity(AllOrdersActivity.this)) {
                getAllOrders(sdf.format(myCalendar.getTime()));
            } else {
                Toast.makeText(AllOrdersActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }

        }

    };

    private void getAllOrders(final String s) {

        orderList.clear();
        paymentList.clear();
        CommonUtils.setProgressBar(mActivity);

        final JSONObject params = new JSONObject();
        try {
            params.put("shop_id", shopId);
            params.put("delivery_status", type);

            if (!type.equals("Total")) {
                if (s.equals(""))
                    params.put("order_date", CommonUtils.getTodayDate());
                else
                    params.put("order_date", s);
            }

            Log.d("request-->", params.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://day2night.in/shop/orderList.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        CommonUtils.cancelProgressBar();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response-->", obj.toString());
                            if (obj.getString("code").equals("1")) {

                                JSONArray orders = obj.getJSONArray("orders");
                                JSONArray payments = obj.getJSONArray("payments");

                                for (int i = 0; i < orders.length(); i++) {
                                    JSONObject object = orders.getJSONObject(i);

                                    String shop_id = shopId;
                                    String customer_id = object.getString("customer_id");
                                    String order_id = object.getString("order_id");
                                    String order_date = object.getString("order_date");
                                    String order_time = object.getString("order_time");
                                    String customer_name = object.getString("customer_name");
                                    String phone = object.getString("phone");
                                    String address = object.getString("address");
                                    String landmark = object.getString("landmark");
                                    String payable_amount = object.getString("payable_amount");
                                    String getCustomer_Order_Id = object.getString("customer_order_id");


                                    String status = "";
                                    if (object.has("delivery_status")){
                                        status = object.getString("delivery_status");
                                    }
                                    JSONObject paymentobject = payments.getJSONObject(i);
                                    String payment_status = paymentobject.getString("payment_status");
                                    String rz_payable_amount = paymentobject.getString("payable_amount");
                                    String payment_method = paymentobject.getString("payment_method");
                                    String raz_payment_id = paymentobject.getString("raz_payment_id");

                                    orderList.add(new OrderDomain(shop_id, customer_id, order_id, order_date, order_time, customer_name, phone, address,
                                            landmark, payable_amount,status,getCustomer_Order_Id));
                                    paymentList.add(new PaymentsDomain(order_id,rz_payable_amount, payment_status, payment_method, raz_payment_id));


                                }

                                if (orderList.size() == 0) {
                                    binding.emptyLayout.setVisibility(View.VISIBLE);
                                    binding.tvEmpty.setText("No orders");
                                } else {
                                    binding.emptyLayout.setVisibility(View.GONE);
                                    adapter.notifyDataSetChanged();
                                    binding.recylerView.scheduleLayoutAnimation();
                                }


                            } else {
                                binding.emptyLayout.setVisibility(View.VISIBLE);
                                binding.tvEmpty.setText(obj.getString("status"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtils.cancelProgressBar();
                binding.emptyLayout.setVisibility(View.VISIBLE);
                binding.tvEmpty.setText("No orders");
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

    private void setupRecyclerview() {

        binding.recylerView.setHasFixedSize(true);
        binding.recylerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new OrdersAdapter(this,mActivity, orderList, paymentList, type);
        binding.recylerView.setAdapter(adapter);

    }
}