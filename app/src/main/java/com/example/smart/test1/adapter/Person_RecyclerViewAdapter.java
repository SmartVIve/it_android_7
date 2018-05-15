package com.example.smart.test1.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smart.test1.R;
import com.example.smart.test1.bean.JsonBean;
import com.example.smart.test1.bean.PersonBean;
import com.example.smart.test1.utils.BitmapCacheUtils;
import com.example.smart.test1.utils.DownloadAvatarUtils;
import com.example.smart.test1.utils.GetCityDataUtils;

import java.util.ArrayList;

/**
 * Created by Smart on 2018-03-18.
 */

public class Person_RecyclerViewAdapter extends RecyclerView.Adapter<Person_RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<PersonBean> personBeans;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private GetCityDataUtils getCityDataUtils = new GetCityDataUtils();
    private BitmapCacheUtils bitmapCacheUtils = new BitmapCacheUtils();
    private OnItemClickListener mOnItemClickListener;//声明接口

    public Person_RecyclerViewAdapter(Context context, ArrayList<PersonBean> personBean) {
        this.context = context;
        this.personBeans = personBean;
        initOptionData();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int index_province = personBeans.get(position).getProvince();
        int index_city = personBeans.get(position).getCity();
        String province = options1Items.get(index_province).getPickerViewText();
        String city = options2Items.get(index_province).get(index_city);

        holder.tv_address.setText(String.format(context.getString(R.string.sf_city), province, city));


        switch (personBeans.get(position).getLevel()) {
            case 1:
                holder.iv_level.setImageResource(R.drawable.icon_pthg);
                break;
            case 2:
                holder.iv_level.setImageResource(R.drawable.icon_gjhg);
                break;
            case 3:
                holder.iv_level.setImageResource(R.drawable.icon_hs);
                break;
        }

        String headIcon = personBeans.get(position).getHeadIcon();
        holder.iv_avatar.setTag(headIcon);
        holder.iv_avatar.setImageResource(R.drawable.icon_profile);
        holder.tv_name.setText(personBeans.get(position).getName());

        if (headIcon != null) {
            bitmapCacheUtils.setGetBitmapListener(headIcon, new BitmapCacheUtils.GetBitmapListener() {
                @Override
                public void getBitmap(Bitmap bitmap) {
                    holder.iv_avatar.setImageBitmap(bitmap);
                }
            });
        }

        //item点击
        if (mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(personBeans.get(position));
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return personBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_address;
        private TextView tv_name;
        private ImageView iv_avatar;
        private ImageView iv_level;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            iv_level = (ImageView) itemView.findViewById(R.id.iv_level);
        }
    }



    private void initOptionData() {
        options1Items = getCityDataUtils.getOptions1Items(context);
        options2Items = getCityDataUtils.getOptions2Items(context);
    }


    public interface OnItemClickListener {
        void onItemClick(PersonBean personBean);
    }

    //点击监听
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}
