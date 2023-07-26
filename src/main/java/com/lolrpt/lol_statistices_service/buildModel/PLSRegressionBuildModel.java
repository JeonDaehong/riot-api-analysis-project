package com.lolrpt.lol_statistices_service.buildModel;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.util.MathArrays;

public class PLSRegressionBuildModel {

    // 데이터 예시: 10개의 컬럼과 50개의 row를 가진 행렬
    private double[][] data = {
            {1.25, 1.72, 3.005, 3.5, 1.155, 3.5876, 7.777, 1.8, 78.9, 1.501},
            {1.25, 1.72, 3.5, 3.5, 1.155, 3.5876, 7.71, 1.8, 78.9, 1.501}
            // Add more rows here
    };

    // PLS 회귀 모델 생성
    public void buildPLSModel(int numComponents) {
        // 데이터 행렬을 생성합니다.
        RealMatrix dataMatrix = new Array2DRowRealMatrix(data);

        // X, Y 데이터를 분리합니다.
        RealMatrix xMatrix = dataMatrix.getSubMatrix(0, dataMatrix.getRowDimension() - 1, 0, dataMatrix.getColumnDimension() - 2);
        RealMatrix yMatrix = dataMatrix.getSubMatrix(0, dataMatrix.getRowDimension() - 1, dataMatrix.getColumnDimension() - 1, dataMatrix.getColumnDimension() - 1);

        // 각 컬럼에서 평균을 빼줍니다.
        double[] xMean = new double[xMatrix.getColumnDimension()];
        for (int j = 0; j < xMatrix.getColumnDimension(); j++) {
            double sum = 0.0;
            for (int i = 0; i < xMatrix.getRowDimension(); i++) {
                sum += xMatrix.getEntry(i, j);
            }
            xMean[j] = sum / xMatrix.getRowDimension();
        }
        for (int i = 0; i < xMatrix.getRowDimension(); i++) {
            for (int j = 0; j < xMatrix.getColumnDimension(); j++) {
                xMatrix.setEntry(i, j, xMatrix.getEntry(i, j) - xMean[j]);
            }
        }

        double yMean = 0.0;
        for (int i = 0; i < yMatrix.getRowDimension(); i++) {
            yMean += yMatrix.getEntry(i, 0);
        }
        yMean /= yMatrix.getRowDimension();
        for (int i = 0; i < yMatrix.getRowDimension(); i++) {
            yMatrix.setEntry(i, 0, yMatrix.getEntry(i, 0) - yMean);
        }

        // X 행렬과 Y 벡터의 공분산을 계산합니다.
        RealMatrix xTranspose = xMatrix.transpose();
        RealMatrix covarianceMatrix = new Covariance(xMatrix).getCovarianceMatrix();

        // X 행렬과 Y 벡터의 cross-covariance matrix를 계산합니다.
        RealMatrix crossCovarianceMatrix = xTranspose.multiply(yMatrix);

        // Singular Value Decomposition(SVD)을 수행합니다.
        RealMatrix crossCovarianceMatrixTranspose = crossCovarianceMatrix.transpose();
        RealMatrix sInverse = MatrixUtils.inverse(crossCovarianceMatrixTranspose.multiply(crossCovarianceMatrix));
        RealMatrix productMatrix = covarianceMatrix.multiply(crossCovarianceMatrix).multiply(sInverse).multiply(crossCovarianceMatrixTranspose);

        // SVD를 위한 행렬 분해를 수행합니다.
        EigenDecomposition eigen = new EigenDecomposition(productMatrix);

        // PLS Components 개수에 따른 회귀식 계수를 구합니다.
        RealMatrix coeffs = eigen.getV().getSubMatrix(0, eigen.getV().getRowDimension() - 1, 0, numComponents - 1);
        RealMatrix beta = sInverse.multiply(crossCovarianceMatrixTranspose).multiply(coeffs);

        // 최종 PLS 회귀식 계수를 출력합니다.
        System.out.println("PLS Regression Coefficients:");
        for (int i = 0; i < beta.getRowDimension(); i++) {
            for (int j = 0; j < beta.getColumnDimension(); j++) {
                System.out.print(beta.getEntry(i, j) + " ");
            }
            System.out.println();
        }
    }

}
