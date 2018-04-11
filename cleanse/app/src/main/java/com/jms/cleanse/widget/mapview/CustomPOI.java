package com.jms.cleanse.widget.mapview;

import robot.boocax.com.sdkmodule.entity.entity_file.poi.sdk.POI_points_entity;

/**
 * Created by WangJun on 2018/3/27.
 */

public class CustomPOI extends POI_points_entity {

    // 表示上位机开关状态
    private boolean state;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {

        this.state = state;
    }
}
