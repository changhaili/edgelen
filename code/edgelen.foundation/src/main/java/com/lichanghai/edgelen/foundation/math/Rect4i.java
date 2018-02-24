package com.lichanghai.edgelen.foundation.math;

import java.text.MessageFormat;

/**
 * Created by lichanghai on 2018/1/9.
 */
public class Rect4i {

    public int left;

    public int top;

    public int right;

    public int bottom;

    public Rect4i() {
    }


    public Rect4i(int left, int right, int top, int bottom) {

        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }


    public Rect4i union(Rect4i other) {

        int l = Math.min(left, other.left);
        int r = Math.min(right, other.right);
        int t = Math.min(top, other.top);
        int b = Math.min(bottom, other.bottom);

        return new Rect4i(l, r, t, b);
    }

    public Rect4i unionSelf(Rect4i other) {

        if (left > other.left) left = other.left;
        if (right < other.right) right = other.right;
        if (top > other.top) top = other.top;
        if (bottom < other.bottom) bottom = other.bottom;
        return this;
    }

    public int getWidth() {
        return right - left;
    }

    public int getHeight() {
        return bottom - top;
    }

    public boolean isInnerPoint(int x, int y) {

        return x >= left && x <= right && y >= top && y <= bottom;
    }


    public boolean hasOverlap(Rect4i other) {

        if (this.isInnerPoint(other.left, other.top)) return true;
        if (this.isInnerPoint(other.right, other.top)) return true;

        if (this.isInnerPoint(other.left, other.bottom)) return true;
        if (this.isInnerPoint(other.right, other.bottom)) return true;

        if (other.isInnerPoint(this.left, this.top)) return true;
        if (other.isInnerPoint(this.right, this.top)) return true;

        if (other.isInnerPoint(this.left, this.bottom)) return true;
        if (other.isInnerPoint(this.right, this.bottom)) return true;

        return false;
    }

    @Override
    public String toString() {
        return MessageFormat.format("left: {0}, right: {1} , top: {2}, bottom: {3}",
                left, right, top, bottom);
    }
}
