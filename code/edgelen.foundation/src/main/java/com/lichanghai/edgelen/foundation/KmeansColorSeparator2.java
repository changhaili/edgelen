package com.lichanghai.edgelen.foundation;

import com.lichanghai.edgelen.foundation.cluster.kmeans3.KMeans3;
import com.lichanghai.edgelen.foundation.cluster.kmeans3.KMeansModel3;
import com.lichanghai.edgelen.foundation.math.Point2i;
import com.lichanghai.edgelen.foundation.math.Point3;
import com.lichanghai.edgelen.foundation.pixelholder.ImagePixelHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lichanghai on 2018/3/22.
 */
public class KmeansColorSeparator2 implements PixelSeparator {

    private ImagePixelHolder pixelHolder;

    private int sampleCount;

    private ColorFilter backFilter;

    private ColorFilter foreFilter;

    public KmeansColorSeparator2(ImagePixelHolder pixelHolder, int sampleCount) {

        this.pixelHolder = pixelHolder;
        this.sampleCount = sampleCount;

        this.run();

    }

    private void run() {

        List<Point3> colors = new ArrayList<>();
        List<Point2i> points = new ArrayList<>();

        int width = pixelHolder.getWidth() * 4 / 5;
        int height = pixelHolder.getHeight() * 4 / 5;

        int xCount = (int) Math.sqrt(sampleCount * width / height);
        int yCount = (int) Math.sqrt(sampleCount * height / width);

        int xStep = width / xCount;
        int yStep = height / yCount;

        for (int xBegin = (pixelHolder.getWidth() - width) / 2, x = xBegin; x < width + xBegin; x += xStep) {

            for (int yBegin = (pixelHolder.getHeight() - height) / 2, y = yBegin; y < height + yBegin; y += yStep) {

                int color = pixelHolder.getColor(x, y);

                double r = (color >> 16) & 0xFF;
                double g = (color >> 8) & 0xFF;
                double b = (color) & 0xFF;

                colors.add(new Point3(r, g, b));

                points.add(new Point2i(x, y));

            }
        }


        // TODO 由方差来确定哪个颜色是背景色，但有时间可能不靠谱
        KMeansModel3[] models = new KMeans3(colors.toArray(new Point3[colors.size()])).cluster(2);

        //Point3[] means = new KMeans(colors.toArray(new Point3[colors.size()])).cluster(2);

        ColorFilter colorFilter1 = getColorFilter(models[0], models[1]);
        ColorFilter colorFilter2 = getColorFilter(models[1], models[0]);

        List<Point2i> points1 = new ArrayList<>();
        List<Point2i> points2 = new ArrayList<>();

        Point2i center1 = new Point2i(0, 0);
        Point2i center2 = new Point2i(0, 0);

        for (Point2i pnt : points) {

            int c = pixelHolder.getColor(pnt.x, pnt.y);

            if (colorFilter1.isSame(c)) {
                points1.add(pnt);

                center1.x += pnt.x;
                center1.y += pnt.y;
            }

            if (colorFilter2.isSame(c)) {
                points2.add(pnt);

                center2.x += pnt.x;
                center2.y += pnt.y;
            }
        }

        center1.x /= points1.size();
        center1.y /= points1.size();

        center2.x /= points2.size();
        center2.y /= points2.size();

        long sd1 = 0;

        for (Point2i pnt : points1) {
            sd1 += (pnt.x - center1.x) * (pnt.x - center1.x) + (pnt.y - center1.y) * (pnt.y - center1.y);
        }

        long sd2 = 0;
        for (Point2i pnt : points2) {
            sd2 += (pnt.x - center2.x) * (pnt.x - center2.x) + (pnt.y - center2.y) * (pnt.y - center2.y);
        }

        sd1 /= points1.size();
        sd2 /= points2.size();

        if (sd1 > sd2) {
            backFilter = colorFilter1;
            foreFilter = colorFilter2;
        } else {
            backFilter = colorFilter2;
            foreFilter = colorFilter1;
        }
    }

    private ColorFilter getColorFilter(final KMeansModel3 thisModel, final KMeansModel3 otherModel) {



        double xs[] = thisModel.sortX();
        double ys[] = thisModel.sortY();
        double zs[] = thisModel.sortZ();

        int size = thisModel.getPoints().size();

//        int r0 = (int) xs[size / 10];
//        int r1 = (int) xs[size - size / 10];
//
//        int g0 = (int) ys[size / 10];
//        int g1 = (int) ys[size - size / 10];
//
//        int b0 = (int) zs[size / 10];
//        int b1 = (int) zs[size - size / 10];

        int r0 = (int) xs[0];
        int r1 = (int) xs[size - 1];

        int g0 = (int) ys[0];
        int g1 = (int) ys[size - 1];

        int b0 = (int) zs[0];
        int b1 = (int) zs[size - 1];

        final int uR = (int) thisModel.getMean().x;
        final int uG = (int) thisModel.getMean().y;
        final int uB = (int) thisModel.getMean().z;

        if (r0 > uR) r0 = uR;
        if (r1 < uR) r1 = uR;

        if (g0 > uG) g0 = uG;
        if (g1 < uG) g1 = uG;

        if (b0 > uB) b0 = uB;
        if (b1 < uB) b1 = uB;

        final int minR = r0 - 10;
        final int maxR = r1 + 10;

        final int minG = g0 - 10;
        final int maxG = g1 + 10;

        final int minB = b0 - 10;
        final int maxB = b1 + 10;

        return new PixelColorFilter() {
            @Override
            public boolean isSame(int color) {

                int r = getRed(color);
                int g = getGreen(color);
                int b = getBlue(color);

                boolean v = r >= minR && r <= maxR && g >= minG && g <= maxG && b >= minB && b <= maxB;
                if (!v) return false;

                double d1 = (r - uR) * (r - uR) +
                        (g - uG) * (g - uG) +
                        (b - uB) * (b - uB);

                Point3 otherMean = otherModel.getMean();

                double d2 = (r - otherMean.x) * (r - otherMean.x) +
                        (g - otherMean.y) * (g - otherMean.y) +
                        (b - otherMean.z) * (b - otherMean.z);

                return d1 < d2;

            }
        };
    }

    @Override
    public boolean isBack(int pixel) {
        return backFilter.isSame(pixel);
    }

    @Override
    public boolean isFore(int pixel) {
        return foreFilter.isSame(pixel);
    }
}
