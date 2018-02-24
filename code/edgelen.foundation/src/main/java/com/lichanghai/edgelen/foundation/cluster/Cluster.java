package com.lichanghai.edgelen.foundation.cluster;

import com.lichanghai.edgelen.foundation.pixelholder.AbstractPixelHolder;

/**
 * Created by lichanghai on 2018/1/11.
 */
public interface Cluster {

    int getWidth();

    int getHeight();

    AbstractPixelHolder[] cluster(int expectedCount);

}
