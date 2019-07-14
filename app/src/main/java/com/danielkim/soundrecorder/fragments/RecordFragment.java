package com.danielkim.soundrecorder.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.danielkim.soundrecorder.R;
import com.danielkim.soundrecorder.RecordingService;
import com.danielkim.soundrecorder.Test;
import com.danielkim.soundrecorder.bandwidthLevel.ConnectionQuality;

import com.danielkim.soundrecorder.unLockScreen.RecService;
import com.devInfoDB;
import com.melnykov.fab.FloatingActionButton;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = RecordFragment.class.getSimpleName();
    static FeatureExtract fExt;
    public String encoded;
    private int position;
    public String value = "";
    static int samplePerFrame = 512;// 23.22ms
    static int samplingRate = 22050;
    //Recording controls
    private FloatingActionButton mRecordButton = null;
    private Button mPauseButton = null;
    public ImageView imageView1,imageView2,imageView3,imageView4,imageView5;
    public TextView mRecordingPrompt,textView2,textView3,textView4;
    private int mRecordPromptCount = 0;
    String stringdouble;
    static PreProcess prp;


    TelephonyManager telephonyManager;
    int networkType;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra( BatteryManager.EXTRA_LEVEL, 0);

            System.out.println("Battery = "+level+"%");
            // batteryTxt.setText(String.valueOf(level) + "%");
        }
    };


    private boolean mStartRecording = true;
    private boolean mPauseRecording = true;
    String fileName;
    int j;
    private Chronometer mChronometer = null;
    long timeWhenPaused = 0; //stores time when user clicks pause button
    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 11025;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    String numberAsString;
    private View rootView;
    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    long t1,t2;

    devInfoDB db;
    Button req;

    private static final String TAG = "ConnectionClass-Sample";


    private View mRunningBar;

    private String mURL ="https://www.google.co.in/imghp?hl=en";
    private int mTries = 0;
    private ConnectionQuality mConnectionClass = ConnectionQuality.UNKNOWN;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Record_Fragment.
     */
    public static RecordFragment newInstance(int position) {
        RecordFragment f = new RecordFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }

    public RecordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);


        boolean a= isNetworkConnected();
        if(a)
        {
            Toast.makeText( getActivity(), "Connection Available", Toast.LENGTH_SHORT ).show();
        }
        else
        {
            Toast.makeText( getActivity(), "No Internet Connection", Toast.LENGTH_SHORT ).show();
            return;
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.fragment_rec, container, false);
        textView4 = (TextView)recordView.findViewById( R.id.unlockPhone );
        req=recordView.findViewById(R.id.req);
        //Toast.makeText(getActivity(),String.valueOf(System.currentTimeMillis()),Toast.LENGTH_LONG).show();
        req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        textView4.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Intent i = new Intent( getActivity(),UnlockOne.class );
               // startActivity( i );
            }
        } );

       // batteryTxt = (TextView) recordView.findViewById(R.id.batteryTxt);
















        /*imageView4.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getActivity(),AbhigyanShakuntalam.class );
                startActivity( intent );
            }
        } );*/



        mChronometer = (Chronometer) recordView.findViewById(R.id.chronometer);
        //update recording prompt text
        mRecordingPrompt = (TextView) recordView.findViewById(R.id.recording_status_text);

        mRecordButton = recordView.findViewById(R.id.btnRecord);
       mRecordButton.setColorNormal(getResources().getColor(R.color.primary));
        mRecordButton.setColorPressed(getResources().getColor(R.color.primary_dark));
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {

                onRecord(mStartRecording);
                //startRecording();
                mStartRecording = !mStartRecording;

              /* boolean a=checkMicPermissions();
                boolean b=checkStoragePermissions();
                if(a&&b){
                    onRecord(mStartRecording);
                    //startRecording();
                    mStartRecording = !mStartRecording;
                }
                else
                {

                        if(!b){
                            getStoragePermissions();
                        }
                    if(!a){
                        getMicPermissions();
                    }

                else {
                    Toast.makeText(getActivity(),"error occured",Toast.LENGTH_LONG).show();
                }}*/

            }
        });

        mPauseButton = (Button) recordView.findViewById(R.id.btnPause);
        mPauseButton.setVisibility(View.GONE); //hide pause button before recording starts
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseRecord(mPauseRecording);
                //stopRecording();
                mPauseRecording = !mPauseRecording;
            }
        });

        return recordView;
    }


    // Recording Start/Stop
    //TODO: recording pause
    private void onRecord(boolean start){

        Intent intent = new Intent(getActivity(), RecService.class);

        if (start) {
            // start recording
            mRecordButton.setImageResource(R.drawable.ic_media_stop);
            //mPauseButton.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(),R.string.toast_recording_start,Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdir();
            }

            //start Chronometer
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordPromptCount == 0) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
                    } else if (mRecordPromptCount == 1) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "..");
                    } else if (mRecordPromptCount == 2) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "...");
                        mRecordPromptCount = -1;
                    }

                    mRecordPromptCount++;
                }
            });

            //start RecordingService
            getActivity().startService(intent);
            //keep screen on while recording
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
            mRecordPromptCount++;

        } else {
            //stop recording
            mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);
            //mPauseButton.setVisibility(View.GONE);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            mRecordingPrompt.setText(getString(R.string.record_prompt));
            getActivity().stopService(intent);
            //allow the screen to turn off again once recording is finished
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            // getFeatures();
            // getFeatures();
            // predictLable();






        }



    }
  /*  public void predictLable(){
        android.support.v4.app.FragmentManager fm= (( getActivity()).getSupportFragmentManager());
        PredictFragment sd=new PredictFragment();
        sd.show(fm,"edit Dialog");
    }*/
    public void getFeatures(){
        android.support.v4.app.FragmentManager fm= (( getActivity()).getSupportFragmentManager());
        infoDia sd=new infoDia();
        sd.show(fm,"edit Dialog");

    }


    //TODO: implement pause recording
    private void onPauseRecord(boolean pause) {
        if (pause) {
            //pause recording
            mPauseButton.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_media_play ,0 ,0 ,0);
            mRecordingPrompt.setText((String)getString(R.string.resume_recording_button).toUpperCase());
            timeWhenPaused = mChronometer.getBase() - SystemClock.elapsedRealtime();
            mChronometer.stop();
        } else {
            //resume recording
            mPauseButton.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_media_pause ,0 ,0 ,0);
            mRecordingPrompt.setText((String)getString(R.string.pause_recording_button).toUpperCase());
            mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
            mChronometer.start();





        }



    }
    public boolean checkMicPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService( Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private static long getUsedMemorySize() {


        long  totalSize = 0L;

        try {
            Runtime info = Runtime.getRuntime();

            totalSize = info.totalMemory();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalSize;

    }
    private static long getUsedMemorySize1() {

        long  freeSize = 0L;

        try {
            Runtime info = Runtime.getRuntime();
            freeSize = info.freeMemory();



        } catch (Exception e) {
            e.printStackTrace();
        }
        return freeSize;

    }



    public void Test() {

        // TextView textView4 = (TextView)getActivity().findViewById( R.id.networkType );

        try{
            System.out.println();
            System.out.println();
            System.out.println("Start MFCC");
            System.out.println();

         /*   SharedPreferences pref = getActivity().getSharedPreferences( "classPath",MODE_PRIVATE );
          String  fileName1 = pref.getString( "newPath",null );
*/

         String fileName1="/storage/emulated/0/AudioRecorder/Vvv.wav";
            System.out.println(fileName1+"<<<<<<<<<<<<<<<<<<<<1111111>>>>>>>>>>>>>>>>>>>>>>>>>>>");

            FeatureVector feature = extractFeatureFromFile(new File(fileName1));
            System.out.println("5===");
            double[][] d = feature.getFeatureVector();
            System.out.println("length of feature vector >>>>"+d.length+"<<<<<");
            for (int i = 0; i < d.length; i++) {

                System.out.println("6===");
                for (j = 0; j < d[i].length; j++) {

                    System.out.println("7===");
                    System.out.println("[" + i + "] [" + j + "]  label= " + Math.ceil(d[i][j]) + "   " + d[i][j]);
                   // value = String.valueOf( d[i][j] );
                    System.out.println( "Hello dosto = "+d[i][j] );
                   // stringdouble= Double.toString(d[i][j]);
                     numberAsString = new Double(d[i][j]).toString();
                    System.out.println( "hello bro........"+numberAsString );
                    if(j==(d[i].length)-1) {
                        System.out.println( "Vinit bro........"+numberAsString );

                     //   textView4.setText("abcdef");
                    }

                }
//


            }


          //  textView3.setText("gfdsggsdfgfs");

        }
        catch (Exception  IOException){
            IOException.printStackTrace();
        }


    }

/*public void setText(View view){
        textView3 = (TextView)view.findViewById( R.id.networkType );
        System.out.println( "ttttttttthgfdhfhdfghdfghttttt = "+numberAsString );
        textView3.setText( "fgsdgsdfgdfgsdfg" );

}*/

    private static FeatureVector extractFeatureFromFile(File speechFile) {
        float[] arrAmp;
        WaveData wd = new WaveData();
        System.out.println("8===");

    /*    try{
            System.out.println("Sleep start 1  before wave");
            Thread.sleep(3000);
        }catch(Exception e){
            System.out.println("Error");
        }
*/
        arrAmp = wd.extractAmplitudeFromFile(speechFile);
        System.out.println("Amp print" + arrAmp.length);

        return extractFeatureFromExtractedAmplitureByteArray(arrAmp);
    }

    public static FeatureVector extractFeatureFromExtractedAmplitureByteArray(float[] arrAmp) {
     /*   try{
            System.out.println("Sleep start 1  before Preprocess");
            Thread.sleep(3000);
        }catch(Exception e){
            System.out.println("Error");
        }*/

        prp = new PreProcess(arrAmp, samplePerFrame, samplingRate);

        System.out.println("10===");

      /*  try{
            System.out.println("Sleep start 1  before Feature Extraction");
            Thread.sleep(3000);
        }catch(Exception e){
            System.out.println("Error");
        }*/

        fExt = new FeatureExtract(prp.framedSignal, samplingRate, samplePerFrame);
        fExt.makeMfccFeatureVector();
        return fExt.getFeatureVector();
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void copyWaveFile(String inFilename,String outFilename){
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            System.out.println("File size: " + totalDataLen);

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }
        System.out.println(">>>>>>>>>>> Real-- "+file.getAbsolutePath() + "/" + new Date() + AUDIO_RECORDER_FILE_EXT_WAV);
        fileName=file.getAbsolutePath() + "/" + new Date() + AUDIO_RECORDER_FILE_EXT_WAV;

        return (file.getAbsolutePath() + "/" + new Date() + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private String getTempFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);

        if(tempFile.exists())
            tempFile.delete();

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>> Temp --"+file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private void startRecording(){
        recorder = new AudioRecord( MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);

        int i = recorder.getState();
        if(i==1) {

            recorder.startRecording();
        }
        isRecording = true;

        recordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                writeAudioDataToFile();
            }
        },"AudioRecorder Thread");

        recordingThread.start();
    }
    public void getMicPermissions(){
        try {
            ActivityCompat.requestPermissions((Activity) getActivity(), new String[]{Manifest.permission.RECORD_AUDIO},
                    69);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public void getBothPerm(){
        try {
            ActivityCompat.requestPermissions((Activity) getActivity(), new String[]{Manifest.permission.RECORD_AUDIO},
                    69);
            ActivityCompat.requestPermissions((Activity) getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    70);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public void getStoragePermissions(){
        try {
            ActivityCompat.requestPermissions((Activity) getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    69);
            ActivityCompat.requestPermissions((Activity) getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    69);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public boolean checkStoragePermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }


    private void writeAudioDataToFile(){
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        int read = 0;

        if(null != os){
            while(isRecording){
                read = recorder.read(data, 0, bufferSize);

                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }
    private void execMfcc(){

        try{
            long t4=System.currentTimeMillis();
           // new Test().temp();
            long t5=System.currentTimeMillis();
            Toast.makeText(getActivity(),"mob time"+String.valueOf(t5-t4),Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
            System.out.print("MFCC Error ");
        }
    }


    private void stopRecording(){
        if(null != recorder){
            isRecording = false;

            int i = recorder.getState();
            if(i==1)
                recorder.stop();
            recorder.release();

            recorder = null;
            recordingThread = null;
        }

        copyWaveFile(getTempFilename(),getFilename());
        deleteTempFile();

        System.out.println("Process finished >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        Test();

    }
    private void deleteTempFile() {
        File file = new File(getTempFilename());

        file.delete();
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////





    /**
     * Listener to update the UI upon connectionclass change.
     */


    /**
     * AsyncTask for handling downloading and making calls to the timer.
     */





}