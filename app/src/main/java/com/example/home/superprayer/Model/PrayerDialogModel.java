package com.example.home.superprayer.Model;

import com.google.android.gms.location.places.Place;

import java.io.Serializable;

/**
 * Created by Home on 11/26/2017.
 */

public class PrayerDialogModel implements Serializable {

    private Place place;

    public PrayerDialogModel(Place place){

        this.place = place;
    }


    public Place getPlace() {
        return place;
    }

}
