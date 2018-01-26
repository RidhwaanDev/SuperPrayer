package com.example.home.superprayer.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.example.home.superprayer.R;

/**
 * Created by Home on 1/23/2018.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences,getString(R.string.settings_pref_root_key));

    }

}
