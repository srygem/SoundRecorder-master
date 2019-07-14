package com.danielkim.soundrecorder.fragments;

public class Energy {


    private int samplePerFrame;

    public Energy(int samplePerFrame) {
        this.samplePerFrame = samplePerFrame;
    }

    public double[] calcEnergy(float[][] framedSignal) {
        double[] energyValue = new double[framedSignal.length];
        for (int i = 0; i < framedSignal.length; i++) {
            float sum = 0;
            for (int j = 0; j < samplePerFrame; j++) {
                // sum the square
                sum += Math.pow(framedSignal[i][j], 2);
            }
            // find log
            energyValue[i] = Math.log(sum);
        }
        return energyValue;
    }
}
