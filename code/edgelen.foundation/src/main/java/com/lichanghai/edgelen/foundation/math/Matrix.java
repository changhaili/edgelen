package com.lichanghai.edgelen.foundation.math;

/**
 * Created by lichanghai on 2018/3/23.
 */
public interface Matrix {

    int getRowCount();

    int getColumnCount();

    double getValue(int row, int column);

    void setValue(int row, int column, double value);
}
