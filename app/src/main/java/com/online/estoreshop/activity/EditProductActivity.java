package com.online.estoreshop.activity;

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
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

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
import com.google.gson.Gson;
import com.online.estoreshop.R;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityAddProductBinding;
import com.online.estoreshop.databinding.ActivityEditProductBinding;
import com.online.estoreshop.models.ProductDomain;
import com.online.estoreshop.models.UserDomain;
import com.online.estoreshop.utils.CommonUtils;
import com.online.estoreshop.utils.Constants;
import com.online.estoreshop.utils.UrlConstants;
import com.online.estoreshop.volleyservice.VolleyInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import static com.online.estoreshop.utils.Constants.PERMISSIONS;
import static com.online.estoreshop.utils.Constants.PICK_IMAGE;
import static com.online.estoreshop.utils.UrlConstants.REQ_majorList3;
import static com.online.estoreshop.utils.UrlConstants.REQ_packLit;

public class EditProductActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks, VolleyInterface {

    ActivityEditProductBinding binding;
    String shopId;
    String shopName;
    private String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    String sProduct, sPrice, sSpecialPrice = "", sDiscount = "", sUnit;
    String category_id;
    Uri imageUri = null;
    String productImage = "";

    ProductDomain productDomain;
    boolean imageChanged = false;

    String sub_category_id;
    String availability = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_product);

        mActivity = this;

        shopId = sharedPreferences.getString(Constants.SHOP_ID, "");
        shopName = sharedPreferences.getString(Constants.SHOP_NAME, "");


        Gson gson = new Gson();
        String strObj = getIntent().getStringExtra("productData");
        sub_category_id = getIntent().getStringExtra("sub_category_id");
        productDomain = gson.fromJson(strObj, ProductDomain.class);
        Log.d("unit-->", productDomain.getStock_availability());

        category_id = productDomain.getCategory_id();

        binding.titleImage.setText(shopName);

        setInitialVallues();


        binding.availabilityLayout.setVisibility(View.VISIBLE);

        binding.back.setOnClickListener(this);
        binding.btnUpdate.setOnClickListener(this);
        binding.btnAddImage.setOnClickListener(this);
        binding.btnPacks.setOnClickListener(this);
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.btnYes) {
                    availability = "1";
                } else {
                    availability = "0";
                }
            }
        });

        List<String> categories = new ArrayList<String>();
        categories.add("no.s");
        categories.add("kg");
        categories.add("g");
        categories.add("mg");
        categories.add("m");
        categories.add("ml");
        categories.add("l");
        categories.add("V");
        categories.add("cm");
        categories.add("inch");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edtUnit.setAdapter(dataAdapter);

        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).equals(productDomain.getUnit())) {
                binding.edtUnit.setSelection(i);
                break;
            }
        }




    }



    private void setInitialVallues() {

        binding.edtProduct.setText(productDomain.getProduct_name());
        binding.edtPrice.setText(productDomain.getOrginal_price());
        binding.edtspecialPrize.setText(productDomain.getSpecial_price());
        binding.edtDiscount.setText(productDomain.getDiscount().replace("%", ""));
//        binding.edtUnit.setText(productDomain.getUnit());
        productImage = productDomain.getProduct_image();

        try {
            byte[] decodedString = Base64.decode(productDomain.getProduct_image(), Base64.NO_WRAP);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(36));

            binding.selectedImage.setVisibility(View.VISIBLE);
            Glide.with(mActivity)
                    .load(decodedByte)
                    .centerCrop()
                    .apply(requestOptions)
                    .placeholder(0)
                    .into(binding.selectedImage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (productDomain.getStock_availability().equals("1")) {
            binding.btnYes.setChecked(true);
            binding.btnNo.setChecked(false);
        } else {
            binding.btnYes.setChecked(false);
            binding.btnNo.setChecked(true);
        }

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
            case R.id.btnPacks: {
                Intent intent = new Intent(mActivity, PackActivity.class);
                intent.putExtra("pname", productDomain.getProduct_name());
                intent.putExtra("pid", productDomain.getProduct_id());
                startActivity(intent);
            }
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

    private void checkValues() {

        sProduct = binding.edtProduct.getText().toString().trim();
        sPrice = binding.edtPrice.getText().toString().trim();
        sSpecialPrice = binding.edtspecialPrize.getText().toString().trim();
        sDiscount = binding.edtDiscount.getText().toString().trim();
        sUnit = binding.edtUnit.getSelectedItem().toString().trim();

        Log.d("product-->", sProduct + " " + (productDomain.getProduct_name()));
        Log.d("product-->", sPrice + " " + (productDomain.getOrginal_price()));
        Log.d("product-->", sSpecialPrice + " " + (productDomain.getSpecial_price()));
        Log.d("product-->", sDiscount + " " + (productDomain.getDiscount().replace("%", "")));
        Log.d("product-->", sUnit + " " + (productDomain.getUnit()));


        if (sProduct.equals("")) {
            Toast.makeText(mActivity, "Enter product name", Toast.LENGTH_SHORT).show();
        } else if (sPrice.equals("")) {
            Toast.makeText(mActivity, "Enter price", Toast.LENGTH_SHORT).show();
        } else if (sUnit.equals("")) {
            Toast.makeText(mActivity, "Enter unit", Toast.LENGTH_SHORT).show();
        } else if (productImage.equals("")) {
            Toast.makeText(mActivity, "Select image", Toast.LENGTH_SHORT).show();
        } else if (sProduct.equals(productDomain.getProduct_name()) && sPrice.equals(productDomain.getOrginal_price()) &&
                sSpecialPrice.equals(productDomain.getSpecial_price()) && sDiscount.equals(productDomain.getDiscount().replace("%", "")) &&
                sUnit.equals(productDomain.getUnit()) && !imageChanged) {
            if (!availability.equals("-1")) {
                editProduct();
            } else {
                Toast.makeText(mActivity, "Not edited any data", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (CommonUtils.checkConnectivity(this)) {
                editProduct();
            } else {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void editProduct() {


        CommonUtils.setProgressBar(mActivity);
        final JSONObject params = new JSONObject();
        try {
            params.put("shop_id", shopId);
            params.put("category_id", category_id);
            params.put("sub_category_id", sub_category_id);
            params.put("product_id", productDomain.getProduct_id());
            params.put("product_name", sProduct);
            params.put("orginal_price", sPrice);
            if (sSpecialPrice.equals("")) {
                params.put("special_price", sPrice);
            } else {
                params.put("special_price", sSpecialPrice);
            }
            params.put("discount", sDiscount);
            params.put("unit", sUnit);
            params.put("stock_availability", availability);
            params.put("show_for_sale", "1");

            if (imageChanged) {
                Bitmap bitmap = loadBitmapFromView(binding.selectedImage);
                convertIntoString(bitmap);
            }
            params.put("product_image", productImage);
//            }

            Log.d("request-->", params.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://estore.day2night.in/shop/updateExistProduct.php",
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
                    imageChanged = true;
                    binding.selectedImage.setVisibility(View.VISIBLE);

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(24));
                    Glide.with(mActivity)
                            .load(selectedImage)
                            .apply(requestOptions)
                            .into(binding.selectedImage);


//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
//                    byte[] b = baos.toByteArray();
//                    productImage = Base64.encodeToString(b, Base64.DEFAULT);

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_LONG).show();
            }
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
        productImage = Base64.encodeToString(b, Base64.DEFAULT);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_packLit) {
            getPackListResponse(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void getPackListResponse(JSONObject response) {
        try {
            String code = response.getString("code");
            if (code.equals("1")) {

                JSONArray packs = response.getJSONArray("packs");


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}