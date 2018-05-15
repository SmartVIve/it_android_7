package com.example.smart.test1.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.smart.test1.R;
import com.example.smart.test1.fragment.PersonFragment;
import com.example.smart.test1.fragment.WorkFragment;
import com.example.smart.test1.fragment.MyFragment;
import com.example.smart.test1.fragment.ViewPagerFragment;
import com.mob.MobSDK;
import com.zhy.m.permission.MPermissions;

import java.lang.reflect.Method;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

/**
 * Created by Smart on 2017-12-18.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private ImageButton btn_write;
    private Button btn_addPerson;
    private Button btn_addWork;
    private RadioGroup group_bottomBar;
    private MyFragment myFragment;
    private ViewPagerFragment viewPagerFragment;
    private PopupWindow popupWindow;
    private static final int ADD_PERSON_REQUEST_CODE = 0;
    private static final int ADD_WORK_REQUEST_CODE = 1;
    private static final int MY_LOGIN_REQUEST_CODE = 2;
    private static final int ADD_PERSON_LOGIN_REQUEST_CODE = 3;
    private static final int ADD_WORK_LOGIN_REQUEST_CODE = 4;
    private long exitTime = 0;
    //权限相关
    public static final int REQUEST_EXTERNAL_STORAGE = 103;
    public static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);


        //bmob初始化
        Bmob.initialize(this, "ed4651f9a69228efadf60e15fd143933");
        MobSDK.init(this);

        initView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_PERSON_REQUEST_CODE) {
                try {
                    Method setRefreshing = PersonFragment.swipeRefreshLayout.getClass().getDeclaredMethod("setRefreshing", boolean.class, boolean.class);
                    setRefreshing.setAccessible(true);
                    setRefreshing.invoke(PersonFragment.swipeRefreshLayout, true, true);
                    PersonFragment.swipeRefreshLayout.setRefreshing(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == ADD_WORK_REQUEST_CODE) {
                try {
                    Method setRefreshing = WorkFragment.swipeRefreshLayout.getClass().getDeclaredMethod("setRefreshing", boolean.class, boolean.class);
                    setRefreshing.setAccessible(true);
                    setRefreshing.invoke(WorkFragment.swipeRefreshLayout, true, true);
                    WorkFragment.swipeRefreshLayout.setRefreshing(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == MY_LOGIN_REQUEST_CODE) {
                group_bottomBar.check(R.id.rbtn_my);
            } else if (requestCode == ADD_PERSON_LOGIN_REQUEST_CODE) {
                btn_addPerson.performClick();
            } else if (requestCode == ADD_WORK_LOGIN_REQUEST_CODE) {
                btn_addWork.performClick();
            }
        }
    }


    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        btn_write = (ImageButton) findViewById(R.id.btn_write);
        group_bottomBar = (RadioGroup) findViewById(R.id.group_bottomBar);
        btn_write.setOnClickListener(this);
        group_bottomBar.setOnCheckedChangeListener(this);


        viewPagerFragment = new ViewPagerFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.id_content, viewPagerFragment, "viewPagerFragment");
        transaction.show(viewPagerFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_write:
                showPopueWindow();
                break;
        }
    }


    private void showPopueWindow() {
        final View popView = View.inflate(this, R.layout.popup_write, null);
        Button btn_close = (Button) popView.findViewById(R.id.btn_close);
        btn_addPerson = (Button) popView.findViewById(R.id.btn_addPerson);
        btn_addWork = (Button) popView.findViewById(R.id.btn_addWork);


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        btn_addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLogin = checkUser(ADD_PERSON_LOGIN_REQUEST_CODE);
                if (isLogin) {
                    Intent intent = new Intent(MainActivity.this, AddPersonActivity.class);
                    startActivityForResult(intent, ADD_PERSON_REQUEST_CODE);
                    popupWindow.dismiss();
                }
            }
        });

        btn_addWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLogin = checkUser(ADD_WORK_LOGIN_REQUEST_CODE);
                if (isLogin) {
                    Intent intent = new Intent(MainActivity.this, AddWorkActivity.class);
                    startActivityForResult(intent, ADD_WORK_REQUEST_CODE);
                    popupWindow.dismiss();
                }
            }
        });


        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        //点击屏幕外消失
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);

        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });

        popupWindow.setAnimationStyle(R.style.popupwindow_write_anim_style);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (checkedId) {
            case R.id.rbtn_home:
                if (myFragment != null) {
                    viewPagerFragment.showHome();
                    transaction.show(viewPagerFragment);
                    transaction.hide(myFragment);
                    transaction.commit();
                }
                break;
            case R.id.rbtn_my:
                boolean isLogin = checkUser(MY_LOGIN_REQUEST_CODE);
                if (isLogin) {
                    viewPagerFragment.showMy();
                    if (myFragment == null) {
                        myFragment = new MyFragment();
                        transaction.add(R.id.id_content, myFragment, "myFragment");
                    }
                    transaction.hide(viewPagerFragment);
                    transaction.show(myFragment);
                    transaction.commit();
                } else {
                    group.check(R.id.rbtn_home);
                }
                break;
        }

    }

    //检验是否登陆
    private boolean checkUser(int resultCode) {
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if (bmobUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, resultCode);
            return false;
        } else {
            return true;
        }
    }

    protected void onResume() {
        super.onResume();
        //权限请求与判断
        for (int i = 0; i < PERMISSIONS_STORAGE.length; i++) {
            if (ContextCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[i]) != PackageManager.PERMISSION_GRANTED) {
                MPermissions.requestPermissions(this, REQUEST_EXTERNAL_STORAGE, PERMISSIONS_STORAGE);
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    //点击两次返回退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出应用", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
