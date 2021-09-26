package com.online.estoreshop.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Bitmap;
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
import com.online.estoreshop.adapter.NotificationAdapter;
import com.online.estoreshop.adapter.OrdersAdapter;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityNotificationBinding;
import com.online.estoreshop.models.NotificationDomain;
import com.online.estoreshop.models.OrderDomain;
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

import static com.online.estoreshop.utils.UrlConstants.REQ_addNewSubCategory;
import static com.online.estoreshop.utils.UrlConstants.REQ_shopOrderNotify;

public class NotificationActivity extends BaseActivity implements VolleyInterface {

    ActivityNotificationBinding binding;
    NotificationAdapter adapter;
    ArrayList<OrderDomain> notificationList = new ArrayList<>();

    String shopId;
    String shopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);

        mActivity = this;

        shopId = sharedPreferences.getString(Constants.SHOP_ID, "");
        shopName = sharedPreferences.getString(Constants.SHOP_NAME, "");

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupRecyclerView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getNotifications();
    }

    private void setupRecyclerView() {
        binding.recylerView.setHasFixedSize(true);
        binding.recylerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new NotificationAdapter(mActivity, notificationList);
        binding.recylerView.setAdapter(adapter);
    }

    private void getNotifications() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("shop_id", shopId);
        String url = UrlConstants.shopOrderNotify;
        volleyUtils.callApi(mActivity, this, url, REQ_shopOrderNotify, params, 1, true);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_shopOrderNotify) {
            notificationResponse(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void notificationResponse(JSONObject response) {
        try {
            notificationList.clear();
            String code = response.getString("code");
            if (code.equals("1")) {

                JSONArray orders = response.getJSONArray("orders");
                for (int i = 0; i < orders.length(); i++) {
                    JSONObject jsonObject = orders.getJSONObject(i);
                    String order_id = jsonObject.getString("order_id");
                    String customer_id = jsonObject.getString("customer_id");
                    String order_date = jsonObject.getString("order_date");
                    String order_time = jsonObject.getString("order_time");
                    String customer_name = jsonObject.getString("customer_name");
                    String phone = jsonObject.getString("phone");
                    String address = jsonObject.getString("address");
                    String landmark = jsonObject.getString("landmark");
                    String payable_amount = jsonObject.getString("payable_amount");
                    String getCustomer_Order_Id= jsonObject.getString("customer_order_id");
                    String shop_id = shopId;
                    notificationList.add(new OrderDomain(shop_id, customer_id, order_id, order_date, order_time, customer_name, phone, address,
                            landmark, payable_amount, "",getCustomer_Order_Id));
                }

                if (notificationList.size() > 0) {
                    adapter.notifyDataSetChanged();
                    binding.recylerView.scheduleLayoutAnimation();

                } else {
                    binding.emptyLayout.setVisibility(View.VISIBLE);
                }

            } else {
                binding.emptyLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}