package com.example.smart.test1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.smart.test1.R;


/**
 * Created by Smart on 2018-04-02.
 */

public class SetNameActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_name;
    private TextView tv_title;
    private Button btn_forward;
    private Button btn_backward;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setname);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        et_name = (EditText) findViewById(R.id.tv_name);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_forward = (Button) findViewById(R.id.btn_forward);
        btn_backward = (Button) findViewById(R.id.btn_backward);

        tv_title.setText(R.string.set_name);
        btn_backward.setText(R.string.cancel);
        btn_forward.setVisibility(View.VISIBLE);
        btn_forward.setText(R.string.save);
        et_name.setText(name);
        btn_backward.setOnClickListener(this);
        btn_forward.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_backward:
                SetNameActivity.this.finish();
                break;
            case R.id.btn_forward:
                Intent intent = new Intent(SetNameActivity.this,MyInfoActivity.class);
                intent.putExtra("name",et_name.getText().toString());
                setResult(RESULT_OK,intent);
                SetNameActivity.this.finish();
                break;
        }
    }
}
