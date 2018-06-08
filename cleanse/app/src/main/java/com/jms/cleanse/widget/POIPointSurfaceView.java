package com.jms.cleanse.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by zhoukan on 2018/3/29.
 *
 * @desc: 绘制poi点
 */

public class POIPointSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private boolean isRunning = false;
    private Paint mPaint;
    private Canvas canvas;
    private SurfaceHolder holder;
    private Thread t;


    public POIPointSurfaceView(Context context) {
        this(context, null);
    }

    public POIPointSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public POIPointSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2);

        holder = getHolder();
        holder.addCallback(this);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        // 设置常量
        this.setKeepScreenOn(true);
        this.setZOrderOnTop(false);
        this.setZOrderMediaOverlay(true);
//        holder.setFormat(-3);

        t = new Thread(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {
        if (isRunning) {
            drawPoint();
        }
    }

    /**
     * 绘制点
     */
    private void drawPoint() {
        try {
            canvas = holder.lockCanvas();
            canvas.drawPoint(10, 20, mPaint);
        } catch (Exception e) {
            e.getStackTrace();
        } finally {

            if (null != canvas)
                holder.unlockCanvasAndPost(canvas);

        }
    }
}
