package com.lichanghai.edgelen.foundation.girth;

import com.lichanghai.edgelen.foundation.math.MathUtils;
import com.lichanghai.edgelen.foundation.math.Point3;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lichanghai on 2018/1/21.
 */
@Deprecated
public class SplineGirth implements Girth {

    private Point3[] points;

    public SplineGirth(Point3[] points) {
        this.points = points;
    }

    public static PointCurve.Quadrant getQuadrant(Point3 from, Point3 to) {
        double angle = MathUtils.azimuthAngle(from.x, from.y, to.x, to.y);

        if (angle >= 45.0 && angle < 135.0) {
            return PointCurve.Quadrant.Right;
        } else if (angle >= 135.0 && angle < 225) {
            return PointCurve.Quadrant.Down;
        } else if (angle >= 225.0 && angle < 315.0) {
            return PointCurve.Quadrant.Left;
        } else {
            return PointCurve.Quadrant.UP;
        }
    }

    public static ArrayList<PointCurve> split2Curves(Point3[] pnts) {

        ArrayList<Point3> tempPnts = new ArrayList<Point3>(Arrays.asList(pnts));
        tempPnts.add(pnts[0]);

        pnts = tempPnts.toArray(new Point3[tempPnts.size()]);

        ArrayList<PointCurve> curves = new ArrayList<PointCurve>();

        ArrayList<Point3> currentPoints = new ArrayList<Point3>();

        currentPoints.add(pnts[0]);
        currentPoints.add(pnts[1]);

        PointCurve.Quadrant lastQuadrant = getQuadrant(pnts[0], pnts[1]);

        for (int i = 2; i < pnts.length; i++) {

            Point3 lastPnt = pnts[i - 1];
            Point3 currentPnt = pnts[i];

            PointCurve.Quadrant quadrant = getQuadrant(lastPnt, currentPnt);

            if (quadrant != lastQuadrant) {
                curves.add(new PointCurve(lastQuadrant, currentPoints.toArray(new Point3[currentPoints.size()])));

                currentPoints.clear();

                currentPoints.add(lastPnt);
                currentPoints.add(currentPnt);
                lastQuadrant = quadrant;
            } else {
                currentPoints.add(currentPnt);
            }
        }

        if (currentPoints.size() > 1) {
            curves.add(new PointCurve(lastQuadrant, currentPoints.toArray(new Point3[currentPoints.size()])));
        }

        return curves;

    }

    public static ArrayList<Point3[]> splitByX(Point3[] pnts) {

        ArrayList<Point3> tempPnts = new ArrayList<Point3>(Arrays.asList(pnts));
        tempPnts.add(pnts[0]);

        pnts = tempPnts.toArray(new Point3[tempPnts.size()]);

        ArrayList<Point3[]> lines = new ArrayList<Point3[]>();

        ArrayList<Point3> currentPoints = new ArrayList<Point3>();

        currentPoints.add(pnts[0]);
        currentPoints.add(pnts[1]);

        boolean toRight = pnts[1].x > pnts[0].x;

        for (int i = 2; i < pnts.length; i++) {

            Point3 lastPnt = pnts[i - 1];
            Point3 currentPnt = pnts[i];

            boolean right = currentPnt.x > lastPnt.x;

            if (toRight != right) {
                lines.add(currentPoints.toArray(new Point3[currentPoints.size()]));

                currentPoints.clear();

                currentPoints.add(lastPnt);
                currentPoints.add(currentPnt);
                toRight = right;
            } else {
                currentPoints.add(currentPnt);
            }
        }

        if (currentPoints.size() > 1) {
            lines.add(currentPoints.toArray(new Point3[currentPoints.size()]));
        }

        return lines;
    }

    @Override
    public double getGirth() {

        ArrayList<PointCurve> curves = split2Curves(this.points);

        double d = 0.0;
        for (PointCurve curve : curves) {

            Point3[] line = curve.getPoints();

            double[] xs = new double[line.length];
            double[] ys = new double[line.length];

            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            for (int i = 0; i < line.length; i++) {

                xs[i] = line[0].x;
                ys[i] = line[0].y;

                if (curve.getQuadrant() == PointCurve.Quadrant.Left || curve.getQuadrant() == PointCurve.Quadrant.Right) {
                    double t = xs[i];
                    xs[i] = ys[i];
                    ys[i] = t;
                }

                if (xs[i] > maxX)
                    maxX = xs[i];

                if (xs[i] < minX)
                    minX = xs[i];

            }

            Spline spline = new Spline(xs, ys, line.length);

            int[] xxs = new int[(int) (maxX - minX)];

            for (int i = 0; i < xxs.length; i++) {
                xxs[i] = i + (int) minX;
            }

            double[] yys = spline.s(xxs);

            for (int i = 0; i < xxs.length - 1; i++) {
                double p = (xxs[i] - xxs[i + 1]) * (xxs[i] - xxs[i + 1])
                        + (yys[i] - yys[i + 1]) * (yys[i] - yys[i + 1]);

                d += Math.sqrt(p);
            }
        }

        return d;

    }

    public double getGirth2() {

        ArrayList<Point3[]> lines = splitByX(this.points);

        double d = 0.0;
        for (Point3[] line : lines) {

            double[] xs = new double[line.length];
            double[] ys = new double[line.length];

            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            for (int i = 0; i < line.length; i++) {

                xs[i] = line[0].x;
                ys[i] = line[0].y;

                if (xs[i] > maxX)
                    maxX = xs[i];

                if (xs[i] < minX)
                    minX = xs[i];

            }

            Spline spline = new Spline(xs, ys, line.length);

            int[] xxs = new int[(int) (maxX - minX)];

            for (int i = 0; i < xxs.length; i++) {
                xxs[i] = i + (int) minX;
            }

            double[] yys = spline.s(xxs);

            for (int i = 0; i < xxs.length - 1; i++) {
                double p = (xxs[i] - xxs[i + 1]) * (xxs[i] - xxs[i + 1])
                        + (yys[i] - yys[i + 1]) * (yys[i] - yys[i + 1]);

                d += Math.sqrt(p);
            }

        }
        return d;
    }
}
