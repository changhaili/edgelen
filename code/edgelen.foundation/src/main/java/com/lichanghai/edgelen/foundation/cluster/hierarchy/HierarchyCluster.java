package com.lichanghai.edgelen.foundation.cluster.hierarchy;

import com.lichanghai.edgelen.foundation.pixelholder.AbstractPixelHolder;
import com.lichanghai.edgelen.foundation.TimeRecord;
import com.lichanghai.edgelen.foundation.cluster.Cluster;

import java.util.*;

/**
 * Created by lichanghai on 2018/1/12.
 * <p>
 * 层次聚类
 */
public class HierarchyCluster implements Cluster {

    static class LinkNode {

        public LinkNode next;
        public LinkNode prev;

        public ClusterTrunk trunk;

        LinkNode(ClusterTrunk trunk) {
            this.trunk = trunk;
        }
    }

    int width;
    int height;

    protected AbstractPixelHolder sourcePixels;

    public HierarchyCluster( AbstractPixelHolder sourcePixels) {

        this.sourcePixels = sourcePixels;
        this.width = sourcePixels.getWidth();
        this.height = sourcePixels.getHeight();
    }


    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    private boolean isUsable(int x, int y) {
        return sourcePixels.hasPixel(x, y);
    }

    private void splitSub(FullOrEmpty fullOrEmpty, int left, int right, int top, int bottom, List<ClusterLeaf> leafs) {

        if (left == right || top == bottom) return;

        if (fullOrEmpty.full) {
            leafs.add(new ClusterLeaf(left, right, top, bottom, this)); // TODO
        } else if (!fullOrEmpty.empty) {
            split(left, right, top, bottom, leafs);
        }
    }

    public static class FullOrEmpty {
        boolean full;
        boolean empty;

        FullOrEmpty(boolean full,
                    boolean empty) {
            this.full = full;
            this.empty = empty;
        }
    }

    private FullOrEmpty checkRegion(int left, int right, int top, int bottom) {

        boolean full = true;
        boolean empty = true;

        if (left >= right || top >= bottom) return new FullOrEmpty(true, true);

        for (int i = left; i < right; i++) {
            for (int j = top; j < bottom; j++) {

                if (isUsable(i, j)) {
                    empty = false;
                } else {
                    full = false;
                }

                if (!full && !empty) return new FullOrEmpty(full, empty);
            }
        }

        return new FullOrEmpty(full, empty);

    }

    protected void split(int left, int right, int top, int bottom, List<ClusterLeaf> leaves) {

        if (left > right - 1 || top > bottom - 1) return;

        int midX = (left + right) / 2;
        int midY = (top + bottom) / 2;

        FullOrEmpty r1 = checkRegion(left, midX, top, midY);
        FullOrEmpty r2 = checkRegion(midX, right, top, midY);
        FullOrEmpty r3 = checkRegion(left, midX, midY, bottom);
        FullOrEmpty r4 = checkRegion(midX, right, midY, bottom);

        if (r1.full && r2.full && r3.full && r4.full) {
            leaves.add(new ClusterLeaf(left, right, top, bottom, this)); //TODO
        }

        splitSub(r1, left, midX, top, midY, leaves);
        splitSub(r2, midX, right, top, midY, leaves);
        splitSub(r3, left, midX, midY, bottom, leaves);
        splitSub(r4, midX, right, midY, bottom, leaves);

    }

    @Override
    public AbstractPixelHolder[] cluster(int expectedCount) {

        TimeRecord timeRecord = TimeRecord.begin();

        List<ClusterLeaf> leaves = new ArrayList<>();
        split(0, sourcePixels.getWidth(), 0, sourcePixels.getHeight(), leaves);

        if (leaves.size() == 0) {
            return new AbstractClusterNode[0];
        }

        timeRecord.record("split time: {0}");

        Collections.sort(leaves, new Comparator<ClusterLeaf>() {
            @Override
            public int compare(ClusterLeaf l0, ClusterLeaf l1) {
                if (l0.getLeft() > l1.getLeft()) return 1;
                else if (l0.getLeft() < l1.getLeft()) return -1;

                else if (l0.getTop() > l1.getTop()) return 1;
                else if (l0.getTop() < l1.getTop()) return -1;
                return 0;
            }
        });

        ClusterLeaf lastLeaf = leaves.get(0); // TODO size
        LinkNode head = new LinkNode(new ClusterTrunk(this));
        head.trunk.add(lastLeaf);

        LinkNode lastNode = head;

        int clusterCount = 1;
        //lastLeaf.leafIndex = clusterCount;

        for (int i = 1, size = leaves.size(); i < size; i++) {

            ClusterLeaf newLeaf = leaves.get(i);

            ClusterLeaf mergedLeaf = lastLeaf.tryMerge(newLeaf);

            if (mergedLeaf != null) {

                lastLeaf.setLeft(mergedLeaf.getLeft());
                lastLeaf.setRight(mergedLeaf.getRight());

                lastLeaf.setTop(mergedLeaf.getTop());

                lastLeaf.setBottom(mergedLeaf.getBottom());

                continue;

            }

            if (!lastLeaf.isNear(newLeaf)) {

                LinkNode newNode = new LinkNode(new ClusterTrunk(this));
                lastNode.next = newNode;
                newNode.prev = lastNode;

                lastNode = newNode;

                clusterCount++;
                //newLeaf.leafIndex = clusterCount;

            }

            lastNode.trunk.add(newLeaf);
            lastLeaf = newLeaf;

        }

        timeRecord.record("generate node time: {0}");

        List<AbstractClusterNode> nodes = new ArrayList<>();

        while (head != null) {

            for (LinkNode outNode = head; outNode != null; outNode = outNode.next) {

                boolean changed = false;
                for (LinkNode inNode = outNode.next; inNode != null; inNode = inNode.next) {

                    if (outNode.trunk.isNear(inNode.trunk)) {

                        if (inNode.prev != null) {
                            inNode.prev.next = inNode.next;
                        }

                        if (inNode.next != null) {
                            inNode.next.prev = inNode.prev;
                        }

                        outNode.trunk.add(inNode.trunk);

                        changed = true;

                        clusterCount--;
                    }
                }

                if (!changed) {

                    nodes.add(outNode.trunk);

                    if (outNode.next != null) {
                        outNode.next.prev = outNode.prev;
                    }
                    if (outNode.prev != null) {
                        outNode.prev.next = outNode.next;
                    } else {
                        head = outNode.next;
                    }
                }
            }
        }

        timeRecord.record("merge time: {0}");

        Collections.sort(nodes, new Comparator<AbstractClusterNode>() {

            @Override
            public int compare(AbstractClusterNode n1, AbstractClusterNode n2) {
                int s1 = n1.getPixelSize();
                int s2 = n2.getPixelSize();
                if (s1 > s2) return -1;
                if (s1 < s2) return 1;
                return 0;
            }
        } );

        if (nodes.size() > expectedCount) {
            nodes = nodes.subList(0, expectedCount);
        }

        return nodes.toArray(new AbstractClusterNode[nodes.size()]);

    }
}
