package com.lichanghai.edgelen.foundation.girth;

import com.lichanghai.edgelen.foundation.math.Point3;

/**
 * Created by lichanghai on 2018/1/20.
 */
public class PointCurve {

    public enum Quadrant {
        UP, Down, Left, Right
    }

    private final Quadrant quadrant;

    private final Point3[] points;

    public PointCurve(Quadrant quadrant, Point3[] points) {
        super();
        this.quadrant = quadrant;
        this.points = points;
    }

    public Quadrant getQuadrant() {
        return quadrant;
    }

    public Point3[] getPoints() {
        return points;
    }

}
