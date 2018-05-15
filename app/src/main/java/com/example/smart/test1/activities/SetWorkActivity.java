package com.example.smart.test1.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.smart.test1.R;
import com.example.smart.test1.bean.JsonBean;
import com.example.smart.test1.bean.WorkBean;
import com.example.smart.test1.utils.GetCityDataUtils;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Smart on 2018-04-21.
 */

public class SetWorkActivity extends AddWorkActivity {
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private GetCityDataUtils getCityDataUtils = new GetCityDataUtils();
    private WorkBean workBean;
    private ZLoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setData();
        setView();
    }

    private void setView() {
        btn_release.setText("确认修改");
        tv_title.setText("修改信息");

        btn_release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_price.getText().toString().isEmpty()) {
                    Toast.makeText(SetWorkActivity.this, "服务价格未填写", Toast.LENGTH_SHORT).show();
                } else if (et_name.getText().toString().isEmpty()) {
                    Toast.makeText(SetWorkActivity.this, "雇主称呼未填写", Toast.LENGTH_SHORT).show();
                } else {
                    workBean.setName(et_name.getText().toString());
                    workBean.setPrice(Integer.parseInt(et_price.getText().toString()));
                    workBean.setRemarks(et_remarks.getText().toString());

                    if (province != -1) {
                        workBean.setProvince(province);
                        workBean.setCity(city);
                    }
                    if (startDate != -1) {
                        workBean.setStartDate(startDate);
                    }
                    if (endDate != -1) {
                        workBean.setEndDate(endDate);
                    }
                    if (startTime != -1) {
                        String startTimeString = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(startTime);
                        workBean.setStartTime(startTimeString);
                    }
                    if (endTime != -1) {
                        String endTimeString = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(endTime);
                        workBean.setEndTime(endTimeString);
                    }
                    if (info != -1) {
                        workBean.setInfo(info);
                    }
                    if (eat != -1) {
                        workBean.setEat(eat);
                    }
                    if (live != -1) {
                        workBean.setLive(live);
                    }

                    if (workBean.getStartDate() > workBean.getEndDate()) {
                        Toast.makeText(SetWorkActivity.this, "服务日期不合法", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog = new ZLoadingDialog(SetWorkActivity.this);
                        dialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
                                .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                                .setHintText("Loading...")
                                .setHintTextSize(16) // 设置字体大小 dp
                                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                                .setCanceledOnTouchOutside(false)
                                .setCancelable(false)
                                .show();
                        update();
                    }

                }

            }
        });

    }

    private void update() {
        new BmobObject();
        workBean.update(workBean.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("bmob", "更新成功");
                    Toast.makeText(SetWorkActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SetWorkActivity.this, MyInfoActivity.class);
                    setResult(RESULT_OK,intent);
                    dialog.cancel();
                    SetWorkActivity.this.finish();
                } else {
                    Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                    Toast.makeText(SetWorkActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }

            }
        });
    }

    private void setData() {
        Intent intent = getIntent();
        workBean = (WorkBean) intent.getSerializableExtra("workBean");

        //获取地址
        options1Items = getCityDataUtils.getOptions1Items(this);
        options2Items = getCityDataUtils.getOptions2Items(this);
        int index_province = workBean.getProvince();
        int index_city = workBean.getCity();
        String province = options1Items.get(index_province).getPickerViewText();
        String city = options2Items.get(index_province).get(index_city);
        //获取服务日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd", Locale.getDefault());
        String startDate = simpleDateFormat.format(workBean.getStartDate());
        String endDate = simpleDateFormat.format(workBean.getEndDate());
        //获取服务时间
        String startTime = workBean.getStartTime();
        String endTime = workBean.getEndTime();

        RadioButton rbtn_eat1 = (RadioButton) findViewById(R.id.rbtn_eat1);
        RadioButton rbtn_eat2 = (RadioButton) findViewById(R.id.rbtn_eat2);
        RadioButton rbtn_live1 = (RadioButton) findViewById(R.id.rbtn_live1);
        RadioButton rbtn_live2 = (RadioButton) findViewById(R.id.rbtn_live2);

        btn_location.setText(String.format(getString(R.string.sf_city), province, city));
        btn_startDate.setText(startDate);
        btn_endDate.setText(endDate);
        btn_startTime.setText(startTime);
        btn_endTime.setText(endTime);
        et_name.setText(workBean.getName());
        et_price.setText(String.valueOf(workBean.getPrice()));
        et_remarks.setText(workBean.getRemarks());

        if (workBean.getEat() == 1) {
            rbtn_eat1.setChecked(true);
        } else {
            rbtn_eat2.setChecked(true);
        }

        if (workBean.getLive() == 1) {
            rbtn_live1.setChecked(true);
        } else {
            rbtn_live2.setChecked(true);
        }

        switch (workBean.getInfo()) {
            case 0:
                btn_info.setText("能自理");
                break;
            case 1:
                btn_info.setText("不能自理");
                break;
            case 2:
                btn_info.setText("半失能");
                break;
            case 3:
                btn_info.setText("手术后");
                break;
        }
    }
}
