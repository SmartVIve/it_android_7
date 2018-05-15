package com.example.smart.test1.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.smart.test1.R;

/**
 * Created by Smart on 2018-05-01.
 */

public class SlideBottomView extends LinearLayout {
    private LinearLayout main;
    private RelativeLayout header;
    private ImageView iv_arrow;
    private float mDownY;
    private int mBaseMarginBottom;
    private int expandedMarginBottom;
    private int collapsedMarginBottom;
    private int hideMarginBottom;
    private int panelState;
    public static final int HIDE = 0;
    public static final int COLLAPSED = 1;
    public static final int EXPANDED = 2;
    private LinearLayout.LayoutParams lp;
    private int pastBottomMargin;
    private int distance;
    private OnPanelStateListener mOnPanelStateListener;

    public SlideBottomView(Context context) {
        super(context);
    }

    public SlideBottomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        main = (LinearLayout) findViewById(R.id.main);
        iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
        header = (RelativeLayout) findViewById(R.id.header);


        header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (panelState == COLLAPSED) {
                    setPanelState(EXPANDED);
                } else if (panelState == EXPANDED) {
                    setPanelState(COLLAPSED);
                }
            }
        });
    }

    //计算高度
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        hideMarginBottom = -main.getHeight();
        collapsedMarginBottom = header.getHeight() - main.getHeight();
        expandedMarginBottom = 0;
        mBaseMarginBottom = (main.getHeight() - header.getHeight()) / 5;

        //默认状态隐藏
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, hideMarginBottom);
        main.setLayoutParams(lp);
        panelState = HIDE;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pastBottomMargin = ((MarginLayoutParams) main.getLayoutParams()).bottomMargin;
                mDownY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getRawY() - mDownY) > 10) {
                    return true;
                }
                break;
        }

        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pastBottomMargin = ((MarginLayoutParams) main.getLayoutParams()).bottomMargin;
                mDownY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getRawY();
                distance = (int) (mDownY - moveY);
                moveRootMarginBottom(distance);
                break;
            case MotionEvent.ACTION_UP:
                //当滑动distance超过mBaseMarginBottom距离后抬手自动收缩或展开
                if (distance > 0 && getPanelState() == COLLAPSED) {
                    if (distance > mBaseMarginBottom) {
                        setPanelState(EXPANDED);
                    } else {
                        setPanelState(COLLAPSED);
                    }
                }
                if (distance < 0 && getPanelState() == EXPANDED) {
                    if (-distance > mBaseMarginBottom) {
                        setPanelState(COLLAPSED);
                    } else {
                        setPanelState(EXPANDED);
                    }

                }

                distance = 0;
                mDownY = 0;
                break;
        }
        return true;
    }

    //拖动
    private void moveRootMarginBottom(int distance) {
        int marginBottom = this.pastBottomMargin + distance;

        if (marginBottom >= expandedMarginBottom) {
            marginBottom = expandedMarginBottom;
        } else if (this.pastBottomMargin + distance <= collapsedMarginBottom) {
            marginBottom = collapsedMarginBottom;
        }

        lp.setMargins(0, 0, 0, marginBottom);
        main.setLayoutParams(lp);
    }


    public void setPanelState(final int mPanelState) {
        main.post(new Runnable() {
            @Override
            public void run() {
                hideMarginBottom = -main.getHeight();
                collapsedMarginBottom = header.getHeight() - main.getHeight();
                mBaseMarginBottom = (main.getHeight() - header.getHeight()) / 5;

                int setMarginBottom = 0;
                int setArrow = 0;
                switch (mPanelState) {
                    case HIDE:
                        setMarginBottom = hideMarginBottom;
                        break;
                    case COLLAPSED:
                        setMarginBottom = collapsedMarginBottom;
                        setArrow = R.drawable.icon_up_arrow;
                        break;
                    case EXPANDED:
                        setMarginBottom = expandedMarginBottom;
                        setArrow = R.drawable.icon_down_arrow;
                        break;
                }
                int currentBottomMargin = ((MarginLayoutParams) main.getLayoutParams()).bottomMargin;
                ValueAnimator anim = ValueAnimator.ofInt(currentBottomMargin, setMarginBottom);
                anim.setDuration(200);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (Integer) animation.getAnimatedValue();
                        lp.setMargins(0, 0, 0, value);
                        main.setLayoutParams(lp);
                    }
                });
                anim.start();
                panelState = mPanelState;
                iv_arrow.setImageResource(setArrow);
                if (mOnPanelStateListener!=null){
                    mOnPanelStateListener.onPanelStateChanged(mPanelState);
                }
            }
        });
    }


    public int getPanelState() {
        return panelState;
    }

    public interface OnPanelStateListener {
        void onPanelStateChanged(int panelState);
    }

    public void setOnPanelStateListener(OnPanelStateListener OnPanelStateListener){
        mOnPanelStateListener = OnPanelStateListener;
    }

}
