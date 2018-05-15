package com.example.smart.test1.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.example.smart.test1.R;
import com.example.smart.test1.bean.WorkBean;
import com.example.smart.test1.view.PickerView;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.BmobWrapper;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Smart on 2018-03-14.
 */

public class AddWorkActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private ArrayList<String> info_options = new ArrayList<>();
    protected PickerView btn_location;
    protected PickerView btn_startDate;
    protected PickerView btn_endDate;
    protected PickerView btn_startTime;
    protected PickerView btn_endTime;
    protected PickerView btn_info;
    protected EditText et_price;
    protected EditText et_name;
    protected EditText et_remarks;
    protected Button btn_release;
    protected TextView tv_title;


    protected int province = -1;
    protected int city = -1;
    protected int eat = -1;
    protected int live = -1;
    protected int info = -1;
    protected long startDate = -1;
    protected long endDate = -1;
    protected long startTime = -1;
    protected long endTime = -1;
    protected double latitude;
    protected double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addwork);

        if (BmobWrapper.getInstance() == null) {
            Bmob.initialize(this, "ed4651f9a69228efadf60e15fd143933");
        }

        initView();
        initPickerView();
        initLocation();
    }



    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        LinearLayout main = (LinearLayout) findViewById(R.id.main);
        Button btn_backward = (Button) findViewById(R.id.btn_backward);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_location = (PickerView) findViewById(R.id.btn_location);
        btn_startDate = (PickerView) findViewById(R.id.btn_startDate);
        btn_endDate = (PickerView) findViewById(R.id.btn_endDate);
        btn_startTime = (PickerView) findViewById(R.id.btn_startTime);
        btn_endTime = (PickerView) findViewById(R.id.btn_endTime);
        btn_info = (PickerView) findViewById(R.id.btn_info);
        RadioGroup group_eat = (RadioGroup) findViewById(R.id.group_eat);
        RadioGroup group_live = (RadioGroup) findViewById(R.id.group_live);
        et_price = (EditText) findViewById(R.id.et_price);
        et_name = (EditText) findViewById(R.id.tv_name);
        et_remarks = (EditText) findViewById(R.id.remarks);
        btn_release = (Button) findViewById(R.id.btn_release);

        tv_title.setText(getResources().getString(R.string.find_person));
        main.setOnClickListener(this);
        btn_backward.setOnClickListener(this);
        btn_location.setOnClickListener(this);
        btn_startDate.setOnClickListener(this);
        btn_endDate.setOnClickListener(this);
        btn_startTime.setOnClickListener(this);
        btn_endTime.setOnClickListener(this);
        btn_info.setOnClickListener(this);
        group_eat.setOnCheckedChangeListener(this);
        group_live.setOnCheckedChangeListener(this);
        btn_release.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        hideInput();
        switch (v.getId()) {
            case R.id.btn_backward:
                AddWorkActivity.this.finish();
                break;
            case R.id.btn_location:
                btn_location.show();
                break;
            case R.id.btn_startDate:
                btn_startDate.show();
                break;
            case R.id.btn_endDate:
                btn_endDate.show();
                break;
            case R.id.btn_startTime:
                btn_startTime.show();
                break;
            case R.id.btn_endTime:
                btn_endTime.show();
                break;
            case R.id.btn_info:
                btn_info.show();
                break;
            case R.id.btn_release:
                String name = et_name.getText().toString();
                String price = et_price.getText().toString();
                String remarks = et_remarks.getText().toString();

                String startTimeString = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(startTime);
                String endTimeString = new SimpleDateFormat("HH:mm",Locale.getDefault()).format(endTime);


                if (city == -1) {
                    Toast.makeText(AddWorkActivity.this, "所在地未选择", Toast.LENGTH_SHORT).show();
                } else if (startDate == -1 || endDate == -1) {
                    Toast.makeText(AddWorkActivity.this, "服务日期未选择", Toast.LENGTH_SHORT).show();
                } else if (startTime == -1 || endTime == -1) {
                    Toast.makeText(AddWorkActivity.this, "服务时间未选择", Toast.LENGTH_SHORT).show();
                } else if (eat == -1) {
                    Toast.makeText(AddWorkActivity.this, "是否含餐未选择", Toast.LENGTH_SHORT).show();
                } else if (live == -1) {
                    Toast.makeText(AddWorkActivity.this, "是否住家未选择", Toast.LENGTH_SHORT).show();
                } else if (info == -1) {
                    Toast.makeText(AddWorkActivity.this, "病人情况未选择", Toast.LENGTH_SHORT).show();
                } else if (price.isEmpty()) {
                    Toast.makeText(AddWorkActivity.this, "服务价格未填写", Toast.LENGTH_SHORT).show();
                } else if (name.isEmpty()) {
                    Toast.makeText(AddWorkActivity.this, "雇主称呼未填写", Toast.LENGTH_SHORT).show();
                } else if (startDate > endDate) {
                    Toast.makeText(AddWorkActivity.this, "服务日期不合法", Toast.LENGTH_SHORT).show();
                } else if (startTime > endTime) {
                    Toast.makeText(AddWorkActivity.this, "服务时间不合法", Toast.LENGTH_SHORT).show();
                } else {
                    final ZLoadingDialog dialog = new ZLoadingDialog(AddWorkActivity.this);
                    dialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
                            .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                            .setHintText("Loading...")
                            .setHintTextSize(16) // 设置字体大小 dp
                            .setHintTextColor(Color.GRAY)  // 设置字体颜色
                            .show();
                    String mobilePhoneNumber = BmobUser.getCurrentUser().getMobilePhoneNumber();
                    WorkBean workBean = new WorkBean();
                    workBean.setLatitude(latitude);
                    workBean.setLongitude(longitude);
                    workBean.setUser(mobilePhoneNumber);
                    workBean.setName(name);
                    workBean.setProvince(province);
                    workBean.setCity(city);
                    workBean.setEat(eat);
                    workBean.setLive(live);
                    workBean.setInfo(info);
                    workBean.setPrice(Integer.parseInt(price));
                    workBean.setStartDate(startDate);
                    workBean.setEndDate(endDate);
                    workBean.setStartTime(startTimeString);
                    workBean.setEndTime(endTimeString);
                    workBean.setRemarks(remarks);
                    workBean.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                dialog.cancel();
                                Toast.makeText(AddWorkActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddWorkActivity.this, MainActivity.class);
                                setResult(RESULT_OK, intent);
                                AddWorkActivity.this.finish();
                            } else {
                                dialog.cancel();
                                Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                Toast.makeText(AddWorkActivity.this, "创建数据失败：" + s, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
        }
    }

    private void initPickerView() {
        info_options.add("能自理");
        info_options.add("不能自理");
        info_options.add("半失能");
        info_options.add("手术后");

        btn_info.initData(this, info_options, new PickerView.PickerViewSelectListener() {
            @Override
            public void onPickerViewSelec(int position, String arg) {
                info = position;
                btn_info.setText(arg);
            }
        });

        btn_location.initLocationData(this, new PickerView.LocationPickerViewSelectListener() {
            @Override
            public void onNPickerViewSelec(int provincePosition, int cityPosition, String arg, String arg2) {
                province = provincePosition;
                city = cityPosition;
                btn_location.setText(arg2);
            }
        });

        final Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.setTime(Calendar.getInstance().getTime());
        endDate.set(2050, 11, 31);

        btn_startDate.initTimeData(this, new boolean[]{true, true, true, false, false, false}, "yy-MM-dd", startDate, endDate, new PickerView.TimePickerViewSelectListener() {
            @Override
            public void onTimePickerViewSelec(long time, String arg) {
                AddWorkActivity.this.startDate = time;
                btn_startDate.setText(arg);
            }
        });

        btn_endDate.initTimeData(this, new boolean[]{true, true, true, false, false, false}, "yy-MM-dd", startDate, endDate, new PickerView.TimePickerViewSelectListener() {
            @Override
            public void onTimePickerViewSelec(long time, String arg) {
                AddWorkActivity.this.endDate = time;
                btn_endDate.setText(arg);
            }
        });

        btn_startTime.initTimeData(this, new boolean[]{false, false, false, true, true, false}, "HH:mm", null, null, new PickerView.TimePickerViewSelectListener() {
            @Override
            public void onTimePickerViewSelec(long time, String arg) {
                startTime = time;
                btn_startTime.setText(arg);
            }
        });

        btn_endTime.initTimeData(this, new boolean[]{false, false, false, true, true, false}, "HH:mm", null, null, new PickerView.TimePickerViewSelectListener() {
            @Override
            public void onTimePickerViewSelec(long time, String arg) {
                endTime = time;
                btn_endTime.setText(arg);
            }
        });
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        hideInput();
        switch (checkedId) {
            case R.id.rbtn_eat1:
                eat = 1;
                break;
            case R.id.rbtn_eat2:
                eat = 2;
                break;
            case R.id.rbtn_live1:
                live = 1;
                break;
            case R.id.rbtn_live2:
                live = 2;
                break;
        }
    }

    private void initLocation() {
        LocationClient mLocationClient = new LocationClient(this);
        //声明LocationClient类
        mLocationClient.registerLocationListener(new MyLocationListener());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    //获取当前位置
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            MyLocationData locData = new MyLocationData.Builder().accuracy(bdLocation.getRadius()).latitude(bdLocation.getLatitude()).longitude(bdLocation.getLongitude()).build();
            latitude = bdLocation.getLatitude();    //获取纬度信息
            longitude = bdLocation.getLongitude();
        }
    }

    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
