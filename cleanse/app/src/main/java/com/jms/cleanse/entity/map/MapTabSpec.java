package com.jms.cleanse.entity.map;

import android.graphics.Bitmap;

/**
 * Created by WangJun on 2018/4/11.
 *
 * popupwindow 中使用的spec
 */

public class MapTabSpec {

    String mapName;
    Bitmap map;

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Bitmap getMap() {
        return map;
    }

    public void setMap(Bitmap map) {
        this.map = map;
    }
}
