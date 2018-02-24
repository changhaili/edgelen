package com.lichanghai.edgelen.foundation.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lichanghai on 2018/2/10.
 *
 * 一元线性回归
 *
 */
public class UnaryLR {

    /**
     * SUM(x)
     */
    private double sumX;

    /**
     * SUM(y)
     */
    private double sumY;

    /**
     * SUM(x*x)
     */
    private double sumXX;

    /**
     * SUM(x*y)
     */
    private double sumXY;

    /**
     * SUM(y*y)
     */
    private double sumYY;

    /**
     * SUM(f(x) -y)
     */
    private double sumDeltaY;

    /**
     * SUM(f(x) -y) ^2)
     */
    private double sumDeltaY2;

    /**
     * ERROR
     */
    private double sse;

    private double sst;

    private double E;

    private List<Point3> points;


    private int xMin, xMax, yMin, yMax;

    /**
     * y=kx +b
     */
    private double b;

    /**
     * y=kx +b
     */
    private double k;

    private boolean coefsValid;

    public UnaryLR() {
        xMax = 0;
        yMax = 0;

        points = new ArrayList<>();
    }

    public UnaryLR(Point3 points[]) {

        this.points = new ArrayList<>();
        for (int i = 0; i < points.length; ++i) {
            addPoint(points[i]);
        }
    }

    public int getPointCount() {
        return this.points.size();
    }

    public double getB() {
        validateCoefficients();
        return b;
    }


    public double getK() {
        validateCoefficients();
        return k;
    }


    /**
     * 添加新的点
     */
    public void addPoint(Point3 point) {

        sumX += point.x;
        sumY += point.y;
        sumXX += point.x * point.x;
        sumXY += point.x * point.y;
        sumYY += point.y * point.y;

        if (point.x > xMax) {
            xMax = (int) point.x;
        }
        if (point.y > yMax) {
            yMax = (int) point.y;
        }

        points.add(point);

        coefsValid = false;
    }

    public double calculateY(double x) {
        if (getPointCount() < 2)
            return Double.NaN;

        validateCoefficients();
        return b + k * x;
    }

    public void reset() {
        points.clear();
        sumX = sumY = sumXX = sumXY = 0;
        coefsValid = false;
    }

    private void validateCoefficients() {
        if (coefsValid)
            return;

        int pointCount = getPointCount();
        if (pointCount >= 2) {
            double xBar =  sumX / pointCount;
            double yBar =  sumY / pointCount;

            k =  ((pointCount * sumXY - sumX * sumY) / (pointCount * sumXX - sumX
                    * sumX));
            b = (yBar - k * xBar);
        } else {
            b = k = Double.NaN;
        }

        coefsValid = true;
    }

    /**
     * 返回误差
     */
    public double getR() {

        for (int i = 0, pointCount = getPointCount(); i < pointCount - 1; i++) {
            double Yi = points.get(i).y;
            double Y = calculateY(points.get(i).x);
            double deltaY = Yi - Y;
            double deltaY2 = deltaY * deltaY;

            sumDeltaY2 += deltaY2;
        }

        sst = sumYY - (sumY * sumY) / getPointCount();
        E = 1 - sumDeltaY2 / sst;

        return MathUtils.round(E, 4);
    }


}
