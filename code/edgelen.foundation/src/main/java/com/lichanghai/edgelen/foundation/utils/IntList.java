package com.lichanghai.edgelen.foundation.utils;

import java.util.AbstractList;
import java.util.Arrays;

/**
 * Created by lichanghai on 2018/2/13.
 *
 */
public class IntList extends AbstractList<Integer> {

    private final static int DEFAULT_LENGTH = 32;

    private int[] values;

    private int _size = 0;

    public IntList() {
        this.values = new int[DEFAULT_LENGTH];
    }

    public IntList(int capacity) {
        this.values = new int[capacity];
    }

    public IntList(int[] values) {

        if (values == null) {
            this.values = new int[DEFAULT_LENGTH];

        } else {
            this.values = Arrays.copyOf(values, values.length);
            this._size = values.length;
        }
    }

    public int getAt(int index) {
        return this.values[index];
    }

    private void extendCapacity(int newCapacity) {
        this.values = Arrays.copyOf(this.values, newCapacity);
    }

    public boolean append(int e) {

        if (this._size >= this.values.length) {
            extendCapacity(this.values.length * 2);
        }

        this.values[this._size++] = e;
        return true;
    }

    public void append(int index, int element) {

        if (this._size >= this.values.length) {
            extendCapacity(this.values.length * 2);
        }

        for (int i = this._size; i > index; i--) {
            this.values[i] = this.values[i - 1];
        }

        this.values[index] = element;

        this._size++;
    }

    public void addAll(IntList list) {

        int expCapacity = this._size + list._size;
        int newCapacity = this.values.length;

        while (expCapacity >= newCapacity) {
            newCapacity *= 2;
        }

        extendCapacity(newCapacity);
        System.arraycopy(list.values, 0, this.values, this._size, list._size);

        this._size = expCapacity;
    }

    public void addAll(int[] arrays) {

        int expCapacity = this._size + arrays.length;
        int newCapacity = this.values.length;

        while (expCapacity >= newCapacity) {
            newCapacity *= 2;
        }

        extendCapacity(newCapacity);
        System.arraycopy(arrays, 0, this.values, this._size, arrays.length);

        this._size = expCapacity;
    }

    public Integer remove(int index) {

        int o = this.values[index];
        for (int i = index + 1; i < this._size; i++) {
            this.values[i - 1] = this.values[i];
        }

        this._size--;
        return o;
    }

    @Override
    public void clear() {
        this._size = 0;
    }

    public int capacity() {
        return this.values.length;
    }

    @Override
    public int size() {
        return this._size;
    }

    public int[] toIntArray() {
        return Arrays.copyOf(this.values, this._size);
    }

    public int[] getOrCopyValues() {
        return (size() == capacity()) ? values : toIntArray();
    }

    @Override
    public String toString() {
        return "count:" + _size;
    }

    public void fillAppend(int value, int count) {

        for (int i = 0; i < count; i++) {
            append(value);
        }
    }

    @Override
    public Integer get(int index) {
        return getAt(index);
    }

    @Override
    public boolean add(Integer e) {
        return append(e);
    }

    public void sort() {

        Arrays.sort(values, 0, _size);
    }

    public int binarySearch(int v) {

        return Arrays.binarySearch(values, 0, _size, v);
    }
}
