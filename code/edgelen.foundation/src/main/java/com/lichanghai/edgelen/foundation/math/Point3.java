package com.lichanghai.edgelen.foundation.math;

/**
 * Created by lichanghai on 2018/1/9.
 */
public class Point3 {

    public double x;

    public double y;

    public double z;

    public Point3(){

    }

    public Point3(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 1.0;
    }

    public Point3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "x:" + x + ",y:" + y + ",z:" + z;
    }

    public Point2i toPoint2i() {
        return new Point2i((int) x, (int) y);
    }

    public Point3 copy() {
        return new Point3(x, y, z);
    }
}
