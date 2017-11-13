package com.example.home.superprayer;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.home.superprayer.Adapter.MyViewPagerAdapter;

public class DashboardActivity extends AppCompatActivity {

    private ViewPager mRootViewPager;
    private MyViewPagerAdapter myViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootViewPager = (ViewPager) findViewById(R.id.fragment_view_pager);
        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        mRootViewPager.setAdapter(myViewPagerAdapter);
    }
}
