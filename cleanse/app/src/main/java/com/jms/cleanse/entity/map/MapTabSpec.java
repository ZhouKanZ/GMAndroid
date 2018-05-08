package com.jms.cleanse.entity.map;

/**
 * Created by WangJun on 2018/4/11.
 *
 * popupwindow 中使用的spec
 */

public class MapTabSpec {

    String mapName;
    byte[] data;

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
