package com.jms.cleanse.entity.uiTest;

import java.util.List;

/**
 * Created by zhoukan on 2018/3/20.
 *
 * @desc: 服务器基本信息  名称  IP地址
 */

public class ServerEntity {

    private String server_address;
    private String ip;
    private List<RobotEntity> robotEntityList;

    // item是否展开 默认不展开，且只有一个可以展开
    private boolean itemIsExpand = false;

    public ServerEntity(String server_address, String ip) {
        this.server_address = server_address;
        this.ip = ip;
    }

    public String getServer_address() {
        return server_address;
    }

    public void setServer_address(String server_address) {
        this.server_address = server_address;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<RobotEntity> getRobotEntityList() {
        return robotEntityList;
    }

    public void setRobotEntityList(List<RobotEntity> robotEntityList) {
        this.robotEntityList = robotEntityList;
    }

    public boolean isItemIsExpand() {
        return itemIsExpand;
    }

    public void setItemIsExpand(boolean itemIsExpand) {
        this.itemIsExpand = itemIsExpand;
    }
}
