package com.online.estoreshop.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.google.gson.Gson;
import com.online.estoreshop.R;
import com.online.estoreshop.adapter.OrderDetailsAdapter;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityOrderDetailsBinding;
import com.online.estoreshop.models.OrderDetailsDomain;
import com.online.estoreshop.models.OrderDomain;
import com.online.estoreshop.models.PaymentsDomain;
import com.online.estoreshop.utils.CommonUtils;
import com.online.estoreshop.utils.Constants;
import com.online.estoreshop.volleyservice.NotiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsActivity extends BaseActivity implements View.OnClickListener, NotiInterface {

    ActivityOrderDetailsBinding binding;
    OrderDetailsAdapter adapter;
    ArrayList<OrderDetailsDomain> orderDetailsList = new ArrayList<>();

    String shopId;
    String shopName;
    String lat;
    String lng;

    OrderDomain orderDomain;
    PaymentsDomain paymentsDomain;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);

        shopId = sharedPreferences.getString(Constants.SHOP_ID, "");
        shopName = sharedPreferences.getString(Constants.SHOP_NAME, "");
        lat = sharedPreferences.getString(Constants.LATTITUDE, "");
        lng = sharedPreferences.getString(Constants.LONGITUDE, "");

        Gson gson = new Gson();
        Gson pgson = new Gson();
        String strObj = getIntent().getStringExtra("orderData");
        String pstrObj = getIntent().getStringExtra("paymentData");
        type = getIntent().getStringExtra("type");
        orderDomain = gson.fromJson(strObj, OrderDomain.class);
        paymentsDomain = pgson.fromJson(pstrObj, PaymentsDomain.class);

        mActivity = this;

        if (type.equals("Undelivered")) {
            binding.actionLayout.setVisibility(View.VISIBLE);
            binding.actionLayout1.setVisibility(View.VISIBLE);
        } else {
            binding.actionLayout.setVisibility(View.GONE);
            binding.actionLayout1.setVisibility(View.GONE);
        }

        liseners();

        setInitialValues();

        setupRecyclerView();

        getOrderDetails();


    }

    private void setInitialValues() {

        binding.tvName.setText(orderDomain.getCustomer_name());
        binding.tvPhone.setText("+91 " + orderDomain.getPhone());
        binding.tvDate.setText("Date : " + orderDomain.getOrder_date());
        binding.tvTime.setText("Time : " + orderDomain.getOrder_time());
        binding.tvLocation.setText(orderDomain.getLandmark() + ", " + orderDomain.getAddress());
        binding.cash.setText(paymentsDomain.getPayment_status());
        if (paymentsDomain.getPayment_status().equals("Payment Recieved")){
            binding.cash.setTextColor(Color.parseColor("#3a873a"));
        }else {
            binding.cash.setTextColor(Color.parseColor("#ff0000"));
        }

    }

    private void liseners() {
        binding.back.setOnClickListener(this);
        binding.btnDecline.setOnClickListener(this);
        binding.btnAccept.setOnClickListener(this);
        binding.btnAccept2.setOnClickListener(this);
        binding.tvPhone.setOnClickListener(this);
        binding.DeliverBtn.setOnClickListener(this);
    }

    private void setupRecyclerView() {

        binding.recylerView.setHasFixedSize(true);
        binding.recylerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new OrderDetailsAdapter(mActivity, orderDetailsList);
        binding.recylerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btnAccept:
                if (CommonUtils.checkConnectivity(this)) {
                    accept_Decline("Accepted" , 1, true);
                } else {
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnAccept2:
                if (CommonUtils.checkConnectivity(this)) {
                    accept_Decline("CustomerPickup" , 1, false);
                } else {
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.DeliverBtn:
                if (CommonUtils.checkConnectivity(this)) {
                    delivered();
                } else {
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnDecline:
                new AlertDialog.Builder(mActivity)
                        .setTitle("Decline Order")
                        .setMessage("Are you sure you want to Decline this order?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (CommonUtils.checkConnectivity(OrderDetailsActivity.this)) {
                                    accept_Decline("Declined" , 2, true);
                                } else {
                                    Toast.makeText(OrderDetailsActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                break;
            case R.id.tvPhone:
                Intent intentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+91" + orderDomain.getPhone()));
                startActivity(intentDial);
                break;
        }
    }

    private void accept_Decline(final String status, final int notification, final boolean flag) {

        final JSONObject params = new JSONObject();
        try {
            params.put("shop_id", shopId);
            params.put("order_id", orderDomain.getOrder_id());
            params.put("order_status", status);
//            params.put("lat", orderDomain.getCustomer_id());
//            params.put("lng", orderDomain.getCustomer_id());
            Log.d("request-->", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://day2night.in/shop/orderAcceptDecline.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        CommonUtils.cancelProgressBar();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response-->", obj.toString());
                            if (obj.getString("code").equals("1")) {
                                Toast.makeText(mActivity, obj.getString("status"), Toast.LENGTH_SHORT).show();
                                binding.actionLayout.setVisibility(View.GONE);
                                binding.actionLayout1.setVisibility(View.GONE);
                                
                                if(obj.getString("last").equals("yes"))
                                {
                                    if(flag) {
                                        delivery(obj.getString("cust_order_id"));

                                    }else{
                                    }callNotificationAPI(1, "", "");

                                } else
                                { callNotificationAPI(notification,"", ""); }
                            } else {
                                Toast.makeText(mActivity, obj.getString("status"), Toast.LENGTH_SHORT).show();
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

    private void delivery(String Cust_order_id) {

        final JSONObject params = new JSONObject();
        try {
            params.put("shop_id", shopId);
            params.put("order_id", orderDomain.getOrder_id());
            params.put("Cust_order_id", Cust_order_id);
            Log.d("request-->", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://day2night.in/shop/insertToDelivery.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonUtils.cancelProgressBar();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response-->1", obj.toString());
                            if (obj.getString("code").equals("1")) {
                                Toast.makeText(mActivity, "Started Delivery Process", Toast.LENGTH_SHORT).show();

                                    FindNearestDboy(obj.getString("cust_order_id"));

                            } else {
                                Toast.makeText(mActivity, obj.getString("status"), Toast.LENGTH_SHORT).show();
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

    private void FindNearestDboy(String Cust_order_id) {
        final JSONObject params = new JSONObject();
        try {
            params.put("shop_id", shopId);
            params.put("order_id", orderDomain.getOrder_id());
            params.put("Cust_order_id", Cust_order_id);
            Log.d("request-->", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://day2night.in/shop/findNearestDboy.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonUtils.cancelProgressBar();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response-->3", obj.toString());
                            if (obj.getString("code").equals("1")) {
                                //Toast.makeText(mActivity, "Started Delivery Process", Toast.LENGTH_SHORT).show();


                                if( obj.getString("is_dboys").equals("yes") ){
                                    String custid =  obj.getString("cust_id");
                                    JSONArray dboys = obj.getJSONArray("dboy");
                                    for(int i=0; i<dboys.length(); i++){
                                        JSONObject object = dboys.getJSONObject(i);
                                        callNotificationAPI(3, object.getString("dboy_id"),custid);
                                    }
                                }else {
                                    Toast.makeText(OrderDetailsActivity.this, "No nearest Delivery Boys", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(mActivity, obj.getString("status"), Toast.LENGTH_SHORT).show();
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

    private void delivered() {

        CommonUtils.setProgressBar(mActivity);

        final JSONObject params = new JSONObject();
        try {
            params.put("cust_order_id", orderDomain.getCustomer_Order_Id());


            // if possible, send shop order ids

            Log.d("request-->2", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://day2night.in/shop/CustomerPickup.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        CommonUtils.cancelProgressBar();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response-->2", obj.toString());
                            if (obj.getString("code").equals("1")) {

                                if(obj.getString("deliver").equals("success"))
                                {
                                    JSONArray shops = obj.getJSONArray("shops");
                                    Toast.makeText(mActivity,"Delivery Successful",Toast.LENGTH_SHORT).show();
                                    //startActivity(new Intent(NewOrderDetailsActivity.this, MainActivity.class));
                                    finish();
                                    for(int i=0; i<shops.length(); i++){
                                        JSONObject object = shops.getJSONObject(i);
                                        //callNotificationAPI(object.getString("shop_id"),object.getString("order_id"));
                                    }


                                }
                                else if(obj.getString("deliver").equals("failed"))
                                {
                                    Toast.makeText(mActivity, "Delivery Failed, Try again", Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                Toast.makeText(mActivity, obj.getString("status"), Toast.LENGTH_SHORT).show();
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


    private void getOrderDetails() {

        CommonUtils.setProgressBar(mActivity);

        final JSONObject params = new JSONObject();
        try {
            params.put("shop_id", shopId);
            params.put("order_id", orderDomain.getOrder_id());
            Log.d("request-->", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://day2night.in/shop/orderProductList.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        CommonUtils.cancelProgressBar();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response-->4", obj.toString());
                            if (obj.getString("code").equals("1")) {

                                if(obj.getString("apt_Status").equals("Accepted")){
                                    binding.actionLayout.setVisibility(View.GONE);
                                    binding.actionLayout1.setVisibility(View.GONE);
                                    binding.actionLayout2.setVisibility(View.VISIBLE);
                                }else if(obj.getString("apt_Status").equals("Declined")){
                                    binding.actionLayout.setVisibility(View.GONE);
                                    binding.actionLayout1.setVisibility(View.GONE);
                                    binding.actionLayout3.setVisibility(View.VISIBLE);

                                }else if(obj.getString("apt_Status").equals("CustomerPickup")){
                                    binding.actionLayout.setVisibility(View.GONE);
                                    binding.actionLayout1.setVisibility(View.GONE);
                                    binding.AcceptedBtn.setText("Customer Pick-Up");
                                    binding.DeliverBtn.setVisibility(View.VISIBLE);
                                    binding.actionLayout2.setVisibility(View.VISIBLE);
                                }
                                else{
                                    binding.actionLayout.setVisibility(View.VISIBLE);
                                    binding.actionLayout1.setVisibility(View.VISIBLE);
                                }
                                JSONArray category = obj.getJSONArray("products");
                                Double Amount = 0.0;
                                for (int i = 0; i < category.length(); i++) {

                                    JSONObject jsonObject = category.getJSONObject(i);
                                    String shop_id = shopId;
                                    String order_id = jsonObject.getString("order_id");
                                    String category_id = jsonObject.getString("category_id");
                                    String product_id = jsonObject.getString("product_id");
                                    String quantity = jsonObject.getString("quantity");
                                    String price = jsonObject.getString("price");
                                    String product_name = jsonObject.getString("product_name");
                                   // String special_price = jsonObject.getString("special_price");
                                    String unit = jsonObject.getString("quantity");
                                    String product_image = jsonObject.getString("product_image");
                                    Amount = Amount+ (Double.parseDouble(price)* Double.parseDouble(quantity));
                                    orderDetailsList.add(new OrderDetailsDomain(shop_id, order_id, category_id, product_id, quantity, price, product_name, price, unit, product_image));

                                }
                                if (orderDetailsList.size() > 0)
                                    binding.table.setVisibility(View.VISIBLE);
                                    binding.tvTotalAmount.setText(String.valueOf(Amount)+"/-");
                                adapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(mActivity, obj.getString("status"), Toast.LENGTH_SHORT).show();
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


    private void callNotificationAPI(int i,String dboys_id ,String custid) {

        String cTopic = "";
        try {
            HashMap<String, Object> params = new HashMap<>();

            JSONObject bodyObject = new JSONObject();
            if (i == 1) {
                bodyObject.put("title", "Order Accepted");
                bodyObject.put("body", "Your order from " + shopName + " has been Accepted your order");
            }else if(i == 2){
                bodyObject.put("title", "Order Declined!!!");
                bodyObject.put("body", "Your order from " + shopName + " has been Declined by shopName . If you paid through online you will get refund of this within 6 business days.");
            }else{
                bodyObject.put("title", "New Order Available");
                bodyObject.put("body", "New order Recieved Order Id "+ custid +", Check new orderes. ");
            }

            bodyObject.put("click_action", "OPEN_ACTIVITY");
            bodyObject.put("sound", "default");

            JSONObject dataObject = new JSONObject();
            dataObject.put("type", "common");

            if(i == 3){
                 cTopic = "d" + dboys_id;
            }else {
                 cTopic = "c" + orderDomain.getCustomer_id();
            }

            params.put("to", "/topics/" + cTopic);
            params.put("priority", "high");
            params.put("type", "common");
            params.put("notification", bodyObject);
            params.put("data", dataObject);

            JSONObject androidObject = new JSONObject();
            JSONObject androidNotObject = new JSONObject();
            androidNotObject.put("sound", "default");
            androidObject.put("notification", androidNotObject);
            params.put("android", androidObject);

            volleyUtils.sendNotification(mActivity, "common", params, this, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void NotificationSuccess(JSONObject response, int requestcode) {
        if (requestcode == 1) {
            finish();
        }
    }

    @Override
    public void NotificationError(String msg, int requestcode) {

    }






    /*
    private void showDeleteDialog() {
        new AlertDialog.Builder(mActivity)
                .setTitle("Delete Order")
                .setMessage("Are you sure you want to delete this order?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (CommonUtils.checkConnectivity(OrderDetailsActivity.this)) {
                            delete();
                        } else {
                            Toast.makeText(OrderDetailsActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void updateStatus(String s) {

        final JSONObject params = new JSONObject();
        try {
            params.put("shop_id", shopId);
            params.put("order_id", orderDomain.getOrder_id());
            params.put("lat", orderDomain.getCustomer_id());
            params.put("lng", orderDomain.getCustomer_id());
//            params.put("delivery_status", s);
            Log.d("request-->", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://day2night.in/shop/deliveryStatusUpdate.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        CommonUtils.cancelProgressBar();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response-->", obj.toString());
                            if (obj.getString("code").equals("1")) {
                                Toast.makeText(mActivity, obj.getString("status"), Toast.LENGTH_SHORT).show();
                                callNotificationAPI(1);
                            } else {
                                Toast.makeText(mActivity, obj.getString("status"), Toast.LENGTH_SHORT).show();
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
    private void NearestBoy() {

        final JSONObject params = new JSONObject();
        try {
            params.put("shop_id", shopId);
            params.put("order_id", orderDomain.getOrder_id());
            params.put("lat", lat);
            params.put("lng", lng);
//            params.put("delivery_status", s);
            Log.d("request-->", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://day2night.in/delivery/Home/findNearestdboy.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        CommonUtils.cancelProgressBar();
                        //    try {
                        //       JSONObject obj = new JSONObject(response);
                        //      Log.d("response-->", obj.toString());
                        //       if (obj.getString("code").equals("1")) {
                        //           Toast.makeText(mActivity, obj.getString("status"), Toast.LENGTH_SHORT).show();
                        //           callNotificationAPI(1);
                        //       } else {
                        //           Toast.makeText(mActivity, obj.getString("status"), Toast.LENGTH_SHORT).show();
                        //       }

                        // } catch (JSONException e) {
                        //       e.printStackTrace();
                        //  }
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


    private void delete() {

        final JSONObject params = new JSONObject();
        try {
            params.put("shop_id", shopId);
            params.put("order_id", orderDomain.getOrder_id());
            Log.d("request-->", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://day2night.in/shop/deleteOrder.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        CommonUtils.cancelProgressBar();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response-->", obj.toString());
                            if (obj.getString("code").equals("1")) {
                                Toast.makeText(mActivity, obj.getString("status"), Toast.LENGTH_SHORT).show();
                                callNotificationAPI(2);
                            } else {
                                Toast.makeText(mActivity, obj.getString("status"), Toast.LENGTH_SHORT).show();
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

     */
}