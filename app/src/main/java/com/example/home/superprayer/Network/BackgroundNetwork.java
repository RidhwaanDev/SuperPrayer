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
import com.example.home.superprayer.R;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Home on 12/10/2017.
 */

public class BackgroundNetwork extends IntentService {

    private static int UPDATE_INTERVAL = (1000 * 60);
    private static int TRUE_UPDATE = (1000 * 60) * 10;

    public static final String KEY_PRAYER_MODEL_TO_NOTIFIY = "BACKGROUND_NETWORK_UPDATE_NOTF";
    private static final String MY_NOTIFICATION_CHANNEL_ID = "my_channel_id_0002";


    private static final String TAG = "PollService";

    private PrayerModel model;



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

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("BACKGROUND RECIEVE", "Received an intent: " + intent);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(TimesFragment.SHARED_PREFS_SERVICE_DATE,Context.MODE_PRIVATE);
        String prayer = preferences.getString(TimesFragment.KEY_NEXT_PRAYER_SERVICE,"");
        long time = preferences.getLong(TimesFragment.KEY_NEXT_TIME_SERVICE,0);
        fireNotifcation(prayer,time);


    }

    private void fireNotifcation(String prayer,long time){

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


    public static void setAlarm(Context c, boolean isOn){
        //Create an alarm manager to start service
        //First create an intent to start the service
        //Wrap the intent into a pending intent so it can be used with AlarmManager


        Intent i = BackgroundNetwork.newInstance(c);
        PendingIntent pendingIntent = PendingIntent.getService(c,0,i,0);



        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        if(isOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),UPDATE_INTERVAL,pendingIntent);
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
