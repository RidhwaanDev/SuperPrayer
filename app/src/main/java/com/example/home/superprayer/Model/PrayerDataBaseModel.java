package com.example.home.superprayer.Model;

import java.util.UUID;

/**
 * Created by Home on 12/24/2017.
 */

public class PrayerDataBaseModel {

    public String mName;
    public int mCount;
    public UUID mID;

    public PrayerDataBaseModel(){
        this.mID = UUID.randomUUID();
    }

    public PrayerDataBaseModel(UUID mID){
        this.mID = mID;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getmCount() {
        return mCount;
    }

    public void setmCount(int mCount) {
        this.mCount = mCount;
    }

    public UUID getmID() {
        return mID;
    }
}
