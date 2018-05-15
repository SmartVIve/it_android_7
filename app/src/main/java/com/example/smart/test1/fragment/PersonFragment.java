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

import com.example.smart.test1.activities.ShowPersonActivity;
import com.example.smart.test1.adapter.Person_RecyclerViewAdapter;
import com.example.smart.test1.bean.PersonBean;
import com.example.smart.test1.R;
import com.example.smart.test1.utils.MyItemDecoration;
import com.example.smart.test1.view.PickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Smart on 2017-12-19.
 */


public class PersonFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ArrayList<String> level_options = new ArrayList<>();
    private ArrayList<String> age_options = new ArrayList<>();
    private ArrayList<String> nearby_options = new ArrayList<>();
    private ArrayList<PersonBean> personBean = new ArrayList<>();
    private ArrayList<String> eat_options = new ArrayList<>();
    private ArrayList<String> live_options = new ArrayList<>();
    private PickerView btn_level;
    private PickerView btn_age;
    private PickerView btn_nearby;
    private PickerView btn_lodging;
    private RecyclerView recyclerView;
    private Person_RecyclerViewAdapter adapter;
    private int level = 0;
    private int age = 0;
    private int nearby = 0;
    private int eat = 0;
    private int live = 0;
    public int province = -1;
    public int city = -1;
    public static SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private static final int ADD_PERSON_REQUEST_CODE = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_index1, null);
        initView();
        initPickerView();
        onRefresh();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_PERSON_REQUEST_CODE) {
                onRefresh();
            }
        }
    }

    private void initView() {
        btn_level = (PickerView) view.findViewById(R.id.btn_level);
        btn_age = (PickerView) view.findViewById(R.id.btn_age);
        btn_nearby = (PickerView) view.findViewById(R.id.btn_nearby);
        btn_lodging = (PickerView) view.findViewById(R.id.btn_lodging);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        btn_level.setOnClickListener(this);
        btn_age.setOnClickListener(this);
        btn_nearby.setOnClickListener(this);
        btn_lodging.setOnClickListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue));
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new Person_RecyclerViewAdapter(getActivity(), personBean);
        recyclerView.addItemDecoration(new MyItemDecoration());
        recyclerView.setAdapter(adapter);
        //item点击监听
        adapter.setOnItemClickListener(new Person_RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PersonBean personBean) {
                Intent intent = new Intent(getActivity(), ShowPersonActivity.class);
                intent.putExtra("personBean", personBean);
                startActivity(intent);
            }
        });



    }


    public void getPerson() {
        BmobQuery<PersonBean> query = new BmobQuery<PersonBean>();
        query.setLimit(500);
        query.order("-createdAt");
        //执行查询方法
        query.findObjects(new FindListener<PersonBean>() {
            @Override
            public void done(List<PersonBean> object, BmobException e) {
                if (e == null) {
                    personBean.clear();
                    personBean.addAll(object);
                    select(province, city, level, age, nearby, eat, live);
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    Toast.makeText(getActivity(), "网络超时", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void select(int province, int city, int level, int age, int nearby, int eat, int live) {
        if (province != -1) {
            for (int i = personBean.size(); i > 0; i--) {
                if (personBean.get(i - 1).getProvince() != province) {
                    personBean.remove(i - 1);
                }
            }
        }

        if (city != -1) {
            for (int i = personBean.size(); i > 0; i--) {
                if (personBean.get(i - 1).getCity() != city) {
                    personBean.remove(i - 1);
                }
            }
        }


        if (level != 0) {
            for (int i = personBean.size(); i > 0; i--) {
                if (personBean.get(i - 1).getLevel() != level) {
                    personBean.remove(i - 1);
                }
            }
        }


        //时间转换
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        switch (age) {
            case 1:
                for (int i = personBean.size(); i > 0; i--) {
                    String format = simpleDateFormat.format(new Date().getTime() - personBean.get(i - 1).getAge());
                    int age2 = Integer.valueOf(format) - 1970;
                    if (!(age2 >= 18 && age2 <= 40)) {
                        personBean.remove(i - 1);
                    }
                }
                break;
            case 2:
                for (int i = personBean.size(); i > 0; i--) {
                    String format = simpleDateFormat.format(new Date().getTime() - personBean.get(i - 1).getAge());
                    int age2 = Integer.valueOf(format) - 1970;
                    if (!(age2 > 40 && age2 < 60)) {
                        personBean.remove(i - 1);
                    }
                }
                break;
            case 3:
                for (int i = personBean.size(); i > 0; i--) {
                    String format = simpleDateFormat.format(new Date().getTime() - personBean.get(i - 1).getAge());
                    int age2 = Integer.valueOf(format) - 1970;
                    if (!(age2 >= 60)) {
                        personBean.remove(i - 1);
                    }
                }
                break;
        }

        switch (nearby) {
            case 1:
                for (int i = personBean.size(); i > 0; i--) {
                    if (personBean.get(i - 1).getNearby() > 500) {
                        personBean.remove(i - 1);
                    }
                }
                break;
            case 2:
                for (int i = personBean.size(); i > 0; i--) {
                    if (personBean.get(i - 1).getNearby() > 1000) {
                        personBean.remove(i - 1);
                    }
                }
                break;
            case 3:
                for (int i = personBean.size(); i > 0; i--) {
                    if (personBean.get(i - 1).getNearby() > 1500) {
                        personBean.remove(i - 1);
                    }
                }
                break;
            case 4:
                for (int i = personBean.size(); i > 0; i--) {
                    if (personBean.get(i - 1).getNearby() > 2000) {
                        personBean.remove(i - 1);
                    }
                }
                break;
        }

        if (eat != 0) {
            for (int i = personBean.size(); i > 0; i--) {
                if (personBean.get(i - 1).getEat() != eat) {
                    personBean.remove(i - 1);
                }
            }
        }

        if (live != 0) {
            for (int i = personBean.size(); i > 0; i--) {
                if (personBean.get(i - 1).getLive() != live) {
                    personBean.remove(i - 1);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_level:
                btn_level.show();
                break;
            case R.id.btn_age:
                btn_age.show();
                break;
            case R.id.btn_nearby:
                btn_nearby.show();
                break;
            case R.id.btn_lodging:
                btn_lodging.show();
                break;
        }
    }

    private void initPickerView() {
        /**
         * 注意：如果是添加JavaBean实体数据，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */

        //选项2
        level_options.add(getString(R.string.unlimited));
        level_options.add(getString(R.string.level1));
        level_options.add(getString(R.string.level2));
        level_options.add(getString(R.string.level3));

        age_options.add(getString(R.string.unlimited));
        age_options.add(getString(R.string.age1));
        age_options.add(getString(R.string.age2));
        age_options.add(getString(R.string.age3));

        nearby_options.add(getString(R.string.unlimited));
        nearby_options.add(getString(R.string.nearby1));
        nearby_options.add(getString(R.string.nearby2));
        nearby_options.add(getString(R.string.nearby3));
        nearby_options.add(getString(R.string.nearby4));

        eat_options.add(getString(R.string.unlimited));
        eat_options.add(getString(R.string.eat));
        eat_options.add(getString(R.string.not_eat));

        live_options.add(getString(R.string.unlimited));
        live_options.add(getString(R.string.live));
        live_options.add(getString(R.string.not_live));

        btn_level.initData(getActivity(), level_options, new PickerView.PickerViewSelectListener() {
            @Override
            public void onPickerViewSelec(int position, String arg) {
                btn_level.setText(arg);
                level = position;
                onRefresh();
            }
        });

        btn_age.initData(getActivity(), age_options, new PickerView.PickerViewSelectListener() {
            @Override
            public void onPickerViewSelec(int position, String arg) {
                btn_age.setText(arg);
                age = position;
                onRefresh();
            }
        });

        btn_nearby.initData(getActivity(), nearby_options, new PickerView.PickerViewSelectListener() {
            @Override
            public void onPickerViewSelec(int position, String arg) {
                btn_nearby.setText(arg);
                nearby = position;
                onRefresh();
            }
        });

        btn_lodging.initData(getActivity(), eat_options,live_options, new PickerView.NPickerViewSelectListener() {
            @Override
            public void onPickerViewSelec(int position1, int position2, String arg) {
                btn_lodging.setText(arg);
                eat = position1;
                live = position2;
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
