package com.mgroup.remotealarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver implements View.OnClickListener {

    private SharedPreferences mPrefs;
    String myName;
    boolean wanted;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("remote_alarm", "scheduled alarm is on");
        setAlarmAgain(context);
        mPrefs = context.getSharedPreferences("remote_alarm", MODE_PRIVATE);
        myName = mPrefs.getString("name", "");
        wanted = mPrefs.getBoolean("is_checked", true);
        if (wanted) {
            CheckWakeUpThread checker = new CheckWakeUpThread(context, myName);
            checker.start();
        }
    }


    public void setAlarmAgain(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 30000, pendingIntent);
    }

    @Override
    public void onClick(View view) {

    }
}
