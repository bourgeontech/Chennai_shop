package com.online.estoreshop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.online.estoreshop.R;
import com.online.estoreshop.adapter.SideBarAdapter;
import com.online.estoreshop.adapter.StoreAdapter;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityMainBinding;
import com.online.estoreshop.interfaces.ClickedItem;
import com.online.estoreshop.models.NotificationDomain;
import com.online.estoreshop.models.ShopDomain;
import com.online.estoreshop.utils.CommonUtils;
import com.online.estoreshop.utils.Constants;
import com.online.estoreshop.utils.UrlConstants;
import com.online.estoreshop.volleyservice.VolleyInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.online.estoreshop.utils.UrlConstants.REQ_shopOrderNotify;

public class MainActivity extends BaseActivity implements View.OnClickListener, ClickedItem, VolleyInterface {

    ActivityMainBinding binding;
    SideBarAdapter adapter;
    ArrayList<ShopDomain> shopList = new ArrayList<>();
    List<DrawerDomain> menuList = new ArrayList<>();
    String shopId = "";
    String shopName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        shopId = sharedPreferences.getString(Constants.SHOP_ID, "0");
        shopName = sharedPreferences.getString(Constants.SHOP_NAME, "0");

        FirebaseMessaging.getInstance().subscribeToTopic(shopId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("topic-->", "Subscribed by with shop ID");
            }
        });

        mActivity = this;
        setUpRecycler();


        binding.container.undeliveredLayout.setOnClickListener(this);
        binding.container.manageItemsLayout.setOnClickListener(this);
        binding.container.allOrdersLayout.setOnClickListener(this);
        binding.container.btnNotifications.setOnClickListener(this);
        binding.container.btnPickLocation.setOnClickListener(this);

        binding.container.navIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        binding.container.titleImage.setText(shopName);

        setUpSidebar();

        onNewIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Animation shake;
        shake = AnimationUtils.loadAnimation(mActivity.getApplicationContext(), R.anim.item_animation_fall_down_1500);
        binding.container.mainLayout.startAnimation(shake);

        getNotifications();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String type = intent.getStringExtra("type");
            if (type != null) {
                startActivity(new Intent(mActivity, NotificationActivity.class));
            }
        } else {
            Log.d("intents-->", "null");
        }

    }


    private void getNotifications() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("shop_id", shopId);
        String url = UrlConstants.shopOrderNotify;
        volleyUtils.callApi(mActivity, this, url, REQ_shopOrderNotify, params, 1, false);
    }

    private void setUpSidebar() {

        menuList.clear();
        menuList.add(new DrawerDomain(R.drawable.delivery_icon, "Delivered Orders"));
        menuList.add(new DrawerDomain(R.drawable.ic_baseline_access_time_24, "Edit Shop Time"));
//        menuList.add(new DrawerDomain(R.drawable.update_stock_icon, "Update stock"));
        menuList.add(new DrawerDomain(R.drawable.report_icon, "Sales reports"));
        menuList.add(new DrawerDomain(R.drawable.not_delivered_icon, "Undelivered Orders"));
        menuList.add(new DrawerDomain(R.drawable.manage_item_icon, "Manage items"));
        menuList.add(new DrawerDomain(R.drawable.all_orders, "All orders"));
        menuList.add(new DrawerDomain(R.drawable.franchise_icon, "Contact Franchisee"));
        menuList.add(new DrawerDomain(R.drawable.logout_icon, "Logout"));

        binding.layoutNav.menuListView.setHasFixedSize(true);
        binding.layoutNav.menuListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SideBarAdapter(this, menuList, this);
        binding.layoutNav.menuListView.setAdapter(adapter);
    }

    private void setUpRecycler() {

//        binding.recycler.setHasFixedSize(true);
//        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new StoreAdapter(this,shopList);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.undeliveredLayout:
                gotoOrders("Undelivered");
                break;
            case R.id.manageItemsLayout:
                startActivity(new Intent(mActivity, ManageItemActivity.class));
                break;
            case R.id.allOrdersLayout:
                gotoOrders("Total");
                break;
            case R.id.btnNotifications:
                startActivity(new Intent(mActivity, NotificationActivity.class));
                break;
            case R.id.btnPickLocation:
                startActivity(new Intent(mActivity, ChangeLocationActivity.class));
                break;

        }
    }

    private void gotoOrders(String type) {
        Intent intent = new Intent(mActivity, AllOrdersActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void clicked(String type, Object object) {

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        String item = (String) object;
        switch (item) {
            case "Delivered Orders":
                gotoOrders("Delivered");
                break;
            case "Update stock":

                break;
            case "Sales reports":
                startActivity(new Intent(mActivity, ReportActivity.class));
                break;
            case "Undelivered Orders":
                gotoOrders("Undelivered");
                break;
            case "Manage items":
                startActivity(new Intent(mActivity, ManageItemActivity.class));
                break;
            case "All orders":
                gotoOrders("Total");
                break;
            case "Contact Franchisee":
                startActivity(new Intent(mActivity, FranchiseActivity.class));
                break;

            case "Edit Shop Time":
                startActivity(new Intent(mActivity, EditTimeActivity.class));
                break;

            case "Logout":
                CommonUtils.setAlerDialog(mActivity, "Confirm Logout", "Are you sure you want to logout?", true, "LOGOUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logoutFunction();
                    }
                });
                break;
        }
    }

    private void logoutFunction() {
        CommonUtils.setProgressBar(mActivity);
        editor.clear();
        editor.apply();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(shopId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.cancelProgressBar();
                Log.d("topic-->", "UnSubscribed");
                Intent intent = new Intent(mActivity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }
        });

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
            String code = response.getString("code");
            if (code.equals("1")) {

                JSONArray orders = response.getJSONArray("orders");
                if (orders.length() > 0) {
                    binding.container.notiDot.setVisibility(View.VISIBLE);
                    binding.container.undeliveredCount.setVisibility(View.VISIBLE);
                    binding.container.undeliveredCount.setText(orders.length() + "");
                    binding.container.notiDot.setText(orders.length() + "");
                    startAnimation();
                } else {
                    binding.container.notiDot.setVisibility(View.GONE);
                    binding.container.notiDot.clearAnimation();
                    binding.container.undeliveredCount.setVisibility(View.GONE);
                }
            } else {
                binding.container.notiDot.clearAnimation();
                binding.container.notiDot.setVisibility(View.GONE);
                binding.container.undeliveredCount.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAnimation() {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        binding.container.notiDot.startAnimation(anim);
    }

}