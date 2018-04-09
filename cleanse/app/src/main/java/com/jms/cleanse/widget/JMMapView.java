package com.jms.cleanse.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import com.alexvasilkov.gestures.GestureController;
import com.alexvasilkov.gestures.State;
import com.alexvasilkov.gestures.animation.ViewPositionAnimator;
import com.alexvasilkov.gestures.views.interfaces.AnimatorView;
import com.alexvasilkov.gestures.views.interfaces.GestureView;
import com.jms.cleanse.widget.mapview.POIConfig;
import com.jms.cleanse.widget.mapview.ScaleUtils;
import com.jms.cleanse.widget.mapview.TestPOI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Pos_vel_status;

/**
 * Created by zhoukan on 2018/3/28.
 *
 * @desc: 绘制POI / 绘制路径  /
 */

public class JMMapView extends View implements GestureView, AnimatorView, View.OnTouchListener {

    private static final String TAG = "JMMapView";
    private final GestureController controller;
    private ViewPositionAnimator positionAnimator;
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;

    // 刷新频率
    public static final int TIME_IN_FRAME = 30;

    private Paint mPaint;
    private Canvas canvas;

    private SurfaceHolder holder;
    private Thread t;
    private boolean isDrawing = false;
    private List<TestPOI> testPOIS;
    private LinkedList<Path> paths;
    private LinkedList<RectF> points;
    // 机器人位置
    private Pos_vel_status.pose pos;
    /************ 地图坐标系相关的配置 *************/
    private int coodinateX;
    private int coodinateY;
    private Bitmap cleanseRes;
    private Bitmap unCleanseRes;
    private Bitmap pathStartRes;
    private Bitmap pathEndRes;
    private Bitmap robotMap;
    // 机器人位置
    private int colorCleanse;
    private int colorUnCleanse;
    // 地图
    private Bitmap map;
    private Resources resources;

    /* 矩阵变换 */
    private float scale = 1.0f;
    private float ratio = 1.0f;
    private float dx = 0;
    private float dy = 0;
    int index = -1;

    float tranX;
    float tranY;


    /* 事件 */
    private OnClickListener onClickListener;
    private Matrix changeMatrix = new Matrix();
    private Matrix matrix = new Matrix();

    @Override
    public GestureController getController() {
        return controller;
    }

    @Override
    public ViewPositionAnimator getPositionAnimator() {
        if (positionAnimator == null) {
            positionAnimator = new ViewPositionAnimator(this);
        }
        return positionAnimator;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float[] point = new float[2];
        point[0] = event.getX();
        point[1] = event.getY();
        index = -1;
        Log.d(TAG, "onTouchEvent: origin point :x" + point[0] + ",y:" + point[1]);
        changeMatrix.invert(matrix);
        matrix.mapPoints(point, new float[]{point[0], point[1]});
        Log.d(TAG, "onTouchEvent: transfrom point:x" + point[0] + ",y:" + point[1]);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startClickTime = Calendar.getInstance().getTimeInMillis();
                break;
            case MotionEvent.ACTION_UP:
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                if (clickDuration < MAX_CLICK_DURATION && onClickListener != null) {
                    clickEvent(point);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return controller.onTouch(this, event);
    }

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
        this.setOnTouchListener(this);

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
        robotMap = BitmapFactory.decodeStream(resources.openRawResource(POIConfig.robotPositionRes));

        colorCleanse = ContextCompat.getColor(this.getContext(), POIConfig.pathColorCleanse);
        colorUnCleanse = ContextCompat.getColor(this.getContext(), POIConfig.pathColorUnCleanse);

        /* make jmmapview support gesture */
        controller = new GestureController(this);
        controller.getSettings()
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setViewport(getWidth(), getHeight())
                .disableBounds();

        controller.addOnStateChangeListener(new GestureController.OnStateChangeListener() {
            @Override
            public void onStateChanged(State state) {
                applyState(state);
            }

            @Override
            public void onStateReset(State oldState, State newState) {
                applyState(newState);
            }
        });


    }

    /**
     * @param state
     */
    private void applyState(State state) {
        state.get(changeMatrix);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawMap();
        this.canvas = canvas;
        canvas.save();
        canvas.drawColor(Color.WHITE);
        canvasChange();
        canvas.concat(changeMatrix);
        drawBitMap();
        drawPath();
        drawPoi();
        drawRobot();
        canvas.restore();
    }

