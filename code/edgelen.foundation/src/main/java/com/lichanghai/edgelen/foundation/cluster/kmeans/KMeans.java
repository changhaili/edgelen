package com.lichanghai.edgelen.foundation.cluster.kmeans;

import com.lichanghai.edgelen.foundation.math.Regularization;
import com.lichanghai.edgelen.foundation.math.RegularizationModel;
import com.lichanghai.edgelen.foundation.utils.IntList;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 * Created by lichanghai on 2018/3/22.
 */
public class KMeans {

    RealVector [] vectors;

    public KMeans(RealVector [] vectors) {

        this.vectors = vectors;
    }

    private boolean checkStopLoop( RealVector[] means, KMeansModel[] models2) {

        double precision = 0.0001;

        for (int i = 0; i < means.length; i++) {

            for(int j = 0;j<means[i].getDimension();j++){

                if (Math.abs(means[i].getEntry(j) - models2[i].getMean().getEntry(j)) > precision) return false;
            }
        }

        return true;
    }


    public KMeansModel[] cluster(int expectedCount) {

        Regularization regularization = new Regularization(3, 0.9, vectors);

        RegularizationModel regularizationModel = regularization.decompose();

        int eigenLength = regularizationModel.getEigenLength();

        RealVector [] newVectors = new RealVector[vectors.length];
        for(int i=0;i<vectors.length;i++){
            newVectors[i] = regularizationModel.transform(vectors[i]);
        }

        KMeansModel[] models = new KMeansModel[expectedCount];

        for (int i = 0; i < expectedCount; i++) {

            double v = -1.0 + 2.0 / (expectedCount + 1) * (i + 1);

            models[i] = new KMeansModel(this, regularizationModel, newVectors);
            models[i].setMean(new ArrayRealVector(eigenLength, v));
        }

        int epoch = 0;

        while (epoch++ < 10000) {

            RealVector[] centers = new RealVector[expectedCount];
            IntList [] vectorList = new IntList[expectedCount];

            for (int i = 0; i < expectedCount; i++) {
                vectorList[i] = new IntList();
                centers[i] = new ArrayRealVector(eigenLength);
            }

            for(int vecIndex = 0;vecIndex < newVectors.length;vecIndex++){

                RealVector currVector = newVectors[vecIndex];

                int lastIndex = -1;
                double lastDist = Double.MAX_VALUE;

                for (int i = 0; i < expectedCount; i++) {

                    RealVector mean = models[i].getMean();

                    double d2 = 0;

                    for(int dim = 0; dim<eigenLength; dim++){

                        double d = mean.getEntry(dim) - currVector.getEntry(dim);
                        d2 += d* d;

                    }

                    if (d2 < lastDist) {
                        lastDist = d2;
                        lastIndex = i;
                    }
                }

                vectorList[lastIndex].add(vecIndex);

                for(int dim=0;dim<eigenLength;dim++){
                    centers[lastIndex].setEntry(dim,
                            centers[lastIndex].getEntry(dim) + currVector.getEntry(dim));
                }

            }

            for (int i = 0; i < expectedCount; i++) {

                int size = vectorList[i].size();

                for(int dim=0;dim<eigenLength;dim++){
                    centers[i].setEntry(dim,centers[i].getEntry(dim) /size);
                }
            }

            if (checkStopLoop(centers, models)) break;

            for (int i = 0; i < expectedCount; i++) {

                models[i].vectorList = vectorList[i];
                models[i].setMean(centers[i]);
            }
        }

        return models;

    }
}
