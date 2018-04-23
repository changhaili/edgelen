package com.lichanghai.edgelen.foundation;

import com.lichanghai.edgelen.foundation.cluster.kmeans.KMeans;
import com.lichanghai.edgelen.foundation.cluster.kmeans.KMeansModel;
import com.lichanghai.edgelen.foundation.math.Point2i;
import com.lichanghai.edgelen.foundation.pixelholder.ImagePixelHolder;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lichanghai on 2018/3/22.
 */
public class KmeansColorSeparator implements PixelSeparator {

    private ImagePixelHolder pixelHolder;

    private int sampleCount;

    private ColorFilter backFilter;

    private ColorFilter foreFilter;

    public KmeansColorSeparator(ImagePixelHolder pixelHolder, int sampleCount) {

        this.pixelHolder = pixelHolder;
        this.sampleCount = sampleCount;

        this.run();

    }

    private void run() {

        List<RealVector> colors = new ArrayList<>();
        List<Point2i> points = new ArrayList<>();

        int width = pixelHolder.getWidth() * 4 / 5;
        int height = pixelHolder.getHeight() * 4 / 5;

        int xCount = (int) Math.sqrt(sampleCount * width / height);
        int yCount = (int) Math.sqrt(sampleCount * height / width);

        if(xCount ==0) xCount = 1;
        if(yCount ==0) yCount = 1;

        int xStep = width / xCount;
        int yStep = height / yCount;

        for (int xBegin = (pixelHolder.getWidth() - width) / 2, x = xBegin; x < width + xBegin; x += xStep) {

            for (int yBegin = (pixelHolder.getHeight() - height) / 2, y = yBegin; y < height + yBegin; y += yStep) {

                int color = pixelHolder.getColor(x, y);

                double r = (color >> 16) & 0xFF;
                double g = (color >> 8) & 0xFF;
                double b = (color) & 0xFF;

                colors.add(new ArrayRealVector(new double[]{r, g, b}));

                points.add(new Point2i(x, y));

            }
        }


        // TODO 由方差来确定哪个颜色是背景色，但有时间可能不靠谱
        KMeansModel[] models = new KMeans(colors.toArray(new RealVector[colors.size()])).cluster(2);

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

    private ColorFilter getColorFilter(final KMeansModel thisModel, final KMeansModel otherModel) {

        final int dimension = thisModel.getMean().getDimension();
        final double [] mins = new double[dimension];
        final double [] maxs = new double[dimension];

        for(int dim = 0; dim < mins.length;dim++){

            double [] vs = thisModel.sort(dim);

            mins[dim] =  vs[10];
            maxs[dim] =  vs[vs.length -10];

            if(mins[dim] > thisModel.getMean().getEntry(dim)){
                mins[dim] =  thisModel.getMean().getEntry(dim);
            }

            if(maxs[dim] < thisModel.getMean().getEntry(dim)){
                maxs[dim] =  thisModel.getMean().getEntry(dim);
            }

            mins[dim] -=10.0/256;
            maxs[dim] +=10.0/256;
        }

        return new PixelColorFilter() {
            @Override
            public boolean isSame(int color) {

                int r = getRed(color);
                int g = getGreen(color);
                int b = getBlue(color);

                RealVector vec = thisModel.getRegularizationModel().transform(new ArrayRealVector(new double[]{r, g, b}));

                for(int i=0;i<dimension;i++){

                    double v = vec.getEntry(i);
                    if(v <= mins[i]) return false;
                    if(v >= maxs[i]) return false;
                }

                double d1 = 0;
                double d2 = 0;

                for(int i=0;i<dimension;i++){
                    double d =  vec.getEntry(i) -  thisModel.getMean().getEntry(i);
                    d1+=d*d;
                }

                for(int i=0;i<dimension;i++){
                    double d =  vec.getEntry(i) -  otherModel.getMean().getEntry(i);
                    d2+=d*d;
                }

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
