package com.jms.cleanse.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.jms.cleanse.R;
import com.jms.cleanse.util.DisplayUtil;

/**
 * Created by zhoukan on 2018/3/23.
 *
 * @desc: 电量指示
 */

public class BatteryBar extends View {


    private @ColorInt int boundColor = android.R.color.white;
    private @ColorInt int indicatorColor =  android.R.color.white;

    private @ColorInt int diviLineColor = R.color.colorPrimary;
    private int diviLineWidth = 1; // 单位 /dp
    private int boundWidth = 2;
    private float currentBattery = 70;

    private Paint rectPaint;
    private Paint boundPaint;
    private int minBattery = 0;
    private int maxBattery = 100;

    public BatteryBar(Context context) {
        this(context,null);
    }

    public BatteryBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BatteryBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBatteryBar();
    }

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    private void initBatteryBar() {

        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(getResources().getColor(boundColor));

        boundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boundPaint.setStyle(Paint.Style.FILL);
        // 色值设置问题  不能直接设置int值  由于它本身的值参数值就是int值，会引起程序的不确定性
        boundPaint.setColor(getResources().getColor(diviLineColor));
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        // 外边框
        canvas.drawRect(0,0,getpx(31),getpx(15),rectPaint);

        // 内边框
        canvas.drawRect( getpx((float) 2.5),getpx(2), getpx((float) 28.5),getpx(13),boundPaint);

        // 头部
        canvas.drawRect(getpx(31),getpx((float) 5.5),getpx((float) 33.5),getpx((float) 9.5),rectPaint);

        float cx = currentBattery/maxBattery;
        Log.d("battery", "onDraw: " + cx);
        canvas.drawRect(getpx((float) 4.5) ,getpx(4),getpx((float) (3.5+cx*22)),getpx(11),rectPaint);

    }

    private float getpx(float dp){
        return DisplayUtil.dip2px(getContext(),dp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension( getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }


}
