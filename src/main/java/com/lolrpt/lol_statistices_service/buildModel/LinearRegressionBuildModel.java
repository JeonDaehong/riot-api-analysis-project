package com.lolrpt.lol_statistices_service.buildModel;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class LinearRegressionBuildModel {

    public static void main(String[] args) {

        // Sample data
        double[][] data = {
                {1.0, 2.0, 3.0, 4.0, 5.0},
                {2.0, 3.0, 4.0, 5.0, 6.0},
                // Add your data here...
                // {col1_row1, col2_row1, col3_row1, col4_row1, col5_row1},
                // {col1_row2, col2_row2, col3_row2, col4_row2, col5_row2},
                // ...
                // {col1_row50, col2_row50, col3_row50, col4_row50, col5_row50}
        };

        // Create a MultipleLinearRegression instance
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();

        // Extract the response (dependent) variable
        double[] response = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            response[i] = data[i][0]; // Assuming the response variable is in the first column
        }

        // Extract the predictor (independent) variables
        double[][] predictors = new double[data.length][data[0].length - 1];
        for (int i = 0; i < data.length; i++) {
            // Skip the first column as it is the response variable
            if (data[i].length - 1 >= 0) System.arraycopy(data[i], 1, predictors[i], 0, data[i].length - 1);
        }

        // Add data to the regression model
        regression.newSampleData(response, predictors);

        // Retrieve the estimated coefficients (beta0, beta1, beta2, ..., betaN)
        double[] coefficients = regression.estimateRegressionParameters();

        // Print the estimated coefficients
        System.out.println("Estimated Coefficients:");
        for (int i = 0; i < coefficients.length; i++) {
            System.out.println("Beta" + i + ": " + coefficients[i]);
        }
    }
}
