package com.mgroup.remotealarm;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;

public class WakeUpThread extends Thread {

    Context mContext;
    private String mName;

    public  WakeUpThread(Context context, String name) {
        this.mContext = context;
        this.mName = name;
    }


    public void run() {
        try {
            Log.v("remote_alarm", "sending http request");
            String url = "https://warm-meadow-45276.herokuapp.com/wake";
            HttpsURLConnection client = NetCipher.getHttpsURLConnection(url);
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("name", mName);
            String query = builder.build().getEncodedQuery();
            OutputStream os = client.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            int responseCode = client.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {

                Log.v("remote_alarm", "could not receive json url from server");
            }

        } catch (Exception e) {
            Log.v("remote_alarm", "exception in receiving json URL " + e);

        }


    }

}
