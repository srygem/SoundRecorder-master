package com.test.libsvmandroidexample;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;
import com.danielkim.soundrecorder.R;

import umich.cse.yctung.androidlibsvm.LibSVM;


public class TrainFragment extends AppCompatDialogFragment {
    public TrainFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
       View view = inflater.inflate(R.layout.fragment_train, null, false);
       final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).show();
        dialog.setCanceledOnTouchOutside(false);
        String outputModelPath = Environment.getExternalStorageDirectory() + "/"+"Download/"+"svmAndroidModel";
        String commandString = "-t 2";
        String dataFilePath = Environment.getExternalStorageDirectory()+"/"+"Download/"+"MyCSVFile";
        new AsyncTrainTask().execute(new String[]{commandString, dataFilePath, outputModelPath});

        return dialog;
    }

    private class AsyncTrainTask extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(),"Executing svm-train, please wait...",Toast.LENGTH_LONG).show();
            Log.d(ContainerActivity.TAG, "==================\nStart of SVM TRAIN\n==================");
        }

        @Override
        protected Void doInBackground(String... params) {
            LibSVM.getInstance().train(TextUtils.join(" ", params));
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getContext(), "SVM Train has executed successfully!", Toast.LENGTH_LONG).show();
            Log.d(ContainerActivity.TAG, "==================\nEnd of SVM TRAIN\n==================");
           // Utility.readLogcat(getContext(), "SVM-Train Results");
            dismiss();
        }
    }

}
