package com.online.estoreshop.activity;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.online.estoreshop.R;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityAddcategoryBinding;
import com.online.estoreshop.utils.CommonUtils;
import com.online.estoreshop.utils.Constants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import static com.online.estoreshop.utils.Constants.PERMISSIONS;
import static com.online.estoreshop.utils.Constants.PICK_IMAGE;

public class AddcategoryActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    ActivityAddcategoryBinding binding;
    private String categoryImage = "";
    String sTitle, sDesc = "";

    String shopId;
    String shopName;
    Uri imageUri;

    private String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_addcategory);

        shopId = sharedPreferences.getString(Constants.SHOP_ID, "");
        shopName = sharedPreferences.getString(Constants.SHOP_NAME, "");

        mActivity = this;


        binding.titleImage.setText(shopName);

        listeners();

    }

    private void listeners() {

        binding.back.setOnClickListener(this);
        binding.btnUpdate.setOnClickListener(this);
        binding.btnAddImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btnUpdate:
                checkValues();
                break;
            case R.id.btnAddImage:
                if (EasyPermissions.hasPermissions(mActivity, perms)) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                } else {
                    requestPerms();
                }
                break;
        }
    }

    private void requestPerms() {

        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, PERMISSIONS, perms)
                        .setRationale("Need storage permissions")
                        .build());

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {


    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(mActivity, "Permission Denied", Toast.LENGTH_SHORT).show();
    }

    private void checkValues() {
        sTitle = binding.edtTitle.getText().toString().trim();
        sDesc = binding.edtDescription.getText().toString().trim();

        if (sTitle.equals("")) {
            Toast.makeText(mActivity, "Enter title", Toast.LENGTH_SHORT).show();
        } else if (categoryImage.equals("")) {
            Toast.makeText(mActivity, "Select image", Toast.LENGTH_SHORT).show();
        } else {
            if (CommonUtils.checkConnectivity(this)) {
                addToDatabase();
            } else {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void addToDatabase() {

        CommonUtils.setProgressBar(mActivity);

        final JSONObject params = new JSONObject();
        try {
            params.put("shop_id", shopId);
            params.put("category_title", sTitle);
            params.put("category_description", sDesc);

            Bitmap bitmap = loadBitmapFromView(binding.selectedImage);
            convertIntoString(bitmap);

            params.put("category_image", categoryImage);

            Log.d("request-->", params.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://day2night.in/shop/addNewCategory.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        CommonUtils.cancelProgressBar();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response-->", obj.toString());
                            if (obj.getString("code").equals("1")) {
                                Toast.makeText(mActivity, "" + obj.getString("status"), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddcategoryActivity.this, obj.getString("status"), Toast.LENGTH_SHORT).show();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            final InputStream imageStream;
            try {
                imageUri = data.getData();
                if (imageUri != null) {
                    imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    convertIntoString(selectedImage);
                    binding.selectedImage.setVisibility(View.VISIBLE);

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(30));
                    Glide.with(mActivity)
                            .load(selectedImage)
                            .apply(requestOptions)
                            .into(binding.selectedImage);


//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
//                    byte[] b = baos.toByteArray();
//                    categoryImage = Base64.encodeToString(b, Base64.DEFAULT);

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_LONG).show();
            }
//                    imageStream = getContentResolver().openInputStream(imageUri);
//                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                    binding.selectedImage.setVisibility(View.VISIBLE);


        } else {
            Toast.makeText(mActivity, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    private void convertIntoString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos); // bm is the bitmap object
        byte[] b = baos.toByteArray();
        categoryImage = Base64.encodeToString(b, Base64.DEFAULT);
    }

    private static int sizeOf(Bitmap data) {
        return data.getRowBytes() * data.getHeight();
    }
}