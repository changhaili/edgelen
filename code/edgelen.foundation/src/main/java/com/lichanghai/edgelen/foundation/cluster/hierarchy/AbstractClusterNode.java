package com.lichanghai.edgelen.foundation.cluster.hierarchy;

import com.lichanghai.edgelen.foundation.pixelholder.AbstractPixelHolder;
import com.lichanghai.edgelen.foundation.cluster.Cluster;
import com.lichanghai.edgelen.foundation.math.Rect4i;
import com.lichanghai.edgelen.foundation.utils.IntList;

/**
 * Created by lichanghai on 2018/1/11.
 */
abstract class AbstractClusterNode extends AbstractPixelHolder {

    protected HierarchyCluster cluster;

    AbstractClusterNode(Cluster cluster) {
        this.cluster = (HierarchyCluster)cluster;
    }

    @Override
    public int getWidth() {
        return cluster.width;
    }

    @Override
    public int getHeight() {
        return cluster.height;
    }

    protected boolean isNear(int a, int b) {
        return Math.abs(a - b) < 2;
    }

    public boolean isNear(AbstractClusterNode other) {

        Rect4i r1 = this.getRectangle();
        Rect4i r2 = other.getRectangle();

        if (isNear(r1.left, r2.right) && hasJoin(r1.top, r1.bottom, r2.top, r2.bottom)) return true;
        if (isNear(r1.right, r2.left) && hasJoin(r1.top, r1.bottom, r2.top, r2.bottom)) return true;

        if (isNear(r1.top, r2.bottom) && hasJoin(r1.left, r1.right, r2.left, r2.right)) return true;
        if (isNear(r1.bottom, r2.top) && hasJoin(r1.left, r1.right, r2.left, r2.right)) return true;

        return false;
    }

    protected boolean hasJoin(int min1, int max1, int min2, int max2) {

        if (min1 >= min2 && min1 <= max2) return true;
        if (max1 >= min2 && max1 < max2) return true;

        if (min2 >= min1 && min2 <= max1) return true;
        if (max2 >= min1 && max2 <= max1) return true;

        return false;
    }

    public abstract int getPixelSize();

    protected abstract int getAt(int selfIndex);

    public String toString() {
        return "count:" + getPixelSize();
    }

    protected abstract void fillPixelList(IntList list);

    public IntList getPixelArray() {

        IntList list = new IntList();

        fillPixelList(list);
        return list;
    }
}
