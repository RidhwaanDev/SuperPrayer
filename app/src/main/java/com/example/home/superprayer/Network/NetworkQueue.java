package com.example.home.superprayer.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.home.superprayer.Model.PrayerModel;

/**
 * Created by Home on 11/6/2017.
 */

public class NetworkQueue {

     //singleton to hold request queue only once
    private static NetworkQueue mNetworkQueue;
    private static RequestQueue mRequestQueue;
    private static Context context;

    private static PrayerModel mPrayer;


    public static NetworkQueue getInstance(Context c){

        if(mNetworkQueue == null){
            mNetworkQueue = new NetworkQueue(c);
        }

        return mNetworkQueue;

    }

    private NetworkQueue(Context c){
        mRequestQueue = Volley.newRequestQueue(c);
        context = c;
    }

    public static void addJSONRequest(JsonObjectRequest obj){
        mRequestQueue.add(obj);
    }



    public static boolean isConnected(Context c){
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkActivityState = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkActivityState != null && networkActivityState.isConnectedOrConnecting();
        return isConnected;
    }

    public static void setPrayerInstance(PrayerModel model){
        mPrayer = model;
    }






}
