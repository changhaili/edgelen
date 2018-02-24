package com.lichanghai.edgelen.foundation;

import com.lichanghai.edgelen.foundation.girth.GirthResult;
import com.lichanghai.edgelen.foundation.math.MathUtils;
import com.lichanghai.edgelen.foundation.math.Matrix3;
import com.lichanghai.edgelen.foundation.math.Point2i;
import com.lichanghai.edgelen.foundation.math.Point3;

/**
 * Created by lichanghai on 2018/1/13.
 */
public class EdgeCurve {

    private final Matrix3 coordinate;

    private final Point2i[] points;


    public EdgeCurve(Matrix3 coordinate, Point2i[] points) {

        this.coordinate = coordinate;
        this.points = points;
    }

    public Point3[] getTransformPoints() {

        Point3[] pnts = new Point3[this.points.length];

        for (int i = 0; i < pnts.length; i++) {
            pnts[i] = MathUtils.transform(this.coordinate, new Point3(points[i].x, points[i].y, 1.0));
        }

        return pnts;
    }

    public GirthResult getGirth() {

        Point3[] pnts = getTransformPoints();

        return MathUtils.calculateGirth(pnts);

    }


}
