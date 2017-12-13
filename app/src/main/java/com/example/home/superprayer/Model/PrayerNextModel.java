package com.example.home.superprayer.Model;

import com.example.home.superprayer.Fragment.TimesFragment;

/**
 * Created by Home on 12/12/2017.
 */

public class PrayerNextModel {


    private NextPrayerEnum eNextPrayer;
    private PrayerModel model;
    private long timeUntilNextPrayer;

    public PrayerNextModel(PrayerModel model){
        this.model = model;
    }

    public NextPrayerEnum geteNextPrayer() {
        return eNextPrayer;
    }

    public void seteNextPrayer(NextPrayerEnum eNextPrayer) {
        this.eNextPrayer = eNextPrayer;
    }

    public PrayerModel getModel() {
        return model;
    }


    public long getTimeUntilNextPrayer() {
        return timeUntilNextPrayer;
    }

    public void setTimeUntilNextPrayer(long timeUntilNextPrayer) {
        this.timeUntilNextPrayer = timeUntilNextPrayer;
    }
}
