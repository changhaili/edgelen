package com.lichanghai.edgelen.foundation.cluster.hierarchy;

import com.lichanghai.edgelen.foundation.cluster.Cluster;
import com.lichanghai.edgelen.foundation.math.Rect4i;
import com.lichanghai.edgelen.foundation.utils.IntList;
import com.lichanghai.edgelen.foundation.pixelholder.AbstractPixelHolder;

/**
 * Created by lichanghai on 2018/1/12.
 */
class ClusterLeaf extends AbstractClusterNode {

    private Rect4i rectangle;

    public ClusterLeaf(int left, int right, int top, int bottom, Cluster Cluster) {

        super(Cluster);

        rectangle = new Rect4i(left, right, top, bottom);
    }

    public int getLeft() {
        return rectangle.left;
    }

    public void setLeft(int left) {
        rectangle.left = left;
    }

    public int getRight() {
        return rectangle.right;
    }

    public void setRight(int right) {
        rectangle.right = right;
    }

    public int getTop() {
        return rectangle.top;
    }

    public void setTop(int top) {
        rectangle.top = top;
    }

    public int getBottom() {
        return rectangle.bottom;
    }

    public void setBottom(int bottom) {
        rectangle.bottom = bottom;
    }

    @Override
    public boolean isNear(AbstractClusterNode other) {

        if (other instanceof ClusterLeaf) {

            return super.isNear(other);

//            long index = ((long) leafIndex) * ((ClusterLeaf) other).leafIndex;
//
//            boolean checkBit = index != 0 && index < Integer.MAX_VALUE;
//            if (checkBit && hierarchyCluster.notNearBitSet.getAt((int) index)) {
//                return false;
//            }
//
//            boolean r = super.isNear(other);
//            if (checkBit && !r) {
//                hierarchyCluster.notNearBitSet.set((int) index);
//            }
//            return r;

        } else {
            return other.isNear(this);
        }
    }

    public ClusterLeaf tryMerge(ClusterLeaf other) {

        if (isNear(rectangle.top, other.rectangle.top) && isNear(rectangle.bottom, other.rectangle.bottom)) {

            if (isNear(rectangle.right, other.rectangle.left)) {
                return new ClusterLeaf(rectangle.left, other.rectangle.right, rectangle.top, rectangle.bottom, cluster);
            } else if (isNear(rectangle.left, other.rectangle.right)) {
                return new ClusterLeaf(other.rectangle.left, rectangle.right, rectangle.top, rectangle.bottom, cluster);
            }
        }

        if (isNear(rectangle.left, other.rectangle.left) && isNear(rectangle.right, other.rectangle.right)) {

            if (isNear(rectangle.bottom, other.rectangle.top)) {
                return new ClusterLeaf(rectangle.left, rectangle.right, rectangle.top, other.rectangle.bottom, cluster);
            }
            if (isNear(rectangle.top, other.rectangle.bottom)) {
                return new ClusterLeaf(rectangle.left, rectangle.right, other.rectangle.top, rectangle.bottom, cluster);
            }
        }

        return null;
    }

    @Override
    public int getPixelSize() {
        return rectangle.getHeight() * rectangle.getWidth();
    }

    @Override
    public AbstractPixelHolder.IntIterable getPixelIndices() {

        return new AbstractPixelHolder.IntIterable() {
            @Override
            public int getAt(int index) {

                return ClusterLeaf.this.getAt(index);
            }

            @Override
            public int getLength() {
                return getPixelSize();
            }

        };
    }

    protected int getAt(int selfIndex) {

        int x = selfIndex % rectangle.getWidth();
        int y = selfIndex / rectangle.getWidth();

        return getIndex(rectangle.left + x, rectangle.top + y);

    }

    @Override
    public boolean hasPixel(int x, int y) {
        return x >= rectangle.left && x < rectangle.right && y >= rectangle.top && y < rectangle.bottom;
    }

    @Override
    public boolean hasPixel(int index) {

        int x = getX(index);
        int y = getY(index);
        return hasPixel(x, y);
    }

    @Override
    protected void fillPixelList(IntList list) {

        for (int x = rectangle.left; x < rectangle.right; x++) {
            for (int y = rectangle.top; y < rectangle.bottom; y++) {

                list.append(getIndex(x, y));
            }
        }
    }

    @Override
    public Rect4i getRectangle() {
        return rectangle;
    }

}
