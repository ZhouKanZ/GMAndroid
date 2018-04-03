package com.jms.cleanse.widget.mapview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

/**
 * Created by zhoukan on 2018/3/30.
 *
 * @desc
 */

public class ScaleUtils {

    public enum ScaleType{
        CENTER_INSIDE
    }

    /**
     *
     * @param type     缩放类型
     * @param bitmap   原始图片
     * @param w        容器宽
     * @param h        容器高
     * @return         返回缩放比  - bitmap 同比缩放，缩放至任一
     */
    public static float getScaleRatio(ScaleType type,Bitmap bitmap,int w,int h){
        // 缩放大小
        switch (type){
            case CENTER_INSIDE:
                int originW = bitmap.getWidth();
                int originH = bitmap.getHeight();
                float wRatio = (float) ((w * 1.0 )/ originW);
                float hRatio = (float) ((h * 1.0 )/ originH);
                Log.d("tag", "getScaleRatio: w" + wRatio+"hr"+hRatio + "view W:"+w+",view H:"+h);
                return Math.min(wRatio,hRatio);
        }
        return 0;
    }

    /**
     *
     * @param bitmap 原始的bitmap
     * @param ratio  缩放比例
     * @return
     */
    public static Bitmap getScaleBitmap(Bitmap bitmap , float ratio){

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        matrix.postScale(ratio,ratio);
        // "recreate a new bitmap"
        Bitmap bm = Bitmap.createBitmap(bitmap,0,0,w,h,matrix,false);
        return bm;
    }

}
