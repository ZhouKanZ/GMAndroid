package com.jms.cleanse.entity.file;

/**
 * Created by WangJun on 2018/4/16.
 */

public class Position {

    public double x,y,yaw;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;

        Position position = (Position) o;

        if (Double.compare(position.x, x) != 0) return false;
        if (Double.compare(position.y, y) != 0) return false;
        return Double.compare(position.yaw, yaw) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(yaw);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
