package com.example.home.superprayer.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.home.superprayer.Manifest;
import com.example.home.superprayer.Model.PrayerModel;
import com.example.home.superprayer.Network.NetWorkResponse;
import com.example.home.superprayer.Network.NetworkPaths;
import com.example.home.superprayer.Network.NetworkQueue;
import com.example.home.superprayer.Network.NetworkRequest;
import com.example.home.superprayer.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.security.Timestamp;

/**
 * Created by Home on 11/5/2017.
 */

public class TimesFragment extends Fragment implements NetWorkResponse {
    private NetworkQueue mNetWorkQueue;
    private FusedLocationProviderClient mLocationClient;
    private static final int MY_REQUEST_LOCATION_PERMISSION = 1000;
    private TextView mFajrText,
            mDuhrText,
            mAsrText,
            mMaghrebText,
            mIshaText;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_times_layout,container,false);


        mFajrText = (TextView) v.findViewById(R.id.fajr_time_tv);
        mDuhrText = (TextView) v.findViewById(R.id.duhr_time_tv);
        mAsrText = (TextView)  v.findViewById(R.id.asr_time_tv);
        mMaghrebText = (TextView) v.findViewById(R.id.maghrib_time_tv);
        mIshaText = (TextView) v.findViewById(R.id.isha_time_tv);





        getActivity().registerReceiver(networkReciever,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mNetWorkQueue = NetworkQueue.getInstance(getActivity());

        mLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        int checkPermissionLocation = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION);
        if(checkPermissionLocation == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();

        } else {
            ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},MY_REQUEST_LOCATION_PERMISSION);
        }

        return v;
    }

    @Override
    public void onDownloadedData(PrayerModel model) {

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editPrefs = prefs.edit();

        Gson gson = new Gson();
        String packagedText = gson.toJson(model);
        editPrefs.putString(getString(R.string.shared_prefs_key),packagedText);
        editPrefs.commit();


             mFajrText.setText(model.getFajr());
             mDuhrText.setText(model.getDuhr());
             mAsrText.setText(model.getAsr());
             mMaghrebText.setText(model.getMaghrb());
             mIshaText.setText(model.getIsha());
    }

    public void updateTimes(){

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        float lat;
        float lng;

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        lat = prefs.getFloat(getString(R.string.lat_location),0);
        lng = prefs.getFloat(getString(R.string.lng_location),0);

        Log.d("SHAREDPREFS LAT LNG", "  LAT  " +  lat + " LNG  " + lng);


        Uri.Builder builder = new Uri.Builder();
         builder.scheme("http")
                .authority(NetworkPaths.AUTHORITY_PATH)
                .appendPath("timings")
                .appendPath(ts)
                .appendQueryParameter("latitude",String.valueOf(lat))
                .appendQueryParameter("longitude", String.valueOf(lng))
                 .appendQueryParameter("school","1")
                .build();

        Log.d("URI BUILDER", "  final path " +   "  " + builder.toString());


        NetworkRequest requestTimes = new NetworkRequest(getActivity());
        requestTimes.mResponse = this;
        requestTimes.requestPrayerTimeSingle(builder.toString());
    }

    public void reloadTimes(){

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String unpackagedText = prefs.getString(getString(R.string.shared_prefs_key),"");
        PrayerModel model = gson.fromJson(unpackagedText,PrayerModel.class);

        mFajrText.setText(model.getFajr());
        mDuhrText.setText(model.getDuhr());
        mAsrText.setText(model.getAsr());
        mMaghrebText.setText(model.getMaghrb());
        mIshaText.setText(model.getIsha());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode){
                case MY_REQUEST_LOCATION_PERMISSION:
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                            getUserLocation();
                    } else {
                        Toast.makeText(getActivity(),R.string.location_permission_denied,Toast.LENGTH_LONG).show();
                    }
            }
    }

    private void getUserLocation(){
        //  check for permission is done elsewhere. method will always be called if permission is granted
        mLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){

                    float lat = (float)location.getLatitude();
                    float lng = (float)location.getLongitude();

                    Log.d("LOCATION TAG", " Latitude" +  "   "  + lat);
                    Log.d("LOCATION TAG", " Longitude" +  "   "  + lng);


                    SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editPrefs = prefs.edit();
                    editPrefs.putFloat(getString(R.string.lat_location),lat);
                    editPrefs.putFloat(getString(R.string.lng_location),lng);
                    editPrefs.commit();


                }
            }
        });
    }

    private BroadcastReceiver networkReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(NetworkQueue.isConnected(context)){
                Toast.makeText(context,R.string.network_check_success,Toast.LENGTH_SHORT).show();
                updateTimes();


            } else {
                Log.d("Network Reciever", " No internet found on Network reciever");
                reloadTimes();

                Toast.makeText(context,R.string.network_check,Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(networkReciever, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(networkReciever);
    }
}
