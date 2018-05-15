package com.example.smart.test1.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart.test1.R;
import com.example.smart.test1.utils.ClassPathResourceUtil;
import com.mob.MobSDK;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.BmobWrapper;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_login;
    private EditText et_phone;
    private EditText et_password;
    private TextView tv_forget;
    private TextView tv_title;
    private Button btn_backward;
    private Button btn_forward;
    private Location location;
    private SharedPreferences sharedPreferences;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BmobWrapper.getInstance() == null){
            Bmob.initialize(this, "ed4651f9a69228efadf60e15fd143933");
        }

        initView();
    }
    private void initView(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_backward = (Button) findViewById(R.id.btn_backward);
        btn_forward = (Button) findViewById(R.id.btn_forward);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_forget = (TextView) findViewById(R.id.tv_forget);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_password = (EditText) findViewById(R.id.et_password);

        btn_backward.setVisibility(View.GONE);
        btn_forward.setVisibility(View.VISIBLE);
        btn_forward.setText(R.string.sign_up);
        tv_title.setText(R.string.login);
        tv_forget.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_forward.setOnClickListener(this);

        //自动填写密码
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String userphone = sharedPreferences.getString("icon_phone", null);
        String userpassword = sharedPreferences.getString("password", null);
        et_phone.setText(userphone);
        et_password.setText(userpassword);
    }

    private void login() {
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if (bmobUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
            Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
        } else {
            //缓存用户对象为空时， 可打开用户注册界面…
        }
    }

    private void openGPSSettings() {
        LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS定位", Toast.LENGTH_SHORT).show();
            getLocationByGps();
            return;
        } else if (alm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            getLocationByNetwork();
            Toast.makeText(this, "network定位", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocationByNetwork() {
        double latitude;
        double longitude;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {

            // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            // Provider被enable时触发此函数，比如GPS被打开
            @Override
            public void onProviderEnabled(String provider) {

            }

            // Provider被disable时触发此函数，比如GPS被关闭
            @Override
            public void onProviderDisabled(String provider) {

            }

            //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
            @Override
            public void onLocationChanged(Location location) {
               /* if (location != null) {
                    Log.e("MapActivity", "Location changed : Lat: "+ location.getLatitude() + " Lng: " + location.getLongitude());
                    Location location1 = new Location("reverseGeocoded");
                    location1.setLatitude(39.90469);
                    location1.setLongitude(116.40717);
                    float v = location.distanceTo(location1);
                    Toast.makeText(LoginActivity.this,"距离："+v/1000,Toast.LENGTH_SHORT).show();
                }*/
            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 500, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude(); //经度
            longitude = location.getLongitude(); //纬度
        }
    }

    ;


    @SuppressLint("MissingPermission")
    private void getLocationByGps() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
        criteria.setAltitudeRequired(false);//无海拔要求
        criteria.setBearingRequired(false);//无方位要求
        criteria.setCostAllowed(true);//允许产生资费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
        String provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            System.out.println("latitude:" + latitude + "," + longitude);
        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_forward:
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                if (et_phone.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                } else if (et_password.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (!ClassPathResourceUtil.isPhonenum(et_phone.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "手机号错误", Toast.LENGTH_SHORT).show();
                } else {
                    final ZLoadingDialog dialog = new ZLoadingDialog(LoginActivity.this);
                    dialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
                            .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                            .setHintText("Loading...")
                            .setHintTextSize(16) // 设置字体大小 dp
                            .setHintTextColor(Color.GRAY)  // 设置字体颜色
                            .setCanceledOnTouchOutside(false)
                            .show();
                    BmobUser bu2 = new BmobUser();
                    bu2.setUsername(et_phone.getText().toString());
                    bu2.setPassword(et_password.getText().toString());
                    bu2.login(new SaveListener<BmobUser>() {
                        @Override
                        public void done(BmobUser bmobUser, BmobException e) {
                            if (e == null) {
                                sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                                SharedPreferences.Editor edit = sharedPreferences.edit();
                                edit.putString("icon_phone", et_phone.getText().toString());
                                edit.putString("password", et_password.getText().toString());
                                edit.commit();

                                dialog.cancel();
                                //Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                setResult(RESULT_OK,intent);
                                //startActivity(intent);
                                LoginActivity.this.finish();
                                //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                                //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息

                            } else if (e.getErrorCode() == 101) {
                                dialog.cancel();
                                Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.cancel();
                                Toast.makeText(LoginActivity.this, "出错：" + e, Toast.LENGTH_SHORT).show();
                                System.out.println(e);
                            }
                        }
                    });

                }
                break;
            case R.id.tv_forget:
                intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
                break;
        }
    }


}

