package com.jms.cleanse.widget.mapview;

import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;

import com.jms.cleanse.R;

/**
 * Created by zhoukan on 2018/3/31.
 *
 * @desc:
 */

public class POIConfig {

    // 消毒点的资源文件
    public static @IdRes int cleanseRes = R.drawable.point_cleanse;
    // 非消毒点的资源文件
    public static @IdRes int unCleanseRes = R.drawable.point_default;
    // 起点
    public static @IdRes int pathStartRes = R.drawable.path_start;
    // 终点
    public static @IdRes int pathEndRes = R.drawable.path_end;
    // 消毒路段颜色
    public static @ColorInt int pathColorCleanse = R.color.colorPathCleanse;
    // 非消毒路段颜色
    public static @ColorInt int pathColorUnCleanse = R.color.colorPathUnCleanse;
    // 消毒路段的px
    public static int width = 10;

    public static @IdRes int robotPosRes = R.drawable.robot_position;
}
