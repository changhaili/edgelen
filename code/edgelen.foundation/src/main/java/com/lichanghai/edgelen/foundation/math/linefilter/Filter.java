package com.lichanghai.edgelen.foundation.math.linefilter;

import com.lichanghai.edgelen.foundation.math.Point2i;
import com.lichanghai.edgelen.foundation.math.Point3;

/**
 * Created by lichanghai on 2018/1/19.
 */
public abstract class Filter {

    public static Point3[] filter(Point3[] pnts, Filter... filters) {

        Point3[] ps = pnts;
        for (Filter filter : filters) {
            ps = filter.filter(ps);
        }

        return ps;

    }

    public static Point3[] filter(Point3[] pnts, double[] weights) {

        Point3[] newPoints = new Point3[pnts.length];

        for (int i = 0; i < pnts.length; i++) {

            double x = 0;
            double y = 0;
            for (int j = 0; j < weights.length; j++) {

                int index = i + (j - weights.length / 2);

                if (index < 0)
                    index = pnts.length + index;

                if (index >= pnts.length) {
                    index = index - pnts.length;
                }

                try {

                    x = x + weights[j] * pnts[index].x;
                    y = y + weights[j] * pnts[index].y;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            newPoints[i] = new Point3(x, y, 0.0);

        }
        return newPoints;
    }

    public abstract Point3[] filter(Point3[] pnts);

    public Point3[] filter(Point2i[] pnts) {

        Point3[] newPnts = new Point3[pnts.length];
        for (int i = 0; i < pnts.length; i++) {
            newPnts[i] = pnts[i].toPoint3();
        }

        return this.filter(newPnts);

    }

}
