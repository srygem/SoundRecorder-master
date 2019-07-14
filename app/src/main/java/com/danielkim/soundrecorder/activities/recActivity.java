package com.danielkim.soundrecorder.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.danielkim.soundrecorder.R;
import com.danielkim.soundrecorder.fragments.infoFrag;

import net.alhazmy13.gota.Gota;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class recActivity extends AppCompatActivity {
    ImageView info,record;
    mainFragRec frag;
    public static String appFolderPath;
    public static String systemPath;
    public static final int RESULT_ENABLE = 11;
    public static final String TAG = "LibSVMExample";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);
        info = findViewById(R.id.info);
        record = findViewById(R.id.record);
        final boolean readStorage = canReadExternalStorage();
        final boolean writeStorage = canWriteExternalStorage();
        if (!readStorage || !writeStorage) {
            try {
                new Gota.Builder(this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_LOGS)
                        .requestId(1)
                        .setListener((Gota.OnRequestPermissionsBack) this)
                        .check();
            } catch (ClassCastException e) {
                Toast.makeText(getApplicationContext(),"class cast exception",Toast.LENGTH_LONG).show();
            }
        }
        systemPath = Environment.getExternalStorageDirectory() + "/";
        appFolderPath = systemPath + "recTest/";

        // create assets folder if it doesn't exist
        createAssetsFolder();
        try {
            String[] list = getAssets().list("data");
            for (String file : list) {
                copyToExternalStorage(file, "data");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        record.setBackgroundColor(getResources().getColor(R.color.accent));
        //ViewCompat.setElevation(record,8);
        frag = new mainFragRec();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container,frag);
        transaction.commit();
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.setBackgroundColor(getResources().getColor(R.color.accent));
                record.setBackgroundColor(getResources().getColor(R.color.primary));
                info.setClickable(false);
                record.setClickable(true);
                infoFrag frag2 = new infoFrag();
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container,frag2);
                transaction.commit();
            }
        });
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.setBackgroundColor(getResources().getColor(R.color.primary));
                record.setBackgroundColor(getResources().getColor(R.color.accent));
                info.setClickable(true);
                record.setClickable(false);
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container,frag);
                transaction.commit();
            }
        });

    }



    private void copyToExternalStorage(String assetName, String assetsDirectory){
        String from = assetName;
        String to = appFolderPath+from;

        // check if the file exists
        File file = new File(to);
        if(file.exists()){
            Log.d(TAG, "copyToExternalStorage: file already exist, no need to copy: "+from);
        } else {
            // do copy
            boolean copyResult = copyAsset(getAssets(), from, assetsDirectory, to);
            Log.d(TAG, "copyToExternalStorage: isCopied -> "+copyResult);
        }
    }
    private boolean copyAsset(AssetManager assetManager, String fromAssetPath, String assetsDirectory, String toPath) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = assetManager.open(assetsDirectory+"/"+fromAssetPath);
            new File(toPath).createNewFile();
            outputStream = new FileOutputStream(toPath);
            copyFile(inputStream, outputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            Log.e(TAG, "copyAsset: unable to copy file: "+fromAssetPath);
            return false;
        }
    }
    private void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, read);
        }
    }
    public boolean canReadExternalStorage(){
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public boolean canWriteExternalStorage(){
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }
    private void createAssetsFolder(){
        // create app assets folder if not created
        File folder = new File(appFolderPath);

        if (!folder.exists()) {
            Log.d(TAG,"LibSVMAssets folder does not exist, creating one");
            folder.mkdirs();
        } else {
            Log.w(TAG,"INFO: LibSVMAssets folder already exists.");
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
