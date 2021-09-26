package com.online.estoreshop.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.online.estoreshop.R;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.utils.Constants;

public class SplashActivity extends BaseActivity {

    private Runnable mRunnable;
    private static final long SPLASH_DISPLAY_LENGTH = 2000;
    private Handler mHandler;

    String LAT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LAT = sharedPreferences.getString(Constants.LATTITUDE, "");

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (LAT.equals("")) {
                    Intent in = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(in);
                    finish();
                } else {
                    Intent in = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(in);
                    finish();
                }
            }
        };
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, SPLASH_DISPLAY_LENGTH);


    }
}