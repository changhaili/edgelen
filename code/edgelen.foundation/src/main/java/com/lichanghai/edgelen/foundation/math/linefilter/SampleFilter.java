package com.lichanghai.edgelen.foundation.math.linefilter;

import com.lichanghai.edgelen.foundation.math.Point3;

import java.util.ArrayList;

/**
 * Created by lichanghai on 2018/1/19.
 */
public class SampleFilter extends Filter {

    private int begin;

    private int step;

    public SampleFilter(int begin, int step) {
        this.begin = begin;
        this.step = step;
    }

    @Override
    public Point3[] filter(Point3[] pnts) {

        ArrayList<Point3> newPnts = new ArrayList<Point3>(pnts.length / step + 1);

        for (int i = begin; i < pnts.length + step; i += step) {
            int index = i < pnts.length ? i : i - pnts.length;

            if (index >= 0 && index < pnts.length) {
                newPnts.add(pnts[index]);
            }
        }

        return newPnts.toArray(new Point3[newPnts.size()]);
    }

}
