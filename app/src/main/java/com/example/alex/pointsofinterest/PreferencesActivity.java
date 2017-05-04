package com.example.alex.pointsofinterest;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by 2davaa02 on 04/05/2017.
 */
public class PreferencesActivity extends PreferenceActivity {
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
