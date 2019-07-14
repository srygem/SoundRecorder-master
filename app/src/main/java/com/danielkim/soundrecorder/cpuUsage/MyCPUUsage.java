package com.danielkim.soundrecorder.cpuUsage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.danielkim.soundrecorder.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyCPUUsage extends AppCompatActivity {

    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_my_cpuusage );

        textView1 = (TextView)findViewById(R.id.textView);

        int totalUsage = 0;
        try {

            // CPU Usage in percentage....
            java.lang.Process p = Runtime.getRuntime().exec("top -m 15 -d 1 -n 1");
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            ///////////////////////////////////////////////////////////////////////////////////////

            String line = reader.readLine();
            while (line != null) {
                if (!TextUtils.isEmpty(line)) {
                    break;
                }
                line = reader.readLine();
            }
            if (!TextUtils.isEmpty(line)) {
                String[] items = line.split(",");
                if (null != items && items.length > 0) {
                    for (String item : items) {
                        if (!TextUtils.isEmpty(item)) {
                            item = item.trim();
                            String usage = item.split(" ")[1];
                            usage = usage.substring(0, usage.length() - 1);
                            int rate = Integer.valueOf(usage);
                            totalUsage += rate;
                        }
                    }

                    System.out.println( "My CPU Usage two = "+totalUsage );
                    textView1.setText( "CPU Usage = "+String.valueOf( totalUsage )+"%" );

                }
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println( "Result = "+(totalUsage/100f) );

    }
}
