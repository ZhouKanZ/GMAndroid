package com.jms.cleanse.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import com.jms.cleanse.R;
import com.jms.cleanse.util.DisplayUtil;

import java.text.DecimalFormat;

/**
 * Created by zhoukan on 2018/4/17.
 *
 * @desc: 角度选择器
 * @keyCode :
 * 1.onTouchEvent的处理
 * 2.文字外发光
 */

public class AngleWheelView extends View {

    private static final String TAG = "AngleWheelView";

    private int addAsubColor;
    private Paint mPaint;
    private int[] res;
    private Bitmap[] bitRes; // [0-3]分别对应 背景 指示背景 中心背景 圆环
    private double currentAngle = 0; // 默认值为0°   [-180° - 180°]
    private double[] centerPos = new double[2];
    private double radius;


    /**************************   指示器拖动事件  ******************************/
    private boolean dragable = false;           // 是否可被拖动
    private RectF indicatorRect = new RectF();  // 指示器标识区域
    private RectF reactRegion = new RectF();    // 反应区域
    private float[] lastPos = new float[2];     // 上一个点的位置
    private float[] currentPos = new float[2];     // 当前点的位置


    /* 如何正确使用onMeasure测量控件的宽高 */

    public AngleWheelView(Context context) {
        this(context, null);
    }

    public AngleWheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * init some utils
     */
    private void init() {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
//        mPaint.setStrokeWidth(15);
        mPaint.setTextSize(DisplayUtil.sp2px(this.getContext(), 15));
        addAsubColor = ContextCompat.getColor(this.getContext(), R.color.addAsubColor);
        res = new int[]{

                R.drawable.angle_wheel_background,
                R.drawable.indicator_background,
                R.drawable.indicator,
                R.drawable.center_background,
                R.drawable.ring,
                R.drawable.add,
                R.drawable.sub,
        };


        bitRes = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), res[0]),  // 背景
                BitmapFactory.decodeResource(getResources(), res[1]),  // 指示器背景
                BitmapFactory.decodeResource(getResources(), res[2]),  // 指示器
                BitmapFactory.decodeResource(getResources(), res[3]),  // 中间背景
                BitmapFactory.decodeResource(getResources(), res[4]),  // 圆环
                BitmapFactory.decodeResource(getResources(), res[5]),  // 添加
                BitmapFactory.decodeResource(getResources(), res[6])  // 减少
        };

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());

        caclAllPadding();
        // first step: draw static content
        canvas.drawBitmap(bitRes[0], 0, 0, null);
        int dw0 = bitRes[0].getWidth() - bitRes[1].getWidth();
        canvas.drawBitmap(bitRes[1], dw0 / 2, dw0 / 2, null);
        int dw1 = bitRes[0].getWidth() - bitRes[3].getWidth();
        canvas.drawBitmap(bitRes[3], dw1 / 2, dw1 / 2, null);
        int dw2 = bitRes[0].getWidth() - bitRes[4].getWidth();
        canvas.drawBitmap(bitRes[4], dw2 / 2, dw2 / 2, null);


        float[] drawPos = caclIndicatorPosition(currentAngle);
        indicatorRect.left = drawPos[0];
        indicatorRect.top = drawPos[1];
        indicatorRect.right = drawPos[0] + bitRes[2].getWidth();
        indicatorRect.bottom = drawPos[1] + bitRes[2].getWidth();

        reactRegion.left = indicatorRect.left - bitRes[2].getWidth();
        reactRegion.top = indicatorRect.top - bitRes[2].getWidth();
        reactRegion.right = indicatorRect.right + bitRes[2].getWidth();
        reactRegion.bottom = indicatorRect.bottom + bitRes[2].getWidth();


        canvas.drawBitmap(bitRes[2], null, indicatorRect, mPaint);

        /* 绘制文字  Android 坐标系与数学坐标系相反  所以在这里需要将角度反转 */
        DecimalFormat df = new DecimalFormat("#");
        String str = df.format(-currentAngle);
        String strAngle = str + "°";
        float dw = mPaint.measureText(strAngle, 0, strAngle.length()) / 2;
        float dh = (mPaint.descent() - mPaint.ascent()) / 2;
        canvas.drawText(removeIrregularAngle(strAngle), (float) centerPos[0] - dw, (float) centerPos[1] + dh - mPaint.descent(), mPaint);
        canvas.restore();
    }

    /**
     * remove irregular angle such as -180 -0
     * @param strAngle
     * @return
     */
    private String removeIrregularAngle(String strAngle) {
        return strAngle.equals("-180°")?"-179°":strAngle.equals("-0°")?"0°":strAngle;
    }

    /**
     * 计算指示器的绘制起点
     *
     * @param currentAngle
     */
    private float[] caclIndicatorPosition(double currentAngle) {
        float[] drawPosition = new float[2];
        drawPosition[0] = (float) (centerPos[0] + Math.cos(convertAngleToRadians(currentAngle)) * radius - bitRes[2].getWidth() / 2);
        drawPosition[1] = (float) (centerPos[1] + Math.sin(convertAngleToRadians(currentAngle)) * radius - bitRes[2].getWidth() / 2);
        return drawPosition;
    }

    /**
     * 将角度转换为弧度
     *
     * @param currentAngle
     * @return
     */
    private double convertAngleToRadians(double currentAngle) {
        return currentAngle * Math.PI / 180;
    }

    /**
     * 计算所有的边距
     */
    private void caclAllPadding() {

        // 获取中心点的坐标
        centerPos[0] = bitRes[0].getWidth() / 2;
        centerPos[1] = bitRes[0].getWidth() / 2;
        Log.d(TAG, "caclAllPadding: " + centerPos[0]);

        // 获取半径
        double width = bitRes[1].getWidth() / 2;
        radius = width - (bitRes[1].getWidth() - bitRes[3].getWidth()) / 4 - 2;
        Log.d(TAG, "caclAllPadding: " + radius);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 判断点是否在可以触发拖动事件内
                dragable = isDragged(x, y);
                lastPos[0] = x;
                lastPos[1] = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // 计算拖动的距离，换算成角度并更新ui
                if (dragable) {
                    currentPos[0] = x;
                    currentPos[1] = y;
                    calcDistance(currentPos);
                    lastPos[0] = x;
                    lastPos[1] = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                dragable = false;
                break;
        }
        return true;
    }

    /**
     * 计算角度
     *
     * @param currentPos
     */
    private void calcDistance(float[] currentPos) {

        double nowAngle = Math.atan2((currentPos[1] - centerPos[1]), (currentPos[0] - centerPos[0]));
        double sweepAngle = Math.toDegrees(nowAngle);
        currentAngle = sweepAngle;
        invalidate();
    }

    private boolean isDragged(float x, float y) {
        return reactRegion.contains(x, y);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = bitRes[0].getWidth() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = bitRes[0].getHeight() + getPaddingTop() + getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {        // 确认值
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) { // wrapcontent / matchparent
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    /**
     *  角度增加 -- 注意边界判断
     */
    public void addAngle() {
        if (Math.ceil(currentAngle) == 180){
            currentAngle = -179;
        }else {
            currentAngle++;
        }
        invalidate();
    }

    /**
     *  角度减少 -- 注意边界判断
     */
    public void subAngle() {
        if (Math.ceil(currentAngle)== -179){
            currentAngle = 180;
        }else {
            currentAngle--;
        }
        invalidate();
    }

    public double getCurrentAngle() {
        return -Math.toRadians(currentAngle);
    }
}
