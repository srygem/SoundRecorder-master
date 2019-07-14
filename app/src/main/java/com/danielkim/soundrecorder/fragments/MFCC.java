package com.danielkim.soundrecorder.fragments;

public class MFCC {

    private final static int       numMelFilters       = 30;    // how much
    private final static double    preEmphasisAlpha    = 0.95;
    private final static double    lowerFilterFreq     = 80.00; // FmelLow

    private final double    sampleRate;
    private final double    upperFilterFreq;
    private final int       samplesPerFrame;

    private final boolean usePreEmphasis;


    final FFT fft;
    final DCT dct;


    public MFCC(int samplesPerFrame, double sampleRate, int numCoefficients) {
        this(samplesPerFrame, sampleRate, numCoefficients, false);
    }

    public MFCC(int samplesPerFrame, double sampleRate, int numCoefficients, boolean preEmphasis) {
        this.samplesPerFrame    = samplesPerFrame;
        this.sampleRate         = sampleRate;
        this.usePreEmphasis     = preEmphasis;
        upperFilterFreq         = sampleRate / 2.0;
        fft = new FFT();
        dct = new DCT(numCoefficients, numMelFilters);
    }

    public double[] process(float[] frame) {
        if (usePreEmphasis) {
            System.out.println("PRE EMPHASISMMMMMMMMMMMMMMMMMMMMMMM>>>>>>>>>>>>>>>>>>>");
            frame = preEmphasis(frame);
        }
        // Magnitude Spectrum
        final double[] bin = magnitudeSpectrum(frame);//fft and spectrum done


        // prepare filter for for melFilter
        final int cBin[] = fftBinIndices();// same for all
        // process Mel filter bank
        final double fBank[] = melFilter(bin, cBin);

        final double f[] = nonLinearTransformation(fBank);

        return dct.perform(f);
    }

    private double[] magnitudeSpectrum(float frame[]) {
        final double magSpectrum[] = new double[frame.length];

        fft.process(frame);

        for (int k = 0; k < frame.length; k++) {
            magSpectrum[k] = Math.sqrt(fft.real[k] * fft.real[k] + fft.imag[k] * fft.imag[k]);
        }
        return magSpectrum;
    }


    private float[] preEmphasis(float inputSignal[]) {
        final float outputSignal[] = new float[inputSignal.length];
        // apply pre-emphasis to each sample
        for (int n = 1; n < inputSignal.length; n++) {
            outputSignal[n] = (float) (inputSignal[n] - preEmphasisAlpha * inputSignal[n - 1]);
        }
        return outputSignal;
    }

    private int[] fftBinIndices() {
        final int cBin[] = new int[numMelFilters + 2];
        cBin[0] = (int) Math.round(lowerFilterFreq / sampleRate * samplesPerFrame);// cBin0
        cBin[cBin.length - 1] = (samplesPerFrame / 2);// cBin24
        for (int i = 1; i <= numMelFilters; i++) {// from cBin1 to cBin23
            final double fc = centerFreq(i);// center freq for i th filter
            cBin[i] = (int) Math.round(fc / sampleRate * samplesPerFrame);
        }
        return cBin;
    }


    private double[] melFilter(double bin[], int cBin[]) {
        final double temp[] = new double[numMelFilters + 2];
        for (int k = 1; k <= numMelFilters; k++) {
            double num1 = 0.0, num2 = 0.0;
            for (int i = cBin[k - 1]; i <= cBin[k]; i++) {
                num1 += ((i - cBin[k - 1] + 1) / (cBin[k] - cBin[k - 1] + 1)) * bin[i];
            }

            for (int i = cBin[k] + 1; i <= cBin[k + 1]; i++) {
                num2 += (1 - ((i - cBin[k]) / (cBin[k + 1] - cBin[k] + 1))) * bin[i];
            }

            temp[k] = num1 + num2;
        }
        final double fBank[] = new double[numMelFilters];
        System.arraycopy(temp, 1, fBank, 0, numMelFilters);
        return fBank;
    }


    private double[] nonLinearTransformation(double fBank[]) {
        double f[] = new double[fBank.length];
        final double FLOOR = -50;
        for (int i = 0; i < fBank.length; i++) {
            f[i] = Math.log(fBank[i]);
            // check if ln() returns a value less than the floor
            if (f[i] < FLOOR) {
                f[i] = FLOOR;
            }
        }
        return f;
    }

    private double centerFreq(int i) {
        final double melFLow    = freqToMel(lowerFilterFreq);
        final double melFHigh   = freqToMel(upperFilterFreq);
        final double temp       = melFLow + ((melFHigh - melFLow) / (numMelFilters + 1)) * i;
        return inverseMel(temp);
    }

    private double inverseMel(double x) {
        final double temp = Math.pow(10, x / 2595) - 1;
        return 700 * (temp);
    }

    protected double freqToMel(double freq) {
        return 2595 * log10(1 + freq / 700);
    }

    private double log10(double value) {
        return Math.log(value) / Math.log(10);
    }
}
