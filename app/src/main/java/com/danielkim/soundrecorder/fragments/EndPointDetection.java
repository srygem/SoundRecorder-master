package com.danielkim.soundrecorder.fragments;

public class EndPointDetection {

    private float[] originalSignal; // input
    private float[] silenceRemovedSignal;// output
    private int samplingRate;
    private int firstSamples;
    private int samplePerFrame;

    public EndPointDetection(float[] originalSignal, int samplingRate) {
        this.originalSignal = originalSignal;
        this.samplingRate = samplingRate;
        samplePerFrame = this.samplingRate / 1000;
        firstSamples = samplePerFrame * 200;// according to formula
    }

    public float[] doEndPointDetection() {
        float[] voiced = new float[originalSignal.length];// for identifying
        // each
        // sample whether it is
        // voiced or unvoiced
        int sum = 0;
        double sd = 0.0;
        double m = 0.0;

        System.out.println("firstSamples-------"+firstSamples);
        System.out.println("originalSignal-------"+originalSignal.length);

        // 1. calculation of mean
        for (int i = 0; i < firstSamples; i++) {
           try {
               sum += originalSignal[i];
           }catch (ArrayIndexOutOfBoundsException e){
               e.getMessage();
           }
        }
       // System.err.println("total sum :" + sum);
        m = sum / firstSamples;// mean
        sum = 0;// reuse var for S.D.

        // 2. calculation of Standard Deviation
        for (int i = 0; i < firstSamples; i++) {
            try {
                sum += Math.pow((originalSignal[i] - m), 2);
            }catch (ArrayIndexOutOfBoundsException e){
                e.getMessage();
            }

        }
        sd = Math.sqrt(sum / firstSamples);
        System.err.println("summm sum :" + sum);
        System.err.println("mew :" + m);
        System.err.println("sigma :" + sd);


        for (int i = 0; i < originalSignal.length; i++) {
            // System.out.println("x-u/SD  ="+(Math.abs(originalSignal[i] -u ) /
            // sd));
            if ((Math.abs(originalSignal[i] - m) / sd) > 2) {
                voiced[i] = 1;
            }
            else {
                voiced[i] = 0;
            }
        }


        int frameCount = 0;
        int usefulFramesCount = 1;
        int count_voiced = 0;
        int count_unvoiced = 0;
        int voicedFrame[] = new int[originalSignal.length / samplePerFrame];
        int loopCount = originalSignal.length - (originalSignal.length % samplePerFrame);// skip
        // the
        // last
        for (int i = 0; i < loopCount; i += samplePerFrame) {
            count_voiced = 0;
            count_unvoiced = 0;
            for (int j = i; j < i + samplePerFrame; j++) {
                if (voiced[j] == 1) {
                    count_voiced++;
                }
                else {
                    count_unvoiced++;
                }
            }
            if (count_voiced > count_unvoiced) {
                usefulFramesCount++;
                voicedFrame[frameCount++] = 1;
            }
            else {
                voicedFrame[frameCount++] = 0;
            }
        }

        // 5. silence removal
        silenceRemovedSignal = new float[usefulFramesCount * samplePerFrame];
        int k = 0;
        for (int i = 0; i < frameCount; i++) {
            if (voicedFrame[i] == 1) {
                for (int j = i * samplePerFrame; j < i * samplePerFrame + samplePerFrame; j++) {
                    silenceRemovedSignal[k++] = originalSignal[j];
                }
            }
        }
        // end
        return silenceRemovedSignal;
    }
}

