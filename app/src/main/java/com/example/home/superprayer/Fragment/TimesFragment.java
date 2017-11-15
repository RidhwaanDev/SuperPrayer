package com.example.home.superprayer.Fragment;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorSpace;
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
import android.widget.ProgressBar;
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
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.w3c.dom.Text;

import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

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
    private ProgressBar mDownloadProgress;
    private CircularProgressBar mNextPrayerProgress;

    private enum CurrentPrayer {FAJR,DUHR,ASR,MAGHRIB,ISHA,END}
    private CurrentPrayer eNextPrayer;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_times_layout,container,false);




        mFajrText = (TextView) v.findViewById(R.id.fajr_time_tv);
        mDuhrText = (TextView) v.findViewById(R.id.duhr_time_tv);
        mAsrText = (TextView)  v.findViewById(R.id.asr_time_tv);
        mMaghrebText = (TextView) v.findViewById(R.id.maghrib_time_tv);
        mIshaText = (TextView) v.findViewById(R.id.isha_time_tv);

        mDownloadProgress = (ProgressBar) v.findViewById(R.id.download_not_finish_progress);
        mDownloadProgress.setVisibility(View.VISIBLE);

        mNextPrayerProgress = (CircularProgressBar) v.findViewById(R.id.prgb_progress_until_next_prayer);

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

        if(model != null){
            mDownloadProgress.setVisibility(View.GONE);
        }
        mNextPrayerProgress.setProgressWithAnimation(65, 3200);

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editPrefs = prefs.edit();

        Gson gson = new Gson();
        String packagedText = gson.toJson(model);
        editPrefs.putString(getString(R.string.shared_prefs_key),packagedText);
        editPrefs.commit();

        eNextPrayer = getCurrentPrayer(model);
        switch (eNextPrayer){
            case FAJR:
                Log.d("PRAYER", "FAJR");
            case DUHR:
                Log.d("PRAYER", "DUHR");
            case ASR:
                Log.d("PRAYER", "ASR");
            case MAGHRIB:
                Log.d("PRAYER", "MAGHRIB");
            case ISHA:
                Log.d("PRAYER", "ISHA");
            case END:
                Log.d("PRAYER", "END");

        }


             mFajrText.setText(model.getFajr());
             mDuhrText.setText(model.getDuhr());
             mAsrText.setText(model.getAsr());
             mMaghrebText.setText(model.getMaghrb());
             mIshaText.setText(model.getIsha());
    }

    private CurrentPrayer getCurrentPrayer(PrayerModel model){

        int fajr = convertPrayertoInt(model.getFajr24());
        int duhr = convertPrayertoInt(model.getDuhr24());
        int asr = convertPrayertoInt(model.getAsr24());
        int maghrib = convertPrayertoInt(model.getMaghrb24());
        int isha = convertPrayertoInt(model.getIsha24());

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date currentDate = new Date();
        String stringTime = sdf.format(currentDate);


        int absoluteNumberTime = convertPrayertoInt(stringTime);
        int times_array[] = {fajr,duhr,asr,maghrib,isha,absoluteNumberTime};
        Arrays.sort(times_array);
        for (int i = 0; i < times_array.length; i++) {
            Log.d("ARRAY TAG" , "  " + times_array[i]);
            if(times_array[i] == absoluteNumberTime){
                   if(i == 5){
                    return eNextPrayer.END;
                  }
                    int nextPrayer = times_array[i + 1];

                    if(nextPrayer == fajr){

                        return eNextPrayer.FAJR;
                    }
                    if(nextPrayer == duhr){
                    return eNextPrayer.DUHR;
                    }
                    if(nextPrayer == asr){
                    return eNextPrayer.ASR;
                }
                    if(nextPrayer == maghrib){
                    return eNextPrayer.MAGHRIB;
                  }
                    if(nextPrayer == isha){
                    return eNextPrayer.ISHA;
                }
            }

        }

        return null;



        //  Log.d("CURRENT TIME" , " Time is  " + stringTime);
        //   Log.d("CURRENT TIME" , " Time is  " + absoluteNumberTime);
    //  Log.d("Prayer Time Stamps", "Fajr" +  "\t" + fajr + "\n" + "Duhr" + "\t" + duhr + "\n" + "Asr" + "\t" + asr +" \n" + "Maghrib" +"\t" +maghrib + " \n " +"isha" + "\n" + isha);
    }

    private int convertPrayertoInt(String time){

            String[] timeSplit = time.split(":");
            String concat = (timeSplit[0] + timeSplit[1]);
            int absoluteTime = Integer.parseInt(concat);

            return absoluteTime;

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
        mDownloadProgress.setVisibility(View.GONE);

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
                Toast.makeText(context,R.string.network_check_success,Toast.LENGTH_SHORT);
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
