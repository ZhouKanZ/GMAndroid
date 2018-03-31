package com.jms.cleanse.entity.robot;

import java.util.List;

/**
 * Created by LiuLy on 2017/5/18.
 */
public class SonarEntity {


    private List<List<Double>> sonarList;

    public List<List<Double>> getSonarList() {
        return sonarList;
    }

    public void setSonarList(List<List<Double>> sonarList) {
        this.sonarList = sonarList;
    }
}