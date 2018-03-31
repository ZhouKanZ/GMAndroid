package com.jms.cleanse.entity.uiTest;

/**
 * Created by zhoukan on 2018/3/22.
 *
 * @desc:
 */

public class TaskEntity {

    private String taskName;
    private String location;
    private String createTime;
    private boolean isExecuting;

    public TaskEntity(String taskName, String location, String createTime, boolean isExecuting) {
        this.taskName = taskName;
        this.location = location;
        this.createTime = createTime;
        this.isExecuting = isExecuting;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isExecuting() {
        return isExecuting;
    }

    public void setExecuting(boolean executing) {
        isExecuting = executing;
    }
}
