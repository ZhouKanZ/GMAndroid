package com.jms.cleanse.util;

import android.content.Context;

import com.jms.cleanse.config.RobotConfig;

/**
 * Created by zhoukan on 2018/3/26.
 *
 * @desc:
 */

public class DisplayUtil {

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    /**
     * 将dip或dp值转换为px值，保证尺寸不变
     * @param context
     * @param dipValue
     * @return
     */
    public static float dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return  dipValue * scale + 0.5f;
    }


    /**
     * 将px值转换为sp值，保证文字大小不变
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public static double[] getAndroidCoordinate(double x,double y,int maxX, int maxY){
        double ox = x / RobotConfig.MAP_SCALE;
        double oy = y / RobotConfig.MAP_SCALE;
        // 转换成Android坐标系
        double ax = maxX / 2 + ox;
        double ay = maxY / 2 - oy;
        return new double[]{ax,ay};
    }

}
