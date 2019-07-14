package com.danielkim.soundrecorder.fragments;

public class Delta {

    int M;

    public Delta() {
    }


    public void setRegressionWindow(int M) {
        this.M = M;
    }

    public double[][] performDelta2D(double[][] mfccFeature) {
        int noOfMfcc = mfccFeature[0].length;
        int frameCount = mfccFeature.length;
        // 1. calculate sum of mSquare i.e., denominator
        double mSqSum = 0;
        for (int i = -M; i < M; i++) {
            mSqSum += Math.pow(i, 2);
        }

        double delta[][] = new double[frameCount][noOfMfcc];
        for (int i = 0; i < noOfMfcc; i++) {

            for (int k = 0; k < M; k++) {

                delta[k][i] = mfccFeature[k][i]; // 0 padding
            }
            // from frameCount-M to frameCount
            for (int k = frameCount - M; k < frameCount; k++) {
                // delta[l][i] = 0;
                delta[k][i] = mfccFeature[k][i];
            }
            for (int j = M; j < frameCount - M; j++) {
                // travel from -M to +M
                double sumDataMulM = 0;
                for (int m = -M; m <= +M; m++) {

                    sumDataMulM += m * mfccFeature[m + j][i];
                }
                // 3. divide
                delta[j][i] = sumDataMulM / mSqSum;
            }
        }
        return delta;
    }// end of fn

    public double[] performDelta1D(double[] data) {
        int frameCount = data.length;

        double mSqSum = 0;
        for (int i = -M; i < M; i++) {
            mSqSum += Math.pow(i, 2);
        }
        double[] delta = new double[frameCount];

        for (int k = 0; k < M; k++) {
            delta[k] = data[k]; // 0 padding
        }
        // from frameCount-M to frameCount
        for (int k = frameCount - M; k < frameCount; k++) {
            delta[k] = data[k];
        }
        for (int j = M; j < frameCount - M; j++) {
            // travel from -M to +M
            double sumDataMulM = 0;
            for (int m = -M; m <= +M; m++) {

                sumDataMulM += m * data[m + j];
            }
            // 3. divide
            delta[j] = sumDataMulM / mSqSum;
        }

        return delta;
    }
}
