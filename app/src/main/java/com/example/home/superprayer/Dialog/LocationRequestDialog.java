package com.example.home.superprayer.Dialog;

import android.annotation.SuppressLint;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.example.home.superprayer.R;
import android.support.v4.app.DialogFragment;
/**
 * Created by Home on 1/4/2018.
 */

public class LocationRequestDialog extends DialogFragment {

    public static LocationRequestDialog newInstance(){
        return  new LocationRequestDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

 //       View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_location_prompt,null,false)
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_location_prompt,null);



        LottieAnimationView animationView =  v.findViewById(R.id.dialog_animation_view);
        animationView.loop(true);
        animationView.playAnimation();

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setCancelable(false)
                .create();


    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onStart() {
        super.onStart();
        ((android.support.v7.app.AlertDialog) getDialog()).getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.colorDuhr);
        ((android.support.v7.app.AlertDialog) getDialog()).getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.colorMaghrib);

    }

}
