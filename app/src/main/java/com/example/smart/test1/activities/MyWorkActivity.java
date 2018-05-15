package com.example.smart.test1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart.test1.R;
import com.example.smart.test1.adapter.MyWork_RecyclerViewAdapter;
import com.example.smart.test1.bean.PersonBean;
import com.example.smart.test1.bean.WorkBean;
import com.example.smart.test1.utils.MyItemDecoration;
import com.example.smart.test1.view.SwipeItemLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.BmobWrapper;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Smart on 2018-04-21.
 */

public class MyWorkActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;
    private MyWork_RecyclerViewAdapter adapter;
    private ArrayList<PersonBean> personBean = new ArrayList<>();
    private ArrayList<WorkBean> workBean = new ArrayList<>();
    private int count = 0;
    private static final int SET_PERSON_REQUEST_CODE = 0;
    private static final int SET_WORK_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywork);


        if (BmobWrapper.getInstance() == null){
            Bmob.initialize(this, "ed4651f9a69228efadf60e15fd143933");
        }


        initView();
        onRefresh();

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SET_PERSON_REQUEST_CODE || requestCode == SET_WORK_REQUEST_CODE) {
                /*swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        onRefresh();
                    }
                });*/
                onRefresh();
            }
        }
    }

    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        Button btn_backward = (Button) findViewById(R.id.btn_backward);
        tv_title.setText("我的招聘/求职信息");
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue));
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyItemDecoration());
        recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));
        adapter = new MyWork_RecyclerViewAdapter(this, personBean, workBean);
        recyclerView.setAdapter(adapter);


        btn_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyWorkActivity.this.finish();
            }
        });
    }

    public void setPerson(Object object) {
        if (object instanceof PersonBean) {
            PersonBean personBean = (PersonBean) object;
            Intent intent = new Intent(MyWorkActivity.this, SetPersonActivity.class);
            intent.putExtra("personBean", personBean);
            startActivityForResult(intent, SET_PERSON_REQUEST_CODE);
        } else {
            WorkBean workBean = (WorkBean) object;
            Intent intent = new Intent(MyWorkActivity.this, SetWorkActivity.class);
            intent.putExtra("workBean", workBean);
            startActivityForResult(intent, SET_WORK_REQUEST_CODE);
        }
    }

    //两个列表信息都获取到后才刷新列表
    protected Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            count += 1;
            if (count == 2) {
                count = 0;
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
            return false;
        }
    });


    public void getData() {

        String mobilePhoneNumber = BmobUser.getCurrentUser().getMobilePhoneNumber();//获取用户账号

        BmobQuery<PersonBean> queryPerson = new BmobQuery<PersonBean>();
        queryPerson.addWhereEqualTo("user", mobilePhoneNumber);
        queryPerson.setLimit(500);
        queryPerson.order("-createdAt");
        //执行查询方法
        queryPerson.findObjects(new FindListener<PersonBean>() {
            @Override
            public void done(List<PersonBean> object, BmobException e) {
                if (e == null) {
                    personBean.clear();
                    personBean.addAll(object);
                    handler.sendEmptyMessage(0);
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    Toast.makeText(MyWorkActivity.this, "网络超时", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        BmobQuery<WorkBean> queryWork = new BmobQuery<WorkBean>();
        queryWork.addWhereEqualTo("user", mobilePhoneNumber);
        queryWork.setLimit(500);
        queryPerson.order("-createdAt");
        //执行查询方法
        queryWork.findObjects(new FindListener<WorkBean>() {
            @Override
            public void done(List<WorkBean> object, BmobException e) {
                if (e == null) {
                    workBean.clear();
                    workBean.addAll(object);
                    handler.sendEmptyMessage(0);
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    Toast.makeText(MyWorkActivity.this, "网络超时", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }


    //刷新
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getData();
    }
}
