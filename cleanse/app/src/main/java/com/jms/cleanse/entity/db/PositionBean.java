package com.jms.cleanse.entity.db;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class PositionBean {

    @Id
    long id;
    /**
     * x : 3.0
     * y : 0.2
     * yaw : 0.0
     */
    public double x;
    public double y;
    public double yaw;
}