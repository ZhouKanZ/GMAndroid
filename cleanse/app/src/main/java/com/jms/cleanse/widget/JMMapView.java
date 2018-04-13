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

import com.alexvasilkov.gestures.GestureController;
import com.alexvasilkov.gestures.State;
import com.alexvasilkov.gestures.animation.ViewPositionAnimator;
import com.alexvasilkov.gestures.views.interfaces.AnimatorView;
import com.alexvasilkov.gestures.views.interfaces.GestureView;
import com.jms.cleanse.config.RobotConfig;
import com.jms.cleanse.entity.db.PoiPoint;
import com.jms.cleanse.entity.db.PositionBean;
import com.jms.cleanse.util.DisplayUtil;
import com.jms.cleanse.widget.mapview.POIConfig;
import com.jms.cleanse.widget.mapview.ScaleUtils;

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

public class JMMapView extends SurfaceView implements SurfaceHolder.Callback, Runnable, GestureView, AnimatorView {

    /**
     * MAPVIEW的模式
     */
    public enum Mode {
        EDIT, // 编辑模式
        EXE,  // 执行模式
        FREE  // 自由模式
    }

    private static final String TAG = "JMMapView";

    private final GestureController controller;                   // 手势控制
    private ViewPositionAnimator positionAnimator;                // 动画
    private static final int MAX_CLICK_DURATION = 200;            // 点击事件触发最大间隔
    private long startClickTime;
    private static int TIME_IN_FRAME = 30;                  // 刷新频率

    private Paint mPaint;
    private Canvas canvas;
    private SurfaceHolder holder;
    private Thread t;
    private boolean isDrawing = false;
    private List<PoiPoint> testPOIS;
    private LinkedList<Path> paths;                         // 路径的集合
    private LinkedList<RectF> points;                       // 点对应的矩形区域
    private Pos_vel_status.pose pos;                        // 机器人位置

    /************ 地图坐标系相关的配置 *************/
    private int coodinateX;
    private int coodinateY;
    private Bitmap cleanseRes;
    private Bitmap unCleanseRes;
    private Bitmap pathStartRes;
    private Bitmap pathEndRes;
    private Bitmap robotMap;
    private Bitmap map;
    private Resources resources;
    // 机器人位置
    private int colorCleanse;
    private int colorUnCleanse;
    /* 矩阵变换 */
    private float ratio = 1.0f;
    private float tranX;
    private float tranY;
    private boolean changeFlag = false;
    /* 事件 */
    private OnClickListener onClickListener;
    private Matrix changeMatrix = new Matrix();
    private Matrix matrix = new Matrix();
    private Matrix temp = new Matrix();

    /*********        任务编辑模式           **********/
    private boolean isTaskEditing = false;
    private Bitmap centerFlag;


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

        // surfaceView上面可以被覆盖
        setZOrderOnTop(true);
        setZOrderMediaOverlay(true);
//        this.setSurfaceTextureListener(this);

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
        robotMap = BitmapFactory.decodeStream(resources.openRawResource(POIConfig.robotPosRes));
        centerFlag = BitmapFactory.decodeStream(resources.openRawResource(POIConfig.centerFlagRes));

        colorCleanse = ContextCompat.getColor(this.getContext(), POIConfig.pathColorCleanse);
        colorUnCleanse = ContextCompat.getColor(this.getContext(), POIConfig.pathColorUnCleanse);

        /* make jmmapview support gesture */
        controller = new GestureController(this);
        controller.getSettings()
//                .setRotationEnabled(true)
                .disableBounds()
                .setMaxZoom((float) 1.5);

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

