package com.jms.cleanse.entity.robot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuLy on 2017/5/23.
 */
public class LocalPath {

    private List<List<Double>> localPathList = new ArrayList<>();

    public List<List<Double>> getLocalPathList() {
        return localPathList;
    }

    public void setLocalPathList(List<List<Double>> localPathList) {
        this.localPathList = localPathList;
    }
}
