package com.example.smart.test1.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.smart.test1.R;
import com.example.smart.test1.bean.JsonBean;
import com.example.smart.test1.bean.WorkBean;
import com.example.smart.test1.utils.GetCityDataUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Smart on 2018-04-20.
 */

public class ShowWorkActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_title;
    private TextView tv_name;
    private TextView tv_address;
    private TextView tv_live;
    private TextView tv_eat;
    private TextView tv_date;
    private TextView tv_price;
    private TextView tv_info;
    private TextView tv_remarks;
    private Button btn_backward;
    private Button btn_call_phone;
    private WorkBean workBean;

    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private GetCityDataUtils getCityDataUtils = new GetCityDataUtils();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showwork);

        initView();
        initOptionData();
        setData();
    }


    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_live = (TextView) findViewById(R.id.tv_live);
        tv_eat = (TextView) findViewById(R.id.tv_eat);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_info = (TextView) findViewById(R.id.tv_info);
        tv_remarks = (TextView) findViewById(R.id.tv_remarks);
        btn_backward = (Button) findViewById(R.id.btn_backward);
        btn_call_phone = (Button) findViewById(R.id.btn_call_phone);

        tv_title.setText("雇主主页");
        btn_backward.setOnClickListener(this);
        btn_call_phone.setOnClickListener(this);
    }

    private void setData() {
        Intent intent = getIntent();
        workBean = (WorkBean) intent.getSerializableExtra("workBean");

        tv_name.setText(workBean.getName());
        int index_province = workBean.getProvince();
        int index_city = workBean.getCity();
        String province = options1Items.get(index_province).getPickerViewText();
        String city = options2Items.get(index_province).get(index_city);
        tv_address.setText(String.format(getString(R.string.sf_city), province, city));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd", Locale.getDefault());
        String startDate = simpleDateFormat.format(workBean.getStartDate());
        String endDate = simpleDateFormat.format(workBean.getEndDate());
        tv_date.setText(String.format(getString(R.string.sf_date),startDate,endDate));
        tv_price.setText(String.format(getString(R.string.sf_price_hour),workBean.getPrice()));
        tv_remarks.setText(workBean.getRemarks());
        switch (workBean.getInfo()){
            case 0:
                tv_info.setText(R.string.can_self_care);
                break;
            case 1:
                tv_info.setText(R.string.cannot_self_care);
                break;
            case 2:
                tv_info.setText(R.string.semi_disability);
                break;
            case 3:
                tv_info.setText(R.string.postop);
                break;
        }
        switch (workBean.getLive()) {
            case 0:
                tv_live.setText(R.string.unlimited_live);
                break;
            case 1:
                tv_live.setText(R.string.live);
                break;
            case 2:
                tv_live.setText(R.string.not_live);
                break;
        }
        switch (workBean.getEat()) {
            case 0:
                tv_eat.setText(R.string.unlimited_eat);
                break;
            case 1:
                tv_eat.setText(R.string.eat);
                break;
            case 2:
                tv_eat.setText(R.string.not_eat);
                break;
        }


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_backward:
                this.finish();
                break;
            case R.id.btn_call_phone:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String user = workBean.getUser();
                Uri uri = Uri.parse("tel:" + user);
                intent.setData(uri);
                startActivity(intent);
                break;
        }
    }
    private void initOptionData() {
        options1Items = getCityDataUtils.getOptions1Items(this);
        options2Items = getCityDataUtils.getOptions2Items(this);
    }
}
