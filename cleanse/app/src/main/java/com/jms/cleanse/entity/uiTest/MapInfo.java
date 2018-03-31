package com.jms.cleanse.entity.uiTest;

/**
 * Created by zhoukan on 2018/3/22.
 *
 * @desc: 测试UI的类
 */

public class MapInfo {

    private int resId;
    private String name;

    public MapInfo(int resId, String name) {
        this.resId = resId;
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
