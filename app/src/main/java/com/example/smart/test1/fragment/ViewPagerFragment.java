package com.example.smart.test1.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart.test1.R;
import com.example.smart.test1.activities.MapActivity;
import com.example.smart.test1.adapter.ViewPagerAdapter;
import com.example.smart.test1.view.PickerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smart on 2018-04-08.
 */

public class ViewPagerFragment extends Fragment implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private View view;
    private ViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private RadioButton rbtn_findPerson;
    private RadioButton rbtn_findWork;
    private PickerView btn_location;
    private RadioGroup group_nav;
    private TextView tv_title;
    private ImageButton btn_map;
    private static int PERSON_PAGE=0;
    private static int WORK_PAGE=1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_viewpager, null);
        initView();


        return view;
    }



    private void initView() {
        rbtn_findPerson = (RadioButton) getActivity().findViewById(R.id.rbtn_findPerson);
        rbtn_findWork = (RadioButton) getActivity().findViewById(R.id.rbtn_findWork);
        btn_location = (PickerView) getActivity().findViewById(R.id.btn_title_location);
        group_nav = (RadioGroup) getActivity().findViewById(R.id.group_nav);
        tv_title = (TextView) getActivity().findViewById(R.id.tv_title);
        btn_map = (ImageButton) getActivity().findViewById(R.id.btn_map);
        Button btn_backward = (Button) getActivity().findViewById(R.id.btn_backward);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        final PersonFragment content1 = new PersonFragment();
        WorkFragment content2 = new WorkFragment();
        fragments.add(content1);
        fragments.add(content2);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(viewPagerAdapter);

        btn_backward.setVisibility(View.GONE);
        tv_title.setText(getResources().getString(R.string.my));

        btn_map.setOnClickListener(this);
        btn_location.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
        group_nav.setOnCheckedChangeListener(this);
        btn_location.initLocationData(getActivity(), new PickerView.LocationPickerViewSelectListener() {
            @Override
            public void onNPickerViewSelec(int provincePosition, int cityPosition, String arg, String arg2) {
                content1.province = provincePosition;
                content1.city = cityPosition;
                btn_location.setText(arg);
                content1.onRefresh();
            }
        });


        showHome();
    }

    public void showMy() {
        group_nav.setVisibility(View.GONE);
        btn_location.setVisibility(View.GONE);
        tv_title.setVisibility(View.VISIBLE);
        btn_map.setVisibility(View.GONE);
    }

    public void showHome() {
        if (rbtn_findPerson.isChecked()) {
            btn_location.setVisibility(View.VISIBLE);
        }
        btn_map.setVisibility(View.VISIBLE);
        group_nav.setVisibility(View.VISIBLE);
        tv_title.setVisibility(View.GONE);
    }

    //点击nav，设置viewPager显示
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (checkedId) {
            case R.id.rbtn_findPerson://找护工
                btn_location.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(PERSON_PAGE);
                break;
            case R.id.rbtn_findWork://找雇主
                btn_location.setVisibility(View.GONE);
                viewPager.setCurrentItem(WORK_PAGE);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    //viewPager 滑动监听，设置nav
    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_SETTLING) {
            if (viewPager.getCurrentItem() == PERSON_PAGE){
                rbtn_findPerson.setChecked(true);
                btn_location.setVisibility(View.VISIBLE);
            }else {
                rbtn_findWork.setChecked(true);
                btn_location.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_title_location:
                btn_location.show();
                break;
            case R.id.btn_map:
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
                break;
        }
    }

}
