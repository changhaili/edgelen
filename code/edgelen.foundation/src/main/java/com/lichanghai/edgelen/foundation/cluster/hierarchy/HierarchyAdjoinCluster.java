package com.lichanghai.edgelen.foundation.cluster.hierarchy;

import com.lichanghai.edgelen.foundation.pixelholder.AbstractPixelHolder;
import com.lichanghai.edgelen.foundation.TimeRecord;
import com.lichanghai.edgelen.foundation.utils.IntList;

import java.util.*;

/**
 * Created by lichanghai on 2018/1/12.
 * <p>
 * 层次聚类, 使用邻接矩阵
 * <p>
 * 比较慢，目前没有找到优化的方法
 */
@Deprecated
 class HierarchyAdjoinCluster extends HierarchyCluster {

    public HierarchyAdjoinCluster(AbstractPixelHolder sourcePixels) {
        super(sourcePixels);
    }


    private AbstractPixelHolder[] clusterNear(List<ClusterTrunk> trunks, int clusterCount) {

        int leaveSize = trunks.size();

        Set<ClusterTrunk> [] sets = new HashSet[leaveSize];
        IntList nearList = new IntList();

        for (int i = 0; i < leaveSize; i++) {

           sets[i] = new HashSet<>();

            for (int j = i+1; j < leaveSize; j++) {

                if (trunks.get(i).isNear(trunks.get(j))) {
                    nearList.append( (i<<16) + j);
                }
            }
        }



        for(int index : nearList) {

            int i = index >> 16;
            int j = index & 0xFFFF;

            if( sets[i] == sets[j]) continue;

            if(sets[i].contains(j)) continue;
            if(sets[j].contains(i)) continue;

            sets[i].add(trunks.get(j));
            sets[i].addAll(sets[j]);

            sets[j] = sets[i];

        }

        Map<Set, ClusterTrunk> trunkMap = new HashMap<>();

        for(Set<ClusterTrunk> set : sets){

           if(!trunkMap.containsKey(set)){

               ClusterTrunk newTrunk =new ClusterTrunk(this);
               newTrunk.clusterNodes.addAll(set);

               trunkMap.put(set, newTrunk);
           }
        }

        AbstractPixelHolder [] pixelHolders = trunkMap.values().toArray(new ClusterTrunk[trunkMap.size()]);
        //AbstractPixelHolder [] pixelHolders = trunkSet.toArray(new ClusterTrunk[trunkSet.size()]);

        Arrays.sort(pixelHolders, new Comparator<AbstractPixelHolder>() {
            @Override
            public int compare(AbstractPixelHolder o1, AbstractPixelHolder o2) {

                int t1 = ((ClusterTrunk)o1).getPixelSize();
                int t2 = ((ClusterTrunk)o2).getPixelSize();
                return t1 == t2 ? 0 : ((t1 > t2) ? -1 : 1);
            }
        });

        return Arrays.copyOf(pixelHolders, clusterCount);
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


        ClusterLeaf lastLeaf = leaves.get(0);
        List<ClusterTrunk> trunks = new ArrayList<>();

        trunks.add(new ClusterTrunk(this));
        trunks.get(0).add(lastLeaf);

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
                trunks.add(new ClusterTrunk(this));
            }

            trunks.get(trunks.size() - 1).add(newLeaf);
            lastLeaf = newLeaf;

        }


        return this.clusterNear(trunks, expectedCount);


    }
}
