package com.danielkim.soundrecorder.unLockScreen;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.danielkim.soundrecorder.DBHelper;
import com.danielkim.soundrecorder.MySharedPreferences;
import com.danielkim.soundrecorder.R;
import com.danielkim.soundrecorder.RecordingService;
import com.danielkim.soundrecorder.activities.MainActivity;
import com.danielkim.soundrecorder.activities.recActivity;
import com.danielkim.soundrecorder.fragments.RecordFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.danielkim.soundrecorder.App.CHANNEL_ID;

public class RecService extends Service {
    public  int xx=0;
    private static final String LOG_TAG = "RecordingService";

    private String mFileName = null;
    private String mFilePath ;

    private MediaRecorder mRecorder = null;

    private DBHelper mDatabase;

    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;
    private OnTimerChangedListener onTimerChangedListener = null;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Timer mTimer = null;
    private TimerTask mIncrementTimerTask = null;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        Intent i=new Intent(this, recActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,i,0);
        Notification not=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("s")
                .setContentText("s")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pi)
                .build();
        startForeground(1,not);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null) {
            try{
                stopRecording();}
            catch (Exception e){
                Log.d("vgftw", e.getMessage());
            }
        }
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new DBHelper(getApplicationContext());
    }


    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }
    public void startRecording() {
        setFileNameAndPath();

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        if (MySharedPreferences.getPrefHighQuality(this)) {
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setAudioEncodingBitRate(192000);
        }

        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();

            //startTimer();
            //startForeground(1, createNotification());

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }
    public void setFileNameAndPath(){
        int count = 0;
        File f;

        do{
            count++;
            xx++;
            mFileName = getString(R.string.default_file_name)
                    + "_" + (mDatabase.getCount() + count) + ".wav";
            // mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

            mFilePath += "/" + mFileName;
            System.out.println( "my new File path = "+mFilePath );
            SharedPreferences sharedPreferences=getSharedPreferences("prefs", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("fileName",mFilePath);
            editor.apply();



            f = new File(mFilePath);
        }while (f.exists() && !f.isDirectory());
    }
    public void stopRecording() {
      /*  SharedPreferences.Editor pref = getSharedPreferences( "classPath",MODE_PRIVATE ).edit();
        pref.putString( "newPath",mFilePath );
        pref.apply();*/
        System.out.println( "gggggggggg = "+mFilePath+"ttttttttt = "+xx );
        RecordFragment rf = new RecordFragment();
        //  rf.Test();
        try {
            mRecorder.stop();
        }
        catch (final Exception e){
            Handler handler=new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(e instanceof IllegalStateException){
                        Toast.makeText(getApplicationContext(),"illegal state exception",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"illegal state exception",Toast.LENGTH_LONG).show();

                    }}
            });

        }

        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        mRecorder.release();

        Toast.makeText(this, getString(R.string.toast_recording_finish) + " " + mFilePath, Toast.LENGTH_LONG).show();

        //remove notification
        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }

        mRecorder = null;

        try {
            mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);

        } catch (Exception e){
            Log.e(LOG_TAG, "exception", e);
        }
    }

    private void startTimer() {
        mTimer = new Timer();
        mIncrementTimerTask = new TimerTask() {
            @Override
            public void run() {
                mElapsedSeconds++;
                if (onTimerChangedListener != null)
                    onTimerChangedListener.onTimerChanged(mElapsedSeconds);
                NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mgr.notify(1, createNotification());
            }
        };
        mTimer.scheduleAtFixedRate(mIncrementTimerTask, 1000, 1000);
    }

    //TODO:
    private Notification createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_mic_white_36dp)
                        .setContentTitle(getString(R.string.notification_recording))
                        .setContentText(mTimerFormat.format(mElapsedSeconds * 1000))
                        .setOngoing(true);

        mBuilder.setContentIntent(PendingIntent.getActivities(getApplicationContext(), 0,
                new Intent[]{new Intent(getApplicationContext(), MainActivity.class)}, 0));

        return mBuilder.build();
    }

}
