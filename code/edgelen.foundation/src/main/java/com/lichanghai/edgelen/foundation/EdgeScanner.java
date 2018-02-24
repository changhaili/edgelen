package com.lichanghai.edgelen.foundation;

import com.lichanghai.edgelen.foundation.math.MathUtils;
import com.lichanghai.edgelen.foundation.math.Point2i;
import com.lichanghai.edgelen.foundation.pixelholder.AbstractPixelHolder;
import com.lichanghai.edgelen.foundation.utils.IntList;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by lichanghai on 2018/1/14.

 * 将边界连成一条线
 */
public class EdgeScanner {

    private final static double ERROR_ANGLE = 10000;

    private final AbstractPixelHolder group;

    public EdgeScanner(AbstractPixelHolder group) {

        this.group = group;
    }

    private double[][] calculateAngles(int bx, int by, int x, int y, int precision, BitSet newIndicesBitSet) {

        double[][] azimuthAngles = new double[(precision * 2 + 1)][(precision * 2 + 1)];

        double sAngle = MathUtils.azimuthAngle(bx, by, x, y);

        for (int r = -precision; r <= precision; r++) {

            for (int c = -precision; c <= precision; c++) {

                int nx = c + x;
                int ny = r + y;

                if (nx < 0 || ny < 0) {
                    azimuthAngles[r + precision][c + precision] = ERROR_ANGLE;
                    continue;
                }

                if (!this.group.hasPixel(nx, ny)) {
                    azimuthAngles[r + precision][c + precision] = ERROR_ANGLE;
                    continue;
                }

                if (newIndicesBitSet.get(this.group.getIndex(nx, ny))) {
                    azimuthAngles[r + precision][c + precision] = ERROR_ANGLE;
                    continue;
                }

                double nAngle = MathUtils.azimuthAngle(bx, by, nx, ny);
                azimuthAngles[r + precision][c + precision] = nAngle - sAngle;
            }

        }

        azimuthAngles[precision][precision] = ERROR_ANGLE;

        return azimuthAngles;
    }

    private boolean clockwise = true;

    private int findNext(int bx, int by, int x, int y, int percision, BitSet newIndicesBitSet) {

        double[][] azimuthAngles = this.calculateAngles(bx, by, x, y, percision, newIndicesBitSet);

        double sAngle = ERROR_ANGLE;

        int rIndex = 0;
        int cIndex = 0;

        for (int tries = 0; tries < 2; tries++) {

            if (clockwise) {

                for (int r = -percision; r <= percision; r++) {

                    int columPercision = (int) Math.sqrt(percision * percision - r * r);

                    for (int c = -columPercision; c <= columPercision; c++) {

                        double angle = azimuthAngles[r + percision][c + percision];

                        if (percision > 50 && angle != ERROR_ANGLE) {
                            System.out.println(percision + ": " + angle);
                        }

                        if (angle == ERROR_ANGLE || angle <= 0)
                            continue;

                        if (sAngle == ERROR_ANGLE || sAngle > angle) {
                            sAngle = angle;
                            rIndex = r;
                            cIndex = c;
                        }

                    }
                }
            } else {

                for (int r = -percision; r <= percision; r++) {

                    int columPercision = (int) Math.sqrt(percision * percision - r * r);

                    for (int c = -columPercision; c <= columPercision; c++) {

                        double angle = azimuthAngles[r + percision][c + percision];

                        if (angle == ERROR_ANGLE || angle > 0)
                            continue;

                        if (sAngle == ERROR_ANGLE || sAngle < angle) {
                            sAngle = angle;
                            rIndex = r;
                            cIndex = c;
                        }

                    }
                }

            }

            if (sAngle == ERROR_ANGLE) {
                clockwise = !clockwise;
            } else {
                break;
            }
        }

        if (sAngle == ERROR_ANGLE) {
            return -1;
        }

        return this.group.getIndex(cIndex + x, rIndex + y);

    }

    private Point2i getBorderCenter() {

        int left = Integer.MAX_VALUE;
        int top = Integer.MAX_VALUE;

        int right = Integer.MIN_VALUE;
        int bottom = Integer.MIN_VALUE;

        for (int pnt : this.group.getPixelIndices()) {

            int x = this.group.getX(pnt);
            int y = this.group.getY(pnt);

            if (x < left)
                left = x;
            if (x > right)
                right = x;
            if (y < top)
                top = y;
            if (y > bottom)
                bottom = y;
        }

        return new Point2i(left / 2 + right / 2, top / 2 + bottom / 2);
    }

    public Point2i[] getEdgePoints() {

        Point2i baryCenter = this.getBorderCenter();

        int bx = baryCenter.x;
        int by = baryCenter.y;

        IntList reservedIndices = new IntList(this.group.getPixelIndices().toArray());

        BitSet allIndicesBitset = new BitSet();
        for (Integer i : reservedIndices) {
            allIndicesBitset.set(i);
        }

        BitSet accessedBitSet = new BitSet();

        // int edgeSize = border.;

        ArrayList<IntList> outlineList = new ArrayList<IntList>();

        while (true) {

            if (reservedIndices.isEmpty())
                break;

            IntList newReservedIndices = new IntList();
            for (int i : reservedIndices) {

                if (!accessedBitSet.get(i)) {
                    newReservedIndices.append(i);
                }
            }

            reservedIndices = newReservedIndices;

            int firstIndex = newReservedIndices.getAt(0);

            int currentIndex = firstIndex;

            IntList newIndices = new IntList();

            while (true) {

                accessedBitSet.set(currentIndex);
                newIndices.append(currentIndex);

                int x = this.group.getX(currentIndex);
                int y = this.group.getY(currentIndex);

                int findIndex = -1;
                int percision = 1;

                for (percision = 1; percision < 50; percision++) {

                    findIndex = this.findNext(bx, by, x, y, percision, accessedBitSet);

                    if (findIndex != -1) {
                        break;
                    }
                }

                if (findIndex == -1) {
                    break;
                }
                currentIndex = findIndex;

                if (currentIndex == firstIndex)
                    break;
            }

            outlineList.add(newIndices);

        }

        IntList maxList = new IntList();

        for (IntList outList : outlineList) {

            if (outList.size() > maxList.size()) {
                maxList = outList;
            }
        }

        Point2i[] points = new Point2i[maxList.size()];

        for (int i = 0; i < points.length; i++) {

            int x = this.group.getX(maxList.getAt(i));
            int y = this.group.getY(maxList.getAt(i));

            points[i] = new Point2i(x, y);

        }

        return points;
    }

}
