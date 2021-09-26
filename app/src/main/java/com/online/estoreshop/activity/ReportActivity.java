package com.online.estoreshop.activity;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.online.estoreshop.R;
import com.online.estoreshop.adapter.ReportAdapter;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityReportBinding;
import com.online.estoreshop.models.ReportDomain;
import com.online.estoreshop.utils.CommonUtils;
import com.online.estoreshop.utils.Constants;
import com.online.estoreshop.utils.UrlConstants;
import com.online.estoreshop.volleyservice.VolleyInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.online.estoreshop.utils.UrlConstants.REQ_salesReport;

public class ReportActivity extends BaseActivity implements View.OnClickListener, VolleyInterface {

    ActivityReportBinding binding;
    ReportAdapter adapter;
    ArrayList<ReportDomain> reportList = new ArrayList<>();
    String shopId = "";
    boolean flag= false;
    final Calendar myCalendar = Calendar.getInstance();
    String startDate = "";
    String endDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report);

        shopId = sharedPreferences.getString(Constants.SHOP_ID, "");

        mActivity = this;

        setupRecycler();

        callService("","");

        binding.tvDate2.setText(CommonUtils.getTodayDate2());
        binding.back.setOnClickListener(this);
        binding.tvDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                new DatePickerDialog(mActivity, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        binding.tvDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                new DatePickerDialog(mActivity, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }


    private void setupRecycler() {
        binding.recylerView.setHasFixedSize(true);
        binding.recylerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new ReportAdapter(mActivity, reportList);
        binding.recylerView.setAdapter(adapter);
    }

    private void callService(String date1, String date2) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("shop_id", shopId);
        if (date1.equals("") && date2.equals("")) {
            params.put("startdate", CommonUtils.getTodayDate());
            params.put("enddate", CommonUtils.getTodayDate());
        } else if(date1.equals("")){
            params.put("startdate", date2);
            params.put("enddate", date2);
        }else if(date2.equals("")){
            params.put("startdate", date1);
            params.put("enddate", CommonUtils.getTodayDate());
        }
        else{
            params.put("startdate", date1);
            params.put("enddate", date2);
        }
        String url = UrlConstants.salesReport;
        volleyUtils.callApi(mActivity, this, url, REQ_salesReport, params, 1, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }


    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_salesReport) {
            salesReportResponse(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void salesReportResponse(JSONObject response) {
        try {

            reportList.clear();
            String code = response.getString("code");
            if (code.equals("1")) {

                JSONArray reportDetail = response.getJSONArray("reportDetail");
                for (int i = 0; i < reportDetail.length(); i++) {
                    JSONObject jsonObject = reportDetail.getJSONObject(i);

                    String report_date = jsonObject.getString("report_date");
                    String customer_name = jsonObject.getString("customer_name");
                    String category_title = jsonObject.getString("category_title");
                    String sub_category_title = jsonObject.getString("sub_category_title");
                    String quantity = jsonObject.getString("quantity");
                    String price = jsonObject.getString("price");
                    String product_name = jsonObject.getString("product_name");
                    String unit = jsonObject.getString("unit");

                    reportList.add(new ReportDomain(report_date, customer_name, category_title, sub_category_title, quantity, price, product_name, unit));

                }

                if (reportList.size() > 0) {
                    binding.titlelayout.setVisibility(View.VISIBLE);
                    binding.emptyLayout.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    binding.recylerView.scheduleLayoutAnimation();
                } else {
                    binding.emptyLayout.setVisibility(View.VISIBLE);
                    binding.titlelayout.setVisibility(View.GONE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat df = new SimpleDateFormat("dd EEEE, MMMM yyyy", Locale.US);

            if(flag){
                flag = false;
                binding.tvDate1.setText(df.format(myCalendar.getTime()));
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                if (CommonUtils.checkConnectivity(ReportActivity.this)) {
                    startDate = sdf.format(myCalendar.getTime());
                    callService(sdf.format(myCalendar.getTime()),endDate);
                } else {
                    Toast.makeText(ReportActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }else {
                binding.tvDate2.setText(df.format(myCalendar.getTime()));
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                if (CommonUtils.checkConnectivity(ReportActivity.this)) {
                    endDate = sdf.format(myCalendar.getTime());
                    callService(startDate,sdf.format(myCalendar.getTime()));
                } else {
                    Toast.makeText(ReportActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

}