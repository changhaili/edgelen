package com.lichanghai.edgelen.foundation.utils;

import java.util.BitSet;
import java.util.function.Supplier;

/**
 * Created by lichanghai on 2018/2/13.
 */
@Deprecated
public class TreeArray<T> {

    private static class Node {

        public Node parent;
        public Node left;
        public Node right;
        public Object data;
    }

    private final BitSet bitSet;

    private Node root = new Node();

    private final int deep;

    private final int length;

    public TreeArray(int length) {

        deep = (int) (Math.log(length) / Math.log(2)) + 1;
        bitSet = new BitSet(length);
        this.length = length;
    }

    private Node getNode(int index, boolean create) {

        Node node = root;
        for (int i = deep - 1; i >= 0; i--) {

            Node child = null;
            int b = (index >> deep) & 0x01;
            if (b == 0) {
                child = node.left;
                if (child == null && create) {
                    child = new Node();
                    node.left = child;
                    child.parent = node;

                }
            } else {

                child = node.right;
                if (child == null && create) {
                    child = new Node();
                    node.right = child;
                    child.parent = node;
                }
            }
            node = child;

            if (node == null) return node;
        }

        return node;
    }

    public T get(int index) {

        if (!bitSet.get(index)) return null;

        Node node = getNode(index, false);

        return node == null ? null : (T) node.data;
    }

    public T set(int index, T element) {

        Node node = getNode(index, true);

        T data = (T) node.data;
        node.data = element;

        bitSet.set(index);

        return data;
    }

    public void remove(int index) {

        Node node = getNode(index, false);

        while (node != root) {

            Node parent = node.parent;

            if (parent.left == node) {
                parent.left = null;
            }
            if (parent.right == node) {
                parent.right = null;
            }

            if (parent.left != null || parent.right != null) {
                return;
            }
            node = parent;
        }
    }

    public int getLength() {

        return this.length;
    }

    public boolean contains(int index) {

        return bitSet.get(index);
    }

    public T computeIfAbsent(int index, Supplier<T> supplier) {

        Node node = this.getNode(index, true);
        if (contains(index)) {
            return (T) node.data;
        }

        node.data = supplier.get();

        return (T) node.data;
    }

    private int getIndex(Node node) {

        int v = 0;
        int d = 0;

        while (node != null) {

            int b = 0;
            if (node.parent != null && node.right == node) {
                b = 1;
            }

            v += (b << d);
            d++;
        }

        return v;
    }

    public int search(int index) {

        Node node = root;
        for (int i = deep - 1; i >= 0; i--) {

            Node child = null;
            int b = (index >> deep) & 0x01;
            if (b == 0) {
                child = node.left;
            } else {
                child = node.right;
            }

            if (child == null) {
                break;
            }
        }

        if (node.left == null && node.right == null) return index;

        return index + 1; // TODO
    }
}
