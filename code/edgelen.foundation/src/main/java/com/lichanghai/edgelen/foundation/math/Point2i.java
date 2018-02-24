package com.lichanghai.edgelen.foundation.math;

/**
 * Created by lichanghai on 2018/1/9.
 */
public class Point2i {

    public int x;

    public int y;

    public Point2i() {

    }

    public Point2i(int x, int y) {

        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        return x & y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Point2i)) {
            return false;
        }

        Point2i p = (Point2i) obj;

        return p.x == x && p.y == y;
    }

    public Point3 toPoint3() {
        return new Point3(x, y);
    }

}
