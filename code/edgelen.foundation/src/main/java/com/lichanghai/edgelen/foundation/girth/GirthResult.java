package com.lichanghai.edgelen.foundation.girth;

/**
 * Created by lichanghai on 2018/1/19.
 */
public class GirthResult {

    private final String id;

    private final double recommender;

    private final double[] others;

    public double getRecommender() {
        return recommender;
    }

    public double[] getOthers() {
        return others;
    }

    public GirthResult(String id, double recommender, double[] others) {

        this.id = id;
        this.recommender = recommender;
        this.others = others;
    }

}
