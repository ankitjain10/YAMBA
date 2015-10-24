package com.example.dell.mmb;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RefreshService extends IntentService {

    private static final String TAG = "RefreshService";

    public RefreshService() {
        super(TAG);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        Log.d(TAG, "onStartCommand interval :" + intent.getIntExtra("interval", 0));
        try {
            ((MyApplication) getApplicationContext()).pullAndInsert();
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch timeline", e);
        }
    }
}
