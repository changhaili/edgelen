package com.lichanghai.edgelen.foundation;

/**
 * Created by lichanghai on 2018/3/22.
 */
public class AbsoluteColorSeparator implements PixelSeparator {

    private ColorFilter backFilter;

    private  ColorFilter foreFilter;

    public AbsoluteColorSeparator(ColorFilter backFilter, ColorFilter foreFilter){

        this.backFilter = backFilter;
        this.foreFilter = foreFilter;
    }

    @Override
    public boolean isBack(int pixel) {
        return backFilter.isSame(pixel);
    }

    @Override
    public boolean isFore(int pixel) {
        return foreFilter.isSame(pixel);
    }
}
