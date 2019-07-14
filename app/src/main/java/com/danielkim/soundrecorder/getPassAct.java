package com.danielkim.soundrecorder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.danielkim.soundrecorder.bandwidthLevel.ConnectionQuality;
import com.danielkim.soundrecorder.fragments.FileViewerDia;
import com.danielkim.soundrecorder.fragments.infoDia;
import com.danielkim.soundrecorder.unLockScreen.RecService;
import com.devInfoDB;
import com.danielkim.soundrecorder.fragments.PredictFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class getPassAct extends AppCompatActivity {
    FloatingActionButton fab;
    TextView passPhrase;
    Button next;
    private Button mPauseButton = null;
    private boolean mStartRecording = true;
    private boolean mPauseRecording = true;
    String fileName;
    TextView prevRec;
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
    public ImageButton train;
    AudioManager amanager;

    devInfoDB db;
    public TextView mRecordingPrompt;
    private int mRecordPromptCount = 0;
    Button req;

    private static final String TAG = "ConnectionClass-Sample";


    private View mRunningBar;

    private String mURL ="https://www.google.co.in/imghp?hl=en";
    private int mTries = 0;
    private ConnectionQuality mConnectionClass = ConnectionQuality.UNKNOWN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_pass);
        fab=findViewById(R.id.recFab);
        passPhrase=findViewById(R.id.phraseText);
        next=findViewById(R.id.nextButton);
        mChronometer=findViewById(R.id.chronometer);
        train=findViewById(R.id.train);
        mRecordingPrompt=findViewById(R.id.prompt);
         amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        prevRec=findViewById(R.id.prevRec);
        train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fm= (getSupportFragmentManager());
                infoDia sd=new infoDia();
                sd.show(fm,"infoDialog");
            }
        });
        mRecordingPrompt.setText("press record button.....");
        checkPermission();
        if(checkSupport()){
            Toast.makeText(getApplicationContext(),"Device Supported",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"Device not supported",Toast.LENGTH_LONG).show();

        }
        passPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // getSpeechInput();
               // Intent i=new Intent(getPassAct.this,getPassText.class);
               // startActivity(i);
            }
        });
     /*   fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;

            }
        });*/

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                predictLabel();

            }
        });
        prevRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPrevRec();

            }
        });
        mPauseButton = findViewById(R.id.btnPause);
        mPauseButton.setVisibility(View.GONE); //hide pause button before recording starts
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseRecord(mPauseRecording);
                //stopRecording();
                mPauseRecording = !mPauseRecording;
            }
        });



        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);


        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());


        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {



            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {


            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)

                    passPhrase.setText(matches.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        onRecord(mStartRecording);
                        mStartRecording=!mStartRecording;

                        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                        amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
                        amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                        amanager.setStreamMute(AudioManager.STREAM_RING, false);
                        amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
                        //passPhrase.setText("You will see input here");
                        break;

                    case MotionEvent.ACTION_DOWN:

                        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                        amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
                        amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                        amanager.setStreamMute(AudioManager.STREAM_RING, true);
                        amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        onRecord(mStartRecording);
                        mStartRecording=!mStartRecording;
                      //  editText.setText("");
                      //  editText.setHint("Listening...");
                        break;
                }
                return false;
            }
        });
    }
   /* public String wavToText(File file){
        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
        recognizer.startRecognition(new File("speech.wav").toURI().toURL());
        SpeechResult result = recognizer.getResult();
        recognizer.stopRecognition();

    }*/
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);

       switch (requestCode) {
           case 10:
               if (resultCode == RESULT_OK && data != null) {
                   ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                   passPhrase.setText(result.get(0));
               }
               break;
       }
   }
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }
    private boolean checkSupport(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        if (intent.resolveActivity(getPackageManager()) != null) {
            return true;
        } else {
            return false;
        }
    }
   public void openPrevRec(){
       android.support.v4.app.FragmentManager fm = ((AppCompatActivity) this).getSupportFragmentManager();
       FileViewerDia vd = new FileViewerDia();
       vd.show(fm, "verify number");
   }

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
    private void onRecord(boolean start){

        Intent intent = new Intent(this, RecService.class);

        if (start) {
            // start recording
           // fab.setImageResource(R.drawable.ic_media_stop);
            //mPauseButton.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),R.string.toast_recording_start,Toast.LENGTH_SHORT).show();
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
            startService(intent);
            //keep screen on while recording
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
            mRecordPromptCount++;

        } else {
            //stop recording
            //fab.setImageResource(R.drawable.ic_mic_white_36dp);
            //mPauseButton.setVisibility(View.GONE);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            mRecordingPrompt.setText(getString(R.string.record_prompt));
            stopService(intent);
            //allow the screen to turn off again once recording is finished
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            // getFeatures();
            // getFeatures();
            // predictLable();






        }



    }
    public void predictLabel(){
        android.support.v4.app.FragmentManager fm= (getSupportFragmentManager());
        PredictFragment sd=new PredictFragment();
        sd.show(fm,"edit Dialog");
    }
}
