package com.example.smart.test1.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.smart.test1.R;
import com.example.smart.test1.activities.AboutActivity;
import com.example.smart.test1.activities.FeedbackActivity;
import com.example.smart.test1.activities.MyInfoActivity;
import com.example.smart.test1.activities.MyWorkActivity;
import com.example.smart.test1.utils.BitmapCacheUtils;
import com.example.smart.test1.utils.DownloadAvatarUtils;

import cn.bmob.v3.BmobUser;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Smart on 2017-12-20.
 */

public class MyFragment extends Fragment implements View.OnClickListener {
    private TextView id;
    private Button btn_myWork;
    private Button btn_myInfo;
    private Button btn_about;
    private Button btn_feedback;
    private Button btn_share;
    private ImageView iv_avatar;
    private View popView;
    private PopupWindow popupWindow;
    private BitmapCacheUtils bitmapCacheUtils = new BitmapCacheUtils();
    private View view;
    private Intent intent;
    private static final int LOGIN_REQUEST_CODE = 0;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_my,null);




        initView();
        setUserData();


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == LOGIN_REQUEST_CODE){
            initView();
            setUserData();
        }
    }

    private void initView() {
        id = (TextView) view.findViewById(R.id.tv_id);
        btn_myWork = (Button) view.findViewById(R.id.btn_myWork);
        btn_myInfo = (Button) view.findViewById(R.id.btn_myInfo);
        btn_feedback = (Button) view.findViewById(R.id.btn_feedback);
        btn_about = (Button) view.findViewById(R.id.btn_about);
        btn_share = (Button) view.findViewById(R.id.btn_share);
        iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
        btn_myWork.setOnClickListener(this);
        btn_myInfo.setOnClickListener(this);
        btn_feedback.setOnClickListener(this);
        btn_about.setOnClickListener(this);
        btn_share.setOnClickListener(this);
    }

    public void setUserData() {
        String mobilePhoneNumber = (String) BmobUser.getObjectByKey("mobilePhoneNumber");
        String headIcon = (String) BmobUser.getObjectByKey("headIcon");
        id.setText(mobilePhoneNumber);
        Log.e("name",mobilePhoneNumber);
        if (headIcon != null) {
            bitmapCacheUtils.setGetBitmapListener(headIcon, new BitmapCacheUtils.GetBitmapListener() {
                @Override
                public void getBitmap(Bitmap bitmap) {
                    iv_avatar.setImageBitmap(bitmap);
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_myWork:
                Intent intent = new Intent(getActivity(), MyWorkActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_about:
                this.intent = new Intent(getActivity(),AboutActivity.class);
                startActivity(this.intent);
                break;
            case R.id.btn_feedback:
                this.intent = new Intent(getActivity(),FeedbackActivity.class);
                startActivity(this.intent);
                break;
            case R.id.btn_myInfo:
                this.intent = new Intent(getActivity(),MyInfoActivity.class);
                startActivity(this.intent);
                break;
            case R.id.btn_share:
                showPopueWindow();
                break;
        }
    }


    private void showShare(String platform) {
        final OnekeyShare oks = new OnekeyShare();
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        if (platform != null) {
            oks.setPlatform(platform);
        }
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle("找护工App");
        // titleUrl QQ和QQ空间跳转链接
        //oks.setTitleUrl("http://bmob-cdn-17249.b0.upaiyun.com/2018/04/07/98e362fe40a455fa809aaa4a7dcd1316.apk");
        oks.setTitleUrl("http://www.jnshu.com/");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("找护工App");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        /*Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.drawable.bg_logo);
        oks.setImageData(imageData);*/
        oks.setImageUrl("http://bmob-cdn-17249.b0.upaiyun.com/2018/04/20/ed5c8e164083cc2580e85100845a9f1b.png");
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl("http://www.jnshu.com/");
        //oks.setUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(getActivity());
    }

    private void showPopueWindow() {
        popView = View.inflate(getActivity(), R.layout.popup_share, null);
        Button btn_wechat = (Button) popView.findViewById(R.id.btn_wechat);
        Button btn_wechat_moments = (Button) popView.findViewById(R.id.btn_wechat_moments);
        Button btn_qq = (Button) popView.findViewById(R.id.btn_qq);
        Button btn_cancel = (Button) popView.findViewById(R.id.btn_cancel);

        btn_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(QQ.NAME);
            }
        });

        btn_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(Wechat.NAME);
            }
        });

        btn_wechat_moments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(WechatMoments.NAME);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
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
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);
            }
        });

        //popueWindow弹出动画
        popupWindow.setAnimationStyle(R.style.popupwindow_anim_style);
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.5f;
        getActivity().getWindow().setAttributes(lp);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
    }



}
