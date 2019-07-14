package com.danielkim.soundrecorder.fragments;

import java.io.Serializable;

public class FeatureVector implements Serializable {


    private double[][] mfccFeature;
    private double[][] featureVector;// all
    private int noOfFrames;
    private int noOfFeatures;

    public FeatureVector() {
    }

    public double[][] getMfccFeature() {
        return mfccFeature;
    }

    public void setMfccFeature(double[][] mfccFeature2) {
        this.mfccFeature = mfccFeature2;
    }

    public int getNoOfFrames() {
        return featureVector.length;
    }

    public void setNoOfFrames(int noOfFrames) {
        this.noOfFrames = noOfFrames;
    }

    public int getNoOfFeatures() {
        return featureVector[0].length;
    }

    public void setNoOfFeatures(int noOfFeatures) {
        this.noOfFeatures = noOfFeatures;
    }


    public double[][] getFeatureVector() {
        return featureVector;
    }


    public void setFeatureVector(double[][] featureVector) {
        this.featureVector = featureVector;
    }
}

