package com.example.home.superprayer.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Home on 11/9/2017.
 */

public class PrayerModel {

    private String Fajr;
    private String Duhr;
    private String Asr;
    private String Maghrb;
    private String Isha;



    public String getFajr() {
        return format12Hour(Fajr);
    }

    public void setFajr(String fajr) {
        Fajr = fajr;
    }

    public String getDuhr() {
        return format12Hour(Duhr);
    }

    public void setDuhr(String duhr) {
        Duhr = duhr;
    }

    public String getAsr() {
        return format12Hour(Asr);
    }

    public void setAsr(String asr) {
        Asr = asr;
    }

    public String getMaghrb() {
        return format12Hour(Maghrb);
    }

    public void setMaghrb(String maghrb) {
        Maghrb = maghrb;
    }

    public String getIsha() {
        return format12Hour(Isha);
    }

    public void setIsha(String isha) {
        Isha = isha;
    }

    private String format12Hour(String time){
        try{
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateobj = sdf.parse(time);
            String string_formatted = new SimpleDateFormat("hh:mm aa ").format(dateobj).toString();
            return string_formatted;

        } catch (ParseException e){
            e.printStackTrace();
        }

        return null;
    }
}
