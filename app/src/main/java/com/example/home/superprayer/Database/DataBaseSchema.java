package com.example.home.superprayer.Database;

/**
 * Created by Home on 12/24/2017.
 */

public class DataBaseSchema {

    public static final class PrayerTable {

        public static final String NAME = "PRAYERS";

        public static final class Cols {

                public static final String UUID  = "uuid";
                public static final String PRAYER = "prayer_name";
                public static final String COUNT = "count";

        }
    }
}
