package com.example.dell.mmb;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by dell on 8/2/2015.
 */
public class PrefActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }
}
