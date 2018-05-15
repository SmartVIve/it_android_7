package com.example.smart.test1.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by Smart on 2018-03-18.
 */

public class MyItemDecoration extends RecyclerView.ItemDecoration {
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //设定底部边距为2px
        outRect.set(0, 0, 0, 2);
    }

}
