package com.example.home.superprayer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.home.superprayer.Model.PrayerDataBaseModel;

import java.util.ArrayList;

/**
 * Created by Home on 12/24/2017.
 */

public class DatabaseManager {

    private static DatabaseManager sDataBaseManager;

    private SQLiteDatabase mDataBase;


    public static DatabaseManager getInstance(Context c){
        if(sDataBaseManager == null){
            sDataBaseManager = new DatabaseManager(c);
        }

        return sDataBaseManager;
    }

    private DatabaseManager (Context c){
        mDataBase = new DataBaseHelper(c).getWritableDatabase();
    }

    public void updatePrayer(PrayerDataBaseModel model){
        String uuID = model.getmID().toString();
        ContentValues values = getContentValues(model);
        mDataBase.update(DataBaseSchema.PrayerTable.NAME,values,DataBaseSchema.PrayerTable.Cols.UUID + " = ? ", new String[]{uuID});
    }

    public void addPrayer(PrayerDataBaseModel model){
        ContentValues values = getContentValues(model);
        mDataBase.insert(DataBaseSchema.PrayerTable.NAME,null,values);
    }

    public ContentValues getContentValues(PrayerDataBaseModel model){
        ContentValues values = new ContentValues();
        values.put(DataBaseSchema.PrayerTable.Cols.UUID,model.getmID().toString());
        values.put(DataBaseSchema.PrayerTable.Cols.PRAYER,model.getmName());
        values.put(DataBaseSchema.PrayerTable.Cols.COUNT,model.getmCount());

        return values;
    }

    public DataCursor queryForPrayer(String whichColumn, String[] whichRow){
        Cursor cursor = mDataBase.query(DataBaseSchema.PrayerTable.NAME,null,whichColumn,whichRow,null,null,null);
        return  new DataCursor(cursor);
    }

    public ArrayList<PrayerDataBaseModel> getDBPrayers(){

         ArrayList<PrayerDataBaseModel> model = new ArrayList<>();
         DataCursor cursor = queryForPrayer(null,null);
         try{
             cursor.moveToFirst();

             while (!cursor.isAfterLast()){
                 model.add(cursor.getPrayer());
                 cursor.moveToNext();
             }
         } finally {
             cursor.close();
         }
        return model;
    }


}
