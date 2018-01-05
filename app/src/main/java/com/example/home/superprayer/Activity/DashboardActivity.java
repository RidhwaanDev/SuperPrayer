package com.example.home.superprayer.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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

import com.example.home.superprayer.Dialog.PrayerDateDialogFragment;
import com.example.home.superprayer.Fragment.CompassFragment;
import com.example.home.superprayer.Fragment.LogFragment;
import com.example.home.superprayer.Dialog.PrayerSearchDialogFragment;
import com.example.home.superprayer.Fragment.TimesFragment;
import com.example.home.superprayer.Model.PrayerDateModel;
import com.example.home.superprayer.Network.NetworkQueue;
import com.example.home.superprayer.R;
import com.example.home.superprayer.Util.LocationUtil;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import  com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DashboardActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    //constant key/code vars
    private static final int REQUEST_PLACE_AUTO_COMPLETE = 1000;
    private static final String CODE_SHOW_PRAYER_DIALOG = "SHOW PRAYER DIALOG PLEASE";
    private static final String CODE_SHOW_PRAYER_DATE_PICKER_DIALOG = "SHOW PRAYER DATE PICKER DIALOG PLEASE";
    private static final int MY_REQUEST_LOCATION_PERMISSION =1002;
    private static final int REQUEST_CHECK_SETTINGS = 1032;
    private static int CURRENT_FRAG = 0;
    private static final int LOCATION_UPDATE_INTERVAL = 0 * 120;

    // logic vars
    private LocationManager mLocationManager;




    //view vars
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

             mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
             if(!isLocationPermissionExist()){
                 requestLocationPermission();
             } else {
                requestLocationUpdates();
             }


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

    private void initfragment(){

            TimesFragment currentFragment = new TimesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_layout_id, currentFragment, "Prayer Times");
            transaction.commit();

    }


    private boolean isLocationOn(){

        boolean isLocationEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return isLocationEnabled && isGpsEnabled;


    }

    /**
     * Logic for getting location
     *
     * 1. Request location permission
     *  a. if granted
     *     1.) Check if location is on
     *        a_1: if on, then add to shared prefs and continue.
     *        a_2: if not on, then get user to turn it on.
     *              - If they reject it then ask to enter manually
     *                    - if they reject manually then close the app
     *
     *
     *
     *
     */


    private boolean isLocationPermissionExist(){

        int checkPermissionCode = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(checkPermissionCode == PackageManager.PERMISSION_GRANTED){
            return true;

        } else {
            requestLocationPermission();
            return false;
        }

    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates(){

        //permission checked in isLocationPermissionExist
        Log.d("REQUESTING  UPDATES", "  LOCATION UPDATES REQUESTED");

        if(isLocationPermissionExist()) {
            if (isLocationOn()) {
                LocationListener mLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {

                            float lat = (float) location.getLatitude();
                            float lng = (float) location.getLongitude();

                            Log.d("LOCATION TAG DASHBOARD", " Latitude" + "   " + lat);
                            Log.d("LOCATION TAG DASHBOARD", " Longitude" + "   " + lng);


                            SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editPrefs = prefs.edit();
                            editPrefs.putFloat(getString(R.string.lat_location), lat);
                            editPrefs.putFloat(getString(R.string.lng_location), lng);
                            editPrefs.commit();

                            initfragment();

                        } else {
                            //enter manually
                            Log.d("LOCATION TAG DASHBOARD", "  NULL LOCATION ");
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                };

                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_INTERVAL, mLocationListener);
            } else {
                Log.d("CREATING LOCATION REQ", "  " + "creating location request");
                createLocationRequest();
            }
        }
    }

     private void requestLocationPermission() {

         ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_REQUEST_LOCATION_PERMISSION);


        /* if (checkPermissionLocation == PackageManager.PERMISSION_GRANTED) {

             // check if location is on
            if(isLocationOn()) {

                LocationListener mLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {

                            float lat = (float) location.getLatitude();
                            float lng = (float) location.getLongitude();

                            Log.d("LOCATION TAG DASHBOARD", " Latitude" + "   " + lat);
                            Log.d("LOCATION TAG DASHBOARD", " Longitude" + "   " + lng);


                            SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editPrefs = prefs.edit();
                            editPrefs.putFloat(getString(R.string.lat_location), lat);
                            editPrefs.putFloat(getString(R.string.lng_location), lng);
                            editPrefs.commit();
                        } else {
                            Log.d("LOCATION TAG DASHBOARD", "  NULL LOCATION ");
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                };

                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_INTERVAL, mLocationListener);
            } else {
                //turn on location

            }


         } else {
         }*/

     }

   protected void createLocationRequest(){
       /**
        * To create a request to get location settings first we must create an object defining the type of location we want ( Accuracy, update interval ).
        * Then we must pass tha to location settings request builder object. Finally we check if that location setting exist with SettingsClient and Task<?>
        *
        *
        */

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setFastestInterval(LocationUtil.FASTEST_UPDATE_INTERLVAL);
        mLocationRequest.setInterval(LocationUtil.LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener(this,new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
                    try{
                        //show dialog
                        ResolvableApiException  resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(DashboardActivity.this,REQUEST_CHECK_SETTINGS);

                    } catch (IntentSender.SendIntentException e2){
                        e.printStackTrace();
                    }
                }
            }
        });
   }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_REQUEST_LOCATION_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("REQUEST PERMISSON", "    " + "permission granted");
                        requestLocationUpdates();

                } else {
                    Toast.makeText(DashboardActivity.this,R.string.location_permission_denied,Toast.LENGTH_LONG).show();
                    //offer to enter manually
                }
                break;
        }
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

            switch (requestCode){

                case REQUEST_PLACE_AUTO_COMPLETE:
                    Place place = PlaceAutocomplete.getPlace(this,data);

                    //  LatLng coordinates = place.getLatLng();

                    //     double lat = coordinates.latitude;
                    //    double lng = coordinates.longitude;

                    PrayerSearchDialogFragment dialogFragment = PrayerSearchDialogFragment.newInstance(place);
                    dialogFragment.show(getFragmentManager(),CODE_SHOW_PRAYER_DIALOG);

                    Toast.makeText(this,place.getName() + "  "  + place.getLatLng().toString(),Toast.LENGTH_SHORT).show();
                    break;
                case REQUEST_CHECK_SETTINGS:
                    LocationSettingsStates locState = LocationSettingsStates.fromIntent(data);
                    if(locState.isLocationPresent() && locState.isLocationUsable()){
                        initfragment();
                    }
                    break;
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

            Fragment currentFragment;


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
