package com.jms.cleanse.entity.file;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangJun on 2018/4/13.
 */

public class POITask {

    private String name;
    private List<POIPoint> poiPoints = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<POIPoint> getPoiPoints() {
        return poiPoints;
    }

    public void setPoiPoints(List<POIPoint> poiPoints) {
        this.poiPoints = poiPoints;
    }
}
