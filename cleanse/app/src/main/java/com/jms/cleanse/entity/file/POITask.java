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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof POITask)) return false;

        POITask poiTask = (POITask) o;

        if (getName() != null ? !getName().equals(poiTask.getName()) : poiTask.getName() != null)
            return false;
        return getPoiPoints() != null ? getPoiPoints().equals(poiTask.getPoiPoints()) : poiTask.getPoiPoints() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getPoiPoints() != null ? getPoiPoints().hashCode() : 0);
        return result;
    }
}
