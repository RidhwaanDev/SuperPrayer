package com.example.home.superprayer.Fragment;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.home.superprayer.R;

import java.io.BufferedReader;
import java.util.List;

/**
 * Created by Home on 1/23/2018.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences setting_prefs;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences,getString(R.string.settings_pref_root_key));
        setting_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ListPreference methodpref = (ListPreference) findPreference("settings_pref_key_method?");
        ListPreference schoolpref = (ListPreference) findPreference("settings_pref_key_school?");

        methodpref.setSummary(methodpref.getEntry());
        schoolpref.setSummary(schoolpref.getEntry());

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        switch(s){
            case "settings_pref_key_school?":

                ListPreference schoolPreference = (ListPreference) findPreference(s);
                schoolPreference.setSummary(schoolPreference.getEntry());

                break;

            case "settings_pref_key_method?":

                ListPreference methodPreference = (ListPreference) findPreference(s);
                methodPreference.setSummary(methodPreference.getEntry());

                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        setting_prefs.registerOnSharedPreferenceChangeListener(this);


    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        setting_prefs.unregisterOnSharedPreferenceChangeListener(this);

    }
}
