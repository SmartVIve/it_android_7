package com.example.smart.test1.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.smart.test1.R;
import com.example.smart.test1.bean.JsonBean;
import com.example.smart.test1.bean.PersonBean;
import com.example.smart.test1.utils.BitmapCacheUtils;
import com.example.smart.test1.utils.DownloadAvatarUtils;
import com.example.smart.test1.utils.GetCityDataUtils;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobWrapper;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class SetPersonActivity extends AddPersonActivity {
    private PersonBean personBean;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private GetCityDataUtils getCityDataUtils = new GetCityDataUtils();
    private BitmapCacheUtils bitmapCacheUtils = new BitmapCacheUtils();
    private ZLoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BmobWrapper.getInstance() == null) {
            Bmob.initialize(this, "ed4651f9a69228efadf60e15fd143933");
        }

        setData();
        setView();
    }


    private void setView() {
        btn_release.setText("确认修改");
        tv_title.setText("修改信息");

        btn_release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_name.getText().toString().isEmpty()) {
                    Toast.makeText(SetPersonActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                } else if (et_price.getText().toString().isEmpty()) {
                    Toast.makeText(SetPersonActivity.this, "服务价格未填写", Toast.LENGTH_SHORT).show();
                } else {


                    personBean.setName(et_name.getText().toString());
                    personBean.setPrice(Integer.parseInt(et_price.getText().toString()));
                    personBean.setIntroduction(et_introduction.getText().toString());

                    if (province != -1) {
                        personBean.setProvince(province);
                        personBean.setCity(city);
                    }
                    if (birthDate != -1) {
                        personBean.setAge(birthDate);
                    }
                    if (level != -1) {
                        personBean.setLevel(level);
                    }
                    if (workTime != -1) {
                        personBean.setWorkTime(workTime);
                    }
                    if (eat != -1) {
                        personBean.setEat(eat);
                    }
                    if (live != -1) {
                        personBean.setLive(live);
                    }
                    dialog = new ZLoadingDialog(SetPersonActivity.this);
                    dialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
                            .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                            .setHintText("Loading...")
                            .setHintTextSize(16) // 设置字体大小 dp
                            .setHintTextColor(Color.GRAY)  // 设置字体颜色
                            .setCanceledOnTouchOutside(false)
                            .setCancelable(false)
                            .show();
                    if (bm != null) {
                        upload(headClipFile.getAbsolutePath());
                    } else {
                        update();
                    }

                }

            }
        });
    }

    protected Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            personBean.setHeadIcon(bmobFile.getFileUrl());
            update();
            return false;
        }
    });

    /*
     * bomb上传头像
     */
    protected void upload(String headIconFile) {
        bmobFile = new BmobFile(new File(headIconFile));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("i", "上传完成" + bmobFile.getFileUrl());
                    handler.sendEmptyMessage(0);
                } else {
                    Toast.makeText(SetPersonActivity.this, "上传失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("e", "上传失败:" + e.getMessage());
                }
            }
        });
    }


    private void update() {
        new BmobObject();
        personBean.update(personBean.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("bmob", "更新成功");
                    Toast.makeText(SetPersonActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SetPersonActivity.this, MyInfoActivity.class);
                    setResult(RESULT_OK, intent);
                    dialog.cancel();
                    SetPersonActivity.this.finish();
                } else {
                    Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                    dialog.cancel();
                    Toast.makeText(SetPersonActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setData() {
        Intent intent = getIntent();
        personBean = (PersonBean) intent.getSerializableExtra("personBean");

        //获取生日
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String age = simpleDateFormat.format(personBean.getAge());
        //获取地址
        options1Items = getCityDataUtils.getOptions1Items(this);
        options2Items = getCityDataUtils.getOptions2Items(this);
        int index_province = personBean.getProvince();
        int index_city = personBean.getCity();
        String province = options1Items.get(index_province).getPickerViewText();
        String city = options2Items.get(index_province).get(index_city);

        RadioButton rbtn_level1 = (RadioButton) findViewById(R.id.rbtn_level1);
        RadioButton rbtn_level2 = (RadioButton) findViewById(R.id.rbtn_level2);
        RadioButton rbtn_level3 = (RadioButton) findViewById(R.id.rbtn_level3);
        RadioButton rbtn_workTime1 = (RadioButton) findViewById(R.id.rbtn_workTime1);
        RadioButton rbtn_workTime2 = (RadioButton) findViewById(R.id.rbtn_workTime2);
        RadioButton rbtn_workTime3 = (RadioButton) findViewById(R.id.rbtn_workTime3);
        RadioButton rbtn_eat1 = (RadioButton) findViewById(R.id.rbtn_eat1);
        RadioButton rbtn_eat2 = (RadioButton) findViewById(R.id.rbtn_eat2);
        RadioButton rbtn_eat3 = (RadioButton) findViewById(R.id.rbtn_eat3);
        RadioButton rbtn_live1 = (RadioButton) findViewById(R.id.rbtn_live1);
        RadioButton rbtn_live2 = (RadioButton) findViewById(R.id.rbtn_live2);
        RadioButton rbtn_live3 = (RadioButton) findViewById(R.id.rbtn_live3);

        et_name.setText(personBean.getName());
        btn_birthdate.setText(age);
        btn_location.setText(String.format(getString(R.string.sf_city), province, city));
        et_introduction.setText(personBean.getIntroduction());
        et_price.setText(String.valueOf(personBean.getPrice()));

        switch (personBean.getLevel()) {
            case 1:
                rbtn_level1.setChecked(true);
                break;
            case 2:
                rbtn_level2.setChecked(true);
                break;
            case 3:
                rbtn_level3.setChecked(true);
                break;
        }

        switch (personBean.getWorkTime()) {
            case 1:
                rbtn_workTime1.setChecked(true);
                break;
            case 2:
                rbtn_workTime2.setChecked(true);
                break;
            case 3:
                rbtn_workTime3.setChecked(true);
                break;
        }

        switch (personBean.getEat()) {
            case 0:
                rbtn_eat1.setChecked(true);
                break;
            case 1:
                rbtn_eat2.setChecked(true);
                break;
            case 2:
                rbtn_eat3.setChecked(true);
                break;
        }

        switch (personBean.getLive()) {
            case 0:
                rbtn_live1.setChecked(true);
                break;
            case 1:
                rbtn_live2.setChecked(true);
                break;
            case 2:
                rbtn_live3.setChecked(true);
                break;
        }

        String headIcon = personBean.getHeadIcon();
        if (personBean.getHeadIcon() != null) {
            bitmapCacheUtils.setGetBitmapListener(headIcon, new BitmapCacheUtils.GetBitmapListener() {
                @Override
                public void getBitmap(Bitmap bitmap) {
                    btn_camera.setImageBitmap(bitmap);
                }
            });
        }


    }
}