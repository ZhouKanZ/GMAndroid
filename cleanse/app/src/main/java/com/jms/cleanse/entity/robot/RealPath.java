package com.jms.cleanse.entity.robot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuLy on 2017/5/24.
 */
public class RealPath {
    private List<List<Double>> realPathList = new ArrayList<>();

    public List<List<Double>> getRealPathList() {
        return realPathList;
    }

    public void setRealPathList(List<List<Double>> realPathList) {
        this.realPathList = realPathList;
    }
}
