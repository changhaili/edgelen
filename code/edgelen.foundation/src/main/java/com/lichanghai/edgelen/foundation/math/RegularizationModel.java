package com.lichanghai.edgelen.foundation.math;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Created by lichanghai on 2018/3/23.
 */
public class RegularizationModel {

    private Regularization regularization;

    private RealVector[] eigenvectors;

    private double[] eigenvalues;

    private RealMatrix eigenmatrix;

    private RealVector minVector;

    private RealVector maxVector;

    int eigenLength;

    public RegularizationModel(Regularization regularization,
                               double[] eigenvalues,
                               RealVector[] eigenvectors,
                               RealVector minVector,
                               RealVector maxVector) {

        this.regularization = regularization;
        this.eigenvalues = eigenvalues;
        this.eigenvectors = eigenvectors;
        this.minVector = minVector;
        this.maxVector = maxVector;

        double total = MathUtils.sum(eigenvalues);

        double sum = 0;
        int lastIndex = 0;

        for (; lastIndex < eigenvalues.length; lastIndex++) {

            sum += eigenvalues[lastIndex];
            if (sum / total > regularization.reservedRate) {
                break;
            }
        }

        eigenLength = lastIndex + 1;

        eigenmatrix = new Array2DRowRealMatrix(regularization.dimLength, lastIndex + 1);

        for (int i = 0; i <= lastIndex; i++) {

            for (int j = 0; j < regularization.dimLength; j++) {

                eigenmatrix.setEntry(j, i, eigenvectors[i].getEntry(j));
            }
        }
    }

    public Matrix getEigenmatrix() {

        return new Matrix() {
            @Override
            public int getRowCount() {
                return eigenmatrix.getRowDimension();
            }

            @Override
            public int getColumnCount() {
                return eigenmatrix.getColumnDimension();
            }

            @Override
            public double getValue(int row, int column) {
                return eigenmatrix.getEntry(row, column);
            }

            @Override
            public void setValue(int row, int column, double value) {
                eigenmatrix.setEntry(row, column, value);
            }
        };
    }

    public RealVector transform(RealVector vector) {

        RealVector v = this.regularization.normalVector(minVector, maxVector, vector);

        RealMatrix p = new Array2DRowRealMatrix(new double[][]{v.toArray()});

        RealMatrix np = p.multiply(eigenmatrix);

        return new ArrayRealVector(np.getRow(0));
    }

    public int getEigenLength() {
        return eigenLength;
    }
}
