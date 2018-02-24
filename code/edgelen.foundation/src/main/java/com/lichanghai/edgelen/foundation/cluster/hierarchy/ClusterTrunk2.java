package com.lichanghai.edgelen.foundation.cluster.hierarchy;

import com.lichanghai.edgelen.foundation.math.Rect4i;
import com.lichanghai.edgelen.foundation.utils.IntList;
import com.lichanghai.edgelen.foundation.utils.TreeArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lichanghai on 2018/1/12.
 *
 * 试验品
 */
@Deprecated
class ClusterTrunk2 extends AbstractClusterNode {

    private List<ClusterLeaf> clusterNodes = new ArrayList<>();

    private TreeArray<List<ClusterLeaf>> leftLeaves;
    private TreeArray<List<ClusterLeaf>> rightLeaves;
    private TreeArray<List<ClusterLeaf>> topLeaves;
    private TreeArray<List<ClusterLeaf>> bottomLeaves;

    private Rect4i rectangle;

    public ClusterTrunk2(HierarchyCluster hierarchyCluster) {

        super(hierarchyCluster);

//        super(width, height);
//        rectangle = new Rect4i(width, 0, height, 0);
//
//        leftLeaves = new TreeArray<>(height);
//        rightLeaves = new TreeArray<>(height);
//        topLeaves = new TreeArray<>(width);
//        bottomLeaves = new TreeArray<>(width);
    }


    public void add(AbstractClusterNode node) {

        if (node instanceof ClusterLeaf) {

            ClusterLeaf leaf = (ClusterLeaf) node;

//            leftLeaves.computeIfAbsent(leaf.getLeft(), () -> new ArrayList<>()).append(leaf);
//            rightLeaves.computeIfAbsent(leaf.getRight(), () -> new ArrayList<>()).append(leaf);
//            topLeaves.computeIfAbsent(leaf.getTop(), () -> new ArrayList<>()).append(leaf);
//            bottomLeaves.computeIfAbsent(leaf.getBottom(), () -> new ArrayList<>()).append(leaf);

        } else {

            for (ClusterLeaf leaf : ((ClusterTrunk2) node).clusterNodes) {
                add(leaf);
            }
        }

        Rect4i rect = node.getRectangle();

        rectangle.unionSelf(rect);
    }

    @Override
    public boolean hasPixel(int index) {

        for (AbstractClusterNode clusterNode : clusterNodes) {
            if (clusterNode.hasPixel(index)) return true;
        }

        return false;
    }

    @Override
    public boolean hasPixel(int x, int y) {

        for (AbstractClusterNode clusterNode : clusterNodes) {
            if (clusterNode.hasPixel(x, y)) return true;
        }

        return false;
    }

//    private boolean isNearLeaf(ClusterLeaf leaf) {
//
//        leaf.getLeft();
//    }

    @Override
    public boolean isNear(AbstractClusterNode other) {

        if (!super.isNear(other) && !rectangle.hasOverlap(other.getRectangle())) return false;

        for (AbstractClusterNode clusterNode : clusterNodes) {
            if (clusterNode.isNear(other)) return true;
        }
        return false;
    }

    @Override
    protected int getAt(int index) {

        int i = 0;
        int count = 0;
        for (; i < this.clusterNodes.size(); i++) {

            if (index > count) {
                count += this.clusterNodes.get(i).getPixelSize();
            }
        }

        return this.clusterNodes.get(i).getAt(index - count);
    }

    @Override
    public IntIterable getPixelIndices() {

        return new IntIterable() {

            @Override
            public int getAt(int index) {
                return ClusterTrunk2.this.getAt(index);
            }

            @Override
            public int getLength() {
                return getPixelSize();
            }

            @Override
            public int[] toArray() {
                return getPixelArray().toIntArray();
            }

            @Override
            public Iterator<Integer> iterator() {

                return new Iterator<Integer>() {

                    Iterator<ClusterLeaf> nodeIterator = clusterNodes.iterator();

                    Iterator<Integer> integerIterator = null;

                    @Override
                    public boolean hasNext() {

                        if (integerIterator != null && integerIterator.hasNext()) {
                            return true;
                        }

                        integerIterator = null;

                        if (!nodeIterator.hasNext()) {
                            return false;
                        }

                        AbstractClusterNode node = nodeIterator.next();
                        integerIterator = node.getPixelIndices().iterator();

                        if (integerIterator == null) return false;

                        return integerIterator.hasNext();

                    }

                    @Override
                    public Integer next() {
                        return integerIterator.next();
                    }

                    @Override
                    public void remove() {

                    }
                };
            }
        };

    }

    @Override
    public int getPixelSize() {

        int length = 0;

        for (AbstractClusterNode clusterNode : clusterNodes) {
            length += clusterNode.getPixelSize();
        }

        return length;
    }

    @Override
    protected void fillPixelList(IntList list) {

        for (AbstractClusterNode node : clusterNodes) {
            node.fillPixelList(list);
        }
    }

    @Override
    public Rect4i getRectangle() {
        return this.rectangle;
    }
}
