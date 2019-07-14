package com.danielkim.soundrecorder.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.danielkim.soundrecorder.fragments.FeatureVector;
import com.test.libsvmandroidexample.ContainerActivity;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import umich.cse.yctung.androidlibsvm.LibSVM;

import static android.content.Context.MODE_PRIVATE;


public class PredictFragment extends AppCompatDialogFragment {
    String encoded;
    public PredictFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_predict, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).show();
        dialog.setCanceledOnTouchOutside(false);

        convertToBase64();
        List<String> commands = new ArrayList<>();

        commands.add(Environment.getExternalStorageDirectory()+"/"+"Download/"+"testFile");
        commands.add(Environment.getExternalStorageDirectory()+"/"+"Download/"+"svmAndroidModel");

        commands.add(Environment.getExternalStorageDirectory() + "/Download/"+"svmPredictFile");
        new AsyncPredictTask().execute(commands.toArray(new String[0]));

        return dialog;
    }
    public void readLabel(){
        File sdcard = Environment.getExternalStorageDirectory();

//Get the text file
        File file = new File(sdcard,"Download/svmPredictFile");

//Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                Toast.makeText(getActivity(),line,Toast.LENGTH_LONG).show();
                if(Integer.parseInt(line)==-1){
                    Toast.makeText(getActivity(),"api chosen",Toast.LENGTH_LONG).show();
                        gethitmfcc();
                }
                else{
                    Toast.makeText(getActivity(),"dev chosen",Toast.LENGTH_LONG).show();
                    localMfcc();
                }
            }
            br.close();
        }
        catch (IOException e) {
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
            //You'll need to add proper error handling here
        }

    }
    private class AsyncPredictTask extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(getActivity(),"Executing svm-predict, please wait...",Toast.LENGTH_LONG).show();
            Log.d(ContainerActivity.TAG, "==================\nStart of SVM PREDICT\n==================");
        }

        @Override
        protected Void doInBackground(String... params) {
            LibSVM.getInstance().predict(TextUtils.join(" ", params));
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getContext(), "SVM Predict has executed successfully!", Toast.LENGTH_LONG).show();
            readLabel();
            Log.d(ContainerActivity.TAG, "==================\nEnd of SVM PREDICT\n==================");
            dismiss();
        }
    }
    public void localMfcc(){
        long t1=System.currentTimeMillis();
        System.out.println(String.valueOf(t1));
        SharedPreferences shared = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        String filename = (shared.getString("fileName", ""));
        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+filename);
        if(file.length()==0){
            Toast.makeText(getActivity(),"audio not found",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(), "Found audio at=" + file, Toast.LENGTH_LONG).show();
        }


        //	Log.d("path",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS )+"/sound.wav");
        FeatureVector feature = new Test().extractFeatureFromFile(file);

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
    public void gethitmfcc(){

        // Toast.makeText(getActivity(),String.valueOf(n),Toast.LENGTH_LONG).show();
        //x1=System.currentTimeMillis();
      //  System.out.println("x1"+String.valueOf(x1));

        // System.out.println("begingethit**********************************************************");

        String url = "http://139.59.0.248:8080/SpeakerWebApp/malik/speaker/saveAudio";

        final JSONObject js = new JSONObject();
        if(!encoded.isEmpty()){
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
                           // Toast.makeText(getActivity(),"gethitmfcc",Toast.LENGTH_LONG).show();




                            if(status.equals("Success") ){
                               // Toast.makeText(getActivity(),"inside success",Toast.LENGTH_LONG).show();

                                System.out.println("inside Success");
                               // x2=System.currentTimeMillis();
                             //   System.out.println("x2value"+String.valueOf(x2));
                               // x3=x2-x1;
                               // Log.d("onResponse",String.valueOf(x2-x1));
                                // Toast.makeText(getActivity(),String.valueOf(n),Toast.LENGTH_LONG).show();
                              //  boolean result=writeDataSet(n,x3);





                                Log.d("status",status);
                                System.out.println( "Success>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );

                            }
                            else {
                                System.out.print("else block");
                            }


                        }catch (Exception e) {
                            Log.d("ERROR",e.getMessage());
                            //Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }






                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   Log.d("error",error.getMessage());
                if (error instanceof NetworkError) {
                    System.out.println( "Oops. Network error!");

                } else if (error instanceof ServerError) {
                    System.out.println( "Oops. Network error!");

                    Toast.makeText(getActivity(), "Oops. Server error!", Toast.LENGTH_LONG).show();

                } else if (error instanceof AuthFailureError) {
                    System.out.println( "Oops. Network error!");

                    Toast.makeText(getActivity(), "Oops. Auth error!", Toast.LENGTH_LONG).show();

                } else if (error instanceof ParseError) {
                    System.out.println( "Oops. Network error!");

                    Toast.makeText(getActivity(), "Oops. Parse error!", Toast.LENGTH_LONG).show();

                } else if (error instanceof NoConnectionError) {
                    System.out.println( "Oops. Network error!");

                    Toast.makeText(getActivity(), "Oops. NoConnection error!", Toast.LENGTH_LONG).show();

                } else if (error instanceof TimeoutError) {
                    System.out.println( "Oops. Network error!");

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
    else{
        Toast.makeText(getActivity(),"encoded null",Toast.LENGTH_LONG).show();
        }}
    public void getHitdnn(){

    }

    public void convertToBase64(){
        SharedPreferences shared = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        String filename = (shared.getString("fileName", ""));
        if(filename.isEmpty()){
            Toast.makeText(getActivity(),"file null",Toast.LENGTH_LONG).show();

        }
        else{
            Toast.makeText(getActivity(),filename,Toast.LENGTH_LONG).show();


        }
        File file=new File(filename);

        if(!file.exists()){
            Toast.makeText(getActivity(),"audio not found",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(), "Found audio at=" +filename, Toast.LENGTH_LONG).show();
        }
        byte[] bytes = new byte[0];
        try {
            bytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG",e.getMessage());
        }

        encoded = Base64.encodeToString(bytes, 0);
        Log.d("Encoded String: ", encoded);
        Toast.makeText(getActivity(),"encoded",Toast.LENGTH_LONG).show();
    }

}
