package com.jms.cleanse.entity.file;

import robot.boocax.com.sdkmodule.entity.entity_file.poi.sdk.Position;

/**
 * Created by WangJun on 2018/4/13.
 */

public class POIPoint {

    private String name;
    private boolean state;
    private Position position;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
