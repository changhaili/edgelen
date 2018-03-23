package com.lichanghai.edgelen.foundation.cluster.kmeans3;

import com.lichanghai.edgelen.foundation.math.Point3;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lichanghai on 2018/3/23.
 */
public class KMeansModel {

    private List<Point3> points = new ArrayList<>();

    //public Point3 minPoint = new Point3(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE);

    //public Point3 maxPoint = new Point3(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);

    private Point3 mean = new Point3(0, 0, 0);

    public List<Point3> getPoints() {
        return points;
    }

    private interface ElementGetter {

        double get(Point3 pnt);

    }

    private double[] sort(final ElementGetter elementGetter) {

        double[] vs = new double[points.size()];

        for (int i = 0; i < vs.length; i++) {
            vs[i] = elementGetter.get(points.get(i));
        }


        Arrays.sort(vs);

        return vs;
    }

    public double[] sortX() {

        return sort(new ElementGetter() {
            @Override
            public double get(Point3 pnt) {
                return pnt.x;
            }
        });
    }


    public double[] sortY() {

        return sort(new ElementGetter() {
            @Override
            public double get(Point3 pnt) {
                return pnt.y;
            }
        });
    }

    public double[] sortZ() {

        return sort(new ElementGetter() {
            @Override
            public double get(Point3 pnt) {
                return pnt.z;
            }
        });
    }

    public Point3 getMean() {
        return mean;
    }

    public void setMean(Point3 mean) {
        this.mean = mean;
    }
}
