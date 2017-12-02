package com.example.home.superprayer.Dialog;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.home.superprayer.Model.PrayerDateModel;
import com.example.home.superprayer.Model.PrayerModel;
import com.example.home.superprayer.Network.NetWorkResponse;
import com.example.home.superprayer.Network.NetworkRequest;
import com.example.home.superprayer.R;
import com.google.android.gms.location.places.Place;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Home on 12/1/2017.
 */

public class PrayerDateDialogFragment extends DialogFragment implements NetWorkResponse {

    public static final String KEY_PUT_DATE = "key_pute_date";

   private String months[] = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"};

    private PrayerDateModel model;

    private TextView mFajrText,
            mDuhrText,
            mAsrText,
            mMaghrebText,
            mIshaText;
    private TextView mDialogTitleText;

    private NetworkRequest mNetworkRequest;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_prayer_search,null);


        mNetworkRequest = new NetworkRequest(getActivity());
        mNetworkRequest.mResponse = this;



        Bundle args = getArguments();
        model = (PrayerDateModel) args.get(KEY_PUT_DATE);


        mFajrText =  v.findViewById(R.id.fajr_time_tv);
        mDuhrText =  v.findViewById(R.id.duhr_time_tv);
        mAsrText =  v.findViewById(R.id.asr_time_tv);
        mMaghrebText =  v.findViewById(R.id.maghrib_time_tv);
        mIshaText =  v.findViewById(R.id.isha_time_tv);

        mDialogTitleText = v.findViewById(R.id.dialog_location_title);
        mDialogTitleText.setText(months[model.getMonth()] + " " + model.getDay() + "," + " " + model.getYear());


        float lat;
        float lng;

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        lat = prefs.getFloat(getString(R.string.lat_location),0);
        lng = prefs.getFloat(getString(R.string.lng_location),0);

        String requestPath = NetworkRequest.BuildRequest(lat,lng,new Timestamp(model.getmTime()).toString());
        mNetworkRequest.requestPrayerTimeSingle(requestPath);



        return new AlertDialog.Builder(getActivity())

                .setView(v)
                .setPositiveButton(android.R.string.ok, null)
                    .create();
    }

    @Override
    public void onDownloadedData(PrayerModel model) {
        mFajrText.setText(model.getFajr());
        mDuhrText.setText(model.getDuhr());
        mAsrText.setText(model.getAsr());
        mMaghrebText.setText(model.getMaghrb());
        mIshaText.setText(model.getIsha());
    }

    public static PrayerDateDialogFragment newPrayerDateDialogInstance (PrayerDateModel model){

        Bundle args = new Bundle();
        args.putSerializable(KEY_PUT_DATE, model);


        PrayerDateDialogFragment dateDialogFragment = new PrayerDateDialogFragment();
        dateDialogFragment.setArguments(args);
        return dateDialogFragment;


    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.colorMaghrib);
    }
}
