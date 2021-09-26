package com.online.estoreshop.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.online.estoreshop.R;
import com.online.estoreshop.base.BaseActivity;
import com.online.estoreshop.databinding.ActivityEditTimeBinding;
import com.online.estoreshop.utils.Constants;
import com.online.estoreshop.utils.UrlConstants;
import com.online.estoreshop.volleyservice.VolleyInterface;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import static com.online.estoreshop.utils.UrlConstants.REQ_shopTimeEdit;

public class EditTimeActivity extends BaseActivity implements View.OnClickListener, VolleyInterface {

    ActivityEditTimeBinding binding;
    String sOpenTime = "", sCLoseTime = "";

    String shopId;
    String OPEN_TIME;
    String CLOSE_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_time);

        shopId = sharedPreferences.getString(Constants.SHOP_ID, "");
        OPEN_TIME = sharedPreferences.getString(Constants.OPEN_TIME, "");
        CLOSE_TIME = sharedPreferences.getString(Constants.CLOSE_TIME, "");

        mActivity = this;

        binding.tvOpenTime.setText(OPEN_TIME);
        binding.tvCLoseTime.setText(CLOSE_TIME);

        binding.back.setOnClickListener(this);
        binding.tvOpenTime.setOnClickListener(this);
        binding.tvCLoseTime.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);

    }

    private void timePick2() {
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(mActivity,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker,
                                          int selectedHour, int selectedMinute) {
                        int hour = selectedHour;
                        String timeSet = "";
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "pm";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "am";
                        } else if (hour == 12) {
                            timeSet = "pm";
                        } else {
                            timeSet = "am";
                        }

                        String min = "";
                        if (selectedMinute < 10)
                            min = "0" + selectedMinute;
                        else
                            min = String.valueOf(selectedMinute);

                        String aTime = String.valueOf(hour) + ':' +
                                min + " " + timeSet;
                        binding.tvCLoseTime.setText(aTime);
                    }
                }, hour, minute, false);


        mTimePicker.show();
    }

    private void timePick() {
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(mActivity,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker,
                                          int selectedHour, int selectedMinute) {

                        int hour = selectedHour;
                        String timeSet = "";
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "pm";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "am";
                        } else if (hour == 12) {
                            timeSet = "pm";
                        } else {
                            timeSet = "am";
                        }

                        String min = "";
                        if (selectedMinute < 10)
                            min = "0" + selectedMinute;
                        else
                            min = String.valueOf(selectedMinute);

                        String aTime = String.valueOf(hour) + ':' +
                                min + " " + timeSet;
                        binding.tvOpenTime.setText(aTime);
                    }
                }, hour, minute, false);
        mTimePicker.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tvOpenTime:
                timePick();
                break;
            case R.id.tvCLoseTime:
                timePick2();
                break;
            case R.id.btnSubmit:
                checkValues();
                break;
        }
    }

    private void checkValues() {
        sOpenTime = binding.tvOpenTime.getText().toString().trim();
        sCLoseTime = binding.tvCLoseTime.getText().toString().trim();
        if (sOpenTime.equals("")) {
            Toast.makeText(mActivity, "Please select open time", Toast.LENGTH_SHORT).show();
        } else if (sCLoseTime.equals("")) {
            Toast.makeText(mActivity, "Please select closing time", Toast.LENGTH_SHORT).show();
        } else {
            callService();
        }
    }

    private void callService() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("shop_id", shopId);
        params.put("open_time", sOpenTime);
        params.put("close_time", sCLoseTime);
        String url = UrlConstants.shopTimeEdit;
        volleyUtils.callApi(mActivity, this, url, REQ_shopTimeEdit, params, 1, true);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_shopTimeEdit) {
            editor.putString(Constants.OPEN_TIME, sOpenTime);
            editor.putString(Constants.CLOSE_TIME, sCLoseTime);
            editor.apply();
            editTimeResponse(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void editTimeResponse(JSONObject response) {
        try {
            String code = response.getString("code");
            Toast.makeText(mActivity, "" + response.getString("status"), Toast.LENGTH_SHORT).show();
            if (code.equals("1")) {


                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}