package com.jms.cleanse.entity.robot;

import java.util.List;

/**
 * Created by LiuLy on 2017/10/19.
 */

public class LaserEntity {

    private List<Double> headList;
    private List<Double> distanceList;
    private double laser_pos_x;
    private double laser_pos_y;
    private double laser_pos_yaw;


    private double thisAngle;
    private double laserPointX;
    private double laserPointY;

    private String mac_address;

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public List<Double> getHeadList() {
        return headList;
    }

    public void setHeadList(List<Double> headList) {
        this.headList = headList;
    }

    public List<Double> getDistanceList() {
        return distanceList;
    }

    public void setDistanceList(List<Double> distanceList) {
        this.distanceList = distanceList;
    }

    public double getLaser_pos_x() {
        return laser_pos_x;
    }

    public void setLaser_pos_x(double laser_pos_x) {
        this.laser_pos_x = laser_pos_x;
    }

    public double getLaser_pos_y() {
        return laser_pos_y;
    }

    public void setLaser_pos_y(double laser_pos_y) {
        this.laser_pos_y = laser_pos_y;
    }

    public double getLaser_pos_yaw() {
        return laser_pos_yaw;
    }

    public void setLaser_pos_yaw(double laser_pos_yaw) {
        this.laser_pos_yaw = laser_pos_yaw;
    }

    public double getThisAngle() {
        return thisAngle;
    }

    public void setThisAngle(double thisAngle) {
        this.thisAngle = thisAngle;
    }

    public double getLaserPointX() {
        return laserPointX;
    }

    public void setLaserPointX(double laserPointX) {
        this.laserPointX = laserPointX;
    }

    public double getLaserPointY() {
        return laserPointY;
    }

    public void setLaserPointY(double laserPointY) {
        this.laserPointY = laserPointY;
    }
}