    private void drawRobot() {

        if (pos != null){
            canvas.drawBitmap(robotMap, (float) (pos.getX() - robotMap.getWidth() / 2), (float) (pos.getY() - robotMap.getHeight()), mPaint);
        }

    }

//    @Override
//    public void run() {
//
//        while (isDrawing) {
//            long startTime = System.currentTimeMillis();
//            drawMap();
//            long endTime = System.currentTimeMillis();
//            int diffTime = (int) (endTime - startTime);
//            while (diffTime <= TIME_IN_FRAME) {
//                diffTime = (int) (System.currentTimeMillis() - startTime);
//                Thread.yield();
//            }
//        }
//    }

//    /**
//     * 绘制地图
//     */
//    private void drawMap() {
//        try {
//            if (null != holder && holder.getSurface().isValid() && bitmapIsValid()) {
//                canvas = holder.lockCanvas();
//                canvas.save();
//                canvas.drawColor(Color.WHITE);
//                canvasChange();
//                canvas.concat(changeMatrix);
//                drawBitMap();
//                drawPath();
//                drawPoi();
//                canvas.restore();
//            }
//        } catch (Exception e) {
//            e.getStackTrace();
//        } finally {
//            if (null != canvas) {
//                holder.unlockCanvasAndPost(canvas);
//            }
//        }
//    }

    /**
     * @aim: 设置bitmap后进行的变换
     */
    private void canvasChange() {
        if (!bitmapIsValid()){
            return;
        }
        coodinateX = map.getWidth();
        coodinateY = map.getHeight();
        // 缩放倍数
        ratio = ScaleUtils.getScaleRatio(ScaleUtils.ScaleType.CENTER_INSIDE, map, getWidth(), getHeight());
        // 宽的差值
        float dw = getWidth() - map.getWidth() * ratio;
        // 高的差值
        float dh = getHeight() - map.getHeight() * ratio;
        // 横屏
        if (getWidth() > getHeight()) {
            tranX = dw / (2 * ratio); // 此时的canvas的坐标系已经发生更改,所以在移动canvas的过程中必须除以缩放倍数 （ps：关键点）
            tranY = 0;
            // 竖屏
        } else {
            tranY = dh / (2 * ratio);
            tranX = 0;
        }
        Log.d(TAG, "canvasChange: " + ratio + "tranX" + tranY + "tranY" + tranY);
        changeMatrix.preScale(ratio, ratio);
        changeMatrix.preTranslate(tranX, tranY);
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
                canvas.drawBitmap(pathStartRes, (float) (poiPoint.getAx() - pathStartRes.getWidth() / 2), (float) (poiPoint.getAy() - pathStartRes.getHeight()), mPaint);
            } else if (i > 0 && i == testPOIS.size() - 1) {
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


    /**
     * 只想出现一次
     */
    public void setMap(Bitmap map) {

//        Matrix m = new Matrix();
//        m.setRotate(90);
//        Log.d(TAG, "setMap: "+map.getWidth()+"h:"+map.getHeight());
//        this.map = Bitmap.createBitmap(map,0,0,map.getWidth(),map.getHeight(),m,true);
        this.map = map;
        controller.getSettings().setImage(map.getWidth(), map.getHeight());
        invalidate();
    }

    public void setTestPOIS(List<TestPOI> testPOIS) {
        if (testPOIS != null && testPOIS.size() > 0) {
            this.testPOIS.clear();
            this.testPOIS.addAll(testPOIS);
            invalidate();
        }
    }

    /**
     * @param pos
     */
    public void setPos_vel_status(Pos_vel_status.pose pos) {
        this.pos= pos;
    }

    /**
     * 点击事件
     * @param point
     */
    private void clickEvent(float[] point) {
        for (int i = 0; i < points.size(); i++) {
            RectF rectF = points.get(i);
            if (rectF.contains(point[0],point[1])){
                onClickListener.onPointClick(testPOIS.get(i),i);
            }
        }
    }

    /**
     * 根据poi点得到对应的路径组合
     *
     * @param testPOIS
     */
    private void getPathList(List<TestPOI> testPOIS) {
        // 记录上一个点的坐标
        paths.clear();
        points.clear();
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


    /**
     * bitmap 有效
     *
     * @return
     */
    private boolean bitmapIsValid() {
        return map != null && map.getWidth() > 0 && map.getHeight() > 0;
    }
}
