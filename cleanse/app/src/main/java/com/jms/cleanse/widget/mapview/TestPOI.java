package com.jms.cleanse.widget.mapview;

import android.graphics.RectF;

import com.jms.cleanse.config.RobotConfig;
import com.jms.cleanse.util.DisplayUtil;

/**
 * Created by zhoukan on 2018/3/31.
 *
 * @desc:
 */

public class TestPOI {

    private double x;
    private double y;
    private double yaw;
    // 是否为消毒点
    private boolean isCleanse = false;
    private RectF rect;

    private double ax;
    private double ay;

    public TestPOI(double x, double y,boolean isCleanse) {
        this.x = x;
        this.y = y;
        this.isCleanse = isCleanse;
        rect = new RectF();
    }


    /**
     * @param x           物理坐标
     * @param y
     * @param maxX        像素坐标系最大值
     * @param maxY
     * @param pointRadius 点的半径   （ps实际为矩形，但都为正方形，故用radius来表示）
     * @return [点的矩形区域]
     */
    public RectF getRect(double x, double y, int maxX, int maxY, int pointRadius) {
        // 转换成中心位置的像素坐标
        double[] androidCoor = getAndroidCoordinate(maxX,maxY);
        double ax = androidCoor[0];
        double ay = androidCoor[1];
        rect.left = (float) (ax - pointRadius);
        rect.top = (float) (ay - pointRadius);
        rect.right = (float) (ax + pointRadius );
        rect.bottom = (float) (ay + pointRadius );
        return this.rect;
    }

    /**
     *
     * @param maxX
     * @param maxY
     * @return 返回Android的坐标
     */
    public double[] getAndroidCoordinate(int maxX, int maxY){
        double[] position = DisplayUtil.getAndroidCoordinate(this.x,this.y,maxX,maxY);
        this.ax = position[0];
        this.ay = position[1];
        return position;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public boolean isCleanse() {
        return isCleanse;
    }

    public void setCleanse(boolean cleanse) {
        isCleanse = cleanse;
    }

    public double getAx() {
        return ax;
    }

    public double getAy() {
        return ay;
    }
}
