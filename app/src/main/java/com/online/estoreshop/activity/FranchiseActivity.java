package com.online.estoreshop.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.online.estoreshop.R;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityFranchiseBinding;

public class FranchiseActivity extends BaseActivity implements View.OnClickListener {

    ActivityFranchiseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_franchise);

        mActivity = this;

        binding.btnCall.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.callCustomerCare.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCall:
            case R.id.callCustomerCare:
                callCustomerCare();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void callCustomerCare() {
        Intent intentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+919847132202"));
        startActivity(intentDial);
    }
}