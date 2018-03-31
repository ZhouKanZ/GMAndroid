package com.jms.cleanse.entity.robot;

/**
 * Created by LiuLy on 2017/5/18.
 */
public class RobotEntity {
    public static final float R = 8f;
    public static final float tRange = 8f;


    private int id;
    private String name;
    private String mac_address;
    private String ip_address;
    private Stat stat;
    private Status status;
    private Pose pose;
    private Vel vel;
    private String rosVersion;

    public String getRosVersion() {
        return rosVersion;
    }

    public void setRosVersion(String rosVersion) {
        this.rosVersion = rosVersion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Pose getPose() {
        return pose;
    }

    public void setPose(Pose pose) {
        this.pose = pose;
    }

    public Vel getVel() {
        return vel;
    }

    public void setVel(Vel vel) {
        this.vel = vel;
    }

    @Override
    public String toString() {
        return "RobotEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mac_address='" + mac_address + '\'' +
                ", ip_address='" + ip_address + '\'' +
                ", stat=" + stat +
                ", status=" + status +
                ", pose=" + pose +
                ", vel=" + vel +
                ", rosVersion='" + rosVersion + '\'' +
                '}';
    }
}
