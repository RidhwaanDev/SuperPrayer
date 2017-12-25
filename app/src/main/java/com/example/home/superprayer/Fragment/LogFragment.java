package com.example.home.superprayer.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.home.superprayer.Database.DatabaseManager;
import com.example.home.superprayer.Model.PrayerDataBaseModel;
import com.example.home.superprayer.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Home on 11/5/2017.
 */

public class LogFragment extends android.support.v4.app.Fragment {


    private DatabaseManager dbManager;

    private Button mFajrAdd,mDuhrAdd,mAsrAdd,mMaghribAdd,mIshaAdd;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_log_layout,container,false);
        dbManager = DatabaseManager.getInstance(getActivity());

        //Button button = v.findViewById(R.id.button_add_to_chart);

        mFajrAdd = v.findViewById(R.id.btn_add_fajr_missed);
        mDuhrAdd = v.findViewById(R.id.btn_add_duhr_missed);
        mAsrAdd = v.findViewById(R.id.btn_add_asr_missed);
        mIshaAdd = v.findViewById(R.id.btn_add_isha_missed);

        mFajrAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        PrayerDataBaseModel fajr = new PrayerDataBaseModel();
        fajr.setmCount(0);
        fajr.setmName("Fajr");

        PrayerDataBaseModel duhr = new PrayerDataBaseModel();
        fajr.setmCount(0);
        fajr.setmName("Duhr");

        PrayerDataBaseModel asr = new PrayerDataBaseModel();
        fajr.setmCount(0);
        fajr.setmName("Asr");

        PrayerDataBaseModel maghrib = new PrayerDataBaseModel();
        fajr.setmCount(0);
        fajr.setmName("Maghrib");

        PrayerDataBaseModel isha = new PrayerDataBaseModel();
        fajr.setmCount(0);
        fajr.setmName("Isha");

        dbManager.addPrayer(fajr);
        dbManager.addPrayer(duhr);
        dbManager.addPrayer(asr);
        dbManager.addPrayer(maghrib);
        dbManager.addPrayer(isha);



        BarChart barChart = v.findViewById(R.id.bar_char_prayer);

        final List<BarEntry> entries = new ArrayList<>();

        entries.add(new BarEntry(23,100));
        entries.add(new BarEntry(36,200));
        entries.add(new BarEntry(23,400));
        entries.add(new BarEntry(51,253));
        entries.add(new BarEntry(12,813));

        BarDataSet barSet = new BarDataSet(entries,"Prayer");
        BarData barData = new BarData(barSet);
        barChart.setData(barData);
        barChart.invalidate();

      /*  button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entries.add(new BarEntry(21,9));
            }
        }); */


        return v;
    }
}
