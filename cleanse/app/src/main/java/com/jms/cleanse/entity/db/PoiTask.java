package com.jms.cleanse.entity.db;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

/**
 * Created by WangJun on 2018/4/10.
 */

@Entity
public class PoiTask {

    @Id
    long id;

    public String name;
    public ToMany<PoiPoint> poiPoints;

}
