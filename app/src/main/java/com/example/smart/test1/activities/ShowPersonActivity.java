package com.example.smart.test1.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smart.test1.R;
import com.example.smart.test1.bean.JsonBean;
import com.example.smart.test1.bean.PersonBean;
import com.example.smart.test1.utils.BitmapCacheUtils;
import com.example.smart.test1.utils.DownloadAvatarUtils;
import com.example.smart.test1.utils.GetCityDataUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Smart on 2018-04-19.
 */

public class ShowPersonActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_avatar;
    private TextView tv_title;
    private TextView tv_name;
    private TextView tv_age;
    private TextView tv_address;
    private TextView tv_live;
    private TextView tv_eat;
    private TextView tv_workTime;
    private TextView tv_price;
    private TextView tv_introduce;
    private Button btn_backward;
    private Button btn_call_phone;

    private PersonBean personBeans;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private GetCityDataUtils getCityDataUtils = new GetCityDataUtils();
    private BitmapCacheUtils bitmapCacheUtils = new BitmapCacheUtils();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showperson);

        initView();
        initOptionData();
        setData();
    }


    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_age = (TextView) findViewById(R.id.tv_age);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_live = (TextView) findViewById(R.id.tv_live);
        tv_eat = (TextView) findViewById(R.id.tv_eat);
        tv_workTime = (TextView) findViewById(R.id.tv_worktime);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_introduce = (TextView) findViewById(R.id.tv_introduce);
        btn_backward = (Button) findViewById(R.id.btn_backward);
        btn_call_phone = (Button) findViewById(R.id.btn_call_phone);

        tv_title.setText(R.string.personal_homepage);
        btn_backward.setOnClickListener(this);
        btn_call_phone.setOnClickListener(this);
    }

    private void setData() {
        Intent intent = getIntent();
        personBeans = (PersonBean) intent.getSerializableExtra("personBean");

        tv_name.setText(personBeans.getName());
        String province = options1Items.get(personBeans.getProvince()).getPickerViewText();
        String city = options2Items.get(personBeans.getProvince()).get(personBeans.getCity());
        tv_address.setText(String.format(getString(R.string.sf_city), province, city));
        tv_price.setText(String.format(getString(R.string.sf_price_hour), personBeans.getPrice()));
        tv_introduce.setText(personBeans.getIntroduction());
        //时间转换
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String format = simpleDateFormat.format(new Date().getTime() - personBeans.getAge());
        int age = Integer.valueOf(format) - 1970;
        tv_age.setText(String.format(getString(R.string.sf_age), age));

        switch (personBeans.getEat()) {
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
        switch (personBeans.getLive()) {
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
        switch (personBeans.getWorkTime()) {
            case 1:
                tv_workTime.setText(R.string.day_and_night);
                break;
            case 2:
                tv_workTime.setText(R.string.day);
                break;
            case 3:
                tv_workTime.setText(R.string.night);
                break;
        }


        String headIcon = personBeans.getHeadIcon();
        if (personBeans.getHeadIcon() != null) {
            bitmapCacheUtils.setGetBitmapListener(headIcon, new BitmapCacheUtils.GetBitmapListener() {
                @Override
                public void getBitmap(Bitmap bitmap) {
                    iv_avatar.setImageBitmap(bitmap);
                }
            });
        }


    }



    private void initOptionData() {
        options1Items = getCityDataUtils.getOptions1Items(this);
        options2Items = getCityDataUtils.getOptions2Items(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_backward:
                this.finish();
                break;
            case R.id.btn_call_phone:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String user = personBeans.getUser();
                Uri uri = Uri.parse("tel:" + user);
                intent.setData(uri);
                startActivity(intent);
                break;
        }
    }
}
