package com.danielkim;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.test.libsvmandroidexample.ContainerActivity;
import com.test.libsvmandroidexample.Utility;

import umich.cse.yctung.androidlibsvm.LibSVM;

public class SVMtrain extends Fragment {
    private LibSVM svm = new LibSVM();
    private String inputFilePath = Environment.getExternalStorageDirectory() + "/" + "Download/" + "prop";
    private String outputModelPath = Environment.getExternalStorageDirectory() + "/" + "LibSVMAssets/" + "prop" + "model";

    public void train() {
        new AsyncTrainTask().execute(new String[]{"-t 2", inputFilePath, outputModelPath});
        //svm.train("-t 2 "+inputFilePath+" "+outputModelPath);
    }

    private class AsyncTrainTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // showProgressDialog(this,"\"Executing svm-train, please wait...\"");
            // android.support.v4.app.FragmentManager fm= (( this).getSupportFragmentManager());
            //    vd.show(fm,"verify number");
            Log.d(ContainerActivity.TAG, "==================\nStart of SVM TRAIN\n==================");
        }

        @Override
        protected Void doInBackground(String... params) {
            LibSVM.getInstance().train(TextUtils.join(" ", params));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //progressDialog.dismiss();
            Toast.makeText(getContext(), "SVM Train has executed successfully!", Toast.LENGTH_LONG).show();
            Log.d(ContainerActivity.TAG, "==================\nEnd of SVM TRAIN\n==================");
            Utility.readLogcat(getContext(), "SVM-Train Results");
        }
    }

    public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog m_Dialog = new ProgressDialog(context);
        m_Dialog.setMessage(message);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setCancelable(false);
        m_Dialog.show();
        return m_Dialog;
    }

}
