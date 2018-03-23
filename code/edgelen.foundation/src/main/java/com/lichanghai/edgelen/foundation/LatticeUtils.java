package com.lichanghai.edgelen.foundation;

import com.lichanghai.edgelen.foundation.cluster.density.DensityCluster;
import com.lichanghai.edgelen.foundation.cluster.hierarchy.HierarchyCluster;
import com.lichanghai.edgelen.foundation.math.*;
import com.lichanghai.edgelen.foundation.pixelholder.AbstractPixelHolder;
import com.lichanghai.edgelen.foundation.pixelholder.AllPixelHolder;
import com.lichanghai.edgelen.foundation.pixelholder.ImagePixelHolder;
import com.lichanghai.edgelen.foundation.pixelholder.IndexPixelHolder;
import com.lichanghai.edgelen.foundation.utils.ImageUtils;
import com.lichanghai.edgelen.foundation.utils.IntList;
import com.lichanghai.edgelen.foundation.utils.IntegerFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * Created by lichanghai on 2018/1/8.
 */
public class LatticeUtils {

    private static IntList getBackBorders(final ImagePixelHolder pixelHolder,
                                          final PixelSeparator pixelSeparator,
                                          int borderWidth) {

        pixelHolder.attachFilter(new IntegerFilter() {
            @Override
            public boolean isSatisfied(int i) {

                int x = pixelHolder.getX(i);
                int y = pixelHolder.getY(i);
                int color = pixelHolder.getColor(x, y);

                return pixelSeparator.isBack(color);
            }
        });

        int width = pixelHolder.getWidth();
        int height = pixelHolder.getHeight();

        int[] leftWidths = new int[height];
        int[] rightWidths = new int[height];

        int[] topWidths = new int[width];
        int[] bottomWidths = new int[width];


        BitSet filterBitSet = new BitSet();
        IntList pixelIndices = new IntList();

        //AllPixelHolder pixelHolder = new AllPixelHolder(width,height, pixelColors, null);

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width && leftWidths[y] < borderWidth; x++) {

                int index = pixelHolder.getIndex(x, y);

                if (pixelHolder.hasPixel(index)) {
                    leftWidths[y]++;

                    if (!filterBitSet.get(index)) {
                        filterBitSet.set(index);
                        pixelIndices.append(index);
                    }
                }
            }

