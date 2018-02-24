package com.lichanghai.edgelen.foundation.math.linefilter;

import com.lichanghai.edgelen.foundation.math.Point3;

/**
 * Created by lichanghai on 2018/1/19.
 */
public class GaussFilter extends Filter {

    private double[] weights;

    public GaussFilter(int radius) {
        this.weights = calc(radius);
    }

    private double[] calc(int radius) {

        int size = radius * 2 + 1;
        double[] data = new double[size];

        double sigma = radius / 3.0;
        double twoSigmaSquare = 2.0 * sigma * sigma;
        double sigmaRoot = Math.sqrt(twoSigmaSquare * Math.PI);
        double sum = 0.0;

        for (int i = -radius; i <= radius; i++) {
            double distance = i * i;
            int index = i + radius;
            data[index] = Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
            sum += data[index];
        }

        for (int i = 0; i < data.length; i++) {
            data[i] /= sum;
        }

        return data;
    }

    @Override
    public Point3[] filter(Point3[] pnts) {
        return filter(pnts, this.weights);
    }

}
