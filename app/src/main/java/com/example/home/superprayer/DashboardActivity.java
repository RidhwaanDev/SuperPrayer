package com.example.home.superprayer;

import android.app.Application;
import android.net.sip.SipSession;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.home.superprayer.Adapter.MyViewPagerAdapter;
import com.example.home.superprayer.Fragment.CompassFragment;
import com.example.home.superprayer.Fragment.LogFragment;
import com.example.home.superprayer.Fragment.TimesFragment;
import com.google.android.gms.maps.model.Dash;
import com.squareup.leakcanary.LeakCanary;

public class DashboardActivity extends AppCompatActivity {

    private ViewPager mRootViewPager;
    private MyViewPagerAdapter myViewPagerAdapter;

    private BottomNavigationView mBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



            TimesFragment currentFragment = new TimesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_layout_id, currentFragment, "Prayer Times");
            transaction.commit();

            mBottomNav = findViewById(R.id.bottom_navigation);
            mBottomNav.setOnNavigationItemSelectedListener(listener);




        //mRootViewPager =  findViewById(R.id.fragment_view_pager);
     //   myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
     //   mRootViewPager.setAdapter(myViewPagerAdapter);




    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();

            Fragment currentFragment = null;


                switch (itemId) {


                    case R.id.action_prayer_times_frag:

                        currentFragment = new TimesFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_layout_id, currentFragment, "Prayer Times");
                        transaction.commit();

                        Log.d("VIEW PAGER", " HELLO ITS PRAYER ");
                        return true;

                    case R.id.action_log_frag:
                        currentFragment = new LogFragment();
                        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                        transaction2.replace(R.id.fragment_layout_id, currentFragment, "Prayer Log");
                        transaction2.commit();


                        Log.d("VIEW PAGER", " HELLO ITS LOG ");
                        return true;
                    case R.id.action_compass_frag:
                        currentFragment = new CompassFragment();
                        Log.d("VIEW PAGER", " HELLO ITS MOM ");
                        FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                        transaction3.replace(R.id.fragment_layout_id, currentFragment, "Compass");
                        transaction3.commit();
                        return true;

                }

         return false;
        }


    };


}
