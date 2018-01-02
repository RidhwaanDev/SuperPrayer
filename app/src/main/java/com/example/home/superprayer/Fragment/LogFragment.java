package com.example.home.superprayer.Fragment;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.home.superprayer.Database.DatabaseManager;
import com.example.home.superprayer.Dialog.DataBaseClearDialog;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Home on 11/5/2017.
 */

public class LogFragment extends android.support.v4.app.Fragment implements View.OnClickListener {


    private DatabaseManager dbManager;

    private RelativeLayout mFajrCount,mDuhrCount,mAsrCount,mMaghribCount,mIshaCount;
    private TextView mFajrMissedText,mDuhrMissedText,mAsrMissedText,mMaghribMissedText,mIshaMissedText;
    private static final String CLEAR_DATA_KEY = "clear_data_base_key_unique_100";



    private ArrayList<PrayerDataBaseModel> mPrayerList;
    private BarChart mBarChart;
    private BarDataSet barSet;

    private List<BarEntry> mEntries;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_log_layout,container,false);
        dbManager = DatabaseManager.getInstance(getActivity());

       // showTip(container);

        mPrayerList = dbManager.getDBPrayers();
        mBarChart = v.findViewById(R.id.bar_char_prayer);
        mBarChart.animateY(GraphUtility.ANIM_DURATION);
        mFajrMissedText = v.findViewById(R.id.tv_prayer_missed_count);
        mDuhrMissedText = v.findViewById(R.id.tv_prayer_missed_count2);
        mAsrMissedText = v.findViewById(R.id.tv_prayer_missed_count3);
        mMaghribMissedText = v.findViewById(R.id.tv_prayer_missed_count4);
        mIshaMissedText = v.findViewById(R.id.tv_prayer_missed_count5);


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

        mBarChart.setNoDataText(getString(R.string.no_data_text));
        mBarChart.setDrawGridBackground(false);
        mBarChart.setDrawBorders(false);
        mBarChart.setDragEnabled(false);
        mBarChart.setScaleEnabled(false);
        mBarChart.setPinchZoom(false);
        mBarChart.setDoubleTapToZoomEnabled(false);


        XAxis x_axis = mBarChart.getXAxis();
        x_axis.setDrawAxisLine(false);
        x_axis.setDrawGridLines(false);
        x_axis.setDrawLabels(false);

        YAxis y_axis = mBarChart.getAxisRight();
        y_axis.setDrawAxisLine(false);
        y_axis.setDrawGridLines(false);
        y_axis.setDrawLabels(false);
        y_axis.setAxisMinimum(GraphUtility.MIN);

        YAxis y_axis_secondary = mBarChart.getAxisLeft();
        y_axis_secondary.setDrawAxisLine(false);
        y_axis_secondary.setDrawGridLines(true);
        y_axis_secondary.setTextSize(GraphUtility.Y_AXIS_TEXT_SIZE);
        y_axis_secondary.setAxisMinimum(GraphUtility.MIN);
        y_axis_secondary.setAxisMaximum(GraphUtility.MAX);
        y_axis_secondary.setGranularity(GraphUtility.GRANULARITY);
        Legend legend = mBarChart.getLegend();


        LegendEntry[] legendEntries = new LegendEntry[5];
        for (int i = 0;  i <= legendEntries.length ; i++){

            switch(i){
                case 0:
                    legendEntries[0] = new LegendEntry();
                    legendEntries[0].label = getString(R.string.string_fajr);
                    legendEntries[0].formColor =  R.color.colorFajr;

                    break;
                case 1:
                    legendEntries[1] = new LegendEntry();
                    legendEntries[1].label = getString(R.string.string_duhr);
                    legendEntries[1].formColor = R.color.colorDuhr;
                    break;
                case 2:
                    legendEntries[2] = new LegendEntry();
                    legendEntries[2].label = getString(R.string.string_asr);
                    legendEntries[2].formColor = R.color.colorAsr;
                    break;
                case 3:
                    legendEntries[3] = new LegendEntry();
                    legendEntries[3].label = getString(R.string.string_maghrib);
                    legendEntries[3].formColor = R.color.colorDuhr;
                    break;
                case 4:
                    legendEntries[4] = new LegendEntry();
                    legendEntries[4].label = getString(R.string.string_isha);
                    legendEntries[4].formColor= R.color.colorIsha;
                    break;
            }

        }


       // int colors[] = new int[]{(R.color.colorFajr),R.color.colorDuhr,R.color.colorAsr,R.color.colorMaghrib,R.color.colorIsha };
      //  String labels[] = new String[]{getString(R.string.string_duhr),getString(R.string.string_asr),getString(R.string.string_maghrib),getString(R.string.string_isha)};
       // legend.setExtra(colors,labels);

        setHasOptionsMenu(true);

        mEntries = new ArrayList<>();

        mEntries.add(new BarEntry(GraphUtility.FAJR_POS,dbManager.fajr.getmCount()));
        mEntries.add(new BarEntry(GraphUtility.DUHR_POS,dbManager.duhr.getmCount()));
        mEntries.add(new BarEntry(GraphUtility.ASR_POS,dbManager.asr.getmCount()));
        mEntries.add(new BarEntry(GraphUtility.MAGHRIB_POS,dbManager.maghrib.getmCount()));
        mEntries.add(new BarEntry(GraphUtility.ISHA_POS,dbManager.isha.getmCount()));

        barSet = new BarDataSet(mEntries,null);
        barSet.setColors(new int[]{R.color.colorFajr,R.color.colorDuhr,R.color.colorAsr,R.color.colorMaghrib,R.color.colorIsha}, getActivity());
        BarData barData = new BarData(barSet);
        barData.setBarWidth(4);
        mBarChart.setData(barData);

        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
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

    private void update(){
        ArrayList<PrayerDataBaseModel> list = dbManager.getDBPrayers();
        mEntries.clear();
        for(PrayerDataBaseModel model: list){
            if(model.getmID().equals(dbManager.fajr.getmID())){
                mEntries.add(new BarEntry(GraphUtility.FAJR_POS,model.getmCount()));
                mFajrMissedText.setText(model.getmCount() + " " +"missed");

            }
            if(model.getmID().equals(dbManager.duhr.getmID())){
                mEntries.add(new BarEntry(GraphUtility.DUHR_POS,model.getmCount()));
                mDuhrMissedText.setText(model.getmCount() + " " +"missed");

            }

            if(model.getmID().equals(dbManager.asr.getmID())){
                mEntries.add(new BarEntry(GraphUtility.ASR_POS,model.getmCount()));
                mAsrMissedText.setText(model.getmCount() + " " +"missed");


            }

            if(model.getmID().equals(dbManager.maghrib.getmID())){
                mEntries.add(new BarEntry(GraphUtility.MAGHRIB_POS,model.getmCount()));
                mMaghribMissedText.setText(model.getmCount() + " " +"missed");


            }
            if(model.getmID().equals(dbManager.isha.getmID())){
                mEntries.add(new BarEntry(GraphUtility.ISHA_POS,model.getmCount()));
                mIshaMissedText.setText(model.getmCount() + " " +"missed");


            }
            mBarChart.notifyDataSetChanged();
            mBarChart.invalidate();
        }
    }

    @Override
    public void onClick(View view) {
            ArrayList<PrayerDataBaseModel> list = dbManager.getDBPrayers();
        int id = view.getId();
        switch (id) {
            case R.id.fajr_missed_view:
                for (PrayerDataBaseModel model: list) {
                    if(model.getmID().equals(dbManager.fajr.getmID())){
                        Log.d("ENTRY TAG", " ENTERED");
                        model.setmCount(model.getmCount() + 1);
                        dbManager.updatePrayer(model);
                        update();
                    }
                }
                break;
            case R.id.duhr_missed_view:
                for (PrayerDataBaseModel model: list) {
                    if(model.getmID().equals(dbManager.duhr.getmID())){
                        Log.d("ENTRY TAG", " ENTERED");
                        model.setmCount(model.getmCount() + 1);
                        dbManager.updatePrayer(model);
                        update();
                    }
                }
                break;
            case R.id.asr_view:
                for (PrayerDataBaseModel model: list) {
                    if(model.getmID().equals(dbManager.asr.getmID())){
                        Log.d("ENTRY TAG", " ENTERED");
                        model.setmCount(model.getmCount() + 1);
                        dbManager.updatePrayer(model);
                        update();
                    }
                }
                break;
            case R.id.maghrib_view:
                for (PrayerDataBaseModel model: list) {
                    if(model.getmID().equals(dbManager.maghrib.getmID())){
                        Log.d("ENTRY TAG", " ENTERED");
                        model.setmCount(model.getmCount() + 1);
                        dbManager.updatePrayer(model);
                        update();
                    }
                }
                break;
            case R.id.isha_view:
                for (PrayerDataBaseModel model: list) {
                    if(model.getmID().equals(dbManager.isha.getmID())){
                        Log.d("ENTRY TAG", " ENTERED");
                        model.setmCount(model.getmCount() + 1);
                        dbManager.updatePrayer(model);
                        update();
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

        final ArrayList<PrayerDataBaseModel> testList = dbManager.getDBPrayers();

        int id = item.getItemId();
        switch (id){

            case R.id.delete_database:

                //Yes button clicked
                android.support.v4.app.FragmentManager fg = getFragmentManager();
                DataBaseClearDialog dataBaseClearDialog = new DataBaseClearDialog();
             //   dataBaseClearDialog.show(fg,CLEAR_DATA_KEY);

                for (PrayerDataBaseModel model : testList) {
                    model.setmCount(0);
                    dbManager.updatePrayer(model);
                }

                 update();

                break;

                default:
        }

        return super.onOptionsItemSelected(item);
    }

    private void showTip(View v){

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean shouldShow = prefs.getBoolean(getString(R.string.prefs_never_show_again),true);
        if(shouldShow){
            Snackbar myTipBar = Snackbar.make(v,R.string.log_tip,Snackbar.LENGTH_INDEFINITE);
            myTipBar.setAction(R.string.never_show_again, new MyShowAgainListener());
            myTipBar.show();
        }
    }

    private class MyShowAgainListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(getString(R.string.prefs_never_show_again),false);
        }
    }

}
