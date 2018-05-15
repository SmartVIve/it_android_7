package com.example.smart.test1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smart.test1.R;
import com.example.smart.test1.bean.JsonBean;
import com.example.smart.test1.bean.WorkBean;
import com.example.smart.test1.utils.GetCityDataUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Smart on 2018-03-19.
 */

public class Work_RecyclerViewAdapter extends RecyclerView.Adapter<Work_RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WorkBean> workBeans;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private GetCityDataUtils getCityDataUtils = new GetCityDataUtils();
    private Work_RecyclerViewAdapter.OnItemClickListener mOnItemClickListener;//声明接口

    public Work_RecyclerViewAdapter(Context context, ArrayList<WorkBean> workBeans) {
        this.context = context;
        this.workBeans = workBeans;
        initOptionData();
    }

    @Override
    public Work_RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final Work_RecyclerViewAdapter.ViewHolder holder, final int position) {
        Date startdate = new Date(workBeans.get(position).getStartDate());//获取时间戳转换为date类型
        Date enddate = new Date(workBeans.get(position).getEndDate());

        String startDate = getTime(startdate);//date转换为文字类型
        String endDate = getTime(enddate);
        String date = startDate + "至" + endDate;


        int index_province = workBeans.get(position).getProvince();
        int index_city = workBeans.get(position).getCity();
        String province = options1Items.get(index_province).getPickerViewText();
        String city = options2Items.get(index_province).get(index_city);

        holder.tv_address.setText(String.format(context.getString(R.string.sf_city), province, city));
        holder.tv_price.setText(String.format(context.getString(R.string.sf_price_hour),workBeans.get(position).getPrice()));
        holder.tv_date.setText(date);

        if (mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(workBeans.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return workBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_address;
        private TextView tv_price;
        private TextView tv_date;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }

    private void initOptionData() {
        options1Items = getCityDataUtils.getOptions1Items(context);
        options2Items = getCityDataUtils.getOptions2Items(context);
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd", Locale.getDefault());
        return format.format(date);
    }

    public interface OnItemClickListener {
        void onItemClick(WorkBean workBean);
    }

    //点击监听
    public void setOnItemClickListener(Work_RecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
