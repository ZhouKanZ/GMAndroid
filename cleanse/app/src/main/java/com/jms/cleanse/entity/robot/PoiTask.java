package com.jms.cleanse.entity.robot;

import com.jms.cleanse.widget.mapview.CustomPOI;

import java.util.List;

/**
 * Created by WangJun on 2018/4/9.
 */

public class PoiTask {

    private String name;
    private List<CustomPOI> pois;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CustomPOI> getPois() {
        return pois;
    }

    public void setPois(List<CustomPOI> pois) {
        this.pois = pois;
    }
}
