package com.danielkim;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class base64 {
    public void convert(File wav){
       // wav=new File(Environment.getExternalStorageDirectory() +"/Download/sound.wav");
        byte[] bytes = new byte[0];
        try {
            bytes = FileUtils.readFileToByteArray(wav);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG",e.getMessage());
        }

        String encoded = Base64.encodeToString(bytes, 0);
        Log.d("Encoded String: ", encoded);

        byte[] decoded = Base64.decode(encoded, 0);
        Log.d("Decoded String: ", Arrays.toString(decoded));

        try
        {
            File file2 = new File("fileName.wav");
            FileOutputStream os = new FileOutputStream(file2, true);
            os.write(decoded);
            os.close();
            Log.d("Tag","output");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("Tag",e.getMessage());
        }
    }

}

