package com.mgroup.remotealarm;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
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

public class CheckWakeUpThread extends Thread {

    Context mContext;
    private String responseStatus;
    private String mName;

    public CheckWakeUpThread(Context context,String name) {
        this.mContext = context;
        this.mName = name;
    }


    public void run() {
        try {
            Log.v("MGCarAppStore", "not sending http request");
            String url = "https://warm-meadow-45276.herokuapp.com/checkwake";
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

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            responseStatus = response.toString();
            in.close();
            Log.v("remote_alarm", "server response is "+responseStatus);
            if(responseStatus.equals("yes")) {
                startAlarm();
            }

        } catch (Exception e) {
            Log.v("remote_alarm", "exception in receiving json URL " + e);

        }


    }

    public void startAlarm(){


        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(mContext, alarmUri);
        ringtone.play();
    }
}
