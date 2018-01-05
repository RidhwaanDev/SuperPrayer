package com.example.home.superprayer.Network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.home.superprayer.Interface.NetWorkResponse;
import com.example.home.superprayer.Model.PrayerModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Home on 11/6/2017.
 */

public class NetworkRequest {

    private Context mContext;
    private RequestQueue mQueue;
    private NetworkQueue mNetworkQueue;
    public NetWorkResponse mResponse = null;

    public NetworkRequest(Context context) {
        mQueue = Volley.newRequestQueue(context);
        mNetworkQueue = NetworkQueue.getInstance(context);
        this.mContext = context;

    }

    public JSONObject requestPrayerTimeSingle(String URL_PATH) {
        JsonObjectRequest mJsonRequest = new JsonObjectRequest(Request.Method.GET, URL_PATH, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    JSONObject res = response;
                    String content = res.toString();
                    Log.d("TAG", "   " +  content);
                    try{
                        JSONObject root = res.getJSONObject("data");
                        JSONObject dataOBJ = root.getJSONObject("timings");

                        String fajr = dataOBJ.getString("Fajr");
                        String duhr = dataOBJ.getString("Dhuhr");
                        String asr = dataOBJ.getString("Asr");
                        String maghrib = dataOBJ.getString("Maghrib");
                        String isha = dataOBJ.getString("Isha");
                        String sunrse = dataOBJ.getString("Sunrise");
                        String sunset = dataOBJ.getString("Sunset");

                        PrayerModel model = new PrayerModel();
                        model.setFajr(fajr);
                        model.setDuhr(duhr);
                        model.setAsr(asr);
                        model.setMaghrb(maghrib);
                        model.setIsha(isha);
                        model.setSunrise(sunrse);
                        model.setSunset(sunset);

                        mResponse.onDownloadedData(model);


                        mNetworkQueue.setPrayerInstance(model);




                    } catch (JSONException e){
                        e.printStackTrace();
                    }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }

        });


      //  mQueue.add(mJsonRequest);
        mNetworkQueue.addJSONRequest(mJsonRequest);
        return null;

    }


    public static Timestamp getTimeStampOf(){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try{

            Date date = dateFormat.parse("12/25/2023");
            long time = date.getTime();
           return new Timestamp(time);
        }catch (ParseException e){
            e.printStackTrace();
        }

        return null;

    }

    public static String BuildRequest(double lat, double lng, String ts){

        //current time stamp

        if(ts == null){
            Long tsLong = System.currentTimeMillis()/1000;
            ts = tsLong.toString();
        }


        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(NetworkPaths.AUTHORITY_PATH)
                .appendPath("timings")
                .appendPath(ts)
                .appendQueryParameter("latitude",String.valueOf(lat))
                .appendQueryParameter("longitude", String.valueOf(lng))
                .appendQueryParameter("school","1")
                .build();

        return builder.toString();
    }

}

