package com.jms.cleanse.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import java.util.List;

import robot.boocax.com.sdkmodule.action_map.ChangeMatrix;
import robot.boocax.com.sdkmodule.entity.entity_file.poi.sdk.AllPOIEntity;
import robot.boocax.com.sdkmodule.entity.entity_file.poi.sdk.CommandPOI;
import robot.boocax.com.sdkmodule.entity.entity_file.poi.sdk.POI_points_entity;
import robot.boocax.com.sdkmodule.surface.surfaceview.SurfaceViewPOI;
import robot.boocax.com.sdkmodule.utils.coordinateutils.coordinateTranslate.PhytoBmp;

/**
 * Created by zhoukan on 2018/3/28.
 *
 * @desc:
 */

public class JMSurfaceViewPOI extends SurfaceViewPOI {

    private static Paint paint;
    private static boolean mDrawFlag = false;

    public JMSurfaceViewPOI(Context context) {
        this(context, null);
    }

    public JMSurfaceViewPOI(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JMSurfaceViewPOI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        mDrawFlag = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        mDrawFlag = false;
    }

    @Override
    public SurfaceHolder getHolder() {
        return super.getHolder();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
    }


    /**
     * 绘制
     */
    public static synchronized void draw() {
        if (mDrawFlag) {
            try {
                mCanvas = mHolder.lockCanvas();
                if (mCanvas != null) {
                    mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    ChangeMatrix.matrixWithCanvas(mCanvas);
//                    if (CommandPOI.poiName != null) {
//                        List<Float> result_double = PhytoBmp.getResult_double(((POI_points_entity) AllPOIEntity.poiMap.get(CommandPOI.poiName)).position.x, ((POI_points_entity) AllPOIEntity.poiMap.get(CommandPOI.poiName)).position.y);
//                        mCanvas.drawCircle(((Float) result_double.get(0)).floatValue(), ((Float) result_double.get(1)).floatValue(), 8.0F, paint);
//                        RectF dst = new RectF(((Float) result_double.get(0)).floatValue() - 8.0F, ((Float) result_double.get(1)).floatValue() - 23.0F, ((Float) result_double.get(0)).floatValue() + 8.0F, ((Float) result_double.get(1)).floatValue());
//                        mCanvas.drawBitmap(bitmap, (Rect) null, dst, paint);
//                    }
                    mCanvas.drawPoint(100,100,paint);
                }
            } catch (Exception var5) {
                var5.printStackTrace();
            } finally {
                if (mCanvas != null) {
                    mHolder.unlockCanvasAndPost(mCanvas);
                }
            }
        }
    }
}
