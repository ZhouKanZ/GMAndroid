package com.jms.cleanse.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.jms.cleanse.widget.mapview.POIConfig;
import com.jms.cleanse.widget.mapview.ScaleUtils;
import com.jms.cleanse.widget.mapview.TestPOI;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhoukan on 2018/3/28.
 *
 * @desc: 绘制POI / 绘制路径  /
 */

public class JMMapView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final String TAG = "JMMapView";

    private Paint mPaint;
    private Canvas canvas;

    private SurfaceHolder holder;
    private Thread t;
    private boolean isDrawing = false;
    private List<TestPOI> testPOIS;
    private LinkedList<Path> paths;
    private LinkedList<RectF> points;

    /************ 地图坐标系相关的配置 *************/
    private int coodinateX;
    private int coodinateY;
    private Bitmap cleanseRes;
    private Bitmap unCleanseRes;
    private Bitmap pathStartRes;
    private Bitmap pathEndRes;
    private int colorCleanse;
    private int colorUnCleanse;
    // 地图
    private Bitmap map;

    private Resources resources;

    /* 事件 */
    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onPointClick(TestPOI poi, int pos);

        void onPathClick(int pos);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public JMMapView(@NonNull Context context) {
        this(context, null);
    }

    public JMMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("ResourceType")
    public JMMapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.setFocusable(true);
        this.setZOrderOnTop(true);

        holder = getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSPARENT);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);

        testPOIS = new ArrayList<>();
        paths = new LinkedList<>();
        points = new LinkedList<>();

        resources = getResources();
        cleanseRes = BitmapFactory.decodeStream(resources.openRawResource(POIConfig.cleanseRes));
        unCleanseRes = BitmapFactory.decodeStream(resources.openRawResource(POIConfig.unCleanseRes));
        pathStartRes = BitmapFactory.decodeStream(resources.openRawResource(POIConfig.pathStartRes));
        pathEndRes = BitmapFactory.decodeStream(resources.openRawResource(POIConfig.pathEndRes));

        colorCleanse = ContextCompat.getColor(this.getContext(), POIConfig.pathColorCleanse);
        colorUnCleanse = ContextCompat.getColor(this.getContext(), POIConfig.pathColorUnCleanse);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDrawing = true;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;
    }

    @Override
    public void run() {
        if (isDrawing) {
            drawMap();
        }
    }

    /**
     * 绘制地图
     */
    private void drawMap() {

        try {
            if (null != holder) {
                canvas = holder.lockCanvas();
                canvas.save();
                if (!canvasChange()) {
                    return;
                }
                canvas.drawColor(Color.WHITE);

                drawBitMap();
                drawPath();
                drawPoi();
                canvas.restore();
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {

            if (null != canvas) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * @aim: 进行画布操作，将绘制起点设置为bitmap的左上角，坐标系转换到bitmap的像素坐标系上，方便作图
     */
    private boolean canvasChange() {
        if (map != null) {
            // 缩放倍数
            float ratio = ScaleUtils.getScaleRatio(ScaleUtils.ScaleType.CENTER_INSIDE, map, getWidth(), getHeight());
            // 宽的差值
            float dw = getWidth() - map.getWidth() * ratio;
            // 高的差值
            float dh = getHeight() - map.getHeight() * ratio;
            canvas.scale(ratio, ratio);
            // 横屏
            if (getWidth() > getHeight()) {
                canvas.translate(dw / (2 * ratio), 0);
                // 竖屏
            } else {
                canvas.translate(0, dh / (2 * ratio));
            }
            // 计算坐标系的最大值
            coodinateX = map.getWidth();
            coodinateY = map.getHeight();
            Log.d(TAG, "canvasChange: " + coodinateX + ",y" + coodinateY);
        } else {
            return false;
        }
        return true;
    }


    /**
     * 绘制地图，在绘制之前需要确认bitmap缩放的方式并返回得到的bitmap.
     */
    private void drawBitMap() {
        if (map != null) {
            canvas.drawBitmap(map, 0, 0, mPaint);
        }
    }

    /**
     * 绘制POI点 测试点数据 (-2.5，4.4)
     */
    private void drawPoi() {
        TestPOI lastPoi = null;
        for (int i = 0; i < testPOIS.size(); i++) {
            TestPOI poiPoint = testPOIS.get(i);
            if (poiPoint.isCleanse()) {
                canvas.drawBitmap(cleanseRes, null, points.get(i), mPaint);
            } else if (lastPoi != null && lastPoi.isCleanse()) {
                canvas.drawBitmap(cleanseRes, null, points.get(i), mPaint);
            } else {
                canvas.drawBitmap(unCleanseRes, null, points.get(i), mPaint);
            }
            lastPoi = poiPoint;

            /* 绘制起点和终点 */
            if (i == 0) {
//                canvas.drawBitmap(pathStartRes,null,getBitmapRectF(poiPoint.getAx(),poiPoint.getAy(),pathStartRes),mPaint);
                canvas.drawBitmap(pathStartRes, (float) (poiPoint.getAx() - pathStartRes.getWidth() / 2), (float) (poiPoint.getAy() - pathStartRes.getHeight()), mPaint);
//                canvas.drawRect(getBitmapRectF(poiPoint.getAx(),poiPoint.getAy(),pathEndRes),mPaint);
            } else if (i > 0 && i == testPOIS.size() - 1) {
//                canvas.drawBitmap(pathStartRes,null,getBitmapRectF(poiPoint.getAx(),poiPoint.getAy(),pathStartRes),mPaint);
//                mPaint.setColor(Color.BLUE);
//                canvas.drawRect(getBitmapRectF(poiPoint.getAx(),poiPoint.getAy(),pathEndRes),mPaint);
                canvas.drawBitmap(pathEndRes, (float) (poiPoint.getAx() - pathEndRes.getWidth() / 2), (float) (poiPoint.getAy() - pathEndRes.getHeight()), mPaint);
            }
        }

    }

    /**
     * 获取Bitmap矩形区域
     *
     * @param ax
     * @param ay
     * @param bm
     */
    private RectF getBitmapRectF(double ax, double ay, Bitmap bm) {

        int w = bm.getWidth();
        int h = bm.getHeight();

        RectF rectF = new RectF();
        rectF.left = (float) (ax - w / 2);
        rectF.top = (float) (ay - h);
        rectF.right = (float) (ay + w / 2);
        rectF.bottom = (float) ay;

        return rectF;
    }

    @SuppressLint("ResourceType")
    private void drawPath() {

        getPathList(this.testPOIS);
        for (int i = 0; i < testPOIS.size(); i++) {
            TestPOI poiPoint = testPOIS.get(i);
            // 最后一个点 直接跳出循环
            if (i == testPOIS.size() - 1) {
                break;
            }
            if (poiPoint.isCleanse()) {
                mPaint.setColor(colorCleanse);
            } else {
                mPaint.setColor(colorUnCleanse);
            }
            canvas.drawPath(paths.get(i), mPaint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = 0;
        int h = 0;
        if (null != map) {
            w = map.getWidth();
            h = map.getHeight();
        }
        setMeasuredDimension(getDefaultSize(w, widthMeasureSpec), getDefaultSize(h, heightMeasureSpec));
    }

    /**
     * 设置地图
     */
    public void setMap(Bitmap map) {
        this.map = map;
        invalidate();
    }

    public void setTestPOIS(List<TestPOI> testPOIS) {
        if (testPOIS != null && testPOIS.size() > 0) {
            this.testPOIS.clear();
            this.testPOIS.addAll(testPOIS);
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 点被点击
                Log.d(TAG, "onTouchEvent: 我被点了" + x+",y"+y);
                for (int i = 0; i <points.size() ; i++) {
                    if (onClickListener!=null){
                        if (points.get(i).contains(x,y)){
                            onClickListener.onPointClick(testPOIS.get(i),i);
                            Log.d(TAG, "onTouchEvent: " + i+"我被点了");
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 根据poi点得到对应的路径组合
     *
     * @param testPOIS
     */
    private void getPathList(List<TestPOI> testPOIS) {

        // 记录上一个点的坐标
        double[] lastPoi = null;
        for (int i = 0; i < testPOIS.size(); i++) {
            TestPOI poiPoint = testPOIS.get(i);
            double[] currentPoi = poiPoint.getAndroidCoordinate(coodinateX, coodinateY);
            Log.d(TAG, "getPathList: x:" + currentPoi[0] + ",y:" + currentPoi[1]);
            if (lastPoi != null) {
                Path p = new Path();
                p.moveTo((float) lastPoi[0], (float) lastPoi[1]);
                p.lineTo((float) currentPoi[0], (float) currentPoi[1]);
                // 添加路径
                paths.add(p);
            }
            lastPoi = currentPoi;
            // 添加矩形区域
            points.add(poiPoint.getRect(currentPoi[0], currentPoi[1], coodinateX, coodinateY, cleanseRes.getWidth() / 2));

        }
    }
}
