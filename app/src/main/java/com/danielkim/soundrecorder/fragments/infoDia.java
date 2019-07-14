package com.danielkim.soundrecorder.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.danielkim.soundrecorder.R;
import com.danielkim.soundrecorder.Test;
import com.danielkim.soundrecorder.activities.MyAdmin;
import com.danielkim.soundrecorder.bandwidthLevel.ConnectionClassManager;
import com.danielkim.soundrecorder.bandwidthLevel.ConnectionQuality;
import com.danielkim.soundrecorder.bandwidthLevel.DeviceBandwidthSampler;
import com.devInfoDB;
import com.test.libsvmandroidexample.TrainFragment;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.DEVICE_POLICY_SERVICE;

public class infoDia extends AppCompatDialogFragment {
   // TextView t1,t2,t3,t4,t5;
    int p;
    long x1,x2,x3;
    EditText no,casEt;
    File file,exportDir;
    private static final String TAG = "ConnectionClass-Sample";
        String cas;
    private Button lock, disable, enable;
    public static final int RESULT_ENABLE = 11;
    private DevicePolicyManager devicePolicyManager;
    private ActivityManager activityManager;
    private ComponentName compName;
    private ConnectionClassManager mConnectionClassManager;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private ConnectionChangedListener mListener;
    private TextView mTextView;
    private Button b1,b2,b3;
    private View mRunningBar;
   public Integer batStatus;
    devInfoDB db;
    public String encoded,state;
    long t9;
    File file2;
    ProgressBar pb;
   PrintWriter printWriter;
    String ram,cpu,band,b;
    private String mURL ="https://www.google.co.in/imghp?hl=en";
    private int mTries = 0;
    private ConnectionQuality mConnectionClass = ConnectionQuality.UNKNOWN;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Nullable
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.frag_info, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(rootView).show();
        dialog.setCanceledOnTouchOutside(false);
        mTextView=rootView.findViewById(R.id.band);
        mRunningBar=rootView.findViewById(R.id.pb);
        mDeviceBandwidthSampler=DeviceBandwidthSampler.getInstance();
        mConnectionClassManager = ConnectionClassManager.getInstance();
        mListener = new ConnectionChangedListener();
        pb=rootView.findViewById(R.id.pb);
        no=rootView.findViewById(R.id.no);
        b1=rootView.findViewById(R.id.button1);
        casEt=rootView.findViewById(R.id.cas);
        devicePolicyManager = (DevicePolicyManager) getActivity().getSystemService(DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        compName = new ComponentName(getActivity(), MyAdmin.class);

        lock = (Button) rootView.findViewById(R.id.lock);
        enable = (Button) rootView.findViewById(R.id.enableBtn);
        disable = (Button) rootView.findViewById(R.id.disableBtn);
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean active = devicePolicyManager.isAdminActive(compName);

                if (active) {
                    devicePolicyManager.lockNow();
                } else {
                    Toast.makeText(getActivity(), "You need to enable the Admin Device Features", Toast.LENGTH_SHORT).show();
                }


            }
        });
        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why we need this permission");
                if(intent==null){
                    Toast.makeText(getActivity(),"intent null",Toast.LENGTH_LONG).show();
                }
                startActivityForResult(intent, RESULT_ENABLE);

            }
        });
        disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devicePolicyManager.removeActiveAdmin(compName);
                disable.setVisibility(View.GONE);
                enable.setVisibility(View.VISIBLE);
            }
        });
        exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        state = Environment.getExternalStorageState();
            db=new devInfoDB(getActivity());
        AssetManager am = getActivity().getAssets();
        InputStream inputStream = null;
        try {
            inputStream = am.open("sound.wav");
        } catch (IOException e) {
            Log.d("file", e.toString());
            e.printStackTrace();
        }
        if(inputStream==null){
            Toast.makeText(getActivity(),"null",Toast.LENGTH_LONG).show();
        }
        else{
            file2 = createFileFromInputStream(inputStream);
            if(file2==null){
                Toast.makeText(getActivity(),"file null",Toast.LENGTH_LONG).show();

            }
           else {convertToBase64(file2);}
            }

        //    Toast.makeText(getActivity(),getActivity().getDatabasePath(db.getDatabaseName()).getAbsolutePath(),Toast.LENGTH_LONG).show();
        file = new File(exportDir, "MyCSVFile");  if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }
        printWriter = null;

        try {
            if (file.exists()) {
                printWriter = new PrintWriter(new FileWriter(file.getAbsoluteFile(), true));
                Toast.makeText(getActivity(),"if",Toast.LENGTH_LONG).show();


           }
            else{
                Toast.makeText(getActivity(),"else",Toast.LENGTH_LONG).show();

                file.createNewFile();
                printWriter = new PrintWriter(new FileWriter(file));
            }
        } catch (IOException e) {
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }



        //  new hitAPI().execute();


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=no.getText().toString();
                cas=casEt.getText().toString();
                if(s.isEmpty()){
                    b1.setError(" no. of records needed");
                    b1.setFocusable(true);
                    Toast.makeText(getActivity()," no. of records needed",Toast.LENGTH_LONG).show();
                }
                if(cas.isEmpty()){
                    b1.setError("Enter case");
                    b1.setFocusable(true);
                    Toast.makeText(getActivity(),"case needed",Toast.LENGTH_LONG).show();
                }
                else{
                //Toast.makeText(getActivity(),"onClick",Toast.LENGTH_LONG).show();
                try{
                    pb.setVisibility(View.VISIBLE);
                    p=Integer.parseInt(s);
                         gethit(p);
                         b1.setEnabled(false);
                         b1.setClickable(false);
                }
                catch(Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();


                }}}



        });
        b2=rootView.findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // makeCsv();
               trainData();
                //exportDB();
            }
        });
        b3=rootView.findViewById(R.id.button3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               predictLabel();
            }
        });




        mListener = new ConnectionChangedListener();





    return dialog;

    }
    private class hitAPI extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          // x3= gethit();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
         //   writeDataSet(1);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }



    private class DownloadImage extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {


            mDeviceBandwidthSampler=DeviceBandwidthSampler.getInstance();
            mDeviceBandwidthSampler.startSampling();
           // mRunningBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(String... url) {
            String imageURL = url[0];
            try {
                URLConnection connection = new URL(imageURL).openConnection();
                connection.setUseCaches(false);
                connection.connect();
                InputStream input = connection.getInputStream();
                try {
                    byte[] buffer = new byte[512];

                    while (input.read(buffer) != -1) {
                    }
                } finally {
                    input.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error while downloading image.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            mDeviceBandwidthSampler.stopSampling();
            if (mConnectionClass == ConnectionQuality.UNKNOWN && mTries < 10) {
                mTries++;
                new DownloadImage().execute(mURL);
            }
            if (!mDeviceBandwidthSampler.isSampling()) {
               // mRunningBar.setVisibility(View.GONE);
            }

        }
    }
    @Override
    public void onPause() {
        super.onPause();
        mConnectionClassManager.remove(mListener);


    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isActive = devicePolicyManager.isAdminActive(compName);
        disable.setVisibility(isActive ? View.VISIBLE : View.GONE);
        enable.setVisibility(isActive ? View.GONE : View.VISIBLE);
        mConnectionClassManager = ConnectionClassManager.getInstance();
        mListener = new ConnectionChangedListener();
        mConnectionClassManager.register(mListener);
    }
    private class ConnectionChangedListener
            implements ConnectionClassManager.ConnectionClassStateChangeListener {


        @Override
        public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
            mConnectionClass = bandwidthState;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    band = mConnectionClass.toString();


                }
            });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case RESULT_ENABLE :
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getActivity(), "You have enabled the Admin Device features", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Problem to enable the Admin Device features", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), String.valueOf(resultCode), Toast.LENGTH_SHORT).show();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);

        if(data==null){
            Toast.makeText(getActivity(),"data null", Toast.LENGTH_SHORT).show();

        }


    }
    public Integer getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return 2;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return 3;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return 4;
            default:
                return 0;
        }
    }
    public int batteryStatus(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getActivity().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = (level / (float)scale)*100;
        int bpct= Math.round(batteryPct);
        return bpct;
    }
    public Integer networkType(){
        ConnectivityManager connectivityManager=(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);;
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        Integer s=0;
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                s= 1;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                s= getNetworkClass(getContext());
            }
        } else {
            s= 0;
        }
        return s;

    }
    public String freeRam(){
        RandomAccessFile reader = null;
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            String load = reader.readLine();
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            double availableMegs = mi.availMem/0x100000L ;
            Toast.makeText(getActivity(),String.valueOf(availableMegs),Toast.LENGTH_LONG).show();

            Toast.makeText(getActivity(),load,Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException f){
            f.printStackTrace();
        }
       ;
       /* ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        double availableMegs = mi.availMem / 0x100000L;
        double percentAvail = mi.availMem / (double)mi.totalMem * 100.0;
                return String.valueOf(100-Math.round(percentAvail));*/
       return "65";
    }
    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" ");

            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" ");

            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }
    public void makeDb(){
        for(int i=0;i<49;i++){
           Integer batStatus=batteryStatus();
            Integer networkType=networkType();
            if(networkType.equals("mobile")){
                networkType= getNetworkClass(getContext());

            }
            String ram=freeRam();
           String cpu=String.valueOf(Math.round(readUsage()*100));
            new DownloadImage().execute(mURL);
            long a=Math.round(mConnectionClassManager.band());
            Integer status;

            try{
              // long t7=System.currentTimeMillis();

               t9= new Test().temp(file2);
              // long t8=System.currentTimeMillis();
               }
            catch (Exception e){

                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                System.out.print("Error ");
            }
            Context context = getActivity();

            if(t9>x3){
                status=-1;
            }
            else {
                status=1;
            }
            String cas="h";
            db.insertData(cas,batStatus,String.valueOf(networkType),cpu,ram,String.valueOf(a),String.valueOf(status),String.valueOf(t9),String.valueOf(x3),String.valueOf(t9-x3));
        }
    }

    public void makeCsv(){
        boolean res2=db.exportDatabase();


        if(res2){
            Toast.makeText(getActivity(),"exported",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getActivity(),"not exported",Toast.LENGTH_LONG).show();

        }
    }
    public void dataset(){
        makeDb();
        makeCsv();
    }
    public void gethit(final int n){
       // Toast.makeText(getActivity(),String.valueOf(n),Toast.LENGTH_LONG).show();
        x1=System.currentTimeMillis();
        System.out.println("x1"+String.valueOf(x1));

      // System.out.println("begingethit**********************************************************");

        String url = "http://139.59.0.248:8080/SpeakerWebApp/malik/speaker/saveAudio";

        final JSONObject js = new JSONObject();

        try {

            js.put("fileName", "vgFile.wav");
            js.put("audio", encoded);


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            Log.d("json",jsonObject.toString());
                            String status = jsonObject.getString("responseStatusMsg");
                            System.out.println(status);



                            if(status.equals("Success") ){

                                System.out.println("inside Success");
                                x2=System.currentTimeMillis();
                                System.out.println("x2value"+String.valueOf(x2));
                                x3=x2-x1;
                                Log.d("onResponse",String.valueOf(x2-x1));
                               // Toast.makeText(getActivity(),String.valueOf(n),Toast.LENGTH_LONG).show();
                                boolean result=writeDataSet(n,x3);

                                if(result){
                                   // Toast.makeText(getActivity(),"Training Data Written",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(getActivity(),"Training data insertion failed",Toast.LENGTH_LONG).show();

                                }






                                Log.d("status",status);
                               System.out.println( "Success>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );

                            }
                            else {
                                System.out.print("else block");
                            }


                        }catch (Exception e) {
                            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }






                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //   Log.d("error",error.getMessage());
                if (error instanceof NetworkError) {
                    Toast.makeText(getActivity(), "Oops. Network error!", Toast.LENGTH_LONG).show();

                } else if (error instanceof ServerError) {
                   Toast.makeText(getActivity(), "Oops. Server error!", Toast.LENGTH_LONG).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getActivity(), "Oops. Auth error!", Toast.LENGTH_LONG).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(getActivity(), "Oops. Parse error!", Toast.LENGTH_LONG).show();

                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), "Oops. NoConnection error!", Toast.LENGTH_LONG).show();

                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Oops. Timeout error!", Toast.LENGTH_LONG).show();

                    error.printStackTrace();
                    Toast.makeText( getActivity(), "Slow Internet Connection", Toast.LENGTH_SHORT ).show();
                }}
        }

        )
        {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*48,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getActivity()).add(jsonObjReq);

    }
    public void convertToBase64(File wav){
      /*  String path;
        SharedPreferences shared = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        String file = (shared.getString("fileName", ""));
        if(file.length()==0){
            Toast.makeText(getActivity(),"record first",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getActivity(),"Found audio",Toast.LENGTH_LONG).show();
            path=file;*/
        Context context=getActivity();
//        String s= context.getDatabasePath(db.getDatabaseName()).toString();
        byte[] bytes = new byte[0];
        try {
            bytes = FileUtils.readFileToByteArray(wav);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG",e.getMessage());
        }

        encoded = Base64.encodeToString(bytes, 0);
        Log.d("Encoded String: ", encoded);}
        public void trainData(){
        TrainFragment sd=new TrainFragment();

        android.support.v4.app.FragmentManager fm= (( getActivity()).getSupportFragmentManager());
        FragmentTransaction fragtrans = fm.beginTransaction();
        fragtrans.add(sd, "work");
        fragtrans.commit();
    }
    public void predictLabel(){
        android.support.v4.app.FragmentManager fm= (( getActivity()).getSupportFragmentManager());
        PredictFragment sd=new PredictFragment();
        sd.show(fm,"edit Dialog");
    }
    public void exportDB(){
      // File f=new File("/data/data/com.danielkim.soundrecorder/databases/devInfo");
        File f=new File(getActivity().getDatabasePath(db.getDatabaseName()).getAbsolutePath());

        FileInputStream fis=null;
        FileOutputStream fos=null;

        try
        {
            fis=new FileInputStream(f);
            fos=new FileOutputStream("/mnt/sdcard/devInfo.db");
            while(true)
            {
                int i=fis.read();
                if(i!=-1)
                {fos.write(i);}
                else
                {break;}
            }
            fos.flush();
            Toast.makeText(getActivity(), "Data Written", Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getActivity(), " ERROR", Toast.LENGTH_LONG).show();
        }
        finally
        {
            try
            {
                fos.close();
                fis.close();
            }
            catch(IOException ioe)
            {}
        }
    }
    public boolean writeDataSet(int n,long t){
        /**First of all we check if the external storage of the device is available for writing.
         * Remember that the external storage is not necessarily the sd card. Very often it is
         * the device storage.
         *
         */         Toast.makeText(getActivity(),String.valueOf("Remaining records to write => "+n),Toast.LENGTH_LONG).show();
                int status;


            try
            {


                   batStatus=batteryStatus();
                    Integer networkType=networkType();
                    if(networkType.equals("mobile")){
                        networkType= getNetworkClass(getContext());

                    }
                    ram=freeRam();
                    cpu=String.valueOf(Math.round(readUsage()*100));
                    new DownloadImage().execute(mURL);
                    long a=Math.round(mConnectionClassManager.band());
                    band=String.valueOf(Math.abs(a));
                    try{

                        System.out.println("begin");
                        t9= new Test().temp(file2);
                        System.out.println("after");


                        }
                    catch (Exception e){
                        Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                        System.out.print("Error ");
                    }

                    if(t9>t-5000){
                       Log.d("x3if", String.valueOf(t));
                        status=-1;
                    }
                    else {
                        Log.d("x3else", String.valueOf(t));
                        status=1;}
                           boolean result= db.insertData(cas,batStatus,String.valueOf(networkType),cpu,ram,String.valueOf(Math.abs(a)),String.valueOf(status),String.valueOf(t9),String.valueOf(t-5000),String.valueOf(t9-t+5000));
                           if(result){
                               Log.d("DB INSERTED"," suCCESS ");
                               if(n>1){
                               gethit(n-1);
                                   String record = status + " 1:" + batStatus + " 2:" + networkType + " 3:" + cpu + " 4:" + ram  + " 5:" + Math.abs(a) + " ";
                                   printWriter.println(record);}
                               if(n==1){
                                   pb.setVisibility(View.GONE);
                                   exportDB();
                                   if(printWriter != null) printWriter.close();
                                   b1.setEnabled(true);
                                   b1.setClickable(true);
                               }
                           }


                           else {
                               Log.d("DB INSERTED"," fail ");

                           }}

            catch(Exception exc) {
                System.out.println( exc.getMessage());
                Toast.makeText(getActivity(),exc.getMessage(),Toast.LENGTH_LONG).show();
                return false;
            }
            finally {

            }


            return true;
        }
        public void getStoragePermissions(){
            try {
                ActivityCompat.requestPermissions((Activity) getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
    public void getMicPermissions(){
        try {
            ActivityCompat.requestPermissions((Activity) getActivity(), new String[]{Manifest.permission.RECORD_AUDIO},
                    69);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public boolean checkMicPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
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


    }




