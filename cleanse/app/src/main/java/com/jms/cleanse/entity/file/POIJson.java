package com.jms.cleanse.entity.file;

import com.jms.cleanse.widget.mapview.CustomPOI;

import java.util.List;
import java.util.Map;

import robot.boocax.com.sdkmodule.entity.entity_file.poi.sdk.Charge_points_Entity;

/**
 * Created by WangJun on 2018/4/10.
 */

public class POIJson {

    private String version;
    private String encoding;
    private List<CustomPOI> poi_info;
    private List<Charge_points_Entity> charge_points_info;
    private Map<String, List<String>> groups;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public List<CustomPOI> getPoi_info() {
        return poi_info;
    }

    public void setPoi_info(List<CustomPOI> poi_info) {
        this.poi_info = poi_info;
    }

    public List<Charge_points_Entity> getCharge_points_info() {
        return charge_points_info;
    }

    public void setCharge_points_info(List<Charge_points_Entity> charge_points_info) {
        this.charge_points_info = charge_points_info;
    }

    public Map<String, List<String>> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, List<String>> groups) {
        this.groups = groups;
    }
}
