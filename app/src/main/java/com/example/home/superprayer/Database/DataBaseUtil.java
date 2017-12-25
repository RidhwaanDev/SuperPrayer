package com.example.home.superprayer.Database;

import com.example.home.superprayer.Model.PrayerDataBaseModel;

/**
 * Created by Home on 12/24/2017.
 */

public class DataBaseUtil {


    private static final int OFF_SET = 10;

    public static final int FAJR_POS = 20 + OFF_SET;
    public static final int DUHR_POS = FAJR_POS + OFF_SET;
    public static final int ASR_POS = DUHR_POS + OFF_SET;
    public static final int MAGHRIB_POS = ASR_POS +OFF_SET;
    public static final int ISHA_POS = MAGHRIB_POS + OFF_SET;




}
