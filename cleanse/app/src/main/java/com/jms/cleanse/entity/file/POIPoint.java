package com.jms.cleanse.entity.file;


/**
 * Created by WangJun on 2018/4/13.
 */

public class POIPoint {

    private String name;
    private boolean state;
    private Position position;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof POIPoint)) return false;

        POIPoint poiPoint = (POIPoint) o;

        if (isState() != poiPoint.isState()) return false;
        if (getName() != null ? !getName().equals(poiPoint.getName()) : poiPoint.getName() != null)
            return false;
        return getPosition() != null ? getPosition().equals(poiPoint.getPosition()) : poiPoint.getPosition() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (isState() ? 1 : 0);
        result = 31 * result + (getPosition() != null ? getPosition().hashCode() : 0);
        return result;
    }
}