        while (isDrawing) {
            long startTime = System.currentTimeMillis();
            drawMap();
            long endTime = System.currentTimeMillis();
            int diffTime = (int) (endTime - startTime);
            while (diffTime <= TIME_IN_FRAME) {
                diffTime = (int) (System.currentTimeMillis() - startTime);
                Thread.yield();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasChange();
    }

    /**
     * @aim: 进行画布操作，将绘制起点设置为bitmap的左上角，坐标系转换到bitmap的像素坐标系上，方便作图
     */
    private void canvasChange() {
        if (!bitmapIsValid()) {
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
//        Log.d(TAG, "canvasChange: " + ratio + "tranX" + tranY + "tranY" + tranY);
        changeMatrix.reset();
        changeMatrix.preScale(ratio, ratio);
        changeMatrix.preTranslate(tranX, tranY);
        // 只会执行一次
        changeFlag = false;
    }

    /**
     * 绘制地图
     */
    private void drawMap() {

        synchronized (holder) {
            try {
                if (null != holder) {
                    canvas = holder.lockCanvas();
                    canvas.save();
                    canvas.drawColor(Color.WHITE);
                    if (changeFlag) {
                        canvasChange();
                    }
                    canvas.concat(changeMatrix);
                    drawBitMap();
                    drawPath();
                    drawPoi();
                    drawRobot();
                    canvas.restore();
                    drawCenterFlag();
                }
            } catch (Exception e) {
                e.getStackTrace();
            } finally {
                if (null != canvas) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    /**
     * 绘制中心区域
     */
    private void drawCenterFlag() {
        if (isTaskEditing) {
            canvas.drawBitmap(centerFlag, getWidth() / 2 - centerFlag.getWidth() / 2, getHeight() / 2 - centerFlag.getHeight(), mPaint);
        }
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

        PoiPoint lastPoi = null;
        for (int i = 0; i < testPOIS.size(); i++) {
            PoiPoint poiPoint = testPOIS.get(i);
            if (poiPoint.state) {
                canvas.drawBitmap(cleanseRes, null, points.get(i), mPaint);
            } else if (lastPoi != null && lastPoi.state) {
                canvas.drawBitmap(cleanseRes, null, points.get(i), mPaint);
            } else {
                canvas.drawBitmap(unCleanseRes, null, points.get(i), mPaint);
            }
            lastPoi = poiPoint;

            if (!isTaskEditing) {
                double[] androidRobotPos = DisplayUtil.getAndroidCoordinate(poiPoint.position.getTarget().x, poiPoint.position.getTarget().y, coodinateX, coodinateY);
                /* 绘制起点和终点 */
                if (i == 0) {
                    canvas.drawBitmap(pathStartRes, (float) (androidRobotPos[0] - pathStartRes.getWidth() / 2), (float) (androidRobotPos[1] - pathStartRes.getHeight()), mPaint);
                } else if (i > 0 && i == testPOIS.size() - 1) {
                    canvas.drawBitmap(pathEndRes, (float) (androidRobotPos[0]  - pathEndRes.getWidth() / 2), (float) (androidRobotPos[1]  - pathEndRes.getHeight()), mPaint);
                }
            }
        }

    }

    private void drawRobot() {

        if (pos != null) {
            double[] androidRobotPos = DisplayUtil.getAndroidCoordinate(pos.getX(), pos.getY(), coodinateX, coodinateY);
//            Log.d(TAG, "drawRobot: x:" + androidRobotPos[0] + "y:" + androidRobotPos[1] + "dx" + (float) (androidRobotPos[0] - robotMap.getWidth() / 2) + "dy:" + (float) (androidRobotPos[1] - robotMap.getHeight() / 2));
            canvas.drawBitmap(robotMap, (float) (androidRobotPos[0] - (robotMap.getWidth() / 2)), (float) (androidRobotPos[1] - (robotMap.getHeight() / 2)), mPaint);
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
            PoiPoint poiPoint = testPOIS.get(i);
            // 最后一个点 直接跳出循环
            if (i == testPOIS.size() - 1) {
                break;
            }
            if (poiPoint.state) {
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
        if (bitmapIsValid())
            changeFlag = true;
    }

    public void setTestPOIS(List<PoiPoint> testPOIS) {
        if (testPOIS != null && testPOIS.size() > 0) {
            this.testPOIS.clear();
            this.testPOIS.addAll(testPOIS);
        }else {
            this.testPOIS.clear();
        }
    }

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

    /**
     *  完成编辑
     */
    public void editComplete() {
        if (isTaskEditing){
            isTaskEditing = false;
        }

    }

    public interface OnClickListener {
        void onPointClick(PoiPoint poi, int pos);
        void onPathClick(int pos);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float[] point = new float[2];
        point[0] = event.getX();
        point[1] = event.getY();
        changeMatrix.invert(matrix);
        matrix.mapPoints(point, new float[]{point[0], point[1]});
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startClickTime = Calendar.getInstance().getTimeInMillis();
                break;
            case MotionEvent.ACTION_UP:
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                clickEvent(point, clickDuration);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }

        return controller.onTouch(this, event);
    }

    private void clickEvent(float[] point, long clickDuration) {
        if (clickDuration < MAX_CLICK_DURATION && onClickListener != null) {
            for (int i = 0; i < points.size(); i++) {
                RectF rectF = points.get(i);
                if (rectF.contains(point[0], point[1])) {
                    onClickListener.onPointClick(testPOIS.get(i), i);
                }
            }
        }
    }

    /**
     * 设置绘制模式
     *
     * @param taskEditing
     */
    public void setTaskEditing(boolean taskEditing) {
        isTaskEditing = taskEditing;
        testPOIS.clear();
    }

    /**
     * 得到中心点的在地图上的位置
     *
     * @return
     */
    private float[] getCenterFlagOnBitmap() {
        float[] centerPoint = new float[]{getWidth() / 2, getHeight() / 2};
        changeMatrix.invert(temp);
        temp.mapPoints(centerPoint, new float[]{centerPoint[0], centerPoint[1]});
        return centerPoint;
    }

    /**
     * 将图上的坐标转换为物理坐标
     *
     * @param bitmapPos
     * @return
     */
    private float[] convertBitmapPos2PhysicalCoordinate(float[] bitmapPos) {
        float[] changePos = new float[]{bitmapPos[0] - coodinateX / 2, coodinateY / 2 - bitmapPos[1]};
        changePos[0] = (float) (changePos[0] * RobotConfig.MAP_SCALE);
        changePos[1] = (float) (changePos[1] * RobotConfig.MAP_SCALE);
//        Log.d(TAG, "convertBitmapPos2PhysicalCoordinate: x:" + changePos[0] + ",y:" + changePos[1]);
        return changePos;
    }

    /**
     * 添加点
     */
    public void addPoint(boolean isCleanse) {
        if (!isTaskEditing){
            return;
        }
        float[] tempPos = getCenterFlagOnBitmap();
        tempPos = convertBitmapPos2PhysicalCoordinate(tempPos);
        PoiPoint tempTestPoi = new PoiPoint(/*tempPos[0], tempPos[1], isCleanse*/);
        PositionBean position = new PositionBean();
        position.x = tempPos[0];
        position.y = tempPos[1];
        position.yaw = 0;
        tempTestPoi.state = isCleanse;
        tempTestPoi.position.setTarget(position);
        testPOIS.add(tempTestPoi);
    }

    /**
     * 根据poi点得到对应的路径组合
     *
     * @param testPOIS
     */
    private void getPathList(List<PoiPoint> testPOIS) {

        paths.clear();
        points.clear();
        // 记录上一个点的坐标
        double[] lastPoi = null;
        for (int i = 0; i < testPOIS.size(); i++) {
            PoiPoint poiPoint = testPOIS.get(i);
//            double[] currentPoi = poiPoint.getAndroidCoordinate(coodinateX, coodinateY);
            double[] currentPoi = DisplayUtil.getAndroidCoordinate(poiPoint.position.getTarget().x,poiPoint.position.getTarget().y,coodinateX,coodinateY);
//            Log.d(TAG, "getPathList: x:" + currentPoi[0] + ",y:" + currentPoi[1]);
            if (lastPoi != null) {
                Path p = new Path();
                p.moveTo((float) lastPoi[0], (float) lastPoi[1]);
                p.lineTo((float) currentPoi[0], (float) currentPoi[1]);
                // 添加路径
                paths.add(p);
            }
            lastPoi = currentPoi;
            // 添加矩形区域
            points.add(DisplayUtil.getRect(poiPoint.position.getTarget().x,poiPoint.position.getTarget().y,coodinateX,coodinateY,cleanseRes.getWidth()/2));

        }
    }

    public List<PoiPoint> getTestPOIS(){
        return this.testPOIS;
    }

    public void setPos(Pos_vel_status.pose pos) {
        this.pos = pos;
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
