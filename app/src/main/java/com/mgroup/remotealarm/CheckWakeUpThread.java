package com.mgroup.remotealarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class CheckWakeUpThread extends Thread {

    Context mContext;
    private String responseStatus;
    private String mName;
    private String mNumber;
    public static MediaPlayer mMediaPlayer;
    public CheckWakeUpThread(Context context,String name,String number) {
        this.mContext = context;
        this.mName = name;
        this.mNumber = number;
    }


    public void run() {
        try {
            Log.v("remote_alarm", "not sending http request");
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
                Log.v("remote_alarm", "server returned we should wake up");
                startAlarm();
                Intent newIntent= new Intent(mContext, PopUpService.class);
                mContext.startService(newIntent);
            }

        } catch (Exception e) {
            Log.v("remote_alarm", "exception in receiving json URL " + e);

        }


    }

    public void startAlarm(){
        startAlarmMediaPlayer();
    }



    public void startAlarmMediaPlayer() {
        try {
            Uri notification = null;
            notification = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_ALARM);

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
        }catch(Exception e){}
    }

}