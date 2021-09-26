package com.online.estoreshop.base;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.online.estoreshop.volleyservice.VolleyUtils;

public class BaseActivity extends AppCompatActivity {


    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Activity mActivity;
    public VolleyUtils volleyUtils = new VolleyUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("ogl-app", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
}