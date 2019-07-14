package com.danielkim.soundrecorder;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.danielkim.soundrecorder.activities.recActivity;
import com.danielkim.soundrecorder.fragments.FeatureExtract;
import com.danielkim.soundrecorder.fragments.FeatureVector;
import com.danielkim.soundrecorder.fragments.PreProcess;
import com.danielkim.soundrecorder.fragments.WaveData;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class Test extends Exception {
	static PreProcess prp;
	static FeatureExtract fExt;
	Context context;
	static int samplePerFrame = 512;// 23.22ms
	//static FormatControlConf fc = new FormatControlConf();
	static int samplingRate = 22050;
	List<double[]> allFeaturesList = new ArrayList<double[]>();
	int FEATUREDIMENSION = 39;
    public void temp() throws Exception {
        long t1=System.currentTimeMillis();
        System.out.println(String.valueOf(t1));



        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS )+"/OkGoogleBysagar10.wav");
        FeatureVector feature = extractFeatureFromFile(file);

        double[][] d=feature.getFeatureVector();
		/*for(int i=0;i<d.length;i++) {

			for(int j=0;j<d[i].length;j++) {
				System.out.println(d[i][j] );
			}
		}*/
        //	Excel_Generate.xlsGen(d);\
        long t2=System.currentTimeMillis();
        System.out.println(String.valueOf(t2));


    }


	public long temp(File file) throws Exception {
		long t1=System.currentTimeMillis();
        System.out.println(String.valueOf(t1));


        //	Log.d("path",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS )+"/sound.wav");
		FeatureVector feature = extractFeatureFromFile(file);

		double[][] d=feature.getFeatureVector();
		/*for(int i=0;i<d.length;i++) {

			for(int j=0;j<d[i].length;j++) {
				System.out.println(d[i][j] );
			}
		}*/
	//	Excel_Generate.xlsGen(d);\
		long t2=System.currentTimeMillis();
        System.out.println(String.valueOf(t2));

        return (t2-t1);
	}
    private File createFileFromInputStream(InputStream inputStream) {

        try{
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"sound.wav");
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        }catch (IOException e) {
            Log.d("covert", e.toString()); //Logging exception
        }

        return null;
    }
	public void getFile(Context myContext){
        AssetManager am = myContext.getAssets();
        InputStream inputStream = null;
        File file2=null;
        try {
            inputStream = am.open("sound.wav");
        } catch (IOException e) {
            Log.d("file", e.toString());
            e.printStackTrace();
        }
        if(inputStream==null){
           // Toast.makeText(getActivity(),"null",Toast.LENGTH_LONG).show();
        }
        else{
            file2 = createFileFromInputStream(inputStream);
            if(file2==null){
               // Toast.makeText(getActivity(),"file null",Toast.LENGTH_LONG).show();

            }
            else {
            }
        }
	}

	public  FeatureVector extractFeatureFromFile(File speechFile) {
		float[] arrAmp;
		WaveData wd = new WaveData();
		arrAmp = wd.extractAmplitudeFromFile(speechFile);
		System.out.println("Amp print"+arrAmp.length);
		/*for(int i=0;i<arrAmp.length;i++) {
			System.out.println(arrAmp[i]);
		}*/
		return extractFeatureFromExtractedAmplitureByteArray(arrAmp);
	}

	public static FeatureVector extractFeatureFromExtractedAmplitureByteArray(float[] arrAmp) {
		prp = new PreProcess(arrAmp, samplePerFrame, samplingRate);
		fExt = new FeatureExtract(prp.framedSignal, samplingRate, samplePerFrame);
		fExt.makeMfccFeatureVector();
		return fExt.getFeatureVector();
	}
}
