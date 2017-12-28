package com.example.home.superprayer.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.home.superprayer.Database.DatabaseManager;
import com.example.home.superprayer.Graph.GraphUtility;
import com.example.home.superprayer.Model.PrayerDataBaseModel;
import com.example.home.superprayer.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Home on 11/5/2017.
 */

public class LogFragment extends android.support.v4.app.Fragment implements View.OnClickListener {


    private DatabaseManager dbManager;

    private RelativeLayout mFajrCount,mDuhrCount,mAsrCount,mMaghribCount,mIshaCount;



    private ArrayList<PrayerDataBaseModel> mPrayerList;
    private BarChart barChart;
    private BarDataSet barSet;

    private List<BarEntry> mEntries;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_log_layout,container,false);
        dbManager = DatabaseManager.getInstance(getActivity());

        mPrayerList = dbManager.getDBPrayers();


        mFajrCount = v.findViewById(R.id.fajr_missed_view);
        mDuhrCount = v.findViewById(R.id.duhr_missed_view);
        mAsrCount = v.findViewById(R.id.asr_view);
        mMaghribCount = v.findViewById(R.id.maghrib_view);
        mIshaCount = v.findViewById(R.id.isha_view);

        mFajrCount.setOnClickListener(this);
        mDuhrCount.setOnClickListener(this);
        mAsrCount.setOnClickListener(this);
        mMaghribCount.setOnClickListener(this);
        mIshaCount.setOnClickListener(this);

        barChart = v.findViewById(R.id.bar_char_prayer);
        barChart.setNoDataText(getString(R.string.no_data_text));
        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);


        XAxis x_axis = barChart.getXAxis();
        x_axis.setDrawAxisLine(false);
        x_axis.setDrawGridLines(false);
        x_axis.setDrawLabels(false);

        YAxis y_axis = barChart.getAxisRight();
        y_axis.setDrawAxisLine(false);
        y_axis.setDrawGridLines(false);
        y_axis.setDrawLabels(false);

        YAxis y_axis_secondary = barChart.getAxisLeft();
        y_axis_secondary.setDrawAxisLine(false);
        y_axis_secondary.setDrawGridLines(true);

        Legend legend = barChart.getLegend();

        LegendEntry[] legendEntries = new LegendEntry[5];
        for (int i = 0;  i < legendEntries.length; i++){

            switch(i){
                case 0:
                    legendEntries[i] = new LegendEntry();
                    legendEntries[i].label = getString(R.string.string_fajr);
                    legendEntries[i].formColor = R.color.colorFajr;
                    break;
                case 1:
                    legendEntries[i] = new LegendEntry();
                    legendEntries[i].label = getString(R.string.string_duhr);
                    legendEntries[i].formColor = R.color.colorDuhr;
                    break;
                case 2:
                    legendEntries[i] = new LegendEntry();
                    legendEntries[i].label = getString(R.string.string_asr);
                    legendEntries[i].formColor = R.color.colorAsr;
                    break;
                case 3:
                    legendEntries[i] = new LegendEntry();
                    legendEntries[i].label = getString(R.string.string_maghrib);
                    legendEntries[i].formColor = R.color.colorMaghrib;
                    break;
                case 4:
                    legendEntries[i] = new LegendEntry();
                    legendEntries[i].label = getString(R.string.string_isha);
                    legendEntries[i].formColor= R.color.colorIsha;
            }

        }

        legend.setCustom(legendEntries);



        setHasOptionsMenu(true);



        mEntries = new ArrayList<>();

        mEntries.add(new BarEntry(GraphUtility.FAJR_POS,dbManager.fajr.getmCount()));
        mEntries.add(new BarEntry(GraphUtility.DUHR_POS,dbManager.duhr.getmCount()));
        mEntries.add(new BarEntry(GraphUtility.ASR_POS,dbManager.asr.getmCount()));
        mEntries.add(new BarEntry(GraphUtility.MAGHRIB_POS,dbManager.maghrib.getmCount()));
        mEntries.add(new BarEntry(GraphUtility.ISHA_POS,dbManager.isha.getmCount()));

         barSet = new BarDataSet(mEntries,"Prayer Log");
        barSet.setColors(new int[]{R.color.colorFajr,R.color.colorDuhr,R.color.colorAsr,R.color.colorMaghrib,R.color.colorIsha}, getActivity());
        BarData barData = new BarData(barSet);
        barData.setBarWidth(4);
        barChart.setData(barData);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                if(e.getX() == GraphUtility.FAJR_POS){

                }

                if(e.getX() == GraphUtility.DUHR_POS){
                    Log.d("Bar Entry", "   " + "Duhr");
                }

                if(e.getX() == GraphUtility.ASR_POS){
                    Log.d("Bar Entry", "   " + "Asr");
                }

                if(e.getX() == GraphUtility.MAGHRIB_POS){
                    Log.d("Bar Entry", "   " + "maghrib");
                }

                if(e.getX() == GraphUtility.ISHA_POS){
                    Log.d("Bar Entry", "   " + "isha");
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

        return v;
    }

    @Override
    public void onClick(View view) {


        int id = view.getId();
        switch (id) {
            case R.id.fajr_missed_view:
                for (PrayerDataBaseModel model: mPrayerList) {
                    if(model.getmID().equals(dbManager.fajr.getmID())){
                        Log.d("ENTRY TAG", " ENTERED");
                        model.setmCount(model.getmCount() + 1);
                        dbManager.updatePrayer(model);
                    }
                }
                break;
            case R.id.duhr_missed_view:
                for (PrayerDataBaseModel model: mPrayerList) {
                    if(model.getmID().equals(dbManager.duhr.getmID())){
                        Log.d("ENTRY TAG", " ENTERED");
                        model.setmCount(model.getmCount() + 1);
                        dbManager.updatePrayer(model);
                    }
                }
                break;
            case R.id.asr_view:
                for (PrayerDataBaseModel model: mPrayerList) {
                    if(model.getmID().equals(dbManager.asr.getmID())){
                        Log.d("ENTRY TAG", " ENTERED");
                        model.setmCount(model.getmCount() + 1);
                        dbManager.updatePrayer(model);
                    }
                }
            case R.id.maghrib_view:
                for (PrayerDataBaseModel model: mPrayerList) {
                    if(model.getmID().equals(dbManager.maghrib.getmID())){
                        Log.d("ENTRY TAG", " ENTERED");
                        model.setmCount(model.getmCount() + 1);
                        dbManager.updatePrayer(model);
                    }
                }
                break;
            case R.id.isha_view:
                for (PrayerDataBaseModel model: mPrayerList) {
                    if(model.getmID().equals(dbManager.isha.getmID())){
                        Log.d("ENTRY TAG", " ENTERED");
                        model.setmCount(model.getmCount() + 1);
                        dbManager.updatePrayer(model);
                    }
                }
                break;


                default:
                    //do nothing
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.log_menu,menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ArrayList<PrayerDataBaseModel> testList = dbManager.getDBPrayers();

        int id = item.getItemId();
        switch (id){

            case R.id.delete_database:
                Toast.makeText(getActivity(),"YEYSEY", Toast.LENGTH_SHORT).show();

                for (PrayerDataBaseModel model: testList) {
                    Log.d("DATABASE TEST", "   " + model.getmName() + "  " + model.getmCount());
                }


                break;

                default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        dbManager.closeDB();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager.closeDB();
    }
}