            for (int x = width - 1; x >= 0 && rightWidths[y] < borderWidth; x--) {

                int index = pixelHolder.getIndex(x, y);

                if (pixelHolder.hasPixel(index)) {
                    rightWidths[y]++;

                    if (!filterBitSet.get(index)) {
                        filterBitSet.set(index);
                        pixelIndices.append(index);
                    }
                }
            }
        }

        for (int x = 0; x < width; x++) {

            for (int y = 0; y < height && topWidths[x] < borderWidth; y++) {

                int index = pixelHolder.getIndex(x, y);

                if (pixelHolder.hasPixel(index)) {
                    topWidths[x]++;

                    if (!filterBitSet.get(index)) {
                        filterBitSet.set(index);
                        pixelIndices.append(index);
                    }
                }
            }

            for (int y = height - 1; y > 0 && bottomWidths[x] < borderWidth; y--) {

                int index = pixelHolder.getIndex(x, y);

                if (pixelHolder.hasPixel(index)) {
                    bottomWidths[x]++;

                    if (!filterBitSet.get(index)) {
                        filterBitSet.set(index);
                        pixelIndices.append(index);
                    }
                }
            }
        }

        return pixelIndices;

    }

    private static <T> List<T> selectLinePoints(List<T> list) {

        if (list.size() < 10) return list;

        int begin = list.size() / 8;
        int end = list.size() * 7 / 8;

        return list.subList(begin, end);

    }


    private static AbstractPixelHolder getBackPixels(int width, int height, int[] colors, SupportColor backColor) {

        AllPixelHolder pixelHolder = new AllPixelHolder(width, height, colors, null);

        IntList list = new IntList(width * height / 2);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                int index = pixelHolder.getIndex(x, y);

                if (backColor.isSame(colors[index])) {
                    list.append(index);
                }
            }
        }

        return new IndexPixelHolder(width, height, list);
    }

    private static Rect4i getRotatedRectangle(Matrix3 matrix, int width, int height) {

        Point3 topPoint = MathUtils.transform(matrix, new Point3(0, 0, 1.0));
        Point3 rightPoint = MathUtils.transform(matrix, new Point3(width, 0, 1.0));
        Point3 bottomPoint = MathUtils.transform(matrix, new Point3(width, height, 1.0));
        Point3 leftPoint = MathUtils.transform(matrix, new Point3(0, height, 1.0));

        int left = (int) MathUtils.min(topPoint.x, rightPoint.x, bottomPoint.x, leftPoint.x);
        int right = (int) MathUtils.max(topPoint.x, rightPoint.x, bottomPoint.x, leftPoint.x);

        int top = (int) MathUtils.min(topPoint.y, rightPoint.y, bottomPoint.y, leftPoint.y);
        int bottom = (int) MathUtils.max(topPoint.y, rightPoint.y, bottomPoint.y, leftPoint.y);

        int p = 2;
        return new Rect4i(left - p, right + p, top - p, bottom + p);
    }

    private static BackgroundState getBackground(final ImagePixelHolder imagePixelHolder,
                                                 final PixelSeparator pixelSeparator,
                                                 double realWidth, double realHeight) {


        IntList backBorderPixels = getBackBorders(imagePixelHolder, pixelSeparator, 100);

        TimeRecord timeRecord = TimeRecord.begin();

//        imagePixelHolder.attachFilter(new IntegerFilter() {
//            @Override
//            public boolean isSatisfied(int i) {
//
//                int x = imagePixelHolder.getX(i);
//                int y = imagePixelHolder.getY(i);
//                int color = imagePixelHolder.getColor(x, y);
//
//                return pixelSeparator.isBack(color);
//            }
//        });


        //AbstractPixelHolder pixelHolder = new HierarchyCluster(imagePixelHolder).cluster(1)[0]; // TODO size


        AbstractPixelHolder pixelHolder = new HierarchyCluster(new IndexPixelHolder(imagePixelHolder.getWidth(),
                imagePixelHolder.getHeight(), backBorderPixels)).cluster(1)[0]; // TODO size

        ImageUtils.drawPoints("/Users/lichanghai/Mine/edgelen/images/back_cluster.jpg", pixelHolder);

        timeRecord.record("cluster background time:{0}");

        Matrix3 rotateClock = MathUtils.getRotateMatrix(new Point3(pixelHolder.getWidth() / 2,
                pixelHolder.getHeight() / 2, 1.0), -Math.PI / 4);

        int[] naiveLeftBorders = new int[pixelHolder.getHeight()];
        int[] naiveRightBorders = new int[pixelHolder.getHeight()];

        for (int i = 0; i < naiveLeftBorders.length; i++) {
            naiveLeftBorders[i] = Integer.MAX_VALUE;
            naiveRightBorders[i] = Integer.MIN_VALUE;
        }

        timeRecord.record("naive border time:{0}");

        int[] pixels = pixelHolder.getPixelIndices().toArray();// getBackBorders(pixelHolder, backColor, 20).toIntArray();

        timeRecord.record("naive border pixels time:{0}");

        Rect4i rotatedRectangle = getRotatedRectangle(rotateClock, pixelHolder.getWidth(), pixelHolder.getHeight());

        final Point3[] leftPoints = new Point3[rotatedRectangle.getHeight()];
        //Point3[] rightPoints = new Point3[rotatedRectangle.getHeight()];

        final Point3[] topPoints = new Point3[rotatedRectangle.getWidth()];
        final Point3[] bottomPoints = new Point3[rotatedRectangle.getWidth()];

        for (int i = 0; i < pixels.length; i++) {

            int index = pixels[i];
            int x = pixelHolder.getX(index);
            int y = pixelHolder.getY(index);

            if (x > naiveRightBorders[y]) naiveRightBorders[y] = x;
            if (x < naiveLeftBorders[y]) naiveLeftBorders[y] = x;

            Point3 pnt = MathUtils.transform(rotateClock, new Point3(x, y, 1.0));
            //Point3 pnt = new Point3(x, y, 1.0);

            int newX = (int) pnt.x - rotatedRectangle.left;
            int newY = (int) pnt.y - rotatedRectangle.top;

            if (leftPoints[newY] == null || leftPoints[newY].x > pnt.x) leftPoints[newY] = pnt.copy();
            //if(rightPoints[newY] == null || rightPoints[newY].x< pnt.x) rightPoints[newY] = pnt.copy();

            if (topPoints[newX] == null || topPoints[newX].y > pnt.y) topPoints[newX] = pnt.copy();
            if (bottomPoints[newX] == null || bottomPoints[newX].y < pnt.y) bottomPoints[newX] = pnt.copy();

        }

//        ImageUtils.drawPoints("/Users/lichanghai/Mine/edgelen/images/rotate_45.jpg", new ArrayList<Point3>() {
//            {
//                addAll(Arrays.asList(topPoints));
//                addAll(Arrays.asList(bottomPoints));
//            }
//        });
        // TODO


        Point3 topPoint = null;
        for (int i = 0; i < leftPoints.length; i++) {
            if (leftPoints[i] != null) {
                topPoint = leftPoints[i];
                break;
            }
        }

        Point3 bottomPoint = null;
        for (int i = leftPoints.length - 1; i >= 0; i--) {
            if (leftPoints[i] != null) {
                bottomPoint = leftPoints[i];
                break;
            }
        }


        timeRecord.record("getAt background borders:{0}");

        List<Point3> line1 = new ArrayList<>();
        List<Point3> line2 = new ArrayList<>();
        List<Point3> line3 = new ArrayList<>();
        List<Point3> line4 = new ArrayList<>();

        for (Point3 pnt : topPoints) {

            if (pnt == null) continue;

            if (pnt.x < topPoint.x) line4.add(pnt);
            else line1.add(pnt);
        }

        for (Point3 pnt : bottomPoints) {
            if (pnt == null) continue;

            if (pnt.x < bottomPoint.x) line3.add(pnt);
            else line2.add(pnt);
        }

        double[] m1 = MathUtils.unaryLR(selectLinePoints(line1));
        double[] m2 = MathUtils.unaryLR(selectLinePoints(line2));
        double[] m3 = MathUtils.unaryLR(selectLinePoints(line3));
        double[] m4 = MathUtils.unaryLR(selectLinePoints(line4));

        Point3 leftTop = MathUtils.getIntersectPoint(m1[0], -1, m1[1],
                m4[0], -1, m4[1]);

        Point3 rightTop = MathUtils.getIntersectPoint(m1[0], -1, m1[1],
                m2[0], -1, m2[1]);

        Point3 leftBottom = MathUtils.getIntersectPoint(m3[0], -1, m3[1],
                m4[0], -1, m4[1]);

        Point3 rightBottom = MathUtils.getIntersectPoint(m2[0], -1, m2[1],
                m3[0], -1, m3[1]);

        ImageUtils.drawLines("/Users/lichanghai/Mine/edgelen/images/lines.jpg",
                leftTop,rightTop,
                leftTop, leftBottom,
                rightBottom,rightTop,
                rightBottom, leftBottom);

        Matrix3 rotateWise = MathUtils.getRotateMatrix(new Point3(pixelHolder.getWidth() / 2, pixelHolder.getHeight() / 2), Math.PI / 4);

        Point3 nLeftTop = MathUtils.transform(rotateWise, leftTop);
        Point3 nRightTop = MathUtils.transform(rotateWise, rightTop);
        Point3 nLeftBottom = MathUtils.transform(rotateWise, leftBottom);

        Matrix3 coordinateMatrix = MathUtils.getCoordinateMatrix2D(nLeftTop, nRightTop, nLeftBottom, realWidth, realHeight);

        timeRecord.record("linear regression: {0}");

        return new BackgroundState(coordinateMatrix, naiveLeftBorders, naiveRightBorders, pixelHolder.getWidth(), pixelHolder.getHeight());
    }

    private static AbstractPixelHolder getEdgeBorders(AbstractPixelHolder pixelHolder) {

        IntList pixels = pixelHolder.getPixelIndices().toList();

        //return LatticeUtils.getMouldBorder(new IndexPixelHolder(pixelHolder.getWidth(), pixelHolder.getHeight(), pixels));
        return MathUtils.laplaceFilter(new IndexPixelHolder(pixelHolder.getWidth(), pixelHolder.getHeight(), pixels));

    }

    private static EdgeCurve[] getEdgeCurves(final BackgroundState backgroundState,
                                             final ImagePixelHolder imagePixels,
                                             final PixelSeparator pixelSeparator,
                                             final int clusterCount,
                                             final boolean hierarchyCluster) {

        TimeRecord timeRecord = TimeRecord.begin();

        imagePixels.attachFilter(new IntegerFilter() {

            @Override
            public boolean isSatisfied(int i) {

                int x = backgroundState.getX(i);
                int y = backgroundState.getY(i);

                int color = imagePixels.getColor(x, y);

                boolean r = pixelSeparator.isFore(color);
                if (!r) return false;

                int left = backgroundState.getLeftXs()[y];
                int right = backgroundState.getRightXs()[y];

                return x >= left && x <= right;
            }
        });

        AbstractPixelHolder[] pixelHolders;
        if (!hierarchyCluster) {
            pixelHolders = new DensityCluster(imagePixels).cluster(clusterCount);
        } else {
            pixelHolders = new HierarchyCluster(imagePixels).cluster(clusterCount);
        }

        ImageUtils.drawPoints("/Users/lichanghai/Mine/edgelen/images/fore_clusters.jpg", pixelHolders);

        timeRecord.record("cluster time: {0}");

        EdgeCurve[] edgeHolders = new EdgeCurve[pixelHolders.length];

        for (int i = 0; i < edgeHolders.length; i++) {

            AbstractPixelHolder group = getEdgeBorders(pixelHolders[i]);

            ImageUtils.drawPoints("/Users/lichanghai/Mine/edgelen/images/fore_edge_" +i+".jpg", group);

            EdgeScanner scanner = new EdgeScanner(group);

            edgeHolders[i] = new EdgeCurve(backgroundState.getMatrix(), scanner.getEdgePoints());
        }

        return edgeHolders;
    }

    //private static

    public static EdgeCurve[] getEdgeCurves(ImagePixelHolder pixelHolder,
                                            double realWidth,
                                            double realHeight,
                                            PixelSeparator pixelSeparator,
                                            int clusterCount,
                                            boolean hiericalCluster) {

        TimeRecord timeRecord = TimeRecord.begin();

        // pixelColors = GaussianUtils.filter(pixelColors, width, height, 3,3);

        BackgroundState backgroundState = getBackground(pixelHolder, pixelSeparator, realWidth, realHeight);
        timeRecord.record("backgroundState time: {0}");

        return getEdgeCurves(backgroundState, pixelHolder, pixelSeparator, clusterCount, hiericalCluster);
    }



}
