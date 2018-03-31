package com.jms.cleanse.entity.robot;

/**
 * Created by LiuLy on 2017/10/19.
 */

public class RobotVelPose {
    private double vx;
    private double vy;
    private double vtheta;

    private double poseX;
    private double poseY;
    private double poseYaw;

    private String mac_address;

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getVtheta() {
        return vtheta;
    }

    public void setVtheta(double vtheta) {
        this.vtheta = vtheta;
    }

    public double getPoseX() {
        return poseX;
    }

    public void setPoseX(double poseX) {
        this.poseX = poseX;
    }

    public double getPoseY() {
        return poseY;
    }

    public void setPoseY(double poseY) {
        this.poseY = poseY;
    }

    public double getPoseYaw() {
        return poseYaw;
    }

    public void setPoseYaw(double poseYaw) {
        this.poseYaw = poseYaw;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }
}
