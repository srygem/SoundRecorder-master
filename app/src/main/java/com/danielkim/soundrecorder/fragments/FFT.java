package com.danielkim.soundrecorder.fragments;

public class FFT {
    float[] real;
    float[] imag;


    public void process(float signal[]) {
        final int numPoints = signal.length;
        // initialize real & imag array
        real = signal;
        imag = new float[numPoints];


        final double pi = Math.PI;
        final int numStages = (int) (Math.log(numPoints) / Math.log(2));
        final int halfNumPoints = numPoints >> 1;
        int j = halfNumPoints;

        int k;
        for (int i = 1; i < numPoints - 2; i++) {
            if (i < j) {
                // swap
                float tempReal = real[j];
                float tempImag = imag[j];
                real[j] = real[i];
                imag[j] = imag[i];
                real[i] = tempReal;
                imag[i] = tempImag;
            }
            k = halfNumPoints;
            while (k <= j) {
                j -= k;
                k >>= 1;
            }
            j += k;
        }

        // loop for each stage
        for (int stage = 1; stage <= numStages; stage++) {
            int LE = 1;
            for (int i = 0; i < stage; i++) {
                LE <<= 1;
            }
            final int LE2 = LE >> 1;
            double UR = 1;
            double UI = 0;
            // calculate sine & cosine values
            final double SR =  Math.cos(pi / LE2);
            final double SI = -Math.sin(pi / LE2);
            // loop for each sub DFT
            for (int subDFT = 1; subDFT <= LE2; subDFT++) {
                // loop for each butterfly
                for (int butterfly = subDFT - 1; butterfly <= numPoints - 1; butterfly += LE) {
                    int ip = butterfly + LE2;
                    // butterfly calculation
                    float tempReal = (float) (real[ip] * UR - imag[ip] * UI);
                    float tempImag = (float) (real[ip] * UI + imag[ip] * UR);
                    real[ip] = real[butterfly] - tempReal;
                    imag[ip] = imag[butterfly] - tempImag;
                    real[butterfly] += tempReal;
                    imag[butterfly] += tempImag;
                }

                double tempUR = UR;
                UR = tempUR * SR - UI * SI;
                UI = tempUR * SI + UI * SR;
            }
        }
    }
}
