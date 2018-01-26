package com.example.home.superprayer.Activity;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.home.superprayer.Fragment.SettingsFragment;
import com.example.home.superprayer.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.settingsTheme);
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
    public static Intent newInstance(Context c, @Nullable  Object obj){
        Intent i = new Intent(c, SettingsActivity.class);
        return i;
    }
}
