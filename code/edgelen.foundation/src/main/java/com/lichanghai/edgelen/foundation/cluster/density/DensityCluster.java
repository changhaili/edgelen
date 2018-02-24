package com.lichanghai.edgelen.foundation.cluster.density;

import com.lichanghai.edgelen.foundation.pixelholder.AbstractPixelHolder;
import com.lichanghai.edgelen.foundation.pixelholder.IndexPixelHolder;
import com.lichanghai.edgelen.foundation.cluster.Cluster;
import com.lichanghai.edgelen.foundation.utils.IntList;

import java.util.*;

/**
 * Created by lichanghai on 2018/1/25.
 *
 * 伪密度聚类（目前距离为1为密度可达）
 *
 * DensityCluster 速度，精度目前都略快于HierarchyCluster，内存还有待优化
 */
public class DensityCluster implements Cluster {

    AbstractPixelHolder sourcePixels;

    public DensityCluster(AbstractPixelHolder pixelHolder) {
        this.sourcePixels = pixelHolder;
    }

    @Override
    public int getWidth() {
        return sourcePixels.getWidth();
    }

    @Override
    public int getHeight() {
        return sourcePixels.getHeight();
    }

    /**
     * 聚类
     *
     * @return
     */
    @Override
    public IndexPixelHolder[] cluster(int clusterCount) {

        ArrayList<IntList> groups = new ArrayList<IntList>();

//        int[] groupIndices = new int[sourcePixels.getLength()];
//        for (int i = 0; i < groupIndices.length; i++) {
//            groupIndices[i] = -1;
//        }

        Map<Integer, Integer> groupIndices = new HashMap<>();

        int[] pixels = sourcePixels.getPixelIndices().toArray();

        for (int i = 0; i < pixels.length; i++) {

            int index = pixels[i];
            int x = sourcePixels.getX(index);
            int y = sourcePixels.getY(index);

            if (!sourcePixels.hasPixel(x, y))
                continue;

            int a0 = (x - 1 >= 0 && y - 1 >= 0) ? sourcePixels.getIndex(x - 1, y - 1) : -1;
            int a1 = (y - 1 >= 0) ? sourcePixels.getIndex(x, y - 1) : -1;
            int a2 = (x + 1 < sourcePixels.getWidth() && y - 1 >= 0) ? sourcePixels.getIndex(x + 1, y - 1) : -1;
            int a3 = (x - 1 >= 0) ? sourcePixels.getIndex(x - 1, y) : -1;

            int groupIndex = -1;

            if (a0 != -1 && groupIndices.containsKey(a0)) {
                groupIndex = groupIndices.get(a0);
            }

            if (a1 != -1 && groupIndices.containsKey(a1) && groupIndices.get(a1) != groupIndex) { // TODO

                if (groupIndex != -1) {

                    IntList cg = groups.get(groupIndices.get(a1));
                    groups.get(groupIndex).addAll(cg);
                    for (int j : cg) {
                        groupIndices.put(j, groupIndex);//[i] = groupIndex;
                    }
                    cg.clear();
                } else {
                    groupIndex = groupIndices.get(a1);

                }

            }

            if (a2 != -1 && groupIndices.containsKey(a2) && groupIndices.get(a2) != groupIndex) { // TODO

                if (groupIndex != -1) {

                    IntList cg = groups.get(groupIndices.get(a2));
                    groups.get(groupIndex).addAll(cg);
                    for (int j : cg) {
                        groupIndices.put(j, groupIndex);//[i] = groupIndex;
                    }
                    cg.clear();
                } else {
                    groupIndex = groupIndices.get(a2);

                }

            }

            if (a3 != -1 && groupIndices.containsKey(a3) && groupIndices.get(a3) != groupIndex) {

                if (groupIndex != -1) {

                    IntList cg = groups.get(groupIndices.get(a3));
                    groups.get(groupIndex).addAll(cg);
                    for (int j : cg) {
                        groupIndices.put(j, groupIndex);
                    }
                    cg.clear();
                } else {
                    groupIndex = groupIndices.get(a3);

                }

            }

            IntList group = (groupIndex == -1) ? new IntList() : groups.get(groupIndex);
            group.append(index);
            if (groupIndex == -1)
                groups.add(group);
            groupIndices.put(index, (groupIndex == -1) ? groups.size() - 1 : groupIndex);

        }


        IntList[] groupArray = groups.toArray(new IntList[groups.size()]);

        Arrays.sort(groupArray, new Comparator<IntList>() {

            public int compare(IntList o1, IntList o2) {

                int i1 = o1.size();
                int i2 = o2.size();

                return i1 == i2 ? 0 : (i1 < i2 ? 1 : -1);
            }

        });

        IndexPixelHolder[] pixelGrups = new IndexPixelHolder[clusterCount];

        for (int i = 0; i < clusterCount; i++) {
            pixelGrups[i] = new IndexPixelHolder(sourcePixels.getWidth(), sourcePixels.getHeight(), groupArray[i]);
        }

        return pixelGrups;
    }


