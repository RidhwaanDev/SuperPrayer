package com.example.home.superprayer.Fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Typeface;
import android.hardware.usb.UsbInterface;
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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.home.superprayer.DashboardActivity;
import com.example.home.superprayer.Manifest;
import com.example.home.superprayer.Model.NextPrayerEnum;
import com.example.home.superprayer.Model.PrayerModel;
import com.example.home.superprayer.Model.PrayerNextModel;
import com.example.home.superprayer.Network.BackgroundNetwork;
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

import java.lang.annotation.Target;
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
    private static final String MY_NOTIFICATION_CHANNEL_ID = "my_channel_id_0001";

    public static final String SHARED_PREFS_SERVICE_DATE = "shared_prefs_for_service_data";
    public static final String KEY_NEXT_TIME_SERVICE = "next_time_for_service";
    public static final String KEY_NEXT_PRAYER_SERVICE = "next_time_for_service";


    private static final int MY_NOTIFCATION_ID = 0;

    private TextView mFajrText,
            mDuhrText,
            mAsrText,
            mMaghrebText,
            mIshaText;

    private TextView mNextPrayertv;

    private ProgressBar mDownloadProgress;
    private ProgressBar mPrayerProgress;

    private enum CurrentPrayer {FAJR,DUHR,ASR,MAGHRIB,ISHA,END}
    private CurrentPrayer eNextPrayer;
    private long mNextPrayer;

    private SharedPreferences mServicePrefs;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_times_layout,container,false);

        mServicePrefs = getActivity().getApplicationContext().getSharedPreferences(SHARED_PREFS_SERVICE_DATE,Context.MODE_PRIVATE);


        startService();

        mFajrText =  v.findViewById(R.id.fajr_time_tv);
        mDuhrText =  v.findViewById(R.id.duhr_time_tv);
        mAsrText =  v.findViewById(R.id.asr_time_tv);
        mMaghrebText =  v.findViewById(R.id.maghrib_time_tv);
        mIshaText =  v.findViewById(R.id.isha_time_tv);

        mNextPrayertv = v.findViewById(R.id.tv_next_prayer);

        mDownloadProgress = v.findViewById(R.id.download_not_finish_progress);
        mDownloadProgress.setVisibility(View.VISIBLE);

        mPrayerProgress = v.findViewById(R.id.progressUntilNextPrayer);



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

        if(isAdded()) {
            SharedPreferences prefs = getActivity() .getPreferences(Context.MODE_PRIVATE);

            SharedPreferences.Editor editPrefs = prefs.edit();

            Gson gson = new Gson();
            String packagedText = gson.toJson(model);
            editPrefs.putString(getString(R.string.shared_prefs_key),packagedText);
            editPrefs.commit();


        }

        PrayerNextModel nextPrayerModel = getNextPrayer(model);
        switch (nextPrayerModel.geteNextPrayer()){

            case FAJR:
                mFajrText.setTypeface(mFajrText.getTypeface(), Typeface.BOLD);
                handleNextPrayer(nextPrayerModel,"Fajr");
                Log.d("PRAYER", "FAJR");
                break;
            case DUHR:
                mDuhrText.setTypeface(mDuhrText.getTypeface(), Typeface.BOLD);
                handleNextPrayer(nextPrayerModel,"Duhr");

                Log.d("PRAYER", "DUHR");
                break;
            case ASR:
                mAsrText.setTypeface(mAsrText.getTypeface(),Typeface.BOLD);
                handleNextPrayer(nextPrayerModel,"Asr");

                Log.d("PRAYER", "ASR");
                break;
            case MAGHRIB:
                mMaghrebText.setTypeface(mMaghrebText.getTypeface(),Typeface.BOLD);
                handleNextPrayer(nextPrayerModel,"Maghrib");

                Log.d("PRAYER", "MAGHRIB");
                break;
            case ISHA:
                mIshaText.setTypeface(mIshaText.getTypeface(),Typeface.BOLD);
                handleNextPrayer(nextPrayerModel,"Isha");

                Log.d("PRAYER", "ISHA");
                break;
            case END:
                mFajrText.setTypeface(mFajrText.getTypeface(), Typeface.BOLD);
                handleNextPrayer(nextPrayerModel,null);

                Log.d("PRAYER", "END");
            default:
        }


             mFajrText.setText(model.getFajr());
             mDuhrText.setText(model.getDuhr());
             mAsrText.setText(model.getAsr());
             mMaghrebText.setText(model.getMaghrb());
             mIshaText.setText(model.getIsha());


    }

    private void handleNextPrayer(PrayerNextModel model,String nextprayerText){
        // edit mNextPrayetYV

        if(model.geteNextPrayer() == NextPrayerEnum.END){
            mNextPrayertv.setText(R.string.prayer_next_end);
        } else {

            mNextPrayertv.setText(nextprayerText + " " +"in" +" " + model.getTimeUntilNextPrayer());
            mPrayerProgress.setProgress((int)model.getTimeUntilNextPrayer());
        }


    }
    public static PrayerNextModel getNextPrayer(PrayerModel model){

        PrayerNextModel nextModel = new PrayerNextModel(model);

        long nextPrayerTime;


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

                //   Log.d("LOOP TAG",  " Time : " + times_array[i] + "  current time  " + absoluteNumberTime + "  Next prayer imes  " + times_array[i + 1] );

                if(i == 5){
                    nextPrayerTime = 0;
                    nextModel.seteNextPrayer(NextPrayerEnum.END);
                    nextModel.setTimeUntilNextPrayer(0);
                    return nextModel;
                }
                int nextPrayer = times_array[i + 1];

                if(nextPrayer == fajr){
                    nextPrayerTime = timeUntilNextPrayer(model.getIsha24(),stringTime,model.getFajr24());
                    nextModel.seteNextPrayer(NextPrayerEnum.FAJR);
                    nextModel.setTimeUntilNextPrayer(nextPrayerTime);

                    return nextModel;
                }
                if(nextPrayer == duhr){
                    nextPrayerTime = timeUntilNextPrayer(model.getFajr24(),stringTime,model.getDuhr24());
                    nextModel.seteNextPrayer(NextPrayerEnum.DUHR);
                    nextModel.setTimeUntilNextPrayer(nextPrayerTime);

                    return nextModel;
                }
                if(nextPrayer == asr){
                    nextPrayerTime = timeUntilNextPrayer(model.getDuhr24(),stringTime,model.getAsr24());
                    nextModel.seteNextPrayer(NextPrayerEnum.ASR);
                    nextModel.setTimeUntilNextPrayer(nextPrayerTime);
                    return nextModel;
                }
                if(nextPrayer == maghrib){
                    nextPrayerTime = timeUntilNextPrayer(model.getAsr24(),stringTime,model.getMaghrb24());
                    nextModel.seteNextPrayer(NextPrayerEnum.MAGHRIB);
                    nextModel.setTimeUntilNextPrayer(nextPrayerTime);
                    return nextModel;
                }
                if(nextPrayer == isha){
                    nextPrayerTime = timeUntilNextPrayer(model.getMaghrb24(),stringTime,model.getIsha24());
                    nextModel.seteNextPrayer(NextPrayerEnum.ISHA);
                    nextModel.setTimeUntilNextPrayer(nextPrayerTime);
                    return nextModel;
                }
            }

        }

        return null;

        //  Log.d("CURRENT TIME" , " Time is  " + stringTime);
        //   Log.d("CURRENT TIME" , " Time is  " + absoluteNumberTime);
        //  Log.d("Prayer Time Stamps", "Fajr" +  "\t" + fajr + "\n" + "Duhr" + "\t" + duhr + "\n" + "Asr" + "\t" + asr +" \n" + "Maghrib" +"\t" +maghrib + " \n " +"isha" + "\n" + isha);
    }




    public static long timeUntilNextPrayer(String lastPrayer , String currentTime, String nextPrayerTime){

        SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
        try {

            Date date1 = sdf.parse(lastPrayer);
            Date date2 = sdf.parse(nextPrayerTime);
            Date currentDate = sdf.parse(currentTime);

            long diff =  (date2.getTime() - date1.getTime()) / 1000;
            long diff2 = (date2.getTime() - currentDate.getTime()) / 1000;
            Log.d("NEXT PRAYER TIME CALC" ,  "   " + lastPrayer + "  " + nextPrayerTime + "  " + diff);
            Log.d("NEXT PRAYER TIME CALC" ,  "   " + lastPrayer + "  " + nextPrayerTime + "  " + diff2);
            double difference = (double)diff;
            double difference2 = (double)diff2;
            double absoluteDiff = (difference2/difference) * 100;
         //   mPrayerProgress.setProgress((int)(100 - absoluteDiff));
            Log.d("Progress Tag", "  " +" diff1 " + " " + difference + "  " + difference2 + " "  + (difference2/difference) * 100);


            //convert from seconds to minutes
            return diff2 / 60;

        }catch (ParseException e){
            e.printStackTrace();
        }


        return 0;

    }
    private void fireNotifcation(String prayer, long time){

        Resources resources = getResources();
        Intent i = DashboardActivity.newInstance(getActivity());
        PendingIntent pi = PendingIntent.getActivity(getActivity(),0,i,0);


        Notification notification = new NotificationCompat.Builder(getActivity(),MY_NOTIFICATION_CHANNEL_ID).setTicker("my_ticker_0001")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(resources.getString(R.string.notification_title))
                .setContentText(prayer + " " + "in" + " " + time +" " + "minutes")
                .setAutoCancel(true)
                .setContentIntent(pi)
                .build();

        NotificationManagerCompat manager = NotificationManagerCompat.from(getActivity());
        manager.notify(0,notification);
    }

    public static int convertPrayertoInt(String time) {

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

        String requestPath = NetworkRequest.BuildRequest(lat,lng,null);


      /*  Uri.Builder builder = new Uri.Builder();
         builder.scheme("http")
                .authority(NetworkPaths.AUTHORITY_PATH)
                .appendPath("timings")
                .appendPath(ts)
                .appendQueryParameter("latitude",String.valueOf(lat))
                .appendQueryParameter("longitude", String.valueOf(lng))
                 .appendQueryParameter("school","1")
                .build();

        Log.d("URI BUILDER", "  final path " +   "  " + builder.toString());
*/

        NetworkRequest requestTimes = new NetworkRequest(getActivity());
        requestTimes.mResponse = this;
        requestTimes.requestPrayerTimeSingle(requestPath);
    }

    public void reloadTimes(){

        PrayerModel model = getCurrentModel();
        PrayerNextModel nextModel = getNextPrayer(model);
        PrayerNextModel nextPrayerModel = getNextPrayer(model);

        switch (nextPrayerModel.geteNextPrayer()){

            case FAJR:
                mFajrText.setTypeface(mFajrText.getTypeface(), Typeface.BOLD);
                handleNextPrayer(nextPrayerModel,"Fajr");
                Log.d("PRAYER", "FAJR");
                break;
            case DUHR:
                mDuhrText.setTypeface(mDuhrText.getTypeface(), Typeface.BOLD);
                handleNextPrayer(nextPrayerModel,"Duhr");

                Log.d("PRAYER", "DUHR");
                break;
            case ASR:
                mAsrText.setTypeface(mAsrText.getTypeface(),Typeface.BOLD);
                handleNextPrayer(nextPrayerModel,"Asr");

                Log.d("PRAYER", "ASR");
                break;
            case MAGHRIB:
                mMaghrebText.setTypeface(mMaghrebText.getTypeface(),Typeface.BOLD);
                handleNextPrayer(nextPrayerModel,"Maghrib");

                Log.d("PRAYER", "MAGHRIB");
                break;
            case ISHA:
                mIshaText.setTypeface(mIshaText.getTypeface(),Typeface.BOLD);
                handleNextPrayer(nextPrayerModel,"Isha");

                Log.d("PRAYER", "ISHA");
                break;
            case END:
                mFajrText.setTypeface(mFajrText.getTypeface(), Typeface.BOLD);
                handleNextPrayer(nextPrayerModel,null);

                Log.d("PRAYER", "END");
            default:
        }

        mFajrText.setText(model.getFajr());
        mDuhrText.setText(model.getDuhr());
        mAsrText.setText(model.getAsr());
        mMaghrebText.setText(model.getMaghrb());
        mIshaText.setText(model.getIsha());

        mDownloadProgress.setVisibility(View.GONE);

    }

    public PrayerModel getCurrentModel(){

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
    
        Gson gson = new Gson();
        String unpackagedText = prefs.getString(getString(R.string.shared_prefs_key),"");
        PrayerModel model = gson.fromJson(unpackagedText,PrayerModel.class);

        return model;

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

    private void startService(){
        // returns true if not exist ( hence ! )
        boolean doesPendingIntentExist = !BackgroundNetwork.isAlarmOn(getActivity());
        BackgroundNetwork.setAlarm(getActivity(),doesPendingIntentExist);

    }


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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNetWorkQueue = null;
        mLocationClient = null;
    }


}
