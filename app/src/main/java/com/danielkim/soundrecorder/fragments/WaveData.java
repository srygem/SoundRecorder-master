package com.danielkim.soundrecorder.fragments;

import android.media.AudioFormat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class WaveData {

    private byte[] arrFile;
    private byte[] audioBytes;
    private float[] audioData;
    private FileOutputStream fos;
    private ByteArrayInputStream bis;
    //private AudioInputStream audioInputStream;
    private AudioFormat format;
    private double durationSec;

    public WaveData() {
    }

    public byte[] getAudioBytes() {
        return audioBytes;
    }

    public double getDurationSec() {
        return durationSec;
    }

    public float[] getAudioData() {
        return audioData;
    }

    public AudioFormat getFormat() {
        return format;
    }

    public float[] extractAmplitudeFromFile(File wavFile) {
        try {

            // create file input stream
            FileInputStream fis = new FileInputStream(wavFile);
            // create bytearray from file
            arrFile = new byte[(int) wavFile.length()];//for read file and store in byte array
            fis.read(arrFile);
            System.out.print("Wave File Length--"+ arrFile.length+" --");
        } catch (Exception e) {
            System.out.println("SomeException : " + e.toString());
        }
        return extractFloatDataFromAmplitudeByteArray(arrFile);
    }


    public float[] extractFloatDataFromAmplitudeByteArray( byte[] audioBytes) {
        // convert
        audioData = null;
        System.out.println("16 sample");
        int nlengthInSamples = audioBytes.length / 2;
        audioData = new float[nlengthInSamples];

        System.out.println("little Endian");
        for (int i = 0; i < nlengthInSamples; i++) {
            /* First byte is LSB (low order) */
            int LSB = audioBytes[2 * i];
            /* Second byte is MSB (high order) */
            int MSB = audioBytes[2 * i + 1];
            audioData[i] = MSB << 8 | (255 & LSB);
            //System.out.println("LSB = "+LSB+" : MSB = "+MSB+" : I = "+i+" : audioData["+i+"] = "+audioData[i]);
        }
        return audioData;
    }

}