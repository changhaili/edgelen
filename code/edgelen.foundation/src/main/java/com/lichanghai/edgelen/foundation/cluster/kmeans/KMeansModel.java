package com.lichanghai.edgelen.foundation.cluster.kmeans;

import com.lichanghai.edgelen.foundation.math.RegularizationModel;
import com.lichanghai.edgelen.foundation.utils.IntList;
import org.apache.commons.math3.linear.RealVector;

import java.util.Arrays;

/**
 * Created by lichanghai on 2018/3/23.
 */
public class KMeansModel {

    KMeans kMeans;

    RegularizationModel regularizationModel;

    RealVector [] normalVectors;

    IntList vectorList = new IntList();

    public KMeansModel(KMeans kMeans, RegularizationModel regularizationModel, RealVector [] normalVectors){

        this.kMeans = kMeans;
        this.regularizationModel = regularizationModel;
        this.normalVectors = normalVectors;
    }


    //public Point3 minPoint = new Point3(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE);

    //public Point3 maxPoint = new Point3(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);

    private RealVector mean = null;

    public double[] sort(int dimIndex) {

        double[] vs = new double[vectorList.size()];

        for (int i = 0; i < vs.length; i++) {

            vs[i] = normalVectors[vectorList.get(i)].getEntry(dimIndex);
        }

        Arrays.sort(vs);
        return vs;
    }

    public RealVector getMean() {
        return mean;
    }

    public void setMean(RealVector mean) {
        this.mean = mean;
    }


    public int getVectorSize(){
        return this.vectorList.size();
    }

    public  KMeans getKMeans(){
        return this.kMeans;
    }

    public RegularizationModel getRegularizationModel() {
        return regularizationModel;
    }
}
