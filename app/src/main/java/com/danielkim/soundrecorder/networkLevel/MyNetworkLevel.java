package com.danielkim.soundrecorder.networkLevel;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import com.danielkim.soundrecorder.R;

public class MyNetworkLevel extends AppCompatActivity {

    public TextView textView;
    TelephonyManager telephonyManager;
    int networkType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_my_network_level );
        textView = (TextView)findViewById( R.id.myNetworkOneTwo );
        telephonyManager = (TelephonyManager) getSystemService( Context.TELEPHONY_SERVICE);
        networkType = telephonyManager.getNetworkType();
        if (networkType == 13) {
            textView.setText("4G");
        } else if (networkType == 2) {
            textView.setText("2G");
        } else if (networkType == 15) {
            textView.setText("3G");
        } else {
            textView.setText("Unknown");
        }
    }
}
