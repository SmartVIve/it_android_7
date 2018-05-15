package com.example.smart.test1.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.smart.test1.R;
import com.example.smart.test1.bean.JsonBean;
import com.example.smart.test1.bean.PersonBean;
import com.example.smart.test1.bean.WorkBean;
import com.example.smart.test1.utils.BitmapCacheUtils;
import com.example.smart.test1.utils.GetCityDataUtils;
import com.example.smart.test1.view.SlideBottomView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobWrapper;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Smart on 2018-01-15.
 */

public class MapActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private GetCityDataUtils getCityDataUtils = new GetCityDataUtils();
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private boolean isEnlarge = false;
    private Bundle bundle;
    private Button btn_backward;
    private RadioGroup group_nav;
    private RadioButton rbtn_findPerson;
    private RadioButton rbtn_findWork;
    private Button iv_location;
    private SlideBottomView personSlideBottomView;
    private SlideBottomView workSlideBottomView;
    private LatLng personLatLag;
    private LatLng workLatLag;
    private LatLng ll;
    private MapStatus.Builder builder;
    private LocationClient mLocationClient;
    private BitmapCacheUtils bitmapCacheUtils = new BitmapCacheUtils();

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);

        if (BmobWrapper.getInstance() == null) {
            Bmob.initialize(this, "ed4651f9a69228efadf60e15fd143933");
        }
        //地图初始化
        initView();
        initMap();
    }



    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        btn_backward = (Button) findViewById(R.id.btn_backward);
        rbtn_findPerson = (RadioButton) findViewById(R.id.rbtn_findPerson);
        rbtn_findWork = (RadioButton) findViewById(R.id.rbtn_findWork);
        iv_location = (Button) findViewById(R.id.iv_location);
        group_nav = (RadioGroup) findViewById(R.id.group_nav);

        btn_backward.setOnClickListener(this);
        iv_location.setOnClickListener(this);
        group_nav.setVisibility(View.VISIBLE);
        group_nav.setOnCheckedChangeListener(this);

        personSlideBottomView = (SlideBottomView) findViewById(R.id.slideBottomView_person);
        workSlideBottomView = (SlideBottomView) findViewById(R.id.slideBottomView_work);

        options1Items = getCityDataUtils.getOptions1Items(MapActivity.this);
        options2Items = getCityDataUtils.getOptions2Items(MapActivity.this);

    }


    private void initMap() {
        mMapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);


        mLocationClient = new LocationClient(this);
        //声明LocationClient类
        mLocationClient.registerLocationListener(new MyLocationListener());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(3000);
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        //加载marker坐标
        personMarker();


        //标记点击
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final PersonBean personBean = (PersonBean) marker.getExtraInfo().getSerializable("personBean");
                final WorkBean workBean = (WorkBean) marker.getExtraInfo().getSerializable("workBean");

                if (rbtn_findPerson.isChecked()) {
                    TextView tv_name = (TextView) personSlideBottomView.findViewById(R.id.tv_name);
                    TextView tv_address = (TextView) personSlideBottomView.findViewById(R.id.tv_address);
                    TextView tv_live = (TextView) personSlideBottomView.findViewById(R.id.tv_live);
                    TextView tv_eat = (TextView) personSlideBottomView.findViewById(R.id.tv_eat);
                    TextView tv_workTime = (TextView) personSlideBottomView.findViewById(R.id.tv_worktime);
                    TextView tv_price = (TextView) personSlideBottomView.findViewById(R.id.tv_price);
                    TextView tv_introduce = (TextView) personSlideBottomView.findViewById(R.id.tv_introduce);
                    ImageView iv_level = (ImageView) personSlideBottomView.findViewById(R.id.iv_level);
                    Button btn_call_phone = (Button) personSlideBottomView.findViewById(R.id.btn_call_phone);
                    final ImageView iv_avatar = (ImageView) personSlideBottomView.findViewById(R.id.iv_avatar);

                    //设置信息
                    personLatLag = new LatLng(personBean.getLatitude(), personBean.getLongitude());
                    String headIcon = personBean.getHeadIcon();

                    iv_avatar.setImageResource(R.drawable.icon_profile);
                    tv_name.setText(personBean.getName());
                    tv_price.setText(String.format(getString(R.string.sf_price_hour), personBean.getPrice()));
                    tv_introduce.setText(personBean.getIntroduction());
                    iv_level.setImageResource(R.drawable.icon_profile);
                    String province = options1Items.get(personBean.getProvince()).getPickerViewText();
                    String city = options2Items.get(personBean.getProvince()).get(personBean.getCity());

                    tv_address.setText(String.format(getString(R.string.sf_city), province, city));


                    if (headIcon != null) {
                        bitmapCacheUtils.setGetBitmapListener(headIcon, new BitmapCacheUtils.GetBitmapListener() {
                            @Override
                            public void getBitmap(Bitmap bitmap) {
                                iv_avatar.setImageBitmap(bitmap);
                            }
                        });
                    }

                    switch (personBean.getLevel()) {
                        case 1:
                            iv_level.setImageResource(R.drawable.icon_pthg);
                            break;
                        case 2:
                            iv_level.setImageResource(R.drawable.icon_gjhg);
                            break;
                        case 3:
                            iv_level.setImageResource(R.drawable.icon_hs);
                            break;
                    }

                    switch (personBean.getEat()) {
                        case 0:
                            tv_eat.setText(R.string.unlimited_eat);
                            break;
                        case 1:
                            tv_eat.setText(R.string.eat);
                            break;
                        case 2:
                            tv_eat.setText(R.string.not_eat);
                            break;
                    }

                    switch (personBean.getLive()) {
                        case 0:
                            tv_live.setText(R.string.unlimited_live);
                            break;
                        case 1:
                            tv_live.setText(R.string.live);
                            break;
                        case 2:
                            tv_live.setText(R.string.not_live);
                            break;
                    }

                    switch (personBean.getWorkTime()) {
                        case 1:
                            tv_workTime.setText(R.string.day_and_night);
                            break;
                        case 2:
                            tv_workTime.setText(R.string.day);
                            break;
                        case 3:
                            tv_workTime.setText(R.string.night);
                            break;
                    }

                    btn_call_phone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            String user = personBean.getUser();
                            Uri uri = Uri.parse("tel:" + user);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });

                    personSlideBottomView.setPanelState(SlideBottomView.COLLAPSED);
                } else if (rbtn_findWork.isChecked()) {
                    TextView tv_name = (TextView) workSlideBottomView.findViewById(R.id.tv_name);
                    TextView tv_address = (TextView) workSlideBottomView.findViewById(R.id.tv_address);
                    TextView tv_price = (TextView) workSlideBottomView.findViewById(R.id.tv_price);
                    TextView tv_date = (TextView) workSlideBottomView.findViewById(R.id.tv_date);
                    TextView tv_live = (TextView) workSlideBottomView.findViewById(R.id.tv_live);
                    TextView tv_eat = (TextView) workSlideBottomView.findViewById(R.id.tv_eat);
                    TextView tv_remarks = (TextView) workSlideBottomView.findViewById(R.id.tv_remarks);
                    TextView tv_info = (TextView) workSlideBottomView.findViewById(R.id.tv_info);
                    Button btn_call_phone = (Button) workSlideBottomView.findViewById(R.id.btn_call_phone);

                    Date startdate = new Date(workBean.getStartDate());//获取时间戳转换为date类型
                    Date enddate = new Date(workBean.getEndDate());

                    SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd", Locale.getDefault());
                    String startDate = format.format(startdate);//date转换为文字类型
                    String endDate = format.format(enddate);
                    String date = startDate + "至" + endDate;


                    int index_province = workBean.getProvince();
                    int index_city = workBean.getCity();
                    String province = options1Items.get(index_province).getPickerViewText();
                    String city = options2Items.get(index_province).get(index_city);

                    workLatLag = new LatLng(workBean.getLatitude(), workBean.getLongitude());
                    tv_name.setText(workBean.getName());
                    tv_remarks.setText(workBean.getRemarks());
                    tv_address.setText(String.format(getString(R.string.sf_city), province, city));
                    tv_price.setText(String.format(MapActivity.this.getString(R.string.sf_price_hour), workBean.getPrice()));
                    tv_date.setText(date);

                    switch (workBean.getEat()) {
                        case 0:
                            tv_eat.setText(R.string.unlimited_eat);
                            break;
                        case 1:
                            tv_eat.setText(R.string.eat);
                            break;
                        case 2:
                            tv_eat.setText(R.string.not_eat);
                            break;
                    }

                    switch (workBean.getLive()) {
                        case 0:
                            tv_live.setText(R.string.unlimited_live);
                            break;
                        case 1:
                            tv_live.setText(R.string.live);
                            break;
                        case 2:
                            tv_live.setText(R.string.not_live);
                            break;
                    }

                    switch (workBean.getInfo()) {
                        case 0:
                            tv_info.setText(R.string.can_self_care);
                            break;
                        case 1:
                            tv_info.setText(R.string.cannot_self_care);
                            break;
                        case 2:
                            tv_info.setText(R.string.semi_disability);
                            break;
                        case 3:
                            tv_info.setText(R.string.postop);
                            break;
                    }

                    btn_call_phone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            String user = workBean.getUser();
                            Uri uri = Uri.parse("tel:" + user);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });

                    workSlideBottomView.setPanelState(SlideBottomView.COLLAPSED);
                }


                return true;
            }
        });

        //点击空白部分隐藏SlideBottomView
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                SlideBottomView slideBottomView = null;
                if (rbtn_findPerson.isChecked()) {
                    slideBottomView = personSlideBottomView;
                } else if (rbtn_findWork.isChecked()) {
                    slideBottomView = workSlideBottomView;
                }
                switch (slideBottomView.getPanelState()) {
                    case SlideBottomView.EXPANDED:
                        slideBottomView.setPanelState(SlideBottomView.COLLAPSED);
                        break;
                    case SlideBottomView.COLLAPSED:
                        slideBottomView.setPanelState(SlideBottomView.HIDE);
                        break;
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });


        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                SlideBottomView slideBottomView = null;
                if (rbtn_findPerson.isChecked()) {
                    slideBottomView = personSlideBottomView;
                } else if (rbtn_findWork.isChecked()) {
                    slideBottomView = workSlideBottomView;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    if (slideBottomView.getPanelState() == SlideBottomView.EXPANDED) {
                        slideBottomView.setPanelState(SlideBottomView.COLLAPSED);
                    }
                }
            }
        });

        personSlideBottomView.setOnPanelStateListener(new SlideBottomView.OnPanelStateListener() {
            @Override
            public void onPanelStateChanged(int panelState) {
                if (panelState == SlideBottomView.EXPANDED) {
                    SlideBottomViewExpanded(personLatLag);
                } else if (panelState == SlideBottomView.COLLAPSED) {
                    SlideBottomViewCollapsed();
                } else if (panelState == SlideBottomView.HIDE) {
                    //放大缩小回到底部
                    SlideBottomViewHide();
                }
            }
        });

        workSlideBottomView.setOnPanelStateListener(new SlideBottomView.OnPanelStateListener() {
            @Override
            public void onPanelStateChanged(int panelState) {
                if (panelState == SlideBottomView.EXPANDED) {
                    SlideBottomViewExpanded(workLatLag);
                } else if (panelState == SlideBottomView.COLLAPSED) {
                    SlideBottomViewCollapsed();
                } else if (panelState == SlideBottomView.HIDE) {
                    //放大缩小回到底部
                    SlideBottomViewHide();
                }
            }
        });
    }

    //SlideBottomView隐藏，设置按钮padding
    private void SlideBottomViewHide() {
        mBaiduMap.setViewPadding(0, 0, 0, 0);
        float scale = this.getResources().getDisplayMetrics().density;
        setMargins(iv_location, (int) (4*scale),0,0, (int) (52*scale));
    }

    //SlideBottomView坍塌，设置按钮padding
    private void SlideBottomViewCollapsed() {
        //dp转换为px
        float scale = this.getResources().getDisplayMetrics().density;
        int i = (int) (72 * scale + 0.5f);
        mBaiduMap.setViewPadding(0, 0, 0, i);
        setMargins(iv_location, (int) (4*scale),0,0, (int) (124*scale));
        builder = new MapStatus.Builder();
        builder.targetScreen(new Point(mMapView.getWidth() / 2, mMapView.getHeight() / 2));
        //地图动画生效
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    //SlideBottomView展开时，设置中心点
    private void SlideBottomViewExpanded(LatLng latLng) {
        builder = new MapStatus.Builder();
        //设置缩放中心点；
        builder.target(latLng);
        //设置屏幕中心点
        builder.targetScreen(new Point(mMapView.getWidth() / 2, mMapView.getHeight() / 5));
        //地图动画生效
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public void personMarker() {
        BmobQuery<PersonBean> query = new BmobQuery<PersonBean>();
        query.setLimit(500);
        //执行查询方法
        query.findObjects(new FindListener<PersonBean>() {
            @Override
            public void done(List<PersonBean> object, BmobException e) {
                if (e == null) {
                    for (PersonBean personBeans : object) {
                        LatLng point = new LatLng(personBeans.getLatitude(), personBeans.getLongitude());
                        //构建Marker图标
                        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_person_marker);
                        //创建信息存入option
                        bundle = new Bundle();
                        bundle.putSerializable("personBean", personBeans);
                        //构建MarkerOption，用于在地图上添加Marker
                        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap).extraInfo(bundle);
                        //在地图上添加Marker，并显示
                        mBaiduMap.addOverlay(option);
                    }
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    public void workMarker() {
        BmobQuery<WorkBean> query = new BmobQuery<WorkBean>();
        query.setLimit(500);
        //执行查询方法
        query.findObjects(new FindListener<WorkBean>() {
            @Override
            public void done(List<WorkBean> object, BmobException e) {
                if (e == null) {
                    for (WorkBean workBeans : object) {
                        LatLng point = new LatLng(workBeans.getLatitude(), workBeans.getLongitude());
                        //构建Marker图标
                        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_work_marker);
                        //创建信息存入option
                        bundle = new Bundle();
                        bundle.putSerializable("workBean", workBeans);
                        //构建MarkerOption，用于在地图上添加Marker
                        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap).extraInfo(bundle);
                        //在地图上添加Marker，并显示
                        mBaiduMap.addOverlay(option);
                    }
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        mLocationClient.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_backward:
                finish();
                break;
            case R.id.iv_location:
                builder = new MapStatus.Builder();
                //设置缩放中心点；缩放比例；
                builder.target(ll);
                //给地图设置状态
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mBaiduMap.clear();
        switch (checkedId) {
            case R.id.rbtn_findPerson:
                workSlideBottomView.setPanelState(SlideBottomView.HIDE);
                personMarker();
                break;
            case R.id.rbtn_findWork:
                //清除personMarker，加载workMarker
                personSlideBottomView.setPanelState(SlideBottomView.HIDE);
                workMarker();
                break;
        }
    }

    //获取当前位置
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            MyLocationData locData = new MyLocationData.Builder().accuracy(bdLocation.getRadius()).latitude(bdLocation.getLatitude()).longitude(bdLocation.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            if (!isEnlarge) {
                isEnlarge = true;
                ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                builder = new MapStatus.Builder();
                //设置缩放中心点；缩放比例；
                builder.target(ll).zoom(16.0f);
                //给地图设置状态
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            }
            Log.e("ll",bdLocation.getLatitude()+" "+bdLocation.getLongitude());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            SlideBottomView slideBottomView;
            if (rbtn_findPerson.isChecked()) {
                slideBottomView = personSlideBottomView;
            } else {
                slideBottomView = workSlideBottomView;
            }
            if (slideBottomView.getPanelState() == SlideBottomView.EXPANDED) {
                slideBottomView.setPanelState(SlideBottomView.COLLAPSED);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
