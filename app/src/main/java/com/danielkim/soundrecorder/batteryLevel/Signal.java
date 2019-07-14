package com.danielkim.soundrecorder.batteryLevel;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import com.danielkim.soundrecorder.R;

public class Signal extends AppCompatActivity {

    TextView textView,textView1;
    TelephonyManager telephonyManager;
    int networkType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_signal );

        textView = (TextView) findViewById(R.id.textView);
        textView1 = (TextView) findViewById(R.id.textView1);


        telephonyManager = (TelephonyManager) getSystemService( Context.TELEPHONY_SERVICE);
        networkType = telephonyManager.getNetworkType();

        textView1.setText(String.valueOf(networkType));


    }
}
