package com.online.estoreshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.online.estoreshop.R;
import com.online.estoreshop.databinding.ActivityRegisterBinding;
import com.online.estoreshop.models.UserDomain;
import com.online.estoreshop.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityRegisterBinding binding;
    String sName, sPhone, sAddress, sPincode, sLandmark, sPassword, sPassword2, sOwner;

    boolean showPassword1 = true;
    boolean showPassword2 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        binding.btnRegister.setOnClickListener(this);
        binding.showPassword1.setOnClickListener(this);
        binding.showPassword2.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnRegister:
                checkvalues();
//                registerUser();
                break;

            case R.id.showPassword1:
                showPassword1 = !showPassword1;
                if (showPassword1) {
                    binding.showPassword1.setImageResource(R.drawable.viewpassword);
                    binding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    binding.showPassword1.setImageResource(R.drawable.hidepassword);
                    binding.edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                binding.edtPassword.setSelection(binding.edtPassword.length());
                break;

            case R.id.showPassword2:
                showPassword2 = !showPassword2;
                if (showPassword2) {
                    binding.showPassword2.setImageResource(R.drawable.viewpassword);
                    binding.edtPassword2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    binding.showPassword2.setImageResource(R.drawable.hidepassword);
                    binding.edtPassword2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                binding.edtPassword2.setSelection(binding.edtPassword2.length());
                break;
        }

    }

    private void checkvalues() {
        sName = binding.edtName.getText().toString().trim();
        sPhone = binding.edtPhone.getText().toString().trim();
        sAddress = binding.edtAddress.getText().toString().trim();
        sPincode = binding.edtPin.getText().toString().trim();
        sLandmark = binding.edtLandMark.getText().toString().trim();
        sPassword = binding.edtPassword.getText().toString().trim();
        sPassword2 = binding.edtPassword2.getText().toString().trim();
        sOwner = binding.edtOwner.getText().toString().trim();

        if (TextUtils.isEmpty(sName)) {
            Toast.makeText(this, "Please enter shop name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(sOwner)) {
            Toast.makeText(this, "Please enter owner name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(sPhone)) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(sAddress)) {
            Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(sPincode)) {
            Toast.makeText(this, "Please enter pincode", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(sLandmark)) {
            Toast.makeText(this, "Please enter landmark", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(sPassword)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(sPassword2)) {
            Toast.makeText(this, "Please re-enter password", Toast.LENGTH_SHORT).show();
        } else if (sPassword.length() < 8) {
            Toast.makeText(this, "Enter valid password", Toast.LENGTH_SHORT).show();
        } else if (sPassword2.length() < 8) {
            Toast.makeText(this, "Enter valid password", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.PHONE.matcher(sPhone).matches() || sPhone.length() != 10) {
            Toast.makeText(this, "Please enter valid phone number", Toast.LENGTH_SHORT).show();
        } else if (!sPassword.equals(sPassword2)) {
            Toast.makeText(this, "Please enter same passwords", Toast.LENGTH_SHORT).show();
        } else {
            registerUser();
        }


    }

    private void registerUser() {

        if (CommonUtils.checkConnectivity(this)) {

            UserDomain userDomain = new UserDomain(sName, sOwner, sPhone, sAddress, sPincode, sLandmark, sPassword);
            Gson gson = new Gson();
            Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
            intent.putExtra("userData", gson.toJson(userDomain));
            startActivity(intent);

//            callRegisterApi(userDomain);

        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void callRegisterApi(final UserDomain userDomain) {

        final JSONObject params = new JSONObject();
        try {
            params.put("shop_name", userDomain.getShopname());
            params.put("owner_name", userDomain.getOwnername());
            params.put("phone", userDomain.getPhone());
            params.put("address", userDomain.getAddress());
            params.put("pincode", userDomain.getPincode());
            params.put("landmark", userDomain.getLandmark());
            params.put("password", getSha1Hex(userDomain.getPassword()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CommonUtils.setProgressBar(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://estore.day2night.in/shop/register.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        CommonUtils.cancelProgressBar();

                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response-->", obj.toString());
                            if (obj.getString("code").equals("1")) {
                                Gson gson = new Gson();
                                Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
                                intent.putExtra("userData", gson.toJson(userDomain));
                                startActivity(intent);
                            } else {
                                Toast.makeText(RegisterActivity.this, obj.getString("status"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtils.cancelProgressBar();
                Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            public byte[] getBody() {
                return params.toString().getBytes();
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public static String getSha1Hex(String clearString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(clearString.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes) {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}