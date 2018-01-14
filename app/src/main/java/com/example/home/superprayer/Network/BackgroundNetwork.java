package com.example.home.superprayer.Network;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.home.superprayer.Activity.DashboardActivity;
import com.example.home.superprayer.Fragment.TimesFragment;
import com.example.home.superprayer.Interface.NetWorkResponse;
import com.example.home.superprayer.Model.PrayerModel;
import com.example.home.superprayer.Model.PrayerNextModel;
import com.example.home.superprayer.R;
import com.example.home.superprayer.Util.LazyLog;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.home.superprayer.Model.NextPrayerEnum.ASR;
import static com.example.home.superprayer.Model.NextPrayerEnum.DUHR;
import static com.example.home.superprayer.Model.NextPrayerEnum.END;
import static com.example.home.superprayer.Model.NextPrayerEnum.FAJR;
import static com.example.home.superprayer.Model.NextPrayerEnum.ISHA;
import static com.example.home.superprayer.Model.NextPrayerEnum.MAGHRIB;

/**
 * Created by Home on 12/10/2017.
 */

public class BackgroundNetwork extends IntentService  {

    private static int ONE_MIN = (1000 * 60);
    private static int ONE_HOUR = ONE_MIN * 60;
    private static int TRUE_UPDATE = ONE_HOUR * 1; // every hour

    private static final String MY_NOTIFICATION_CHANNEL_ID = "my_channel_id_0002";

    private static final String KEY_LAT = "KEY PREFS LAT";
    private static final String KEY_LNG = "KEY PREFS LANG";
    private static final String KEY_PRAYER_STRING ="KEY PRAYER MODEL";
    private static final String KEY_PRAYER_TIME="KEY_PRAYER_TIME";

    private static final String TAG = "PollService";


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
        // PrayerNextModel nextModel  = TimesFragment.getNextPrayer(mServicePrayerModel);

        Bundle args = intent.getExtras();

        String prayer = args.getString(KEY_PRAYER_STRING);
        long time = args.getLong(KEY_PRAYER_TIME);

        LazyLog.log("BACKGROUND NETWORK",prayer+" " + time);

        fireNotifcation(prayer,time);

    }
    private void fireNotifcation(String prayer, long time){

        Resources resources = getResources();
        Intent i = DashboardActivity.newInstance(BackgroundNetwork.this);
        PendingIntent pi = PendingIntent.getActivity(BackgroundNetwork.this,0,i,0);


        Notification notification = new NotificationCompat.Builder(BackgroundNetwork.this,MY_NOTIFICATION_CHANNEL_ID).setTicker("my_ticker_0001")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(resources.getString(R.string.notification_title))
                .setContentText(prayer +" "+ "in" +" " + time + " " + "minutes")
                .setAutoCancel(true)
                .setContentIntent(pi)
                .build();

        NotificationManagerCompat manager = NotificationManagerCompat.from(BackgroundNetwork.this);
        manager.notify(0,notification);
    }

    public static void setAlarm(Context c, boolean isOn,PrayerModel model){
        //Create an alarm manager to start service
        //First create an intent to start the service
        //Wrap the intent into a pending intent so it can be used with AlarmManager

        Intent i = BackgroundNetwork.newInstance(c);

        PrayerNextModel nextModel = TimesFragment.getNextPrayer(model);
        String prayer = nextModel.geteNextPrayer().toString();
        long time = nextModel.getTimeUntilNextPrayer();

        LazyLog.log("ALARM", prayer +"" +time);


        i.putExtra(KEY_PRAYER_STRING,prayer);
        i.putExtra(KEY_PRAYER_TIME,time);


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
