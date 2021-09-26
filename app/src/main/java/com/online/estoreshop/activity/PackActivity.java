package com.online.estoreshop.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.online.estoreshop.R;
import com.online.estoreshop.adapter.OrdersAdapter;
import com.online.estoreshop.adapter.PackAdapter;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityPackBinding;
import com.online.estoreshop.interfaces.ClickedItem;
import com.online.estoreshop.models.PackDomain;
import com.online.estoreshop.models.PackRequest;
import com.online.estoreshop.utils.AddPackFragment;
import com.online.estoreshop.utils.CommonUtils;
import com.online.estoreshop.utils.Constants;
import com.online.estoreshop.utils.UrlConstants;
import com.online.estoreshop.volleyservice.VolleyInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.online.estoreshop.utils.UrlConstants.REQ_packDelete;
import static com.online.estoreshop.utils.UrlConstants.REQ_packInsert;
import static com.online.estoreshop.utils.UrlConstants.REQ_packLit;

public class PackActivity extends BaseActivity implements ClickedItem, VolleyInterface, AddPackFragment.OptionListener2 {

    ActivityPackBinding binding;
    PackAdapter adapter;
    ArrayList<PackDomain> packList = new ArrayList<>();
    String pname = "";
    String pid = "";

    String shopId;
    String shopName;
    AddPackFragment addPackFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pack);

        shopId = sharedPreferences.getString(Constants.SHOP_ID, "");
        shopName = sharedPreferences.getString(Constants.SHOP_NAME, "");

        pname = getIntent().getStringExtra("pname");
        pid = getIntent().getStringExtra("pid");

        mActivity = this;

        addPackFragment = new AddPackFragment();

        binding.titleImage.setText(shopName);

        setupRecyclerview();

        getPackList();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.addPack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDlg();
            }
        });


    }

    private void showDlg() {
        addPackFragment.setOptionListener2(this);
        addPackFragment.setCancelable(true);
        addPackFragment.show(getSupportFragmentManager(), addPackFragment.getTag());
    }

    private void getPackList() {
        String url = UrlConstants.packList;
        HashMap<String, Object> params = new HashMap<>();
        params.put("product_id", pid);
        volleyUtils.callApi(mActivity, this, url, REQ_packLit, params, 1, true);
    }

    private void setupRecyclerview() {

        binding.recylerView.setHasFixedSize(true);
        binding.recylerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new PackAdapter(mActivity, packList, pname, this);
        binding.recylerView.setAdapter(adapter);

    }

    @Override
    public void clicked(String type, Object object) {
        PackDomain packDomain = (PackDomain) object;
        if (type.equals("delete")) {
            showDeleteDialog(packDomain.getPack_id());
        }
    }

    private void showDeleteDialog(final String pack_id) {
        new AlertDialog.Builder(mActivity)
                .setTitle("Delete Pack")
                .setMessage("Are you sure you want to delete this pack?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (CommonUtils.checkConnectivity(mActivity)) {
                            delete(pack_id);
                        } else {
                            Toast.makeText(mActivity, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void delete(String pack_id) {
        String url = UrlConstants.packDelete;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pack_id", pack_id);
        volleyUtils.callApi(mActivity, this, url, REQ_packDelete, params, 1, true);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_packLit) {
            getPackListResponse(response);
        } else if (requestcode == REQ_packDelete) {
            getPackList();
        } else if (requestcode == REQ_packInsert) {
            getPackList();
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void getPackListResponse(JSONObject response) {
        try {

            packList.clear();
            String code = response.getString("code");
            if (code.equals("1")) {

                JSONArray packs = response.getJSONArray("packs");
                for (int i = 0; i < packs.length(); i++) {
                    JSONObject jsonObject = packs.getJSONObject(i);
                    String pack_id = jsonObject.getString("pack_id");
                    String product_id = jsonObject.getString("product_id");
                    String unit = jsonObject.getString("unit");
                    String quantity = jsonObject.getString("quantity");
                    String description = jsonObject.getString("description");
                    String orginal_price = jsonObject.getString("orginal_price");
                    String special_price = jsonObject.getString("special_price");
                    String discount = jsonObject.getString("discount");

                    packList.add(new PackDomain(pack_id, product_id, unit, quantity, description, orginal_price, special_price, discount));
                }

                if (packList.size() == 0) {
                    binding.emptyLayout.setVisibility(View.VISIBLE);
                    binding.recylerView.setVisibility(View.GONE);
                } else {
                    binding.emptyLayout.setVisibility(View.GONE);
                    binding.recylerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addPack(PackRequest packRequest) {
        Log.d("packRequest", packRequest.toString());
        String url = UrlConstants.packInsert;
        HashMap<String, Object> params = new HashMap<>();
        params.put("product_id", pid);
        params.put("unit", packRequest.getUnit());
        params.put("quantity", packRequest.getQuantity());
        params.put("description", packRequest.getDescription());
        params.put("orginal_price", packRequest.getOrginal_price());
        params.put("special_price", packRequest.getSpecial_price());
        params.put("discount", packRequest.getDiscount());
        volleyUtils.callApi(mActivity, this, url, REQ_packInsert, params, 1, true);
    }

    @Override
    public void optionSelected2(PackRequest packRequest) {
        addPack(packRequest);
    }
}