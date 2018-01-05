package com.example.home.superprayer.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.home.superprayer.Model.PrayerDialogModel;
import com.example.home.superprayer.Model.PrayerModel;
import com.example.home.superprayer.Interface.NetWorkResponse;
import com.example.home.superprayer.Network.NetworkRequest;
import com.example.home.superprayer.R;
import com.google.android.gms.location.places.Place;

/**
 * Created by Home on 11/26/2017.
 */

public class PrayerSearchDialogFragment extends DialogFragment implements NetWorkResponse{

    public static final String KEY_PUT_LAT_EXTRA = "LAT_Extra";
    public static final String KEY_PUT_LNG_EXTRA = "LNG_EXTRA";
    public static final String KEY_PUT_PLACE_EXTRA = "PLACE_EXTRA";


    public static final String KEY_PUT_LAT_FROM_SHARED = "LAT_DEFAULT";
    public static final String KEY_PUT_LNG_FROMSHARED = "LNG_DEFAULT";

    private NetworkRequest mNetworkRequest;
    private PrayerDialogModel mPlaceModel;

    private TextView mFajrText,
            mDuhrText,
            mAsrText,
            mMaghrebText,
            mIshaText;
    private TextView mDialogTitleText;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_prayer_search,null);

        mNetworkRequest = new NetworkRequest(getActivity());
        mNetworkRequest.mResponse = this;

        mFajrText =  v.findViewById(R.id.fajr_time_tv);
        mDuhrText =  v.findViewById(R.id.duhr_time_tv);
        mAsrText =  v.findViewById(R.id.asr_time_tv);
        mMaghrebText =  v.findViewById(R.id.maghrib_time_tv);
        mIshaText =  v.findViewById(R.id.isha_time_tv);

        mDialogTitleText = v.findViewById(R.id.dialog_location_title);

        Bundle b = getArguments();
        double lat,lng;


            lat = (double) b.get(KEY_PUT_LAT_EXTRA);
            lng = (double) b.get(KEY_PUT_LNG_EXTRA);

            mPlaceModel = (PrayerDialogModel) b.get(KEY_PUT_PLACE_EXTRA);
            String placeName = mPlaceModel.getPlace().getName().toString();

            mDialogTitleText.setText(placeName);


        String requestPath = NetworkRequest.BuildRequest(lat,lng,null);
        mNetworkRequest.requestPrayerTimeSingle(requestPath);



        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok,null)
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

    public static PrayerSearchDialogFragment newInstance(Place mLocation){


        PrayerDialogModel model = new PrayerDialogModel(mLocation);

        double lat,lng;
        lat = mLocation.getLatLng().latitude;
        lng = mLocation.getLatLng().longitude;


        Bundle bundle = new Bundle();
        bundle.putDouble(KEY_PUT_LAT_EXTRA,lat);
        bundle.putDouble(KEY_PUT_LNG_EXTRA,lng);

        bundle.putSerializable(KEY_PUT_PLACE_EXTRA,model);

        PrayerSearchDialogFragment searchDialogFragment = new PrayerSearchDialogFragment();
        searchDialogFragment.setArguments(bundle);
        return searchDialogFragment;

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onStart() {
        super.onStart();
        ((android.support.v7.app.AlertDialog) getDialog()).getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.colorDuhr);
        ((android.support.v7.app.AlertDialog) getDialog()).getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.colorMaghrib);

    }
}
