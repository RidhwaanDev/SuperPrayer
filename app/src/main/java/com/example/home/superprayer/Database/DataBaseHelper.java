package com.example.home.superprayer.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Home on 12/24/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private static final String DATABASE_NAME ="prayerlog.db";

    public DataBaseHelper(Context c){
        super(c,DATABASE_NAME,null,VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(" create table " +DataBaseSchema.PrayerTable.NAME + "(" +
                                " _id integer primary key autoincrement, " +
                                DataBaseSchema.PrayerTable.Cols.UUID + " , " +
                                DataBaseSchema.PrayerTable.Cols.PRAYER + " , " +
                                DataBaseSchema.PrayerTable.Cols.COUNT +
                                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
