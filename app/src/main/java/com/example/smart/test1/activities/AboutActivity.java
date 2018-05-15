package com.example.smart.test1.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart.test1.R;
import com.zhy.m.permission.MPermissions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobWrapper;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.update.AppVersion;

/**
 * Created by Smart on 2018-03-23.
 */

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_title;
    private Button btn_backward;
    private Button btn_checkVersion;
    private Button btn_copyright;
    private Button btn_support;
    private int versionCode;
    private Uri uri;
    private Intent intent;
    //权限相关
    public static final int REQUEST_EXTERNAL_STORAGE = 103;
    public static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (BmobWrapper.getInstance() == null){
            Bmob.initialize(this, "ed4651f9a69228efadf60e15fd143933");
        }

        initView();

    }
    protected void onResume() {
        super.onResume();
        //权限请求与判断
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasReadStoragePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED || hasReadStoragePermission != PackageManager.PERMISSION_GRANTED) {
            MPermissions.requestPermissions(this, REQUEST_EXTERNAL_STORAGE, PERMISSIONS_STORAGE);
        }
    }

    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_backward = (Button) findViewById(R.id.btn_backward);
        btn_checkVersion = (Button) findViewById(R.id.btn_checkVersion);
        btn_copyright = (Button) findViewById(R.id.btn_copyright);
        btn_support = (Button) findViewById(R.id.btn_support);

        tv_title.setText(getResources().getString(R.string.about));
        btn_backward.setOnClickListener(this);
        btn_checkVersion.setOnClickListener(this);
        btn_copyright.setOnClickListener(this);
        btn_support.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_backward:
                AboutActivity.this.finish();
                break;
            case R.id.btn_checkVersion:
                Toast.makeText(AboutActivity.this, "正在检查更新...", Toast.LENGTH_SHORT).show();
                checkVersion();
                break;
            case R.id.btn_copyright:
                uri = Uri.parse("http://www.caochuanyun.com");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                break;
            case R.id.btn_support:
                uri = Uri.parse("http://www.caochuanyun.com");
                intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                break;
        }
    }

    private void checkVersion() {
        //获取版本号
        try {
            versionCode = getVersionCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //查询是否可更新
        BmobQuery<AppVersion> appVersionBmobQuery = new BmobQuery<>();
        appVersionBmobQuery.addWhereEqualTo("platform", "Android");
        appVersionBmobQuery.addWhereGreaterThan("version_i", versionCode);
        appVersionBmobQuery.findObjects(new FindListener<AppVersion>() {
            @Override
            public void done(List<AppVersion> list, BmobException e) {
                if (list == null || list.size() == 0) {
                    Toast.makeText(AboutActivity.this, "已经是最新版本了", Toast.LENGTH_SHORT).show();
                } else {
                    String android_url = list.get(0).getAndroid_url();
                    String update_log = list.get(0).getUpdate_log();
                    showDialogUpdate(android_url, update_log);
                }

            }
        });
    }

    /**
     * 提示版本更新的对话框
     *
     * @param android_url
     * @param update_log
     */
    private void showDialogUpdate(final String android_url, String update_log) {
        // 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置提示框的标题
        builder.setTitle("版本升级").
                // 设置提示框的图标
                        setIcon(R.mipmap.ic_launcher).
                // 设置要显示的信息
                        setMessage(update_log).
                // 设置确定按钮
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadNewVersionProgress(android_url);
                    }
                }).
                setNegativeButton("取消", null);
        // 生产对话框
        AlertDialog alertDialog = builder.create();
        // 显示对话框
        alertDialog.show();
    }


    /**
     * 下载新版本程序，需要子线程
     *
     * @param android_url
     */
    private void loadNewVersionProgress(final String android_url) {
        final ProgressDialog pd;    //进度条对话框
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);//点击进度对话框外的区域对话框不消失
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        //启动子线程下载任务
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(android_url, pd);
                    sleep(3000);
                    pd.dismiss(); //结束掉进度条对话框
                    installApk(file);
                } catch (Exception e) {
                    //下载apk失败
                    //Toast.makeText(AboutActivity.this, "下载新版本失败", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /**
     * 从服务器获取apk文件的代码
     * 传入网址uri，进度条对象即可获得一个File文件
     * （要在子线程中执行哦）
     */
    public static File getFileFromServer(String uri, ProgressDialog pd) throws Exception {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(30000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            long time = System.currentTimeMillis();//当前时间的毫秒数
            String path = Environment.getExternalStorageDirectory() + File.separator + "download";
            File filePath = new File(path);
            if (!filePath.exists()) {//download文件夹不存在时创建
                boolean mkdirs = filePath.mkdirs();
            }
            File file = new File(path, time + "updata.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    /**
     * 安装apk
     */
    protected void installApk(File file) {
        Uri data;
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);

        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            data = FileProvider.getUriForFile(AboutActivity.this, "com.example.smart.test1.activities.fileprovider", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }
        //执行的数据类型
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    /*
   * 获取当前程序的版本号
   */
    private int getVersionCode() throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        Log.e("TAG", "版本号" + packInfo.versionCode);
        Log.e("TAG", "版本名" + packInfo.versionName);
        return packInfo.versionCode;
    }
}
