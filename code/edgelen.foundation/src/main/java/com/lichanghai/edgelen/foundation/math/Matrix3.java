package com.lichanghai.edgelen.foundation.math;

import java.text.MessageFormat;

/**
 * Created by lichanghai on 2018/2/9.
 *
 */
public class Matrix3 {

    public double v00, v01, v02;

    public double v10, v11, v12;

    public double v20, v21, v22;

    public final static Matrix3 I = new Matrix3(new double[][]{
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
    });

    public Matrix3(){

    }

    public Matrix3(double v00, double v01, double v02, double v10, double v11, double v12, double v20, double v21,
                   double v22) {

        this.v00 = v00;
        this.v01 = v01;
        this.v02 = v02;

        this.v10 = v10;
        this.v11 = v11;
        this.v12 = v12;

        this.v20 = v20;
        this.v21 = v21;
        this.v22 = v22;
    }

    public Matrix3(double vs[][]) {

        this.v00 = vs[0][0];
        this.v01 = vs[0][1];
        this.v02 = vs[0][2];

        this.v10 = vs[1][0];
        this.v11 = vs[1][1];
        this.v12 = vs[1][2];

        this.v20 = vs[2][0];
        this.v21 = vs[2][1];
        this.v22 = vs[2][2];
    }


    public Matrix3 multiply(double v) {
        return new Matrix3(v00 * v, v01 * v, v02 * v,
                v10 * v, v11 * v, v12 * v,
                v20 * v, v21 * v, v22 * v);
    }

    public Matrix3 multiply(Matrix3 m) {


        double d00 = v00 * m.v00 + v01 * m.v10 + v02 * m.v20;
        double d01 = v00 * m.v01 + v01 * m.v11 + v02 * m.v21;
        double d02 = v00 * m.v02 + v01 * m.v12 + v02 * m.v22;

        double d10 = v10 * m.v00 + v11 * m.v10 + v12 * m.v20;
        double d11 = v10 * m.v01 + v11 * m.v11 + v12 * m.v21;
        double d12 = v10 * m.v02 + v11 * m.v12 + v12 * m.v22;

        double d20 = v20 * m.v00 + v21 * m.v10 + v22 * m.v20;
        double d21 = v20 * m.v01 + v21 * m.v11 + v22 * m.v21;
        double d22 = v20 * m.v02 + v21 * m.v12 + v22 * m.v22;

        return new Matrix3(d00, d01, d02,
                d10, d11, d12,
                d20, d21, d22);
    }

    public Point3 transform(Point3 pnt) {

        double x = v00 * pnt.x + v10 * pnt.y + v20 * pnt.z;
        double y = v01 * pnt.x + v11 * pnt.y + v21 * pnt.z;
        double z = v02 * pnt.x + v12 * pnt.y + v22 * pnt.z;

        return new Point3(x, y, z);
    }

    public Matrix3 t() {
        return new Matrix3(v00, v10, v20,
                v01, v11, v21,
                v02, v12, v22);

    }

    public double det() {

        return v00 * (v11 * v22 - v21 * v12) - v01 * (v10 * v22 - v20 * v12) + v02 * (v10 * v21 - v20 * v11);
    }

    public Matrix3 inverse() {

        double d00 = v11 * v22 - v12 * v21;
        double d01 = v10 * v22 - v12 * v20;
        double d02 = v10 * v21 - v20 * v11;

        double d10 = v01 * v22 - v21 * v02;
        double d11 = v00 * v22 - v02 * v20;
        double d12 = v00 * v21 - v20 * v01;

        double d20 = v01 * v12 - v11 * v02;
        double d21 = v00 * v12 - v10 * v02;
        double d22 = v00 * v11 - v10 * v01;

        d01 = -d01;
        d10 = -d10;
        d12 = -d12;
        d21 = -d21;

        double d = this.det();

        return new Matrix3(d00 / d, d01 / d, d02 / d,
                d10 / d, d11 / d,
                d12 / d, d20 / d, d21 / d, d22 / d).t();
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0},{1},{2} \n,{3}, {4}, {5}\n,{6}, {7}, {8},",
                v00, v01, v02, v10, v11, v12, v20, v21, v22);
    }
}
