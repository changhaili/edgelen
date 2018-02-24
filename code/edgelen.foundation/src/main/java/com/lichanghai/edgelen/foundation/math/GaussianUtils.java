package com.lichanghai.edgelen.foundation.math;

import java.util.Arrays;

/**
 * Created by lichanghai on 2018/1/19.
 *
 * 效率不高
 */
@Deprecated
public class GaussianUtils {

    public final static double eps = 1e-6;
    ;

    public static double gaussian(double x, double sigma) {
        return Math.exp(-(x * x) / (2 * sigma * sigma + eps));
    }

    // 计算高斯核
    public static void gaussianKernel(double[] kernel, double sigma) {
        double sum = 0;
        double[] data = kernel;
        int i;

        int size = kernel.length;
        for (i = 0; i < size; ++i) {
            double index = (size >> 1) - i;
            if ((size & 1) > 0) // size为奇数
            {
                data[i] = gaussian(index, sigma);
            } else // size为偶数
            {
                index -= 0.5;
                data[i] = gaussian(index, sigma);
            }
            sum += data[i];
        }
        // 归一化
        for (i = 0; i < size; ++i) {
            data[i] /= sum;
        }
    }

    // 计算内积
    private static void product(double a[], double b[], double c[], int m, int n, int p) {
        int i, j, k;
        for (i = 0; i < m; ++i) {
            for (j = 0; j < p; ++j) {
                double sum = 0;
                for (k = 0; k < n; ++k) {
                    sum += a[i * n + k] * b[k * p + j];
                }
                c[i * p + j] = sum;
            }
        }
    }

    // 计算二维高斯核
    public static double[] gaussianKernel2d(int sizeX, int sizeY, double sigmaX, double sigmaY) {

        double[] matX = new double[sizeX];
        double[] matY = new double[sizeY];
        gaussianKernel(matX, sigmaX);
        gaussianKernel(matY, sigmaY);

        double[] kernel = new double[sizeX * sizeY];
        product(matX, matY, kernel, sizeX, 1, sizeY);

        return kernel;

    }

    public static int[] filter(int[] pixels, int width, int height, int filterWidth, int filterHeight) {

        double[] kernel = gaussianKernel2d(filterWidth * 2 + 1, filterHeight * 2 + 1, 1.0, 1.0);

        double f = 1.0 / (filterWidth * 2 + 1) / (filterHeight * 2 + 1);

        int[] newPixels = Arrays.copyOf(pixels, pixels.length);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                double r = 0.0;
                double g = 0.0;
                double b = 0.0;

                for (int fi = -filterWidth; fi <= filterWidth; fi++) {

                    int x = i + fi;
                    if (x < 0 || x >= width)
                        continue;

                    for (int fj = -filterHeight; fj <= filterHeight; fj++) {

                        int y = j + fj;
                        if (y < 0 || y >= height)
                            continue;

                        int p = pixels[y * width + x];

                        int kIndex = (filterHeight + fj) * (filterWidth * 2 + 1) + (fi + filterWidth);
                        f = kernel[kIndex];

                        int ir = (p >> 16) & 0xFF;
                        int ig = (p >> 8) & 0xFF;
                        int ib = p & 0xFF;

                        r += f * ir;
                        g += f * ig;
                        b += f * ib;

                    }
                }

                r = r > 256.0 ? 256 : r;
                g = g > 256.0 ? 256 : g;
                b = b > 256.0 ? 256 : b;

                int v = (((int) r) << 16) + (((int) g) << 8) + (int) b;

                newPixels[j * width + i] = v;

            }
        }

        return newPixels;

    }

}
