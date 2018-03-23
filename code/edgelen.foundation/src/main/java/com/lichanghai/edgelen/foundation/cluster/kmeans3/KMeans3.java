package com.lichanghai.edgelen.foundation.cluster.kmeans3;

import com.lichanghai.edgelen.foundation.math.Point3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lichanghai on 2018/3/22.
 */
public class KMeans3 {

    private Point3[] points = null;


    public KMeans3(Point3[] points) {

        this.points = points;
    }

    private boolean checkStopLoop(Point3[] means, KMeansModel[] models2) {

        double precision = 0.0001;

        for (int i = 0; i < means.length; i++) {

            if (Math.abs(means[i].x - models2[i].getMean().x) > precision) return false;
            if (Math.abs(means[i].y - models2[i].getMean().y) > precision) return false;
            if (Math.abs(means[i].z - models2[i].getMean().z) > precision) return false;
        }

        return true;
    }


    public KMeansModel[] cluster(int expectedCount) {

        Point3[] newPnts = new Point3[points.length];

        Point3 minPoint = new Point3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        Point3 maxPoint = new Point3(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);

        for (Point3 pnt : points) {

            if (pnt.x > maxPoint.x) maxPoint.x = pnt.x;
            if (pnt.y > maxPoint.y) maxPoint.y = pnt.y;
            if (pnt.z > maxPoint.z) maxPoint.z = pnt.z;

            if (pnt.x < minPoint.x) minPoint.x = pnt.x;
            if (pnt.y < minPoint.y) minPoint.y = pnt.y;
            if (pnt.z < minPoint.z) minPoint.z = pnt.z;
        }

        double ux = (maxPoint.x + minPoint.x) / 2;
        double uy = (maxPoint.y + minPoint.y) / 2;
        double uz = (maxPoint.z + minPoint.z) / 2;

        for (int i = 0; i < newPnts.length; i++) {

            double x = (points[i].x - ux) * 2 / (maxPoint.x - minPoint.x);
            double y = (points[i].y - uy) * 2 / (maxPoint.y - minPoint.y);
            double z = (points[i].z - uz) * 2 / (maxPoint.z - minPoint.z);

            newPnts[i] = new Point3(x, y, z);
        }

        // 初始化
        KMeansModel[] models = new KMeansModel[expectedCount];

        for (int i = 0; i < expectedCount; i++) {

            double v = -1.0 + 2.0 / (expectedCount + 1) * (i + 1);

            models[i] = new KMeansModel();
            models[i].setMean(new Point3(v, v, v));
        }

        int times = 0;

        while (times++ < 10000) {

            Point3[] centers = new Point3[expectedCount];

            for (int i = 0; i < expectedCount; i++) {
                models[i].getPoints().clear();
                centers[i] = new Point3();
            }


            for (int pntIndex = 0; pntIndex < newPnts.length; pntIndex++) {

                Point3 pnt = newPnts[pntIndex];

                int lastIndex = -1;
                double lastDist = Double.MAX_VALUE;

                for (int i = 0; i < expectedCount; i++) {

                    Point3 mean = models[i].getMean();

                    double d = (mean.x - pnt.x) * (mean.x - pnt.x) +
                            (mean.y - pnt.y) * (mean.y - pnt.z) +
                            (mean.z - pnt.z) * (mean.z - pnt.z);

                    if (d < lastDist) {
                        lastDist = d;
                        lastIndex = i;
                    }
                }

                models[lastIndex].getPoints().add(pnt);

                centers[lastIndex].x += pnt.x;
                centers[lastIndex].y += pnt.y;
                centers[lastIndex].z += pnt.z;

            }

            for (int i = 0; i < expectedCount; i++) {

                int size = models[i].getPoints().size();

                centers[i].x /= size;
                centers[i].y /= size;
                centers[i].z /= size;
            }

            if (checkStopLoop(centers, models)) break;

            for (int i = 0; i < expectedCount; i++) {

                models[i].setMean(centers[i]);
            }
        }

        Point3[] centers = new Point3[expectedCount];

        for (int i = 0; i < expectedCount; i++) {

            for (int j = 0, size = models[i].getPoints().size(); j < size; j++) {

                Point3 pnt = models[i].getPoints().get(j);

                pnt.x = pnt.x * (maxPoint.x - minPoint.x) / 2 + ux;
                pnt.y = pnt.y * (maxPoint.y - minPoint.y) / 2 + uy;
                pnt.z = pnt.z * (maxPoint.z - minPoint.z) / 2 + uz;

            }

            double x = models[i].getMean().x * (maxPoint.x - minPoint.x) / 2 + ux;
            double y = models[i].getMean().y * (maxPoint.y - minPoint.y) / 2 + uy;
            double z = models[i].getMean().z * (maxPoint.z - minPoint.z) / 2 + uz;

            models[i].setMean(new Point3(x, y, z));
        }

        return models;

    }
}
