package com.lichanghai.edgelen.foundation;

import com.lichanghai.edgelen.foundation.math.Matrix3;
import com.lichanghai.edgelen.foundation.pixelholder.AbstractPixelHolder;

/**
 * Created by lichanghai on 2018/2/10.
 */
public class BackgroundState extends AbstractPixelHolder {

    private Matrix3 matrix;

    private int[] leftXs;

    private int[] rightXs;

    private int width;

    private int height;

    public BackgroundState(Matrix3 matrix, int[] leftXs, int[] rightXs, int width, int height) {
        this.matrix = matrix;
        this.leftXs = leftXs;
        this.rightXs = rightXs;
        this.width = width;
        this.height = height;
    }

    public Matrix3 getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix3 matrix) {
        this.matrix = matrix;
    }

    public int[] getLeftXs() {
        return leftXs;
    }

    public void setLeftXs(int[] leftXs) {
        this.leftXs = leftXs;
    }

    public int[] getRightXs() {
        return rightXs;
    }

    public void setRightXs(int[] rightXs) {
        this.rightXs = rightXs;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public IntIterable getPixelIndices() {
        return null;
    }

    @Override
    public boolean hasPixel(int index) {
        return false;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
