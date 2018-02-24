package com.lichanghai.edgelen.foundation.pixelholder;

import com.lichanghai.edgelen.foundation.utils.IntegerFilter;

/**
 * Created by lichanghai on 2018/2/14.
 */
public class ImagePixelHolder extends AbstractPixelHolder {

    private int width;

    private int height;

    private IntegerFilter filter;

    private PixelImage image;

    private int step ;

    public ImagePixelHolder(int step, PixelImage image) {

        this.width = image.getWidth() / step;
        this.height = image.getHeight() / step;

        this.step = step;
        this.image = image;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public IntIterable getPixelIndices() {

        return new IntIterable() {

            @Override
            public int getLength() {
                return ImagePixelHolder.this.getLength();
            }

            @Override
            public int getAt(int index) {
                return index;
            }

        };
    }

    public int getColor(int x, int y) {
        return this.image.getColor(x * step, y*step);

    }

    @Override
    public boolean hasPixel(int index) {
        return filter == null ? true : filter.isSatisfied(index);
    }

    public void attachFilter(IntegerFilter filter) {
        this.filter = filter;
    }
}
