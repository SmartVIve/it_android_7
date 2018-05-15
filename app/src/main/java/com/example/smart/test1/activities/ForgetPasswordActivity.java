package com.example.smart.test1.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart.test1.R;
import com.example.smart.test1.bean.MyUser;
import com.example.smart.test1.utils.ClassPathResourceUtil;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by Smart on 2017-12-17.
 */

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_backward;
    private Button btn_next;
    private EditText et_phone;
    private EditText et_code;
    private Button btn_code;
    private TextView tv_title;
    private Boolean checkCode = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        initView();

    }

    private void initView() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_backward = (Button) findViewById(R.id.btn_backward);
        btn_next = (Button) findViewById(R.id.btn_next);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_code = (EditText) findViewById(R.id.et_code);
        btn_code = (Button) findViewById(R.id.btn_code);

        tv_title.setText(getResources().getString(R.string.find_password));
        btn_backward.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_code.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_code:
                if (et_phone.getText().toString().isEmpty()){
                    Toast.makeText(ForgetPasswordActivity.this,"手机号不能为空",Toast.LENGTH_SHORT).show();
                }else if(!ClassPathResourceUtil.isPhonenum(et_phone.getText().toString())) {
                    Toast.makeText(ForgetPasswordActivity.this, "手机号错误", Toast.LENGTH_SHORT).show();
                }else {
                    //new MyThread().start();
                    BmobSMS.requestSMSCode(et_phone.getText().toString(),"SmartVive", new QueryListener<Integer>() {
                        @Override
                        public void done(Integer smsId,BmobException ex) {
                            if(ex==null){//验证码发送成功
                                Log.i("smile", "短信id："+smsId);//用于查询本次短信发送详情
                            }else {
                                System.out.println(ex);
                            }
                        }
                    });

                    checkCode = true;
                    btn_code.setBackgroundColor(Color.parseColor("#c9cacb"));
                    btn_code.setClickable(false);
                    timer.start();
                }
                break;
            case R.id.btn_next:
                if (et_phone.getText().toString().isEmpty()) {
                    Toast.makeText(ForgetPasswordActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                } else if (et_code.getText().toString().isEmpty()) {
                    Toast.makeText(ForgetPasswordActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                } else if (!ClassPathResourceUtil.isPhonenum(et_phone.getText().toString())) {
                    Toast.makeText(ForgetPasswordActivity.this, "手机号错误", Toast.LENGTH_SHORT).show();
                }else {
                    BmobUser.loginBySMSCode(et_phone.getText().toString(), et_code.getText().toString(), new LogInListener<MyUser>() {
                        @Override
                        public void done(MyUser user, BmobException e) {
                            if(user!=null){
                                Log.i("smile","用户登陆成功");
                                Intent intent = new Intent(ForgetPasswordActivity.this, ForgetPassword02Activity.class);
                                startActivity(intent);
                                ForgetPasswordActivity.this.finish();
                            }else if(e.getErrorCode()==207){
                                Toast.makeText(ForgetPasswordActivity.this,"验证码错误",Toast.LENGTH_SHORT).show();
                            }else if(e.getErrorCode()==101){
                                Toast.makeText(ForgetPasswordActivity.this,"该手机号未注册",Toast.LENGTH_SHORT).show();
                            }else {
                                System.out.println(e);
                            }
                        }
                    });
                }
                break;
            case R.id.btn_backward:
                ForgetPasswordActivity.this.finish();
                break;
        }
    }
    private CountDownTimer timer = new CountDownTimer(60*1000,1000) {
        public void onTick(long millisUntilFinished) {
            btn_code.setText(millisUntilFinished/1000+"秒后重获");
        }
        public void onFinish() {
            btn_code.setClickable(true);
            btn_code.setBackgroundColor(Color.parseColor("#5fc0cd"));
            btn_code.setText("获取验证码");
        }
    };


}
