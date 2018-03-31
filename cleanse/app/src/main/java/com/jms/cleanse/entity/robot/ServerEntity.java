package com.jms.cleanse.entity.robot;

/**
 * Created by WangJun on 2018/3/21.
 */

public class ServerEntity {

    private String serverName;
    private String serverIp;
    private String lock;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

}
