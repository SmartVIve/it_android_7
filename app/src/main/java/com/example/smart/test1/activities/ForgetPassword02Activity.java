package com.example.smart.test1.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart.test1.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Smart on 2017-12-17.
 */

public class ForgetPassword02Activity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_backward;
    private Button btn_submit;
    private EditText et_password;
    private EditText et_password02;
    private TextView tv_title;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword02);

        initView();

    }

    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_backward = (Button) findViewById(R.id.btn_backward);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password02 = (EditText) findViewById(R.id.et_password02);

        tv_title.setText(getResources().getString(R.string.find_password));
        btn_backward.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_backward:
                ForgetPassword02Activity.this.finish();
                break;
            case R.id.btn_submit:
                if (et_password.getText().toString().isEmpty()|| et_password02.getText().toString().isEmpty()){
                    Toast.makeText(ForgetPassword02Activity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }else if (!(et_password.getText().toString().equals(et_password02.getText().toString()))){
                    Toast.makeText(ForgetPassword02Activity.this,"密码不一致",Toast.LENGTH_SHORT).show();
                }else{
                    BmobUser newUser = new BmobUser();
                    newUser.setPassword(et_password.getText().toString());
                    BmobUser bmobUser = BmobUser.getCurrentUser();
                    newUser.update(bmobUser.getObjectId(),new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(ForgetPassword02Activity.this,"修改成功",Toast.LENGTH_SHORT).show();
                                ForgetPassword02Activity.this.finish();
                            }else{
                                Toast.makeText(ForgetPassword02Activity.this,"修改失败",Toast.LENGTH_SHORT).show();
                                System.out.println(e);
                            }
                        }
                    });

                }
                break;
        }
    }
}
