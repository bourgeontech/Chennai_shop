package com.online.estoreshop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.online.estoreshop.R;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityForgotBinding;
import com.online.estoreshop.utils.CommonUtils;
import com.online.estoreshop.utils.UrlConstants;
import com.online.estoreshop.volleyservice.VolleyInterface;

import org.json.JSONObject;

import java.util.HashMap;

import static com.online.estoreshop.utils.UrlConstants.REQ_ShopOwnerPhoneNoExist;

public class ForgotActivity extends BaseActivity implements View.OnClickListener, VolleyInterface {

    ActivityForgotBinding binding;
    String num;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot);

        mActivity = this;
        reference = FirebaseDatabase.getInstance().getReference();

        binding.btnGoBack.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.back.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGoBack:
            case R.id.back:
                finish();
                break;
            case R.id.btnConfirm:
                check();
                break;
        }
    }

    private void check() {
        num = binding.edtPhone.getText().toString().trim();
        if (num.equals("")) {
            Toast.makeText(mActivity, "Enter phone number", Toast.LENGTH_SHORT).show();
        } else if (num.length() < 10) {
            Toast.makeText(mActivity, "Enter valid phone number", Toast.LENGTH_SHORT).show();
        } else {
            checkInDb();
        }
    }

    private void checkInDb() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("phone", num);
        String url = UrlConstants.ShopOwnerPhoneNoExist;
        volleyUtils.callApi(mActivity, this, url, REQ_ShopOwnerPhoneNoExist, params, 1, true);

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_ShopOwnerPhoneNoExist) {
            checkNumexists(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void checkNumexists(JSONObject response) {
        try {
            String code = response.getString("code");
            if (code.equals("1")) {
                Intent intent = new Intent(mActivity, ForgotOtpActivity.class);
                intent.putExtra("number", num);
                startActivity(intent);
            } else {
                Toast.makeText(mActivity, "" + response.getString("status"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}