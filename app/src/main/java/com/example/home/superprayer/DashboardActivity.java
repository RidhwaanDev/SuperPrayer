package com.example.home.superprayer;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.home.superprayer.Adapter.MyViewPagerAdapter;
import com.example.home.superprayer.Fragment.CompassFragment;
import com.example.home.superprayer.Fragment.LogFragment;
import com.example.home.superprayer.Fragment.PrayerSearchDialogFragment;
import com.example.home.superprayer.Fragment.TimesFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.leakcanary.LeakCanary;

public class DashboardActivity extends AppCompatActivity {



    private static final int REQUEST_PLACE_AUTO_COMPLETE = 1000;
    private static final String CODE_SHOW_PRAYER_DIALOG = "SHOW PRAYER DIALOG PLEASE";

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
        int menuID = item.getItemId();

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        switch (menuID){

            case R.id.search_prayer_menu_item:
                startSearchActivity();
                return true;


                default:

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.dashboard_menu,menu);
        return true;
    }

    private void startSearchActivity(){

        try{
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent,REQUEST_PLACE_AUTO_COMPLETE);
        } catch (GooglePlayServicesRepairableException e){
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e){
            e.printStackTrace();
            Toast.makeText(this,"Google Play Services not found. Cannot search.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_PLACE_AUTO_COMPLETE){
                Place place = PlaceAutocomplete.getPlace(this,data);

              //  LatLng coordinates = place.getLatLng();

           //     double lat = coordinates.latitude;
            //    double lng = coordinates.longitude;

                PrayerSearchDialogFragment dialogFragment = PrayerSearchDialogFragment.newInstance(place);
                dialogFragment.show(getFragmentManager(),CODE_SHOW_PRAYER_DIALOG);



                Toast.makeText(this,place.getName() + "  "  + place.getLatLng().toString(),Toast.LENGTH_SHORT).show();


            }

        } else if (resultCode == Activity.RESULT_CANCELED){

            Status status = PlaceAutocomplete.getStatus(this,data);
            Log.d("PLACE AUTO COMPLETE", "    " + status.getStatus().toString());

        } else if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this,"Error. Can not search times", Toast.LENGTH_SHORT).show();

        }

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
