package com.example.home.superprayer.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.home.superprayer.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.settingsTheme);
        super.onCreate(savedInstanceState);

        View v = new AboutPage(this)
                .isRTL(false)
                .addItem(new Element().setTitle("Version 1.0"))
                .setDescription(getString(R.string.my_about_page_description))
                .addEmail("ridhwaan.any@gmail.com")
                .addWebsite("https://www.linkedin.com/in/ridhwaan-a-b8653a131/")
                .addGitHub("RidhwaanDev")
                .create();

        setContentView(v);

    }
}
