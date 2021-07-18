package com.mgroup.remotealarm;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class PopUpService extends Service implements View.OnClickListener {

    private Button stopButton;
    private WindowManager wm1;
    private WindowManager.LayoutParams params = null;
    View popupView;
    LayoutInflater inflater;
    int LAYOUT_FLAG;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        wm1 = (WindowManager) this.getSystemService(this.WINDOW_SERVICE);
        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_window, null);
        if(Utilities.isGreaterThanOreo()){
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        Log.v("remote_alarm", "creating service");
        stopButton = popupView.findViewById(R.id.button);
        stopButton.setOnClickListener(this);
    }

    @Override
    public int onStartCommand(Intent intent,
                              int flags,
                              int startId) {
        super.onStartCommand(intent, flags, startId);
        if ((!Settings.canDrawOverlays(this))&&((Utilities.isGreaterThanOreo()))) {
            Toast.makeText(this, "need to grant permission from main activity first",
                    Toast.LENGTH_LONG).show();
        }else {
            if (!popupView.isShown()) {
                wm1.addView(popupView, params);
            }
        }
        Log.v("remote_alarm", "should show popup now");
        return START_STICKY;
    }


    @Override
    public void onClick(View view) {

        CheckWakeUpThread.mMediaPlayer.stop();
        wm1.removeView(popupView);
        stopSelf();
    }
}
