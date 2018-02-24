package com.lichanghai.edgelen.foundation.girth;

/**
 * Created by lichanghai on 2018/1/23.
 */
public class Spline {

    double h[], u[], q[], g[], M[];
    double S[];
    double xs[], ys[];
    int n;

    double b[];

    double r[], a1[], b1[];

    double x0[], y0[];

    public Spline(double[] xs, double[] ys, int n) {
        this.xs = xs;
        this.ys = ys;
        this.n = n;
    }

    private double[] thomas(double a[], double c[], double d[]) {

        b = new double[n];

        r = new double[n];
        a1 = new double[n];
        b1 = new double[n];

        x0 = new double[n];
        y0 = new double[n];

        for (int i = 0; i < n; i++) {
            b[i] = 2;
        }

        for (int i = 0; i < n - 2; i++) {
            r[i] = a[i];

        }
        a1[0] = b[0];
        b1[0] = c[0] / a1[0];
        for (int i = 1; i < n - 2; i++) {
            a1[i] = b[i] - r[i] * b1[i - 1];
            b1[i] = c[i] / a1[i];
        }

        y0[0] = d[0] / a1[0];
        for (int i = 1; i < n - 2; i++) {
            y0[i] = (d[i] - r[i] * y0[i - 1]) / a1[i];
        }

        x0[n - 2] = y0[n - 2];
        for (int i = n - 3; i >= 0; i--) {
            x0[i] = y0[i] - b1[i] * x0[i + 1];
        }
        return x0;
    }

    public double[] a() {
        h = new double[n - 1];
        u = new double[n - 2];
        q = new double[n - 2];
        g = new double[n];

        for (int i = 0; i < n - 1; i++) {
            h[i] = xs[i + 1] - xs[i];
        }
        for (int i = 0; i < n - 2; i++) {
            u[i] = h[i] / (h[i] + h[i + 1]);
        }
        for (int i = 0; i < n - 2; i++) {
            q[i] = 1 - u[i];
        }
        double y0 = (ys[1] - ys[0]) / (xs[1] - xs[0]);
        double y3 = (ys[n - 1] - ys[n - 2]) / (xs[n - 1] - xs[n - 2]);
        g[0] = 6 / h[0] * ((ys[1] - ys[0]) / h[0] - y0);
        for (int i = 1; i < n - 1; i++) {
            g[i] = 6 / (h[i - 1] + h[i]) * ((ys[i + 1] - ys[i]) / h[i] - ((ys[i] - ys[i - 1]) / h[i - 1]));
        }
        g[n - 1] = 6 / h[n - 2] * (y3 - (ys[n - 1] - ys[n - 2]) / h[n - 2]);
        M = thomas(u, q, g);
        return M;
    }

    public double[] s(int[] yy) {
        double M[] = a();
        S = new double[12];

        for (int i = 0; i < n - 1; i++) {
            for (int f = 0; f < yy.length; f++) {
                if (yy[f] > xs[i] && yy[f] < xs[i + 1]) {
                    S[yy[f]] = Math.pow((xs[i + 1] - yy[f]), 3) * M[i] / (6 * h[i])
                            + Math.pow((yy[f] - xs[i]), 3) * M[i + 1] / (6 * h[i])
                            + (xs[i + 1] - yy[f]) * (ys[i] - h[i] * h[i] * M[i] / 6) / h[i]
                            + (yy[f] - xs[i]) * (ys[i + 1] - h[i] * h[i] * M[i + 1] / 6) / h[i];
                } else if (yy[f] == xs[i]) {
                    S[yy[f]] = ys[i];
                } else if (yy[f] == xs[i + 1]) {
                    S[yy[f]] = ys[i + 1];
                }
            }
        }
        return S;
    }

}
