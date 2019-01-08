package com.example.workstation.rfid_sas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapD extends Thread {

    Bitmap B;
    String Url;

    public void setUrl(String Url)
    {
        this.Url=Url;
    }

    public void run()
    {
        try {
            Log.e("src",Url);
            URL url = new URL(Url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            B= BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
        }
    }

    public Bitmap getBitmap()
    {
        return B;
    }
}
