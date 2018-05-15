package com.example.smart.test1.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart.test1.R;
import com.example.smart.test1.bean.FeedbackBean;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.BmobWrapper;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Smart on 2018-03-23.
 */

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_title;
    private Button btn_backward;
    private EditText et_content;
    private Button btn_release;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        if (BmobWrapper.getInstance() == null){
            Bmob.initialize(this, "ed4651f9a69228efadf60e15fd143933");
        }

        initView();

    }

    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_backward = (Button) findViewById(R.id.btn_backward);
        et_content = (EditText) findViewById(R.id.et_content);
        btn_release = (Button) findViewById(R.id.btn_release);

        tv_title.setText(getResources().getString(R.string.feedback));
        btn_backward.setOnClickListener(this);
        btn_release.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_backward:
                FeedbackActivity.this.finish();
                break;
            case R.id.btn_release:
                if (et_content.getText().toString().isEmpty()) {
                    Toast.makeText(FeedbackActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    saveFeedback();
                    FeedbackActivity.this.finish();
                }
                break;
        }
    }

    private void saveFeedback() {
        FeedbackBean feedback = new FeedbackBean();
        feedback.setContent(et_content.getText().toString());
        feedback.setContacts(BmobUser.getCurrentUser().getMobilePhoneNumber());
        feedback.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                Log.i("bmob", "反馈信息已保存到服务器");
                Toast.makeText(FeedbackActivity.this, "已发送反馈", Toast.LENGTH_SHORT).show();
            }

            public void onFailure(int code, String arg0) {
                // TODO Auto-generated method stub
                Log.e("bmob", "保存反馈信息失败：" + arg0);
                Toast.makeText(FeedbackActivity.this, "发送反馈失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
