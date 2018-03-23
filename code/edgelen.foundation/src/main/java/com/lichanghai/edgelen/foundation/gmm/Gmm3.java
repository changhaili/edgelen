package com.lichanghai.edgelen.foundation.gmm;

import com.lichanghai.edgelen.foundation.math.Matrix3;
import com.lichanghai.edgelen.foundation.math.Point3;

/**
 * Created by lichanghai on 2018/3/19.
 *
 * 需要先处理线性相关问题
 *
 * 三元高斯混合模型，意图更好的解决颜色划分的问题，即可以将颜色分解为RGB三元自变量
 * 实际使用中颜色存在线性相关
 *
 */
public class Gmm3 {

    public static class Model {

        public Point3 u;

        public Matrix3 sd;

        public Matrix3 isd;

        public double det;

    }

    private Point3[] points = null;

    private Point3 minPoint = null;

    private Point3 maxPoint = null;

    public Gmm3(Point3[] points) {

        this.points = points;

        minPoint = new Point3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        maxPoint = new Point3(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);

        for (Point3 pnt : points) {

            if (pnt.x > maxPoint.x) maxPoint.x = pnt.x;
            if (pnt.y > maxPoint.y) maxPoint.y = pnt.y;
            if (pnt.z > maxPoint.z) maxPoint.z = pnt.z;

            if (pnt.x < minPoint.x) minPoint.x = pnt.x;
            if (pnt.y < minPoint.y) minPoint.y = pnt.y;
            if (pnt.z < minPoint.z) minPoint.z = pnt.z;
        }

    }

    // 1/ ((2* pi) ^1.5 * | delta|^0.5) * exp( -1/2 * XT * delta-1 * X)
    private double getProbability(Model model, Point3 pnt) {

        Point3 p = new Point3(pnt.x - model.u.x, pnt.y - model.u.y, pnt.z - model.u.z);

        Matrix3 i = model.isd;

        Point3 p2 = new Point3(p.x * i.v00 + p.y * i.v10 + p.z * i.v20,
                p.x * i.v01 + p.y * i.v11 + p.z * i.v21,
                p.x * i.v02 + p.y * i.v12 + p.z * i.v22);

        double xx = p2.x * p.x + p2.y * p.y + p2.z * p.z;

        double exp = Math.exp(-xx / 2);

        //double det = model.sd.det();
        double divisor = (Math.PI * Math.PI * Math.PI * 8 * model.det);//Math.pow(Math.PI * 2, 1.5) * Math.sqrt(det);

        double v = exp / Math.sqrt(divisor);

        if (v > 1.0) {
            "".toCharArray();
        }

        return v;

    }

    private boolean willStop(Model[] models, Point3[] us) {

        double persion = 0.001;

        for (int i = 0; i < models.length; i++) {

            if (Math.abs(models[i].u.x - us[i].x) > persion) return false;
            if (Math.abs(models[i].u.y - us[i].y) > persion) return false;
            if (Math.abs(models[i].u.z - us[i].z) > persion) return false;
        }

        return true;
    }


