package com.example.home.superprayer.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.home.superprayer.Model.PrayerDataBaseModel;

import java.util.UUID;

/**
 * Created by Home on 12/24/2017.
 */

public class DataCursor extends CursorWrapper {

    public DataCursor(Cursor cursor) {
        super(cursor);
    }

    public PrayerDataBaseModel getPrayer(){
        String id = getString(getColumnIndex(DataBaseSchema.PrayerTable.Cols.UUID));
        String prayer = getString(getColumnIndex(DataBaseSchema.PrayerTable.Cols.PRAYER));
        String count = getString(getColumnIndex(DataBaseSchema.PrayerTable.Cols.COUNT));

        PrayerDataBaseModel model = new PrayerDataBaseModel(UUID.fromString(id));
        model.setmName(prayer);
        model.setmCount(Integer.parseInt(count));
        return model;




    }
}
