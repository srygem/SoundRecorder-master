package com.danielkim.soundrecorder.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.danielkim.soundrecorder.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ThradeActivity extends AppCompatActivity {

    public boolean aa,bb,cc,dd;
    Button layout1,layout2,layout3,layout4,button1;
    TextView textView1,textView2,textView3;
    private int game=0;

    MyTimer mt;
    final Handler handler_interact=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_thrade );


        layout1 = (Button)findViewById( R.id.layout1 );
        layout2 = (Button)findViewById( R.id.layout2 );
        layout3 = (Button)findViewById( R.id.layout3 );
        layout4 = (Button)findViewById( R.id.layout4 );
        button1 = (Button)findViewById( R.id.startGame );
        textView2 = (TextView)findViewById( R.id.overGame );
        textView1 = (TextView)findViewById( R.id.scoreCard );
        textView3 = (TextView)findViewById( R.id.scoreBoard );

        layout1.setBackgroundColor( Color.argb(255, 0, 128, 0 ));
        layout2.setBackgroundColor(Color.argb(255,139,0,0 ));
        layout3.setBackgroundColor(Color.argb(255,0,0,255 ));
        layout4.setBackgroundColor(Color.argb(255,255,255,0 ));

        button1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        } );

        layout1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                increaseScoreOne(1);

                //  Toast.makeText( MainActivity.this, "One", Toast.LENGTH_SHORT ).show();
            }
        } );

        layout2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                increaseScoreOne(2);

                //  Toast.makeText( MainActivity.this, "Two", Toast.LENGTH_SHORT ).show();
            }
        } );


        layout3.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                increaseScoreOne(3);
                // Toast.makeText( MainActivity.this, "Three", Toast.LENGTH_SHORT ).show();

            }
        } );


        layout4.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                increaseScoreOne(4);

                // Toast.makeText( MainActivity.this, "Four", Toast.LENGTH_SHORT ).show();
            }
        } );




    }

    public void startGame(){

        button1.setVisibility( View.GONE );
        textView2.setVisibility( View.GONE );
        textView3.setVisibility( View.GONE );
        layout1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                increaseScoreOne(1);

                //  Toast.makeText( MainActivity.this, "One", Toast.LENGTH_SHORT ).show();
            }
        } );

        layout2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                increaseScoreOne(2);

                //  Toast.makeText( MainActivity.this, "Two", Toast.LENGTH_SHORT ).show();
            }
        } );


        layout3.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                increaseScoreOne(3);
                // Toast.makeText( MainActivity.this, "Three", Toast.LENGTH_SHORT ).show();

            }
        } );


        layout4.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                increaseScoreOne(4);

                // Toast.makeText( MainActivity.this, "Four", Toast.LENGTH_SHORT ).show();
            }
        } );

        Timer timer = new Timer();

        mt = new MyTimer();

        timer.schedule(mt, 1000, 1000);

    }

    class MyTimer extends TimerTask {

        public void run() {

            runOnUiThread(new Runnable() {

                @SuppressLint("ResourceAsColor")
                public void run() {
                    Random rand = new Random();
                    int a=rand.nextInt(6-1)+1;
                  /*  layout1.setBackgroundColor(Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256) ));
                    layout2.setBackgroundColor(Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256) ));
                    layout3.setBackgroundColor(Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256) ));
                    layout4.setBackgroundColor(Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256) ));
                */

                    if(a==1)
                    {
                   /*   layout1.setBackgroundColor(R.color.gray);
                      textView2.setTextColor( R.color.a );
                      layout2.setBackgroundColor(R.color.b);
                      layout3.setBackgroundColor(R.color.c);
                      layout4.setBackgroundColor(R.color.d);*/

                        layout1.setBackgroundColor(Color.argb(255, 128, 128, 128 ));
                        layout2.setBackgroundColor(Color.argb(255,139,0,0 ));
                        layout3.setBackgroundColor(Color.argb(255,0,0,255 ));
                        layout4.setBackgroundColor(Color.argb(255,255,255,0 ));
                        aa = true;
                        bb = false;
                        cc = false;
                        dd = false;

                    }
                    else if(a==2)
                    {
                        layout1.setBackgroundColor(Color.argb(255, 0, 128, 0 ));
                        layout2.setBackgroundColor(Color.argb(255,128,128,128 ));
                        layout3.setBackgroundColor(Color.argb(255,0,0,255 ));
                        layout4.setBackgroundColor(Color.argb(255,255,255,0 ));

                        aa = false;
                        bb = true;
                        cc = false;
                        dd = false;
                    }
                    else if(a==3)
                    {
                        layout1.setBackgroundColor(Color.argb(255, 0, 128, 0 ));
                        layout2.setBackgroundColor(Color.argb(255,139,0,0 ));
                        layout3.setBackgroundColor(Color.argb(255,128,128,128 ));
                        layout4.setBackgroundColor(Color.argb(255,255,255,0 ));

                        aa = false;
                        bb = false;
                        cc = true;
                        dd = false;

                    }
                    else if(a==4)
                    {
                        layout1.setBackgroundColor(Color.argb(255, 0, 128, 0 ));
                        layout2.setBackgroundColor(Color.argb(255,139,0,0 ));
                        layout3.setBackgroundColor(Color.argb(255,0,0,255 ));
                        layout4.setBackgroundColor(Color.argb(255,128,128,128 ));

                        aa = false;
                        bb = false;
                        cc = false;
                        dd = true;

                    }
                }
            });



        }




    }

    private void increaseScoreOne(int x) {

        if((aa && x==1) || (bb && x==2) || (cc && x==3) || (dd && x==4))
        {
            game++;

            Toast.makeText( getApplicationContext(), "hello your score is "+game, Toast.LENGTH_SHORT ).show();
            textView1.setText( "Your Score = "+String.valueOf( game ) );

        }else {

            textView3.setText( "Your Score = "+String.valueOf( game ) );
            game=0;
            Toast.makeText( getApplicationContext(), "Game End"+x, Toast.LENGTH_SHORT ).show();
            textView1.setText( String.valueOf( game ) );


            stopGame();


        }

    }

    public void stopGame(){
        button1.setVisibility( View.VISIBLE );
        textView3.setVisibility( View.VISIBLE );
        textView2.setVisibility( View.VISIBLE );
        layout1.setOnClickListener( null );
        layout2.setOnClickListener( null );
        layout3.setOnClickListener( null );
        layout4.setOnClickListener( null );
        mt.cancel();

    }


}
