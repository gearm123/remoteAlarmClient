package com.mgroup.remotealarm;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

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
    public static MediaPlayer mMediaPlayer;
    public CheckWakeUpThread(Context context) {
        this.mContext = context;
    }


    public void run() {
        try {
            Log.v("remote_alarm", " sending http request");
            Log.v("remote_alarm", "my parsed number is "+Utilities.parseNumber(Utilities.getMyNumber(mContext)));
            String url = "https://damp-castle-07464.herokuapp.com/checkwake";
            HttpsURLConnection client = NetCipher.getHttpsURLConnection(url);
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("number", Utilities.parseNumber(Utilities.getMyNumber(mContext)));
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
            wakingObject resObject = parseJson(responseStatus);
            if(resObject.getStatus().equals("yes")) {
                Log.v("remote_alarm", "server returned we should wake up");
               // if(Utilities.isParsedFilteredContact(mContext,resObject.getWaker())) {
                    Log.v("remote_alarm","user is enbaled - waking up!");
                    startAlarm();
                    Intent newIntent = new Intent(mContext, PopUpService.class);
                    mContext.startService(newIntent);
            //    }else{
                    Log.v("remote_alarm","user is disabled - cannot wake me up");
            //    }
            }

        } catch (Exception e) {
            Log.v("remote_alarm", "exception in receiving json URL " + e);

        }


    }

    public void startAlarm(){
        startAlarmMediaPlayer();
    }

    public wakingObject parseJson(String response){
        JSONObject serverResponse = null;
        wakingObject tmpObject = null;
        try {
            serverResponse = new JSONObject(response);
            String status = serverResponse.getString("status");
            String waker = serverResponse.getString("waker");
            Log.v("remote_alarm", "status is "+status+" and waker is "+waker);
            tmpObject = new wakingObject(status,waker);
        }catch (Exception e){

        }
        return tmpObject;
    }



    public void startAlarmMediaPlayer() {
        try {
            Uri notification = null;
            notification = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_ALARM);
            Log.v("remote_alarm", "trying to start alarm");
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mContext, notification);

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);

            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer arg0) {
                    mMediaPlayer.seekTo(0);
                    mMediaPlayer.start();

                }

            });
        }catch(Exception e){
            Log.v("remote_alarm", "exception playing alarm"+e);
        }
    }

}