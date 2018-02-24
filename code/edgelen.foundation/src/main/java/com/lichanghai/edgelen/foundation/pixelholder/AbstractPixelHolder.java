package com.lichanghai.edgelen.foundation.pixelholder;

import com.lichanghai.edgelen.foundation.math.Point2i;
import com.lichanghai.edgelen.foundation.math.Rect4i;
import com.lichanghai.edgelen.foundation.utils.IntList;

import java.util.Iterator;

/**
 * Created by lichanghai on 2018/2/1.
 */
public abstract class AbstractPixelHolder {

    public abstract class IntIterable implements Iterable<Integer> {

        public abstract int getAt(int index);

        public abstract int getLength();

        public IntList toList(){
            return new IntList(toArray());
        }

        public int[] toArray() {

            int[] values = new int[getLength()];

            for (int i = 0; i < values.length; i++) {
                values[i] = getAt(i);
            }

            return values;
        }

        public Iterator<Integer> iterator() {

            return new Iterator<Integer>() {

                int index = 0;

                @Override
                public boolean hasNext() {
                    return index < getLength();
                }

                @Override
                public Integer next() {
                    return getAt(index++);
                }

                @Override
                public void remove() {

                }
            };

        }
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract IntIterable getPixelIndices();

    public boolean hasPixel(int x, int y) {
        return hasPixel(getIndex(x, y));
    }

    public abstract boolean hasPixel(int index);

    public int getY(int index) {
        return index / this.getWidth();
    }

    public int getX(int index) {
        return index % this.getWidth();
    }

    public int getIndex(int x, int y) {
        return y * this.getWidth() + x;
    }

    public int getLength() {
        return this.getWidth() * this.getHeight();
    }

    public Rect4i getRectangle() {

        int left = Integer.MAX_VALUE;
        int top = Integer.MAX_VALUE;

        int right = Integer.MIN_VALUE;
        int bottom = Integer.MIN_VALUE;

        for (int index : getPixelIndices()) {

            int x = this.getX(index);
            int y = this.getY(index);

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

    public Point2i getBarycenter() {

        IntIterable indices = this.getPixelIndices();

        long bx = 0;
        long by = 0;

        for (int i : indices) {
            int x = this.getX(i);
            int y = this.getY(i);

            bx = bx + x;
            by = by + y;

        }

        return new Point2i((int) (bx / indices.getLength()), (int) (by / indices.getLength()));
    }

    @Override
    public String toString() {
        return "count:" + getPixelIndices().getLength();
    }
}
