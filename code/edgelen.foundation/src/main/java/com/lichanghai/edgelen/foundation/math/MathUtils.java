package com.lichanghai.edgelen.foundation.math;

import com.lichanghai.edgelen.foundation.pixelholder.AbstractPixelHolder;
import com.lichanghai.edgelen.foundation.pixelholder.IndexPixelHolder;
import com.lichanghai.edgelen.foundation.girth.BcurveGirth;
import com.lichanghai.edgelen.foundation.girth.Girth;
import com.lichanghai.edgelen.foundation.girth.GirthResult;
import com.lichanghai.edgelen.foundation.math.linefilter.AvgFilter;
import com.lichanghai.edgelen.foundation.math.linefilter.Filter;
import com.lichanghai.edgelen.foundation.math.linefilter.SampleFilter;
import com.lichanghai.edgelen.foundation.utils.IntList;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lichanghai on 2018/1/9.
 */
public class MathUtils {

    /**
     * 四舍五入
     */
    public static double round(double v, int scale) {

        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 获取 点到线的距离
     *
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x
     * @param y
     * @return
     */
    public static double getPoint2LineLength(double x0, double y0, double x1, double y1, double x, double y) {

        double space = 0;
        double a, b, c;
        a = getEuclideanDistance(x0, y0, x1, y1);
        b = getEuclideanDistance(x0, y0, x, y);
        c = getEuclideanDistance(x1, y1, x, y);
        if (c <= 0.000001 || b <= 0.000001) {
            space = 0;
            return space;
        }
        if (a <= 0.000001) {
            space = b;
            return space;
        }
        if (c * c >= a * a + b * b) {
            space = b;
            return space;
        }
        if (b * b >= a * a + c * c) {
            space = c;
            return space;
        }
        double p = (a + b + c) / 2;
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));
        space = 2 * s / a;
        return space;
    }

    public static double getEuclideanDistance(double x0, double y0, double x1, double y1) {
        return Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));
    }

    /**
     * 三角形X乘
     *
     * @param p0
     * @param p1
     * @param p2
     * @return
     */
    public static double getTriangleSquare(Point3 p0, Point3 p1, Point3 p2) {

        double x0 = p1.x - p0.x;
        double y0 = p1.y - p0.y;
        double x1 = p2.x - p1.x;
        double y1 = p2.y - p1.y;

        return Math.abs((x0 * y1 - y0 * x1)) / 2;
    }

    /**
     * 在平面 判断一个点是否在一个三角形中
     *
     * @param a
     * @param b
     * @param c
     * @param d
     * @return
     */
    public static boolean isInTriangle(Point3 a, Point3 b, Point3 c, Point3 d) {

        double sabc = getTriangleSquare(a, b, c);

        double sadb = getTriangleSquare(a, d, b);
        double sbdc = getTriangleSquare(b, d, c);
        double sadc = getTriangleSquare(a, d, c);

        double sum = sadb + sbdc + sadc;

        return (Math.abs(sabc - sum) / sabc )< .001;

    }

    public static boolean isLeft(Point3 from, Point3 to, Point3 pnt) {

        return (from.x - pnt.x) * (to.y - pnt.y) - (from.y - pnt.y) * (to.x - pnt.x) > 0;

    }

    /**
     * 获取左上点的索引
     *
     * @param p0
     * @param p1
     * @param p2
     * @param innerPoint
     * @return
     */
    public static int getLeftTopPoint(Point3 p0, Point3 p1, Point3 p2, Point3 innerPoint) {

        if (isInTriangle(p0, p1, p2, innerPoint)) {

            double d01 = (p0.x - p1.x) * (p0.x - p1.x) + (p0.y - p1.y) * (p0.y - p1.y);

            double d02 = (p0.x - p2.x) * (p0.x - p2.x) + (p0.y - p2.y) * (p0.y - p2.y);

            double d12 = (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);

            double v01 = Math.sqrt(d01);
            double v02 = Math.sqrt(d02);
            double v12 = Math.sqrt(d12);

            double cos0 = (d01 + d02 - d12) / (2 * v01 * v02);
            double cos1 = (d01 + d12 - d02) / (2 * v01 * v12);
            double cos2 = (d02 + d12 - d01) / (2 * v02 * v12);

            if (cos0 < cos1) {
                return cos0 < cos2 ? 0 : 2;
            } else {
                return cos1 < cos2 ? 1 : 2;
            }
        }

        Point3 other = new Point3(p1.x + p2.x - p0.x, p1.y + p2.y - p0.y, 1.0);

        if (isInTriangle(other, p1, p2, innerPoint))
            return 0;

        other = new Point3(p0.x + p2.x - p1.x, p0.y + p2.y - p1.y, 1.0);

        if (isInTriangle(p0, other, p2, innerPoint))
            return 1;

        other = new Point3(p0.x + p1.x - p2.x, p0.y + p1.y - p2.y, 1.0);

        if (isInTriangle(p0, p1, other, innerPoint))
            return 2;

        return -1;

    }

    /**
     * 求区域
     *
     * @param pnts
     * @return
     */
    public static Rect4i getRectangle(Point3[] pnts) {

        int left = Integer.MAX_VALUE;
        int top = Integer.MAX_VALUE;

        int right = Integer.MIN_VALUE;
        int bottom = Integer.MIN_VALUE;

        for (Point3 pnt : pnts) {

            if (pnt == null) {
               continue;
            }

            int x = (int) pnt.x;
            int y = (int) pnt.y;

            if (x < left)
                left = x;
            if (x > right)
                right = x;
            if (y < top)
                top = y;
            if (y > bottom)
                bottom = y;
        }

        return new Rect4i(left, right, top, bottom);
    }

    /**
     * 求区域
     *
     * @param pnts
     * @return
     */
    public static Rect4i getRectangle(Point2i[] pnts) {

        int left = Integer.MAX_VALUE;
        int top = Integer.MAX_VALUE;

        int right = Integer.MIN_VALUE;
        int bottom = Integer.MIN_VALUE;

        for (Point2i pnt : pnts) {

            int x = (int) pnt.x;
            int y = (int) pnt.y;

            if (x < left)
                left = x;
            if (x > right)
                right = x;
            if (y < top)
                top = y;
            if (y > bottom)
                bottom = y;
        }

        return new Rect4i(left, right, top, bottom);
    }

    /**
     * 计算欧式距离
     *
     * @param p0
     * @param p1
     * @return
     */
    public static double getEuclideanDistance(Point3 p0, Point3 p1) {

        double d = (p0.x - p1.x) * (p0.x - p1.x) + (p0.y - p1.y) * (p0.y - p1.y) + (p0.z - p1.z) * (p0.z - p1.z);

        return Math.sqrt(d);

    }

    /**
     * 求平面方位角
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double azimuthAngle(double x1, double y1, double x2, double y2) {

        double dx, dy, angle = 0;
        dx = x2 - x1;
        dy = y2 - y1;
        if (x2 == x1) {
            angle = Math.PI / 2.0;
            if (y2 == y1) {
                angle = 0.0;
            } else if (y2 < y1) {
                angle = 3.0 * Math.PI / 2.0;
            }
        } else if ((x2 > x1) && (y2 > y1)) {
            angle = Math.atan(dx / dy);
        } else if ((x2 > x1) && (y2 < y1)) {
            angle = Math.PI / 2 + Math.atan(-dy / dx);
        } else if ((x2 < x1) && (y2 < y1)) {
            angle = Math.PI + Math.atan(dx / dy);
        } else if ((x2 < x1) && (y2 > y1)) {
            angle = 3.0 * Math.PI / 2.0 + Math.atan(dy / -dx);
        }
        return (angle * 180 / Math.PI);
    }

    /**
     * 坐标变换
     *
     * @param matrix
     * @param pnt
     * @return  pnt* matrix
     */
    public static Point3 transform(Matrix3 matrix, Point3 pnt) {

        return matrix.transform(pnt);
    }

    public static GirthResult calculateGirth(Point3[] points) {

        double d = 0.0;

        double[] others = new double[8];

        Filter filter = new AvgFilter(2);

        for (int step = 2; step < 10; step++) {

            double sd = 0.0;

            for (int b = 0; b < step; b++) {

                Girth g = new BcurveGirth(Filter.filter(points, filter, new SampleFilter(b, step)));
                sd += g.getGirth();

            }

            d += (sd / step);

            others[step - 2] = (sd / step);
        }

        return new GirthResult("", d / 8, others);
    }

    /**
     * 求平面旋转矩阵
     *
     * @param center
     * @param angle
     * @return
     */
    public static Matrix3 getRotateMatrix(final Point3 center, double angle) {

        Matrix3 moveTo = new Matrix3(new double[][]{
                {1.0, 0.0, 0.0},
                {0.0, 1.0, 0.0},
                {-center.x, -center.y, -center.z}
        });

        Matrix3 rotate = new Matrix3(new double[][]{
                {Math.cos(angle), -Math.sin(angle), 0.0},
                {Math.sin(angle), Math.cos(angle), 0.0},
                {1.0, 1.0, 1.0}
        });

//        RealMatrix moveBack = new Array2DRowRealMatrix(new double[][]{
//                {1.0, 0.0, 0.0},
//                {0.0, 1.0, 0.0},
//                {center.x, center.y, center.z}
//        });

        Matrix3 moveBack = moveTo.inverse();// inverseMatrix(moveTo);

        return moveTo.multiply(rotate).multiply(moveBack);
    }


