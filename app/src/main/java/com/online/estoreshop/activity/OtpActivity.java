package com.online.estoreshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.online.estoreshop.R;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityOtpBinding;
import com.online.estoreshop.models.UserDomain;
import com.online.estoreshop.utils.CommonUtils;
import com.online.estoreshop.utils.Constants;
import com.online.estoreshop.utils.RegisterSuccessFragment;
import com.online.estoreshop.volleyservice.VolleyInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends BaseActivity implements View.OnClickListener, VolleyInterface, RegisterSuccessFragment.OptionListener {

    UserDomain userDomain;
    ActivityOtpBinding binding;
    String pin;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    String TAG = "OTPActivity";
    DatabaseReference reference;
    String userId = "test";

    RegisterSuccessFragment registerSuccessFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp);

        mActivity = this;

        Gson gson = new Gson();
        String strObj = getIntent().getStringExtra("userData");
        userDomain = gson.fromJson(strObj, UserDomain.class);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

//        callRegisterApi(userDomain);

        binding.btnVerify.setOnClickListener(this);
        binding.btnGoBack.setOnClickListener(this);
        //callRegisterApi();
        sendVerificationCode();

    }


    private void sendVerificationCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + userDomain.getPhone(),
//                "+91" + "7025028741",
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                binding.pinview.setValue(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        CommonUtils.setProgressBar(this);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        CommonUtils.cancelProgressBar();
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().getUser() != null)
                                userId = task.getResult().getUser().getUid();
                            callRegisterApi();
                        } else {
                            String message = "Somthing is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            final Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    private void addToDb(String uid) {

        reference.child("users").child(uid).setValue(userDomain).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                showRegisterDialog();
            }
        });
    }

    private void showRegisterDialog() {
        registerSuccessFragment = new RegisterSuccessFragment("Welcome " + userDomain.getOwnername());
        registerSuccessFragment.setOptionListener(this);
        registerSuccessFragment.setCancelable(false);
        registerSuccessFragment.show(getSupportFragmentManager(), registerSuccessFragment.getTag());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnVerify:
                pin = binding.pinview.getValue();
                if (pin.length() < 6) {
                    Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    verifyOtp();
                }
                break;
            case R.id.btnGoBack:
                finish();
                break;
        }
    }

    private void verifyOtp() {
        verifyVerificationCode(pin);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {

    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void callRegisterApi() {

        final JSONObject params = new JSONObject();
        try {

            params.put("shop_name", userDomain.getShopname());
            params.put("owner_name", userDomain.getOwnername());
            params.put("phone", userDomain.getPhone());
            params.put("address", userDomain.getAddress());
            params.put("pincode", userDomain.getPincode());
            params.put("landmark", userDomain.getLandmark());
            params.put("password", getSha1Hex(userDomain.getPassword()));
            params.put("open_time", "00:00:00");
            params.put("close_time", "00:00:00");
            Log.d("request-->", params.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CommonUtils.setProgressBar(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://day2night.in/shop/register.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        CommonUtils.cancelProgressBar();

                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response-->", obj.toString());
                            if (obj.getString("code").equals("1")) {
                                addToDb(userId);
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

    @Override
    public void optionSelected(String item) {
        loginMethod();
    }


    private void loginMethod() {

        final JSONObject params = new JSONObject();
        try {
            params.put("phone", userDomain.getPhone());
            params.put("password", getSha1Hex(userDomain.getPassword()));

            Log.d("request-->", params.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://day2night.in/shop/login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        CommonUtils.cancelProgressBar();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response-->", obj.toString());
                            if (obj.getString("code").equals("1")) {
                                editor.putString(Constants.SHOP_ID, obj.getJSONObject("user_detail").getString("shop_id"));
                                editor.putString(Constants.SHOP_NAME, obj.getJSONObject("user_detail").getString("shop_name"));
                                editor.putString(Constants.OWNER_NAME, obj.getJSONObject("user_detail").getString("owner_name"));
                                editor.putString(Constants.PHONE, obj.getJSONObject("user_detail").getString("phone"));
                                editor.putString(Constants.ADDRESS, obj.getJSONObject("user_detail").getString("address"));
                                editor.putString(Constants.PINCODE, obj.getJSONObject("user_detail").getString("pincode"));
                                editor.putString(Constants.LANDMARK, obj.getJSONObject("user_detail").getString("landmark"));
                                editor.putString(Constants.LATTITUDE, obj.getJSONObject("user_detail").getString("latitude"));
                                editor.putString(Constants.LONGITUDE, obj.getJSONObject("user_detail").getString("longitude"));
                                editor.apply();
                                if (obj.getJSONObject("user_detail").getString("latitude").equals("") || obj.getJSONObject("user_detail").getString("longitude").equals("")) {
                                    gotoLocation(obj);
                                } else {
                                    gotoDashBoard(obj);
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

    private void gotoLocation(JSONObject obj) {
        Intent intent = new Intent(mActivity, PickLocationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    private void gotoDashBoard(JSONObject obj) {


        Intent intent = new Intent(mActivity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }
}