    /**
     * 聚类
     *
     * @return
     */
    public IndexPixelHolder[] cluster2(int clusterCount) {

        ArrayList<IntList> groups = new ArrayList<IntList>();

        int[] groupIndices = new int[sourcePixels.getLength()];
        for (int i = 0; i < groupIndices.length; i++) {
            groupIndices[i] = -1;
        }

        for (int y = 0; y < sourcePixels.getHeight(); y++) {
            for (int x = 0; x < sourcePixels.getWidth(); x++) {

                int index = sourcePixels.getIndex(x, y);

                if (!sourcePixels.hasPixel(x, y))
                    continue;

                int a0 = (x - 1 >= 0 && y - 1 >= 0) ? sourcePixels.getIndex(x - 1, y - 1) : -1;
                int a1 = (y - 1 >= 0) ? sourcePixels.getIndex(x, y - 1) : -1;
                int a2 = (x + 1 < sourcePixels.getWidth() && y - 1 >= 0) ? sourcePixels.getIndex(x + 1, y - 1) : -1;
                int a3 = (x - 1 >= 0) ? sourcePixels.getIndex(x - 1, y) : -1;

                int groupIndex = -1;

                if (a0 != -1 && groupIndices[a0] != -1) {
                    groupIndex = groupIndices[a0];
                }

                if (a1 != -1 && groupIndices[a1] != -1 && groupIndex != groupIndices[a1]) {

                    if (groupIndex != -1) {

                        IntList cg = groups.get(groupIndices[a1]);
                        groups.get(groupIndex).addAll(cg);
                        for (int i : cg) {
                            groupIndices[i] = groupIndex;
                        }
                        cg.clear();
                    } else {
                        groupIndex = groupIndices[a1];

                    }

                }

                if (a2 != -1 && groupIndices[a2] != -1 && groupIndex != groupIndices[a2]) {

                    if (groupIndex != -1) {

                        IntList cg = groups.get(groupIndices[a2]);
                        groups.get(groupIndex).addAll(cg);
                        for (int i : cg) {
                            groupIndices[i] = groupIndex;
                        }
                        cg.clear();
                    } else {
                        groupIndex = groupIndices[a2];

                    }

                }

                if (a3 != -1 && groupIndices[a3] != -1 && groupIndex != groupIndices[a3]) {

                    if (groupIndex != -1) {

                        IntList cg = groups.get(groupIndices[a3]);
                        groups.get(groupIndex).addAll(cg);
                        for (int i : cg) {
                            groupIndices[i] = groupIndex;
                        }
                        cg.clear();
                    } else {
                        groupIndex = groupIndices[a3];

                    }

                }

                IntList group = (groupIndex == -1) ? new IntList() : groups.get(groupIndex);
                group.append(index);
                if (groupIndex == -1)
                    groups.add(group);
                groupIndices[index] = (groupIndex == -1) ? groups.size() - 1 : groupIndex;

            }
        }

        IntList[] groupArray = groups.toArray(new IntList[groups.size()]);

        Arrays.sort(groupArray, new Comparator<IntList>() {

            public int compare(IntList o1, IntList o2) {

                int i1 = o1.size();
                int i2 = o2.size();

                return i1 == i2 ? 0 : (i1 < i2 ? 1 : -1);
            }

        });

        IndexPixelHolder[] pixelGrups = new IndexPixelHolder[clusterCount];

        for (int i = 0; i < clusterCount; i++) {
            pixelGrups[i] = new IndexPixelHolder(sourcePixels.getWidth(), sourcePixels.getHeight(), groupArray[i]);
        }

        return pixelGrups;
    }

}