//    /**
//     * 求逆矩阵 A^(-1)
//     *
//     * @param A
//     * @return
//     */
//    public static RealMatrix inverseMatrix(RealMatrix A) {
//        RealMatrix result = new LUDecomposition(A).getSolver().getInverse();
//        return result;
//    }

    /**
     * 一元线性回归
     *
     * @param points
     * @return
     */
    public static double[] unaryLR(List<Point3> points) {

        UnaryLR linearRegression = new UnaryLR();

        for (Point3 p : points) {
            linearRegression.addPoint(p);
        }

        return new double[]{linearRegression.getK(), linearRegression.getB()};
    }

    /**
     * 两直线的交点
     * line: ax + by + c= 0;
     *
     * @param a1
     * @param b1
     * @param c1
     * @param a2
     * @param b2
     * @param c2
     * @return
     */
    public static Point3 getIntersectPoint(double a1, double b1, double c1, double a2, double b2, double c2) {
        Point3 p;
        double m = a1 * b2 - a2 * b1;
        if (m == 0) {
            return null;
        }
        double x = (c2 * b1 - c1 * b2) / m;
        double y = (c1 * a2 - c2 * a1) / m;
        p = new Point3(x, y, 1.0);
        return p;
    }

    /**
     * 移动到LeftTop
     * 转回直角坐标系统
     *
     * ....
     *
     * @param lt
     * @param rt
     * @param lb
     * @param standardWidth
     * @param standardHeight
     * @return
     */
    public static Matrix3 getCoordinateMatrix2D(Point3 lt, Point3 rt, Point3 lb, double standardWidth, double standardHeight) {


        Matrix3 rotateMatrix = new Matrix3(new double[][]{{rt.x - lt.x, lb.x - lt.x, 0.0},
                {rt.y - lt.y, lb.y - lt.y, 0.0}, {0.0, 0.0,
                1.0}});

        Matrix3 coorMatrix = new Matrix3(new double[][]{{standardWidth, 0, 0}, {0, standardHeight, 0}, {0, 0, 1}});

        Matrix3 moveMatrix = new Matrix3(new double[][]{{1, 0, -lt.x}, {0, 1, -lt.y}, {0, 0, 1}});

        Matrix3 centerMatrix = new Matrix3(new double[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}});

        return centerMatrix.multiply(coorMatrix).multiply(rotateMatrix.inverse()).multiply(moveMatrix);

    }

    public static double max(double... vs) {

        double d = Double.MIN_VALUE;
        for (double v : vs) {
            if (d < v) d = v;
        }

        return d;
    }

    public static double min(double... vs) {

        double d = Double.MIN_VALUE;
        for (double v : vs) {
            if (d > v) d = v;
        }

        return d;
    }


    public static int max(int... vs) {

        int d = Integer.MIN_VALUE;
        for (int v : vs) {
            if (d < v) d = v;
        }

        return d;
    }

    public static int min(int... vs) {

        int d = Integer.MIN_VALUE;
        for (int v : vs) {
            if (d > v) d = v;
        }

        return d;
    }


    public final static double[][] LAPLACE_KERNEL = new double[][]{
            {-1, -1, -1},
            {-1, 8, -1},
            {-1, -1, -1}
    };

    public static AbstractPixelHolder laplaceFilter(AbstractPixelHolder pixelHolder) {

        IntList list = new IntList();

        for (int pixelIndex : pixelHolder.getPixelIndices()) {

            int x = pixelHolder.getX(pixelIndex);
            int y = pixelHolder.getY(pixelIndex);


            int newVal = 0;
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {

                    int nx = x + i;
                    int ny = y + j;

                    if (nx < 0 || nx >= pixelHolder.getWidth()) continue;
                    if (ny < 0 || ny >= pixelHolder.getHeight()) continue;

                    if (!pixelHolder.hasPixel(nx, ny)) continue;

                    newVal += LAPLACE_KERNEL[i + 1][j + 1];

                }
            }

            if (newVal > 2) list.append(pixelIndex);

        }

        return new IndexPixelHolder(pixelHolder.getWidth(), pixelHolder.getHeight(), list);

    }

}
