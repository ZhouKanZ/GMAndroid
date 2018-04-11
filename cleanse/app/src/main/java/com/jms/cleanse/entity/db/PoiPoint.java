package com.jms.cleanse.entity.db;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Created by WangJun on 2018/4/10.
 */

@Entity
public class PoiPoint {

    @Id
    long id;

    /**
     * state : false
     * name : p2
     * position : {"x":3,"y":0.2,"yaw":0}
     */

    public boolean state;
    public String name;
    public ToOne<PositionBean> position;

}
