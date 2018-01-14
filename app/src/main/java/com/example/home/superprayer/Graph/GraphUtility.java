package com.example.home.superprayer.Graph;

import com.example.home.superprayer.Model.PrayerDataBaseModel;

/**
 * Created by Home on 12/24/2017.
 */
public class GraphUtility {


    private static final int OFF_SET = 10;

    public static final int FAJR_POS = 20 + OFF_SET;
    public static final int DUHR_POS = FAJR_POS + OFF_SET;
    public static final int ASR_POS = DUHR_POS + OFF_SET;
    public static final int MAGHRIB_POS = ASR_POS +OFF_SET;
    public static final int ISHA_POS = MAGHRIB_POS + OFF_SET;

    public static final float GRANULARITY = 5f;
    public static final float Y_AXIS_TEXT_SIZE = 12f;
    public static final float MAX = 25f;
    public static final float MIN = 0f;
    public static final int ANIM_DURATION = 750;

}
