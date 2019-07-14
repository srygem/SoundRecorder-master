package com.danielkim.soundrecorder.fragments;

public class DCT {


    final int numCoefficients;
    /**
     * number of Mel Filters
     */
    final int M;


    public DCT(int numCoefficients, int M) {
        this.numCoefficients = numCoefficients;
        this.M = M;
    }

    public double[] perform(double y[]) {
        final double cepc[] = new double[numCoefficients];
        // perform DCT
        for (int n = 1; n <= numCoefficients; n++) {
            for (int i = 1; i <= M; i++) {
                cepc[n - 1] += y[i - 1] * Math.cos(Math.PI * (n - 1) / M * (i - 0.5));
            }
        }
        return cepc;
    }
}
