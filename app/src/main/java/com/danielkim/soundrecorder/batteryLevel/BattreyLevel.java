package com.danielkim.soundrecorder.batteryLevel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.danielkim.soundrecorder.R;

public class BattreyLevel extends AppCompatActivity{

    private TextView batteryTxt;
    Button btn;

    TextView textView, textView1;
    TelephonyManager telephonyManager;
    int networkType;
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra( BatteryManager.EXTRA_LEVEL, 0);
            batteryTxt.setText(String.valueOf(level) + "%");
        }
    };


    @Override
    protected void onCreate(Bundle b) {
        super.onCreate( b );
        setContentView( R.layout.activity_battrey_level );

        textView = (TextView) findViewById(R.id.textView);


        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        networkType = telephonyManager.getNetworkType();


        batteryTxt = (TextView) this.findViewById(R.id.batteryTxt);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        btn = (Button)findViewById(R.id.signal);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intt = new Intent(getApplicationContext(), Signal.class);
                startActivity(intt);
            }
        });
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
