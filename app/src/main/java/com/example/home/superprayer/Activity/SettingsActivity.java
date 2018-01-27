package com.example.home.superprayer.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.home.superprayer.Fragment.SettingsFragment;
import com.example.home.superprayer.R;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener,Preference.OnPreferenceChangeListener {

    private SharedPreferences setting_prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.settingsTheme);
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

        setting_prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final String SETTINGS_NOTIFICATIONS_KEY = getString(R.string.settings_pref_notification);
        final String SETTINGS_METHOD_KEY = getString(R.string.settings_pref_method);
        final String SETTINGS_SCHOOL_KEY = getString(R.string.settings_pref_school);

        boolean shouldNotifiy = setting_prefs.getBoolean(SETTINGS_NOTIFICATIONS_KEY,false);
        String method =  setting_prefs.getString(SETTINGS_METHOD_KEY, "2");
        String school = setting_prefs.getString(SETTINGS_SCHOOL_KEY,"0");

        Log.d("SETTINGS", "  " + method +"  " + school);
    }
    public static Intent newInstance(Context c, @Nullable  Object obj){
        Intent i = new Intent(c, SettingsActivity.class);
        return i;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d("SETTINGS", " Method entry changed: " + "  " + s);


        final String SETTINGS_NOTIFICATIONS_KEY = getString(R.string.settings_pref_notification);
        final String SETTINGS_METHOD_KEY = getString(R.string.settings_pref_method);
        final String SETTINGS_SCHOOL_KEY = getString(R.string.settings_pref_school);





    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return false;
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        // Set up a listener whenever a key changes
//        setting_prefs.registerOnSharedPreferenceChangeListener(this);
//
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        // Unregister the listener whenever a key changes
//        setting_prefs.unregisterOnSharedPreferenceChangeListener(this);
//
//    }


}
