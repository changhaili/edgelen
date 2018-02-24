package com.lichanghai.edgelen.foundation.math.linefilter;

import com.lichanghai.edgelen.foundation.math.Point3;

/**
 * Created by lichanghai on 2018/1/19.
 */
public class AvgFilter extends Filter {

    private int radius;

    public AvgFilter(int radius) {
        this.radius = radius;
    }

    @Override
    public Point3[] filter(Point3[] pnts) {

        double x = 1.0 / radius;

        double[] weights = new double[radius];

        for (int i = 0; i < radius; i++) {
            weights[i] = x;
        }

        return filter(pnts, weights);

    }

}
