package com.example.smart.test1.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.example.smart.test1.bean.JsonBean;
import com.example.smart.test1.fragment.PersonFragment;
import com.example.smart.test1.fragment.WorkFragment;
import com.example.smart.test1.utils.GetCityDataUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Smart on 2018-03-30.
 */

public class PickerView extends android.support.v7.widget.AppCompatButton {
    private OptionsPickerView pickerView;
    private TimePickerView timePickerView;
    private ArrayList<JsonBean> province_Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> city_Items = new ArrayList<>();

    public PickerView(Context context) {
        super(context);
    }

    public PickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public interface PickerViewSelectListener {
        void onPickerViewSelec(int position, String arg);
    }

    public interface NPickerViewSelectListener {
        void onPickerViewSelec(int position1, int position2, String arg);
    }

    public interface LocationPickerViewSelectListener {
        void onNPickerViewSelec(int provincePosition, int cityPosition, String arg, String arg2);
    }

    public interface TimePickerViewSelectListener {
        void onTimePickerViewSelec(long time, String arg);
    }

    public void initData(Context context, final ArrayList<String> optionsItems, final PickerViewSelectListener listener) {
        pickerView = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String arg = optionsItems.get(options1);
                listener.onPickerViewSelec(options1, arg);
            }
        }).setOutSideCancelable(true).setSubmitText("确定").setCancelText("取消").build();

        pickerView.setPicker(optionsItems);
    }

    public void initData(Context context, final ArrayList<String> optionsItems1, final ArrayList<String> optionsItems2, final NPickerViewSelectListener listener) {
        pickerView = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String arg = optionsItems1.get(options1) + " " + optionsItems2.get(options2);
                listener.onPickerViewSelec(options1, options2, arg);
            }
        }).setOutSideCancelable(true).setSubmitText("确定").setCancelText("取消").build();

        pickerView.setNPicker(optionsItems1, optionsItems2, null);
    }

    public void initLocationData(final Context context, final LocationPickerViewSelectListener pickerViewSelectListener) {
        GetCityDataUtils getCityDataUtils = new GetCityDataUtils();
        province_Items = getCityDataUtils.getOptions1Items(context);
        city_Items = getCityDataUtils.getOptions2Items(context);


        pickerView = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String arg = city_Items.get(options1).get(options2);//不带省份
                String arg2 = province_Items.get(options1).getPickerViewText() + "-" + city_Items.get(options1).get(options2);//带省份
                pickerViewSelectListener.onNPickerViewSelec(options1, options2, arg, arg2);
            }
        }).setOutSideCancelable(true).setSubmitText("确定").setCancelText("取消").build();

        pickerView.setPicker(province_Items, city_Items);
    }

    public void initTimeData(Context context, final boolean[] type, final String dateType, final Calendar startDate, final Calendar endDate, final TimePickerViewSelectListener listener) {

        timePickerView = new TimePickerView.Builder(context, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                SimpleDateFormat format = new SimpleDateFormat(dateType);
                String arg = format.format(date);
                listener.onTimePickerViewSelec(date.getTime(), arg);
            }
        }).setType(type).setOutSideCancelable(true).setRangDate(startDate, endDate).setSubmitText("确定").setCancelText("取消").build();

        timePickerView.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
    }


    public void show() {
        if (pickerView != null) {
            pickerView.show();
        } else {
            timePickerView.show();
        }

    }
}

