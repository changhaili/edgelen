package com.lichanghai.edgelen.foundation.girth;

import java.util.ArrayList;

import com.lichanghai.edgelen.foundation.math.MathUtils;
import com.lichanghai.edgelen.foundation.math.Point3;

/**
 * Created by lichanghai on 2018/1/19.
 */
public class BcurveGirth implements Girth {

    private final static int SAMPLE_SIZE = 20;

    public final Point3[] points;

    public BcurveGirth(Point3[] points) {
        this.points = points;
    }

    private Point3 coord(int i, int i1, int i2, int i3, double u) {

        double u3 = u * u * u;
        double u2 = u * u;
        double a0 = -u3 + 3.0D * u2 - 3.0D * u + 1.0D;
        double a1 = 3.0D * u3 - 6.0D * u2 + 4.0D;
        double a2 = -3.0D * u3 + 3.0D * u2 + 3.0D * u + 1.0D;
        double a3 = u3;
        Point3 pi = this.points[i];
        Point3 pi1 = this.points[i1];
        Point3 pi2 = this.points[i2];
        Point3 pi3 = this.points[i3];
        return new Point3(((a0 * pi.x + a1 * pi1.x + a2 * pi2.x + a3 * pi3.x) / 6.0D),
                ((a0 * pi.y + a1 * pi1.y + a2 * pi2.y + a3 * pi3.y) / 6.0D), 1.0);
    }

    private Point3 coord(int i, double u) {
        return coord(i, i + 1, i + 2, i + 3, u);
    }

    public double getGirth() {

        if (this.points.length < 2)
            return 0;

        if (this.points.length == 2) {
            return MathUtils.getEuclideanDistance(this.points[0], this.points[1]);
        }

        int size = this.points.length;

        ArrayList<Point3> pnts = new ArrayList<Point3>();

        for (double u = 0.0D; u < 1.0D; u += 1.0D / SAMPLE_SIZE) {
            Point3 p = coord(0, 0, 0, 1, u);
            pnts.add(p);

        }

        for (double u = 0.0D; u < 1.0D; u += 1.0D / SAMPLE_SIZE) {
            Point3 p = coord(0, 0, 1, 2, u);
            pnts.add(p);
        }

        for (int i = 0; i < this.points.length - 3; i++) {
            for (double u = 0.0D; u < 1.0D; u += 1.0D / SAMPLE_SIZE) {
                Point3 p = coord(i, u);
                pnts.add(p);
            }
        }

        for (double u = 0.0D; u < 1.0D; u += 1.0D / SAMPLE_SIZE) {
            Point3 p = coord(size - 2, size - 1, size - 1, size - 1, u);
            pnts.add(p);
        }

        for (double u = 0.0D; u < 1.0D; u += 1.0D / SAMPLE_SIZE) {
            Point3 p = coord(size - 3, size - 2, size - 1, size - 1, u);
            pnts.add(p);
        }

        double d = 0.0;
        for (int i = 0; i < pnts.size() - 1; i++) {

            d += MathUtils.getEuclideanDistance(pnts.get(i), pnts.get(i + 1));
        }

        return d;

    }

}