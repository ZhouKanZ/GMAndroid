package com.jms.cleanse.entity.uiTest;

/**
 * Created by zhoukan on 2018/3/20.
 *
 * @desc:
 */

public class RobotEntity {

    private String robotName;
    private boolean isSelected = false;

    public RobotEntity(String robotName) {
        this.robotName = robotName;
    }

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {

        this.robotName = robotName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
