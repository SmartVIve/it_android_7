package com.example.smart.test1.activities;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart.test1.R;
import com.example.smart.test1.bean.JsonBean;
import com.example.smart.test1.bean.MyUser;
import com.example.smart.test1.utils.BitmapCacheUtils;
import com.example.smart.test1.utils.DownloadAvatarUtils;
import com.example.smart.test1.utils.GetCityDataUtils;
import com.example.smart.test1.view.PickerView;
import com.zhy.m.permission.MPermissions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.BmobWrapper;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Smart on 2018-03-23.
 */

public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<String> educationItem = new ArrayList<>();
    private GetCityDataUtils getCityDataUtils = new GetCityDataUtils();
    private Button btn_name;
    private Button btn_backward;
    private PickerView btn_birthdate;
    private PickerView btn_education;
    private PickerView btn_location;
    private ImageView btn_camera;
    private TextView tv_name;
    private TextView tv_education;
    private TextView tv_birthdate;
    private TextView tv_location;
    private Button btn_save;
    private Handler handler;

    private BitmapCacheUtils bitmapCacheUtils = new BitmapCacheUtils();
    private BmobFile bmobFile;
    private View popView;
    private PopupWindow popupWindow;
    private Uri imageUri;
    public static final String HEAD_ICON_DIC = Environment
            .getExternalStorageDirectory()
            + File.separator + "headIcon";
    private File headIconFile = null;// 相册或者拍照保存的文件
    private File headClipFile = null;// 裁剪后的头像
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
    private static final int SET_NAME_REQUEST_CODE = 3;
    public static final int REQUEST_CAMERA = 104;
    private String name;
    private int education = -1;
    private int province = -1;
    private int city = -1;
    private long birthDate = -1;
    private Bitmap bm;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);

        if (BmobWrapper.getInstance() == null){
            Bmob.initialize(this, "ed4651f9a69228efadf60e15fd143933");
        }

        initView();
        initOptionData();
        getUserDate();

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

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        btn_name = (Button) findViewById(R.id.btn_name);
        btn_backward = (Button) findViewById(R.id.btn_backward);
        btn_birthdate = (PickerView) findViewById(R.id.btn_birthdate);
        btn_education = (PickerView) findViewById(R.id.btn_education);
        btn_location = (PickerView) findViewById(R.id.btn_location);
        btn_camera = (ImageView) findViewById(R.id.btn_camera);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_education = (TextView) findViewById(R.id.tv_education);
        tv_birthdate = (TextView) findViewById(R.id.tv_birthday);
        tv_location = (TextView) findViewById(R.id.tv_location);
        btn_save = (Button) findViewById(R.id.btn_save);

        tv_title.setText(getResources().getString(R.string.my_info));
        btn_name.setOnClickListener(this);
        btn_backward.setOnClickListener(this);
        btn_birthdate.setOnClickListener(this);
        btn_education.setOnClickListener(this);
        btn_location.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(1900, 0, 1);
        endDate.setTime(Calendar.getInstance().getTime());



        btn_birthdate.initTimeData(this, new boolean[]{true, true, true, false, false, false}, "yyyy-MM-dd", startDate, endDate, new PickerView.TimePickerViewSelectListener() {
            @Override
            public void onTimePickerViewSelec(long time, String arg) {
                birthDate = time;
                tv_birthdate.setText(arg);
            }
        });

        btn_education.initData(this, educationItem, new PickerView.PickerViewSelectListener() {
            @Override
            public void onPickerViewSelec(int position, String arg) {
                education = position;
                tv_education.setText(arg);
            }
        });

        btn_location.initLocationData(this, new PickerView.LocationPickerViewSelectListener() {
            @Override
            public void onNPickerViewSelec(int provincePosition, int cityPosition, String arg, String arg2) {
                province = provincePosition;
                city = cityPosition;
                tv_location.setText(arg2);
            }
        });

    }


    @SuppressLint("HandlerLeak")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_backward:
                MyInfoActivity.this.finish();
                break;
            case R.id.btn_name:
                Intent intent = new Intent(MyInfoActivity.this, SetNameActivity.class);
                intent.putExtra("name", tv_name.getText().toString());
                startActivityForResult(intent, SET_NAME_REQUEST_CODE);
                break;
            case R.id.btn_birthdate:
                btn_birthdate.show();
                break;
            case R.id.btn_education:
                btn_education.show();
                break;
            case R.id.btn_location:
                btn_location.show();
                break;
            case R.id.btn_camera:
                showPopueWindow();
                break;
            case R.id.btn_save://上传头像后更新信息
                if (bm != null) {
                    Toast.makeText(MyInfoActivity.this, "上传中", Toast.LENGTH_SHORT).show();
                    upload(headClipFile.getAbsolutePath());//上传头像
                }else {
                    update();//更新信息
                }
                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        update();//更新信息
                    }
                };
                this.finish();
                break;
        }
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
                //upload(headClipFile.getAbsolutePath());
            } else if (requestCode == SET_NAME_REQUEST_CODE) {
                name = data.getStringExtra("name");
                tv_name.setText(name);
            }
        }
    }


    private void showPopueWindow() {
        popView = View.inflate(MyInfoActivity.this, R.layout.popup_photo, null);
        Button bt_album = (Button) popView.findViewById(R.id.btn_pop_album);
        Button bt_camera = (Button) popView.findViewById(R.id.btn_pop_camera);
        Button bt_cancle = (Button) popView.findViewById(R.id.btn_pop_cancel);

        //选择相片
        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, CHOOSE_PHOTO_REQUEST_CODE);
                popupWindow.dismiss();
            }
        });
        //拍照
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hasCameraPermission = ContextCompat.checkSelfPermission(MyInfoActivity.this,
                        Manifest.permission.CAMERA);
                if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                    MPermissions.requestPermissions(MyInfoActivity.this, REQUEST_CAMERA, Manifest.permission.CAMERA);
                } else {
                    openCamera();
                }
                popupWindow.dismiss();
            }
        });
        //取消
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        //获取屏幕宽高
        int weight = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow(popView,weight,height,true);

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

    /**
     * 打开系统摄像头拍照获取图片
     */

    private void openCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照

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
            Toast.makeText(MyInfoActivity.this, "无sd卡", Toast.LENGTH_LONG);
        }
    }

    protected void clipPhotoBySelf(String filePath) {
        //进入裁剪页面,此处用的是自定义的裁剪页面而不是调用系统裁剪
        Intent intent = new Intent(MyInfoActivity.this, ClipPictureActivity.class);
        intent.putExtra(ClipPictureActivity.IMAGE_PATH_ORIGINAL, filePath);
        intent.putExtra(ClipPictureActivity.IMAGE_PATH_AFTER_CROP,
                headClipFile.getAbsolutePath());

        startActivityForResult(intent, CLIP_PHOTO_BY_SELF_REQUEST_CODE);

    }

    //获取用户信息
    private void getUserDate() {
        long birthday;
        int education;
        int province;
        int city;
        //MyUser中的扩展属性
        String username = (String) BmobUser.getObjectByKey("username");
        String headIcon = (String) BmobUser.getObjectByKey("headIcon");
        String name = (String) BmobUser.getObjectByKey("name");


        birthday = Long.parseLong(BmobUser.getObjectByKey("birthday").toString());
        education = Integer.parseInt(BmobUser.getObjectByKey("education").toString());
        province = Integer.parseInt(BmobUser.getObjectByKey("province").toString());
        city = Integer.parseInt(BmobUser.getObjectByKey("city").toString());


        if (headIcon != null) {
            bitmapCacheUtils.setGetBitmapListener(headIcon, new BitmapCacheUtils.GetBitmapListener() {
                @Override
                public void getBitmap(Bitmap bitmap) {
                    btn_camera.setImageBitmap(bitmap);
                }
            });
        }
        if (birthday != 0) {
            tv_birthdate.setText(getTime(new Date(birthday)));
        }
        tv_education.setText(educationItem.get(education));
        tv_location.setText(String.format(getString(R.string.sf_city),options1Items.get(province).getName(),options2Items.get(province).get(city)));
        tv_name.setText(name);
    }

    /*
    * bomb上传头像
    */
    private void upload(String headIconFile) {
        bmobFile = new BmobFile(new File(headIconFile));

        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(MyInfoActivity.this, "上传完成", Toast.LENGTH_SHORT).show();
                    Log.i("i", "上传完成" + bmobFile.getFileUrl());
                    handler.sendEmptyMessage(0);
                } else {
                    Toast.makeText(MyInfoActivity.this, "上传失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("e", "上传失败:" + e.getMessage());
                }
            }
        });


    }



    private void update() {

        MyUser bmobUser = MyUser.getCurrentUser(MyUser.class);
        if (bm != null) {
            bmobUser.setHeadIcon(bmobFile.getFileUrl());
        }
        if (birthDate != -1) {
            bmobUser.setBirthday(birthDate);
        }
        if (city != -1) {
            bmobUser.setProvince(province);
            bmobUser.setCity(city);
        }
        if (education != -1) {
            bmobUser.setEducation(education);
        }
        if (name != null) {
            bmobUser.setName(name);
        }
        bmobUser.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(MyInfoActivity.this, "更新用户信息成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyInfoActivity.this, "更新用户信息失败", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }




    private void initOptionData() {
        options1Items = getCityDataUtils.getOptions1Items(MyInfoActivity.this);
        options2Items = getCityDataUtils.getOptions2Items(MyInfoActivity.this);

        educationItem.add("小学");
        educationItem.add("初中");
        educationItem.add("高中");
        educationItem.add("大专");
        educationItem.add("本科");
    }


    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date);
    }


}

