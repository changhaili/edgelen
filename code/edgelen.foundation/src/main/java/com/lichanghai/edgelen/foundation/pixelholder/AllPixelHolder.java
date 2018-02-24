package com.lichanghai.edgelen.foundation.pixelholder;

import com.lichanghai.edgelen.foundation.utils.IntegerFilter;

/**
 * Created by lichanghai on 2018/2/9.
 */
public class AllPixelHolder extends AbstractPixelHolder {

    private final int[] colors;

    protected int width;

    protected int height;

    protected IntegerFilter hasPixelFun;

    public AllPixelHolder(int width, int height, int[] colors, IntegerFilter hasPixelFun) {

        this.width = width;
        this.height = height;
        this.colors = colors;

        this.hasPixelFun = hasPixelFun;
    }

    public IntIterable getPixelIndices() {

        return new IntIterable() {

            @Override
            public int getLength() {
                return AllPixelHolder.this.getLength();
            }

            @Override
            public int getAt(int index) {
                return index;
            }

        };
    }

    @Override
    public boolean hasPixel(int index) {

        if(hasPixelFun == null) return true;

        return  hasPixelFun.isSatisfied(index);

    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

}
