package com.example.dell.mmb;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

/**
 * Created by dell on 10/24/2015.
 */
public class MyApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
    static final String TAG = MyApplication.class.getSimpleName();
    static final String ACTION_NEW_STATUS = "com.example.dell.mmb.NEW_STATUS";
    static final String ACTION_REFRESH_SERVICE = "com.example.dell.mmb.RefreshService";
    static final String ACTION_BOOTRECEIVER = "com.example.dell.mmb.RefreshAlarm";
    Twitter twitter;
    SharedPreferences prefs;
    static final Intent broadcastIntent = new Intent(ACTION_BOOTRECEIVER);

    @Override
    public void onCreate() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        super.onCreate();
        Log.v(TAG, "onCreate called");

    }

    public Twitter getTwitter() {
        if (twitter == null) {
            String username = prefs.getString(getString(R.string.username_pref_key),
                    getString(R.string.username_default));
            String password = prefs.getString(getString(R.string.password_pref_key)
                    , getString(R.string.password_default));
            String server = prefs.getString(getString(R.string.server_pref_key)
                    , getString(R.string.server_default));
            String delay = prefs.getString(getString(R.string.delay_pref_key),
                    getString(R.string.delay_default));
            try {
                twitter = new Twitter(username, password);
                twitter.setAPIRootUrl(server);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return twitter;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        twitter = null;
        sendBroadcast(broadcastIntent);
        prefs = sharedPreferences;
        Log.v(TAG, "onSharedPreferenceChanged of key " + key);
    }

    long lastTimeStampSeen = -1;

    public int pullAndInsert() {
        int counter = 0;
        long biggestTimeStampSeen = -1;
        Twitter.Status newStatus =null;
        try {
            List<Twitter.Status> timeLine =
                    getTwitter().getPublicTimeline();
            for (Twitter.Status status : timeLine) {
                newStatus=status;
                getContentResolver().insert(StatusProvider.CONTENT_URI, DbHelper.getContentValues(status));
                if (status.createdAt.getTime() > lastTimeStampSeen) {
                    counter++;
                    biggestTimeStampSeen = (status.createdAt.getTime() > lastTimeStampSeen)
                            ? status.createdAt.getTime() : lastTimeStampSeen;
                    lastTimeStampSeen = status.createdAt.getTime();
                    /*Log.v(TAG, String.format("status %s :user %s :text %s:counter %d:" +
                                    "timeStamp %d",
                            status.createdAt.getTime(), status.user.name, status.text.toString()
                            , counter, biggestTimeStampSeen));
                */
                }
            }
            if (counter > 0) {
                sendBroadcast(new Intent(ACTION_NEW_STATUS).putExtra(DbHelper.C_USER, newStatus.user.name)
                        .putExtra(DbHelper.C_TEXT,newStatus.text.toString() ));
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        lastTimeStampSeen = biggestTimeStampSeen;
        return counter;
    }

}
