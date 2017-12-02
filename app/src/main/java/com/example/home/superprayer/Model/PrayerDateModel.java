package com.example.home.superprayer.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Home on 12/1/2017.
 */

public class PrayerDateModel implements Serializable {

    private long mTime;
    private String mTimeStamp;

    private int day;
    private int month;
    private int year;


    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }

    public String getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(String mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
