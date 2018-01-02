package com.example.home.superprayer.Dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.home.superprayer.Database.DatabaseManager;
import com.example.home.superprayer.Model.PrayerDataBaseModel;
import com.example.home.superprayer.R;

import java.util.ArrayList;

/**
 * Created by Home on 12/28/2017.
 */

public class DataBaseClearDialog extends android.support.v4.app.DialogFragment implements DialogInterface.OnClickListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setTitle(R.string.confirm_delete)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel,null);
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onStart() {
        super.onStart();
        ((android.support.v7.app.AlertDialog) getDialog()).getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.colorDuhr);
        ((android.support.v7.app.AlertDialog) getDialog()).getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.colorMaghrib);

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        final DatabaseManager dbManager = DatabaseManager.getInstance(getActivity());
        final ArrayList<PrayerDataBaseModel> list = dbManager.getDBPrayers();
        Log.d("DIALOG TEST", "     " + "Enter Dialog");

        for (PrayerDataBaseModel model : list) {
            model.setmCount(0);
            dbManager.updatePrayer(model);

        }
    }
}
