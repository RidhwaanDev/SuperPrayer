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
import android.net.wifi.WifiConfiguration;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.home.superprayer.Dialog.LocationRequestDialog;
import com.example.home.superprayer.Dialog.PrayerDateDialogFragment;
import com.example.home.superprayer.Fragment.CompassFragment;
import com.example.home.superprayer.Fragment.LogFragment;
import com.example.home.superprayer.Dialog.PrayerSearchDialogFragment;
import com.example.home.superprayer.Fragment.TimesFragment;
import com.example.home.superprayer.Model.PrayerDateModel;
import com.example.home.superprayer.Model.PrayerModel;
import com.example.home.superprayer.Network.NetworkQueue;
import com.example.home.superprayer.R;
import com.example.home.superprayer.Util.LocationUtil;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import  com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DashboardActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    //constant key/code vars

    private static final String CODE_SHOW_PRAYER_DIALOG = "SHOW PRAYER DIALOG PLEASE";
    private static final String CODE_SHOW_PRAYER_DATE_PICKER_DIALOG = "SHOW PRAYER DATE PICKER DIALOG PLEASE";
    private static final String CODE_SHOW_LOCATION_REQUEST_DIALOG = "SHOW LOCATION REQUEST DIALOG PLEASE";

    private static final int REQUEST_PLACE_AUTO_COMPLETE = 1000;
    private static final int MY_REQUEST_LOCATION_PERMISSION =1002;
    private static final int REQUEST_CHECK_SETTINGS = 1032;
    private static int CURRENT_FRAG = 0;
    private static final int LOCATION_UPDATE_INTERVAL = 0 * 120;

    // logic vars
    private LocationManager mLocationManager;
    private FusedLocationProviderClient mFusedLocation;


    //view vars
    private BottomNavigationView mBottomNav;
    private DrawerLayout mDrawerNav;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolBar;
    private RelativeLayout warningLayout;
     private  TextView retyTextView;

    private static final int NETWORK_ERROR = 1;
    private static final int LOCATION_ERROR = 2;
    private static final int PERMISSION_ERROR = 3;

    private boolean errorExists = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

             mToolBar = findViewById(R.id.nav_toolbar);
             setSupportActionBar(mToolBar);


             mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
             mFusedLocation = LocationServices.getFusedLocationProviderClient(this);


        mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setOnNavigationItemSelectedListener(listener);
        mBottomNav.setVisibility(View.INVISIBLE);

             if(!isLocationPermissionExist()){
                 requestLocationPermission();
             } else {
                 if(isLocationCached()){
                     initfragment();
                 } else {
                     updateWithLastKnownLocation();
                 }
             }




            mDrawerNav = findViewById(R.id.drawer_layout_id);
            mToggle = new ActionBarDrawerToggle(this,mDrawerNav,R.string.open,R.string.close);

            mDrawerNav.addDrawerListener(mToggle);
            mToggle.syncState();



           getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger_menu);
           getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initfragment(){

        TimesFragment currentFragment = new TimesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_layout_id, currentFragment, "Prayer Times");
        /**
         *
         * initfragment calls guarantees that we have a location
         *
         */
        if(NetworkQueue.isConnected(this) || isPrayerCached()){
            errorExists = false;

            if(warningLayout != null){
                warningLayout.setVisibility(View.GONE);
            }
            mBottomNav.setVisibility(View.VISIBLE);
            transaction.commit();

        } else {
            showError(NETWORK_ERROR);
        }


         //   transaction.commit();

    }
    private void showError(int errortype){

        errorExists = true;

         warningLayout = findViewById(R.id.rl_warning_root_container);
        retyTextView = findViewById(R.id.tv_retry_wifi);

        switch (errortype){
            case NETWORK_ERROR:
                retyTextView.setText(R.string.tv_retry_network);
                retyTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!NetworkQueue.isConnected(DashboardActivity.this)){
                            Toast.makeText(DashboardActivity.this,"No network found. Pease turn on Wi-fi/Cellular data", Toast.LENGTH_SHORT).show();
                        } else {
                            initfragment();
                        }
                    }
                });
                break;
            case LOCATION_ERROR:
                retyTextView.setText(R.string.tv_retry_location);
                retyTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resolveLocationSettings();
                    }
                });
                break;
            case PERMISSION_ERROR:
                retyTextView.setText(R.string.tv_retry_permission);
                retyTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestLocationPermission();
                    }
                });
                break;
        }

        warningLayout.setVisibility(View.VISIBLE);


    }

    private boolean isLocationPermissionExist(){

        int checkPermissionCode = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
      return ( checkPermissionCode == PackageManager.PERMISSION_GRANTED);

    }


    private  boolean isLocationCached(){
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        return (prefs.getFloat(getString(R.string.lat_location),0) != 0.0);

    }

    private void updateLatLngPrefs(Location location){

        float lat = (float) location.getLatitude();
        float lng = (float) location.getLongitude();

        Log.d("LOCATION TAG DASHBOARD", " Latitude" + "   " + lat);
        Log.d("LOCATION TAG DASHBOARD", " Longitude" + "   " + lng);


        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editPrefs = prefs.edit();
        editPrefs.putFloat(getString(R.string.lat_location), lat);
        editPrefs.putFloat(getString(R.string.lng_location), lng);
        editPrefs.commit();

    }
    private boolean isPrayerCached(){

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String unpackagedText = prefs.getString(getString(R.string.shared_prefs_key),"");
        PrayerModel model = gson.fromJson(unpackagedText,PrayerModel.class);

        return  model != null;
    }

    @SuppressLint("MissingPermission")
    private void updateLocation(){

        Log.d("UPDATING LOCATION TAG", "  " + "location being updated");
        LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {

                    updateLatLngPrefs(location);
                    initfragment();

                } else {
                    //enter manually
                    Log.d("LOCATION TAG DASHBOARD", "  NULL LOCATION ");
                    showError(LOCATION_ERROR);
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
    }

     private void requestLocationPermission() {
         ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CHANGE_WIFI_STATE}, MY_REQUEST_LOCATION_PERMISSION);
     }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_REQUEST_LOCATION_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("REQUEST PERMISSON", "    " + "permission granted");
                    // updateLocation();
                    updateWithLastKnownLocation();
                } else {
/*
                    Toast.makeText(DashboardActivity.this,R.string.location_permission_denied,Toast.LENGTH_LONG).show();
                    LocationRequestDialog requestDialog = LocationRequestDialog.newInstance();
                    requestDialog.show(getSupportFragmentManager(),CODE_SHOW_LOCATION_REQUEST_DIALOG);
*/
                showError(PERMISSION_ERROR);


                }
                break;
        }
    }

   protected void resolveLocationSettings(){
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
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
               LocationSettingsStates locState = locationSettingsResponse.getLocationSettingsStates();
                if(locState.isLocationPresent() && locState.isLocationUsable()){
                    updateLocation();

                }
            }
        });


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
                        Log.d("ERROR", "  " + e2.getMessage());
                    }
                }
            }
        });
   }

   @SuppressLint("MissingPermission")
   private void updateWithLastKnownLocation(){

           if(isLocationPermissionExist()) {
               mFusedLocation.getLastLocation().addOnSuccessListener(this,new OnSuccessListener<Location>() {
                   @Override
                   public void onSuccess(Location location) {
                       if(location != null){
                           updateLatLngPrefs(location);
                           initfragment();

                       } else {
                           resolveLocationSettings();
                       }

                   }
               });

               mFusedLocation.getLastLocation().addOnFailureListener(this, new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.d("updateWithLastKnownLoca", " failure gettomg ;ast lmpw ");
                       updateLocation();

                   }
               });
           } else {
               requestLocationPermission();
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
                        if (locState.isLocationPresent() && locState.isLocationUsable()) {
                            updateLocation();
                        }

                    break;
            }


        } else if (resultCode == Activity.RESULT_CANCELED){

            switch (requestCode){

                case REQUEST_PLACE_AUTO_COMPLETE:
                    Status status = PlaceAutocomplete.getStatus(this,data);
                    Log.d("PLACE AUTO COMPLETE", "    " + status.getStatus().toString());
                    break;

                case REQUEST_CHECK_SETTINGS:
                  /*  LocationRequestDialog dialog = LocationRequestDialog.newInstance();
                    dialog.show(getSupportFragmentManager(),CODE_SHOW_LOCATION_REQUEST_DIALOG);*/
                  showError(LOCATION_ERROR);
                    break;
            }



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
