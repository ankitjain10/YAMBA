package com.example.dell.mmb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    PendingIntent lastOperation;
    public static final String TAG=BootReceiver.class.getSimpleName();
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //prefs.registerOnSharedPreferenceChangeListener(context);

        long interval = Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).
                getString("delay", "15000"));
        Intent refreshIntent=new Intent(MyApplication.ACTION_REFRESH_SERVICE);
        refreshIntent.putExtra("interval", interval);

        PendingIntent operation = PendingIntent.getService(context,
                -1,
                /*new Intent(MyApplication.ACTION_REFRESH_SERVICE).putExtra("interval", interval),
                */
                refreshIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(lastOperation);

        if (interval > 0) {
            alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(),
                    interval,
                    operation);
            Log.v(TAG,"BootReceiver started "+interval);
            //Toast.makeText(context, "BootReceiver started "+interval, Toast.LENGTH_SHORT).show();
        }
        lastOperation=operation;
    }
}
