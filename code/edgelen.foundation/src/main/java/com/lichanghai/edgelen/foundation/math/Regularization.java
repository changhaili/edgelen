package com.lichanghai.edgelen.foundation.math;

import org.apache.commons.math3.linear.*;

/**
 * Created by lichanghai on 2018/3/23.
 */
public class Regularization {

    RealVector [] vectors;

    int dimLength;

    double reservedRate;

    public Regularization(int dimLength, double reservedRate, RealVector ... vectors ){
        this.dimLength = dimLength;
        this.vectors = vectors;
        this.reservedRate = reservedRate;
    }

    // 归一化
    RealVector normalVector( RealVector minVector, RealVector maxVector, RealVector vector ){

        RealVector newVector = new ArrayRealVector(vector.getDimension());

        for(int i=0;i<dimLength;i++){

            double u = ( maxVector.getEntry(i) + minVector.getEntry(i) )/2;

            double v = (vector.getEntry(i) - u) / (maxVector.getEntry(i) - minVector.getEntry(i));
            newVector.setEntry(i, v);
        }

        return newVector;
    }


    public RegularizationModel decompose (){

        // 正则化
        RealVector minVector = new ArrayRealVector(dimLength, Double.MAX_VALUE);
        RealVector maxVector = new ArrayRealVector(dimLength, Double.MIN_VALUE);

        for(RealVector vec: vectors){

            for(int i=0;i<dimLength;i++){

                if(vec.getEntry(i) < minVector.getEntry(i)){
                    minVector.setEntry(i, vec.getEntry(i));
                }

                if(vec.getEntry(i) > maxVector.getEntry(i)){
                    maxVector.setEntry(i, vec.getEntry(i));
                }
            }
        }

        RealMatrix normalVectorMatrix = new Array2DRowRealMatrix(vectors.length, dimLength);

        for(int i = 0; i<vectors.length;i++){

            RealVector vec = this.normalVector(minVector, maxVector, vectors[i]);

            for(int j=0;j<dimLength;j++){

                normalVectorMatrix.setEntry(i, j, vec.getEntry(j));
            }
        }

        RealMatrix sd = normalVectorMatrix.transpose().multiply(normalVectorMatrix);

        sd = sd.scalarMultiply(1.0/(vectors.length -1));

        EigenDecomposition decomposition = new EigenDecomposition(sd);

        double [] eigenvalues = decomposition.getRealEigenvalues();

        RealVector [] eigenvectors = new RealVector[eigenvalues.length];

        for(int i=0;i<eigenvalues.length;i++){
            eigenvectors[i] = decomposition.getEigenvector(i);
        }

        return new RegularizationModel(this,
                eigenvalues,
                eigenvectors,
                minVector,
                maxVector);

    }

}
