package com.lichanghai.edgelen.foundation.pixelholder;

import com.lichanghai.edgelen.foundation.utils.IntList;

import java.util.BitSet;

/**
 *
 * Created by lichanghai on 2018/2/1.
 *
 * 按前景色索引的位置记录点
 */
public class IndexPixelHolder extends AbstractPixelHolder {

    private final IntList pixelIndices;

    private BitSet pixelBitSet = null;

    private final int width;

    private final int height;

    public IndexPixelHolder(int width, int height, IntList pixelIndices) {

        this.width = width;
        this.height = height;

        this.pixelIndices = pixelIndices;

        pixelIndices.sort();

        //Arrays.sort(this.pixelIndices);


//        for (int i : this.pixelIndices) {
//            pixelBitSet.set(i);
//        }
    }

    public IntIterable getPixelIndices() {

        return new IntIterable() {
            @Override
            public int getLength() {
                return pixelIndices.size();
            }

            @Override
            public int getAt(int index) {
                return pixelIndices.get(index);
            }

            @Override
            public IntList toList() {
                return pixelIndices;
            }

            @Override
            public int[] toArray() {
                return pixelIndices.getOrCopyValues();
            }
        };
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }


    @Override
    public boolean hasPixel(int index) {

        //return pixelIndices.binarySearch(index) >=0;

        if (pixelBitSet == null) {

            pixelBitSet = new BitSet();
            for (int i : this.pixelIndices) {
                pixelBitSet.set(i);
            }
        }
        return this.pixelBitSet.get(index);
    }

}