    public Model[] cluster(int expectedCount) {

        // 归一化

        Point3[] newPnts = new Point3[points.length];

        double ux = (maxPoint.x + minPoint.x) / 2;
        double uy = (maxPoint.y + minPoint.y) / 2;
        double uz = (maxPoint.z + minPoint.z) / 2;

        for (int i = 0; i < newPnts.length; i++) {

            double x = (points[i].x - ux) / (maxPoint.x - minPoint.x);
            double y = (points[i].y - uy) / (maxPoint.y - minPoint.y);
            double z = (points[i].z - uz) / (maxPoint.z - minPoint.z);

            newPnts[i] = new Point3(x, y, z);
        }

        // 初始化

        Model[] models = new Model[expectedCount];

        for (int i = 0; i < expectedCount; i++) {

            models[i] = new Model();
            models[i].sd = Matrix3.I.copy();
            models[i].isd = Matrix3.I.copy();
            models[i].det = 1;

            double v = -1.0 + 2.0 / (expectedCount + 1) * (i + 1);

            models[i].u = new Point3(v, v, v);
        }

        // 迭代
        double[][] ps = new double[newPnts.length][expectedCount];

        Point3[] us = new Point3[expectedCount];

        int times = 0;

        while (times++ < 10000) {

            for (int i = 0; i < expectedCount; i++) {

                System.out.println(models[i].u + ",\n" + models[i].sd);
            }

            for (int i = 0; i < us.length; i++) us[i] = new Point3(0, 0, 0);

            // 计算概率
            for (int pntIndex = 0; pntIndex < newPnts.length; pntIndex++) {
                ps[pntIndex] = new double[expectedCount + 1];

                Point3 pnt = newPnts[pntIndex];

                double total = 0;
                for (int i = 0; i < expectedCount; i++) {
                    ps[pntIndex][i] = getProbability(models[i], pnt);
                    total += ps[pntIndex][i];
                }

                for (int i = 0; i < expectedCount; i++) {
                    ps[pntIndex][i] = ps[pntIndex][i] / total;
                }
            }

            // 计算概率和均值
            for (int pntIndex = 0; pntIndex < newPnts.length; pntIndex++) {
                Point3 pnt = newPnts[pntIndex];

                for (int i = 0; i < expectedCount; i++) {

                    double p = ps[pntIndex][i];
                    us[i].x += p * (pnt.x - models[i].u.x);
                    us[i].y += p * (pnt.y - models[i].u.y);
                    us[i].z += p * (pnt.z - models[i].u.z);
                }
            }

            for (int i = 0; i < expectedCount; i++) {

                us[i].x = us[i].x / newPnts.length + models[i].u.x;
                us[i].y = us[i].y / newPnts.length + models[i].u.y;
                us[i].z = us[i].z / newPnts.length + models[i].u.z;
            }

            if (willStop(models, us))
                break;

            // 计算方差
            Matrix3[] sds = new Matrix3[expectedCount];
            for (int i = 0; i < us.length; i++) sds[i] = new Matrix3();

            for (int pntIndex = 0; pntIndex < newPnts.length; pntIndex++) {
                Point3 pnt = newPnts[pntIndex];

                for (int i = 0; i < expectedCount; i++) {

                    double p = ps[pntIndex][i];

                    int length = newPnts.length;

                    double x = p * (pnt.x - us[i].x);
                    double y = p * (pnt.y - us[i].y);
                    double z = p * (pnt.z - us[i].z);

                    sds[i].v00 += x * x / length;
                    sds[i].v01 += x * y / length;
                    sds[i].v02 += x * z / length;

                    sds[i].v10 += y * x / length;
                    sds[i].v11 += y * y / length;
                    sds[i].v12 += y * z / length;

                    sds[i].v20 += z * x / length;
                    sds[i].v21 += z * y / length;
                    sds[i].v22 += z * z / length;
                }
            }

            for (int i = 0; i < expectedCount; i++) {

                models[i].u.x = us[i].x;
                models[i].u.y = us[i].y;
                models[i].u.z = us[i].z;

                models[i].sd = sds[i];
                models[i].isd = sds[i].inverse();
                models[i].det = sds[i].det();
            }
        }

        // 计算原颜色
        Point3[] centers = new Point3[expectedCount];

        for (int i = 0; i < expectedCount; i++) {

            double x = models[i].u.x * (maxPoint.x - minPoint.x) / 2 + ux;
            double y = models[i].u.y * (maxPoint.y - minPoint.y) / 2 + uy;
            double z = models[i].u.z * (maxPoint.z - minPoint.z) / 2 + uz;

            if (x < 0) x = 0;
            if (y < 0) y = 0;
            if (z < 0) z = 0;

            if (x > 255) x = 255;
            if (y > 255) y = 255;
            if (z > 255) z = 255;

            centers[i] = new Point3(x, y, z);
        }

        return models;

    }


}
