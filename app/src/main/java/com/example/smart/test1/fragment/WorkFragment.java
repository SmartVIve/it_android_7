package com.example.smart.test1.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.smart.test1.activities.ShowWorkActivity;
import com.example.smart.test1.adapter.Work_RecyclerViewAdapter;
import com.example.smart.test1.bean.WorkBean;
import com.example.smart.test1.R;
import com.example.smart.test1.utils.GetCityDataUtils;
import com.example.smart.test1.utils.MyItemDecoration;
import com.example.smart.test1.view.PickerView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Smart on 2017-12-19.
 */

public class WorkFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private Work_RecyclerViewAdapter adapter;
    private GetCityDataUtils getCityDataUtils = new GetCityDataUtils();
    private ArrayList<String> eat_options = new ArrayList<>();
    private ArrayList<String> live_options = new ArrayList<>();
    private ArrayList<String> info_options = new ArrayList<>();
    private ArrayList<WorkBean> workBeans = new ArrayList<>();
    private PickerView btn_location;
    private PickerView btn_lodging;
    private PickerView btn_info;
    private RecyclerView recyclerView;
    private int province = -1;
    private int city = -1;
    private int eat = 0;
    private int live = 0;
    private int info = -1;
    private View view;
    public static SwipeRefreshLayout swipeRefreshLayout;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_index2, null);


        initView();
        initPickerViewData();
        onRefresh();
        return view;
    }

    private void initView() {
        btn_location = (PickerView) view.findViewById(R.id.btn_title_location);
        btn_lodging = (PickerView) view.findViewById(R.id.btn_lodging);
        btn_info = (PickerView) view.findViewById(R.id.btn_info);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        btn_location.setOnClickListener(this);
        btn_lodging.setOnClickListener(this);
        btn_info.setOnClickListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue));
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new Work_RecyclerViewAdapter(getActivity(), workBeans);
        recyclerView.addItemDecoration(new MyItemDecoration());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new Work_RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(WorkBean workBean) {
                Intent intent = new Intent(getActivity(), ShowWorkActivity.class);
                intent.putExtra("workBean", workBean);
                startActivity(intent);
            }
        });



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_title_location:
                btn_location.show();
                break;
            case R.id.btn_lodging:
                btn_lodging.show();
                break;
            case R.id.btn_info:
                btn_info.show();
                break;
        }
    }

    public void getPerson() {


        BmobQuery<WorkBean> query = new BmobQuery<WorkBean>();
        query.setLimit(50);
        query.order("-createdAt");
        //执行查询方法
        query.findObjects(new FindListener<WorkBean>() {
            @Override
            public void done(List<WorkBean> object, BmobException e) {
                if (e == null) {
                    workBeans.clear();
                    workBeans.addAll(object);
                    select(province, city, eat, live, info);//info不限占用一个位置
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    Toast.makeText(getActivity(), "失败：" + e.getMessage(), Toast.LENGTH_SHORT);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void select(int province, int city, int eat, int live, int info) {

        if (province != -1) {
            for (int i = workBeans.size(); i > 0; i--) {
                if (workBeans.get(i - 1).getProvince() != province) {
                    workBeans.remove(i - 1);
                }
            }
        }
        if (city != -1) {
            for (int i = workBeans.size(); i > 0; i--) {
                if (workBeans.get(i - 1).getCity() != city) {
                    workBeans.remove(i - 1);
                }
            }
        }

        if (eat != 0) {
            for (int i = workBeans.size(); i > 0; i--) {
                if (workBeans.get(i - 1).getEat() != eat) {
                    workBeans.remove(i - 1);
                }
            }
        }

        if (live != 0) {
            for (int i = workBeans.size(); i > 0; i--) {
                if (workBeans.get(i - 1).getLive() != live) {
                    workBeans.remove(i - 1);
                }
            }
        }

        if (info != -1) {
            for (int i = workBeans.size(); i > 0; i--) {
                if (workBeans.get(i - 1).getInfo() != info) {
                    workBeans.remove(i - 1);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }


    private void initPickerViewData() {
        eat_options.add(getString(R.string.unlimited));
        eat_options.add(getString(R.string.eat));
        eat_options.add(getString(R.string.not_eat));

        live_options.add(getString(R.string.unlimited));
        live_options.add(getString(R.string.live));
        live_options.add(getString(R.string.not_live));

        info_options.add(getString(R.string.unlimited));
        info_options.add(getString(R.string.can_self_care));
        info_options.add(getString(R.string.cannot_self_care));
        info_options.add(getString(R.string.semi_disability));
        info_options.add(getString(R.string.postop));

        /*--------数据源添加完毕---------*/

        btn_location.initLocationData(getActivity(), new PickerView.LocationPickerViewSelectListener() {
            @Override
            public void onNPickerViewSelec(int provincePosition, int cityPosition, String arg, String arg2) {
                province = provincePosition;
                city = cityPosition;
                btn_location.setText(arg);
                onRefresh();
            }
        });

        btn_lodging.initData(getActivity(), eat_options, live_options, new PickerView.NPickerViewSelectListener() {
            @Override
            public void onPickerViewSelec(int position1, int position2, String arg) {
                eat = position1;
                live = position2;
                btn_location.setText(arg);
                onRefresh();
            }
        });

        btn_info.initData(getActivity(), info_options, new PickerView.PickerViewSelectListener() {
            @Override
            public void onPickerViewSelec(int position, String arg) {
                info = position -1 ;//不限占用一个位置
                btn_info.setText(arg);
                onRefresh();
            }
        });
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getPerson();
    }
}
