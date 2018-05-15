package com.example.smart.test1.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.example.smart.test1.R;
import com.example.smart.test1.bean.PersonBean;
import com.example.smart.test1.view.PickerView;
import com.zhy.m.permission.MPermissions;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.io.File;
import java.util.Calendar;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.BmobWrapper;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Smart on 2018-01-09.
 */

public class AddPersonActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    protected EditText et_name;
    protected PickerView btn_birthdate;
    protected PickerView btn_location;
    protected ImageView btn_camera;
    protected EditText et_introduction;
    protected EditText et_price;
    protected Button btn_release;
    protected BmobFile bmobFile;
    protected TextView tv_title;
    protected Bitmap bm;
    private ZLoadingDialog dialog;


    private String name;
    private String price;
    private String introduction;
    protected int province;
    protected int city;
    protected long birthDate = -1;
    protected int level = -1;
    protected int workTime = -1;
    protected int eat = -1;
    protected int live = -1;
    protected double latitude;
    protected double longitude;

    private PopupWindow popupWindow;
    public static final String HEAD_ICON_DIC = Environment
            .getExternalStorageDirectory()
            + File.separator + "headIcon";
    private File headIconFile = null;// 相册或者拍照保存的文件
    protected File headClipFile = null;// 裁剪后的头像
    private String headFileNameStr = "headIcon.jpg";
    private String clipFileNameStr = "clipIcon.jpg";

    //权限相关
    public static final int REQUEST_EXTERNAL_STORAGE = 103;
    public static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private final int CHOOSE_PHOTO_REQUEST_CODE = 0;
    private final int TAKE_PHOTO_REQUEST_CODE = 1;
    private final int CLIP_PHOTO_BY_SELF_REQUEST_CODE = 2;
    public static final int REQUEST_CAMERA = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addperson);

        if (BmobWrapper.getInstance() == null){
            Bmob.initialize(this, "ed4651f9a69228efadf60e15fd143933");
        }

        initView();
        initLocation();
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
        } else {
            initHeadIconFile();
        }
    }

    //初始化图片保存路径
    private void initHeadIconFile() {
        headIconFile = new File(HEAD_ICON_DIC);
        Log.e("TAG", "initHeadIconFile()---headIconFile.exists() : " + headIconFile.exists());
        if (!headIconFile.exists()) {
            boolean mkdirs = headIconFile.mkdirs();
            Log.e("TAG", "initHeadIconFile()---mkdirs : " + mkdirs);
        }
        headIconFile = new File(HEAD_ICON_DIC, headFileNameStr);
        headClipFile = new File(HEAD_ICON_DIC, clipFileNameStr);
    }

    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        LinearLayout main = (LinearLayout) findViewById(R.id.main);
        tv_title = (TextView) findViewById(R.id.tv_title);
        et_name = (EditText) findViewById(R.id.tv_name);
        btn_birthdate = (PickerView) findViewById(R.id.btn_birthdate);
        btn_location = (PickerView) findViewById(R.id.btn_location);
        Button btn_back = (Button) findViewById(R.id.btn_backward);
        btn_camera = (ImageView) findViewById(R.id.btn_camera);
        RadioGroup group_level = (RadioGroup) findViewById(R.id.group_level);
        RadioGroup group_wordTime = (RadioGroup) findViewById(R.id.group_workTime);
        RadioGroup group_eat = (RadioGroup) findViewById(R.id.group_eat);
        RadioGroup group_live = (RadioGroup) findViewById(R.id.group_live);
        btn_release = (Button) findViewById(R.id.btn_release);
        et_introduction = (EditText) findViewById(R.id.et_introduction);
        et_price = (EditText) findViewById(R.id.et_price);

        tv_title.setText(getResources().getString(R.string.find_work));
        main.setOnClickListener(this);
        btn_birthdate.setOnClickListener(this);
        btn_location.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_release.setOnClickListener(this);
        group_level.setOnCheckedChangeListener(this);
        group_wordTime.setOnCheckedChangeListener(this);
        group_eat.setOnCheckedChangeListener(this);
        group_live.setOnCheckedChangeListener(this);



        btn_location.initLocationData(this, new PickerView.LocationPickerViewSelectListener() {
            @Override
            public void onNPickerViewSelec(int provincePosition, int cityPosition, String arg, String arg2) {
                province = provincePosition;
                city = cityPosition;
                btn_location.setText(arg2);
            }
        });


        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(1900, 0, 1);
        endDate.setTime(Calendar.getInstance().getTime());

        btn_birthdate.initTimeData(this, new boolean[]{true, true, true, false, false, false}, "yyyy-MM-dd", startDate, endDate, new PickerView.TimePickerViewSelectListener() {
            @Override
            public void onTimePickerViewSelec(long time, String arg) {
                birthDate = time;
                btn_birthdate.setText(arg);
            }
        });
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        hideInput();
        switch (checkedId) {
            case R.id.rbtn_level1:
                level = 1;
                break;
            case R.id.rbtn_level2:
                level = 2;
                break;
            case R.id.rbtn_level3:
                level = 3;
                break;
            case R.id.rbtn_workTime1:
                workTime = 1;
                break;
            case R.id.rbtn_workTime2:
                workTime = 2;
                break;
            case R.id.rbtn_workTime3:
                workTime = 3;
                break;
            case R.id.rbtn_eat1:
                eat = 0;
                break;
            case R.id.rbtn_eat2:
                eat = 1;
                break;
            case R.id.rbtn_eat3:
                eat = 2;
                break;
            case R.id.rbtn_live1:
                live = 0;
                break;
            case R.id.rbtn_live2:
                live = 1;
                break;
            case R.id.rbtn_live3:
                live = 2;
                break;
        }
    }

    //点击事件
    @Override
    public void onClick(View v) {
        hideInput();
        switch (v.getId()) {
            case R.id.btn_birthdate:
                btn_birthdate.show();
                break;
            case R.id.btn_location:
                btn_location.show();
                break;
            case R.id.btn_backward:
                AddPersonActivity.this.finish();
                break;
            case R.id.btn_camera:
                showPopueWindow();
                break;
            case R.id.btn_release:
                name = et_name.getText().toString();
                introduction = et_introduction.getText().toString();
                price = et_price.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(AddPersonActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                } else if (city == -1) {
                    Toast.makeText(AddPersonActivity.this, "所在地不能为空", Toast.LENGTH_SHORT).show();
                } else if (birthDate == -1) {
                    Toast.makeText(AddPersonActivity.this, "出生日期未选择", Toast.LENGTH_SHORT).show();
                } else if (level == -1) {
                    Toast.makeText(AddPersonActivity.this, "等级未选择", Toast.LENGTH_SHORT).show();
                } else if (workTime == -1) {
                    Toast.makeText(AddPersonActivity.this, "工作时间未选择", Toast.LENGTH_SHORT).show();
                } else if (eat == -1) {
                    Toast.makeText(AddPersonActivity.this, "是否含餐未选择", Toast.LENGTH_SHORT).show();
                } else if (live == -1) {
                    Toast.makeText(AddPersonActivity.this, "是否住家未选择", Toast.LENGTH_SHORT).show();
                } else if (introduction.isEmpty()) {
                    Toast.makeText(AddPersonActivity.this, "自我介绍未填写", Toast.LENGTH_SHORT).show();
                } else if (price.isEmpty()) {
                    Toast.makeText(AddPersonActivity.this, "服务价格未填写", Toast.LENGTH_SHORT).show();
                } else {
                    dialog = new ZLoadingDialog(this);
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
                break;
        }
    }

    protected Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            update();
            return false;
        }
    });

    //授权回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }
                break;
        }
    }

    //PopueWindow设置
    private void showPopueWindow() {
        View popView = View.inflate(AddPersonActivity.this, R.layout.popup_photo, null);
        Button bt_album = (Button) popView.findViewById(R.id.btn_pop_album);
        Button bt_camera = (Button) popView.findViewById(R.id.btn_pop_camera);
        Button bt_cancle = (Button) popView.findViewById(R.id.btn_pop_cancel);

        //点击相册
        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, CHOOSE_PHOTO_REQUEST_CODE);
                popupWindow.dismiss();
            }
        });

        //点击相机
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hasCameraPermission = ContextCompat.checkSelfPermission(AddPersonActivity.this,
                        Manifest.permission.CAMERA);
                if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddPersonActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
                } else {
                    openCamera();
                }
                popupWindow.dismiss();
            }
        });


        //点击取消
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });


        //获取屏幕宽高
        int weight = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow(popView, weight, height, true);

        //点击屏幕外消失
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });


        //popueWindow弹出动画
        popupWindow.setAnimationStyle(R.style.popupwindow_anim_style);
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);

        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_PHOTO_REQUEST_CODE && data != null) {
                Uri uri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                String picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                Log.i("i", picturePath);
                cursor.close();
                clipPhotoBySelf(picturePath);
            } else if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
                Log.i("TAG", "拍照后返回.........");
                //调用自定义裁剪
                clipPhotoBySelf(headIconFile.getAbsolutePath());
            } else if (requestCode == CLIP_PHOTO_BY_SELF_REQUEST_CODE) {
                Log.i("TAG", "从自定义切图返回..........");
                bm = BitmapFactory.decodeFile(headClipFile.getAbsolutePath());
                btn_camera.setImageBitmap(bm);
                Log.i("TAG", "onActivityResult()---bm : " + bm);

            }
        }
    }


    /**
     * 打开系统摄像头拍照获取图片
     */
    private void openCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照

            Uri imageUri;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                imageUri = Uri.fromFile(headIconFile);
            } else {
                //FileProvider为7.0新增应用间共享文件,在7.0上暴露文件路径会报FileUriExposedException
                //为了适配7.0,所以需要使用FileProvider,具体使用百度一下即可
                imageUri = FileProvider.getUriForFile(this,
                        "com.example.smart.test1.activities.fileprovider", headIconFile);//通过FileProvider创建一个content类型的Uri
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE);
            Log.e("TAG", "openCamera()---intent" + intent);
        } else {
            Toast.makeText(AddPersonActivity.this, "无sd卡", Toast.LENGTH_LONG);
        }
    }

    protected void clipPhotoBySelf(String filePath) {
        //进入裁剪页面,此处用的是自定义的裁剪页面而不是调用系统裁剪
        Intent intent = new Intent(AddPersonActivity.this, ClipPictureActivity.class);
        intent.putExtra(ClipPictureActivity.IMAGE_PATH_ORIGINAL, filePath);
        intent.putExtra(ClipPictureActivity.IMAGE_PATH_AFTER_CROP,
                headClipFile.getAbsolutePath());

        startActivityForResult(intent, CLIP_PHOTO_BY_SELF_REQUEST_CODE);

    }

    /*
     * bomb上传头像
     */
    protected void upload(String headIconFile) {
        bmobFile = new BmobFile(new File(headIconFile));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //Toast.makeText(AddPersonActivity.this, "上传完成" + bmobFile.getFileUrl(), Toast.LENGTH_SHORT).show();
                    Log.i("i", "上传完成" + bmobFile.getFileUrl());
                    handler.sendEmptyMessage(0);
                } else {
                    Toast.makeText(AddPersonActivity.this, "上传失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("e", "上传失败:" + e.getMessage());
                }
            }
        });
    }

    private void update() {
        String mobilePhoneNumber = BmobUser.getCurrentUser().getMobilePhoneNumber();
        PersonBean personBean = new PersonBean();
        personBean.setLatitude(latitude);
        personBean.setLongitude(longitude);
        personBean.setUser(mobilePhoneNumber);
        personBean.setName(name);
        personBean.setProvince(province);
        personBean.setCity(city);
        personBean.setLevel(level);
        personBean.setWorkTime(workTime);
        personBean.setEat(eat);
        personBean.setLive(live);
        personBean.setAge(birthDate);
        personBean.setNearby(1000);
        personBean.setIntroduction(introduction);
        personBean.setPrice(Integer.parseInt(price));
        if (bmobFile != null && !(bmobFile.getFileUrl().isEmpty())) {
            String fileUrl = bmobFile.getFileUrl();
            Log.i("i", "fileUrl:" + fileUrl);
            personBean.setHeadIcon(bmobFile.getFileUrl());
        }
        personBean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(AddPersonActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddPersonActivity.this, MainActivity.class);
                    setResult(RESULT_OK, intent);
                    dialog.cancel();
                    AddPersonActivity.this.finish();
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    dialog.cancel();
                    Toast.makeText(AddPersonActivity.this, "创建数据失败：" + s, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initLocation() {
        LocationClient mLocationClient = new LocationClient(this);
        //声明LocationClient类
        mLocationClient.registerLocationListener(new MyLocationListener());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    //获取当前位置
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            MyLocationData locData = new MyLocationData.Builder().accuracy(bdLocation.getRadius()).latitude(bdLocation.getLatitude()).longitude(bdLocation.getLongitude()).build();
            latitude = bdLocation.getLatitude();    //获取纬度信息
            longitude = bdLocation.getLongitude();
        }
    }


    private void hideInput(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

}
