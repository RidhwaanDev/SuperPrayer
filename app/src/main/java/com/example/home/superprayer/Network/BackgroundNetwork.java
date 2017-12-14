package com.example.home.superprayer.Network;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.home.superprayer.DashboardActivity;
import com.example.home.superprayer.Fragment.TimesFragment;
import com.example.home.superprayer.Model.PrayerModel;
import com.example.home.superprayer.Model.PrayerNextModel;
import com.example.home.superprayer.R;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Home on 12/10/2017.
 */

public class BackgroundNetwork extends IntentService implements NetWorkResponse {

    private static int UPDATE_INTERVAL = (1000 * 60);
    private static int TRUE_UPDATE = (1000 * 60) * 10; // every ten minutes

    public static final String KEY_PRAYER_MODEL_TO_NOTIFIY = "BACKGROUND_NETWORK_UPDATE_NOTF";
    private static final String MY_NOTIFICATION_CHANNEL_ID = "my_channel_id_0002";

    private static final String KEY_LAT = "KEY PREFS LAT";
    private static final String KEY_LNG = "KEY PREFS LANG";



    private static final String TAG = "PollService";


    private PrayerNextModel mServiceNextModel;

    public static Intent newInstance(Context context){

        return new Intent(context,BackgroundNetwork.class);


    }

    public BackgroundNetwork() {
        super(TAG);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);



    }

    private void getTimes(double lat, double lng){

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();


        Log.d("SHAREDPREFS LAT LNG", "  LAT  " +  lat + " LNG  " + lng);

        String requestPath = NetworkRequest.BuildRequest(lat,lng,null);

        NetworkRequest mRequest = new NetworkRequest(getApplicationContext());
        mRequest.mResponse = this;
        mRequest.requestPrayerTimeSingle(requestPath);

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("BACKGROUND RECIEVE", "Received an intent: " + intent);
       // PrayerNextModel nextModel  = TimesFragment.getNextPrayer(mServicePrayerModel);
        if(!NetworkQueue.isConnected(getApplicationContext())){
            setAlarm(getApplicationContext(),false,0,0);
        } else {
            double lat = intent.getDoubleExtra(KEY_LAT,0);
            double lng = intent.getDoubleExtra(KEY_LNG,0);

            getTimes(lat,lng);


        }


}

    @Override
    public void onDownloadedData(PrayerModel model) {
        this.mServiceNextModel = TimesFragment.getNextPrayer(model);

        long time = mServiceNextModel.getTimeUntilNextPrayer();
        String prayer = mServiceNextModel.geteNextPrayer().toString();

        if(time != 0){
            fireNotifcation(prayer,time);

        }


        Log.d("SERVICE CALLBACK" , "   " + model.getFajr24());

    }

    private void fireNotifcation(String prayer, long time){

        Resources resources = getResources();
        Intent i = DashboardActivity.newInstance(BackgroundNetwork.this);
        PendingIntent pi = PendingIntent.getActivity(BackgroundNetwork.this,0,i,0);


        Notification notification = new NotificationCompat.Builder(BackgroundNetwork.this,MY_NOTIFICATION_CHANNEL_ID).setTicker("my_ticker_0001")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(resources.getString(R.string.notification_title))
                .setContentText(prayer +" "+ "in" +" " + time)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .build();

        NotificationManagerCompat manager = NotificationManagerCompat.from(BackgroundNetwork.this);
        manager.notify(0,notification);
    }


    public static void setAlarm(Context c, boolean isOn,double lat, double lng){
        //Create an alarm manager to start service
        //First create an intent to start the service
        //Wrap the intent into a pending intent so it can be used with AlarmManager


        Intent i = BackgroundNetwork.newInstance(c);
        i.putExtra(KEY_LAT,lat);
        i.putExtra(KEY_LNG,lng);
        PendingIntent pendingIntent = PendingIntent.getService(c,0,i,0);



        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        if(isOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),TRUE_UPDATE,pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
        // method  returns true if pending intent exist
    public static boolean isAlarmOn(Context c){
        Intent i = BackgroundNetwork.newInstance(c);
        PendingIntent pendingIntent = PendingIntent.getService(c,0,i,PendingIntent.FLAG_NO_CREATE);
        return  pendingIntent != null;

    }
}
