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
import android.widget.Toast;

import com.example.smart.test1.R;
import com.example.smart.test1.activities.MyWorkActivity;
import com.example.smart.test1.bean.JsonBean;
import com.example.smart.test1.bean.PersonBean;
import com.example.smart.test1.bean.WorkBean;
import com.example.smart.test1.utils.BitmapCacheUtils;
import com.example.smart.test1.utils.DownloadAvatarUtils;
import com.example.smart.test1.utils.GetCityDataUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Smart on 2018-04-21.
 */

public class MyWork_RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int PERSON_ITEM = 1;
    private static final int WORK_ITEM = 2;
    private static final int TITLE_ITEM = 3;
    private Context context;
    private ArrayList<PersonBean> personBeans = new ArrayList<>();
    private ArrayList<WorkBean> workBeans = new ArrayList<>();

    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private GetCityDataUtils getCityDataUtils = new GetCityDataUtils();
    private BitmapCacheUtils bitmapCacheUtils = new BitmapCacheUtils();

    public MyWork_RecyclerViewAdapter(Context context, ArrayList<PersonBean> personBean, ArrayList<WorkBean> workBean) {
        this.context = context;
        this.personBeans = personBean;
        this.workBeans = workBean;
        initOptionData();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder = null;
        if (viewType == PERSON_ITEM) {
            View view = layoutInflater.inflate(R.layout.item_mywork_person, parent, false);
            holder = new Person_ViewHolder(view);
        } else if (viewType == WORK_ITEM) {
            View view = layoutInflater.inflate(R.layout.item_mywork_work, parent, false);
            holder = new Work_ViewHolder(view);
        } else {
            View view = layoutInflater.inflate(R.layout.item_mywork_title, parent, false);
            holder = new Title_ViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof Title_ViewHolder) {
            if (position == 0) {
                ((Title_ViewHolder) holder).tv_title.setText(R.string.job_wanted);
            } else {
                ((Title_ViewHolder) holder).tv_title.setText(R.string.recruit);
            }
        } else if (holder instanceof Person_ViewHolder) {
            int person_position = position - 1;

            int index_province = personBeans.get(person_position).getProvince();
            int index_city = personBeans.get(person_position).getCity();
            String province = options1Items.get(index_province).getPickerViewText();
            String city = options2Items.get(index_province).get(index_city);

            ((Person_ViewHolder) holder).tv_address.setText(String.format(context.getString(R.string.sf_city), province, city));


            switch (personBeans.get(person_position).getLevel()) {
                case 1:
                    ((Person_ViewHolder) holder).iv_level.setImageResource(R.drawable.icon_pthg);
                    break;
                case 2:
                    ((Person_ViewHolder) holder).iv_level.setImageResource(R.drawable.icon_gjhg);
                    break;
                case 3:
                    ((Person_ViewHolder) holder).iv_level.setImageResource(R.drawable.icon_hs);
                    break;
            }

            String headIcon = personBeans.get(person_position).getHeadIcon();
            ((Person_ViewHolder) holder).iv_avatar.setTag(headIcon);
            ((Person_ViewHolder) holder).iv_avatar.setImageResource(R.drawable.icon_profile);
            ((Person_ViewHolder) holder).tv_name.setText(personBeans.get(person_position).getName());

            if (headIcon != null) {
                bitmapCacheUtils.setGetBitmapListener(headIcon, new BitmapCacheUtils.GetBitmapListener() {
                    @Override
                    public void getBitmap(Bitmap bitmap) {
                        ((Person_ViewHolder) holder).iv_avatar.setImageBitmap(bitmap);
                    }
                });
            }
        } else {
            int work_position = position - personBeans.size() - 2;

            Date startdate = new Date(workBeans.get(work_position).getStartDate());//获取时间戳转换为date类型
            Date enddate = new Date(workBeans.get(work_position).getEndDate());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd", Locale.getDefault());
            String startDate = simpleDateFormat.format(startdate);//date转换为文字类型
            String endDate = simpleDateFormat.format(enddate);
            String date = startDate + "至" + endDate;


            int index_province = workBeans.get(work_position).getProvince();
            int index_city = workBeans.get(work_position).getCity();
            String province = options1Items.get(index_province).getPickerViewText();
            String city = options2Items.get(index_province).get(index_city);

            ((Work_ViewHolder) holder).tv_address.setText(String.format(context.getString(R.string.sf_city), province, city));
            ((Work_ViewHolder) holder).tv_price.setText(String.format(context.getString(R.string.sf_price_hour), workBeans.get(work_position).getPrice()));
            ((Work_ViewHolder) holder).tv_date.setText(date);
        }

    }


    @Override
    public int getItemCount() {
        return personBeans.size() + workBeans.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == personBeans.size() + 1) {
            return TITLE_ITEM;
        } else if (position <= personBeans.size()) {
            return PERSON_ITEM;
        } else {
            return WORK_ITEM;
        }
    }

    class Title_ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;

        Title_ViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }

    }


    class Person_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_address;
        private TextView tv_name;
        private ImageView iv_avatar;
        private ImageView iv_level;

        Person_ViewHolder(View itemView) {
            super(itemView);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            iv_level = (ImageView) itemView.findViewById(R.id.iv_level);
            View main = itemView.findViewById(R.id.main);
            View settings = itemView.findViewById(R.id.btn_settings);
            View delete = itemView.findViewById(R.id.btn_delete);
            main.setOnClickListener(this);
            settings.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int person_position = getAdapterPosition() - 1;
            if (v.getId() == R.id.main || v.getId() == R.id.btn_settings) {
                ((MyWorkActivity) (context)).setPerson(personBeans.get(person_position));
            }else {
                personBeans.get(person_position).delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                            Log.i("bmob", "成功");
                        } else {
                            Toast.makeText(context, "删除失败", Toast.LENGTH_LONG).show();
                            Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
                personBeans.remove(person_position);
                notifyItemRemoved(getAdapterPosition());
            }
        }
    }

    class Work_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_address;
        private TextView tv_price;
        private TextView tv_date;

        Work_ViewHolder(View itemView) {
            super(itemView);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            View main = itemView.findViewById(R.id.main);
            View settings = itemView.findViewById(R.id.btn_settings);
            View delete = itemView.findViewById(R.id.btn_delete);
            main.setOnClickListener(this);
            settings.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int work_position = getAdapterPosition() - personBeans.size() - 2;
            if (v.getId() == R.id.main || v.getId() == R.id.btn_settings) {
                ((MyWorkActivity) (context)).setPerson(workBeans.get(work_position));
            }else {
                workBeans.get(work_position).delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                            Log.i("bmob", "成功");
                        } else {
                            Toast.makeText(context, "删除失败", Toast.LENGTH_LONG).show();
                            Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
                workBeans.remove(work_position);
                notifyItemRemoved(getAdapterPosition());
            }
        }
    }



    private void initOptionData() {
        options1Items = getCityDataUtils.getOptions1Items(context);
        options2Items = getCityDataUtils.getOptions2Items(context);
    }


}
