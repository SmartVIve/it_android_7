package com.example.smart.test1.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.LinkedList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Smart on 2017-12-14.
 */

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_signUp;
    private Button btn_code;
    private EditText et_phone;
    private EditText et_password;
    private EditText et_code;
    private TextView tv_title;
    private Button btn_backward;
    private Boolean isCode = false;
    private int phonecode;
    private String mphone;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();

    }
    private void initView(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_backward = (Button) findViewById(R.id.btn_backward);
        btn_code = (Button) findViewById(R.id.btn_code);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_password = (EditText) findViewById(R.id.et_password);
        et_code= (EditText) findViewById(R.id.et_code);
        btn_signUp = (Button) findViewById(R.id.btn_signUp);

        tv_title.setText(getResources().getString(R.string.sign_up));
        btn_backward.setOnClickListener(this);
        btn_signUp.setOnClickListener(this);

    }

    private CountDownTimer timer = new CountDownTimer(60*1000,1000) {
        public void onTick(long millisUntilFinished) {
            btn_code.setText(millisUntilFinished/1000+"秒后重获");
        }
        public void onFinish() {
            btn_code.setClickable(true);
            btn_code.setBackgroundColor(Color.parseColor("#5fc0cd"));
            btn_code.setText("重新获取");
        }
    };

    public  void  code(View view){
        if (et_phone.getText().toString().isEmpty()){
            Toast.makeText(SignUpActivity.this,"手机号不能为空",Toast.LENGTH_SHORT).show();
        }else if(!ClassPathResourceUtil.isPhonenum(et_phone.getText().toString())) {
            Toast.makeText(SignUpActivity.this, "手机号错误", Toast.LENGTH_SHORT).show();
        }else {
            new MyThread().start();
            isCode = true;
            btn_code.setBackgroundColor(Color.parseColor("#c9cacb"));
            btn_code.setClickable(false);
            timer.start();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_signUp:
                if (et_phone.getText().toString().isEmpty()){
                    Toast.makeText(SignUpActivity.this,"手机号不能为空",Toast.LENGTH_SHORT).show();
                }else if (et_password.getText().toString().isEmpty()){
                    Toast.makeText(SignUpActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }else if (et_code.getText().toString().isEmpty()){
                    Toast.makeText(SignUpActivity.this,"验证码不能为空",Toast.LENGTH_SHORT).show();
                }else if(!ClassPathResourceUtil.isPhonenum(et_phone.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "手机号错误", Toast.LENGTH_SHORT).show();
                }else if(!isCode){
                    Toast.makeText(SignUpActivity.this,"请先获取验证码",Toast.LENGTH_SHORT).show();
                }else if(!(et_code.getText().toString().equals(String.valueOf(phonecode)))){
                    Toast.makeText(SignUpActivity.this,"验证码错误",Toast.LENGTH_SHORT).show();
                }else{
                    MyUser user = new MyUser();
                    user.setUsername(mphone);
                    user.setPassword(et_password.getText().toString());
                    user.setMobilePhoneNumber(mphone);
                    user.signUp(new SaveListener<MyUser>() {
                        @Override
                        public void done(MyUser myUser, BmobException e) {
                            if(e==null){
                                Toast.makeText(SignUpActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                SignUpActivity.this.finish();
                            }else if (e.getErrorCode()==202){
                                Toast.makeText(SignUpActivity.this,"该用户已存在",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(SignUpActivity.this,"失败:"+e.toString(),Toast.LENGTH_SHORT).show();
                                System.out.println(e);
                            }
                        }
                    });
                }
                break;
            case R.id.btn_backward :
                    SignUpActivity.this.finish();
                    break;
        }
    }

    public class MyThread extends Thread {
        public void run(){
            try {
                mphone = et_phone.getText().toString();
                phonecode = (int) (Math.random() * 9000+1000);
                String content = "您的验证码是："+phonecode+"。请不要把验证码泄露给其他人。";
                HttpPost httpPost = new HttpPost("http://106.ihuyi.com/webservice/sms.php?method=Submit");
                LinkedList params = new LinkedList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("account", "C19083824"));
                params.add(new BasicNameValuePair("password", "43ff755fa212bb7f74efa1d0bc12c423"));
                params.add(new BasicNameValuePair("mobile", et_phone.getText().toString()));
                params.add(new BasicNameValuePair("content", content));
                String param = URLEncodedUtils.format(params, "UTF-8");
                httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(httpPost);
                String entity2 = EntityUtils.toString(response.getEntity());
                System.out.println(entity2);

                /*phonecode = (int) (Math.random() * 9000+1000);
                System.out.println(phonecode);
                String date = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
                String md5 = MD5Util.encrypt("d7e0cfcfe01f400e9f68cb94b63e41b0b7b8a90d15874ec38bb762b61a07f9b0" + date);
                HttpPost httpPost = new HttpPost("https://api.miaodiyun.com/20150822/industrySMS/sendSMS");
                LinkedList params = new LinkedList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("accountSid", "d7e0cfcfe01f400e9f68cb94b63e41b0"));
                params.add(new BasicNameValuePair("templateid", "145702997"));
                params.add(new BasicNameValuePair("param", ""+phonecode));
                params.add(new BasicNameValuePair("to", et_phone.getText().toString()));
                params.add(new BasicNameValuePair("timestamp", date));
                params.add(new BasicNameValuePair("sig", md5));
                String param = URLEncodedUtils.format(params, "UTF-8");
                httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
                HttpClient client = MyHttpClient.getNewHttpClient();
                HttpResponse response = client.execute(httpPost);
                String entity2 = EntityUtils.toString(response.getEntity());
                System.out.println(entity2);*/

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("失败");
            }
        }
    }



}
