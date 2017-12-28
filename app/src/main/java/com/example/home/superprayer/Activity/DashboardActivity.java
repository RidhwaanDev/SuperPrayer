package com.example.home.superprayer.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
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
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.home.superprayer.Adapter.MyViewPagerAdapter;
import com.example.home.superprayer.Dialog.PrayerDateDialogFragment;
import com.example.home.superprayer.Fragment.CompassFragment;
import com.example.home.superprayer.Fragment.LogFragment;
import com.example.home.superprayer.Dialog.PrayerSearchDialogFragment;
import com.example.home.superprayer.Fragment.TimesFragment;
import com.example.home.superprayer.Model.PrayerDateModel;
import com.example.home.superprayer.Network.NetworkQueue;
import com.example.home.superprayer.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DashboardActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {



    private static final int REQUEST_PLACE_AUTO_COMPLETE = 1000;
    private static final String CODE_SHOW_PRAYER_DIALOG = "SHOW PRAYER DIALOG PLEASE";
    private static final String CODE_SHOW_PRAYER_DATE_PICKER_DIALOG = "SHOW PRAYER DATE PICKER DIALOG PLEASE";


    private ViewPager mRootViewPager;
    private MyViewPagerAdapter myViewPagerAdapter;

    private BottomNavigationView mBottomNav;
    private DrawerLayout mDrawerNav;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolBar;

    private static int CURRENT_FRAG = 0;

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

            case R.id.search_prayer_by_date_item:
                if(!NetworkQueue.isConnected(this)){
                    Toast.makeText(this,"Not connected to internet. Cannot search",Toast.LENGTH_SHORT).show();
                } else {
                    DatePickerDialog dialog = createDatePickerDialog(new Date());
                    dialog.show();

                }

                return true;

                default:

        }

        return super.onOptionsItemSelected(item);
    }


    private DatePickerDialog createDatePickerDialog(Date mDate){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);


        return new DatePickerDialog(this,R.style.DatePickerDialogCustom ,this,year,month,day);

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        long time = calendar.getTimeInMillis();
        String timeStamp = String.valueOf(time);

        PrayerDateModel model = new PrayerDateModel();
        model.setmTime(time);
        model.setmTimeStamp(String.valueOf(model.getmTime()));
        model.setDay(dayOfMonth);
        model.setMonth(monthOfYear);
        model.setYear(year);

        Log.d("onDateSet", "  TIME FLOAT" + "  " + time + "  " + " time stamp " +"  " + timeStamp);

        FragmentManager fg = getFragmentManager();
        PrayerDateDialogFragment dateDialogFragment = PrayerDateDialogFragment.newPrayerDateDialogInstance(model);
        dateDialogFragment.show(fg,CODE_SHOW_PRAYER_DATE_PICKER_DIALOG);

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

                        CURRENT_FRAG = 1;

                        Log.d("VIEW PAGER", " HELLO ITS PRAYER ");
                        return true;

                    case R.id.action_log_frag:
                        currentFragment = new LogFragment();
                        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                        transaction2.replace(R.id.fragment_layout_id, currentFragment, "Prayer Log");
                        transaction2.commit();

                        CURRENT_FRAG = 2;

                        Log.d("VIEW PAGER", " HELLO ITS LOG ");
                        return true;
                    case R.id.action_compass_frag:
                        currentFragment = new CompassFragment();
                        Log.d("VIEW PAGER", " HELLO ITS MOM ");
                        FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                        transaction3.replace(R.id.fragment_layout_id, currentFragment, "Compass");
                        transaction3.commit();


                        CURRENT_FRAG = 3;

                        return true;

                }

         return false;
        }


    };

    public static Intent newInstance(Context c){
       Intent i = new Intent(c,DashboardActivity.class);
       return i;
    }


}
