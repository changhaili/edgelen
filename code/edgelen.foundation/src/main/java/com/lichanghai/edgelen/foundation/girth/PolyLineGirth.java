package com.lichanghai.edgelen.foundation.girth;

import com.lichanghai.edgelen.foundation.math.Point3;


/**
 * Created by lichanghai on 2018/1/21.
 */
@Deprecated
public class PolyLineGirth implements Girth {

    private Point3[] points;

    public PolyLineGirth(Point3[] points) {
        this.points = points;

    }

    public double getGirth() {

        double d = 0;
        for (int i = 0; i < points.length; i++) {

            Point3 p0 = this.points[i];
            Point3 p1 = this.points[i + 1 >= this.points.length ? 0 : i + 1];

            double p = (p0.x - p1.x) * (p0.x - p1.x) + (p0.y - p1.y) * (p0.y - p1.y);

            d += Math.sqrt(p);

        }

        return d;

    }

}
