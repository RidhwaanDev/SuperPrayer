package com.example.home.superprayer;

import android.app.Application;
import android.net.sip.SipSession;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
    private DrawerLayout mDrawerNav;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

             mToolBar = findViewById(R.id.nav_toolbar);
             setSupportActionBar(mToolBar);



            TimesFragment currentFragment = new TimesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_layout_id, currentFragment, "Prayer Times");
            transaction.commit();


            mBottomNav = findViewById(R.id.bottom_navigation);
            mBottomNav.setOnNavigationItemSelectedListener(listener);

            mDrawerNav = findViewById(R.id.drawer_layout_id);
            mToggle = new ActionBarDrawerToggle(this,mDrawerNav,R.string.open,R.string.close);

            mDrawerNav.addDrawerListener(mToggle);
            mToggle.syncState();



           getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger_menu);
           getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //mRootViewPager =  findViewById(R.id.fragment_view_pager);
     //   myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
     //   mRootViewPager.setAdapter(myViewPagerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
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
