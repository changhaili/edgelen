package com.lichanghai.edgelen.foundation.pixelholder;

/**
 * Created by lichanghai on 2018/2/14.
 */
public interface PixelImage {

    int getWidth();

    int getHeight();

    int getColor(int x, int y);
}
