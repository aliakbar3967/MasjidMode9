package com.app.masjidmode.database;

import static android.provider.UserDictionary.Words._ID;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.app.masjidmode.bean.Bean;

import java.util.ArrayList;

public class PrayersDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    public static final String CREATE_PRAYERS =
            "CREATE TABLE IF NOT EXISTS " + FeedPrayer.TABLE_NAME + "( " +
                    FeedPrayer._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FeedPrayer.COLUMN_NAME + " TEXT," +
                    FeedPrayer.COLUM_START + " INTEGER," +
                    FeedPrayer.COLUM_END + " INTEGER," +
                    FeedPrayer.ORIGNAL_PROFILE + " INTEGER" +
                    ");";

    private static class FeedPrayer implements BaseColumns {
        private static final String TABLE_NAME = "prayer";
        private static final String COLUMN_NAME = "name";
        private static final String COLUM_START = "start_time";
        private static final String COLUM_END = "end_time";
        private static final String ORIGNAL_PROFILE = "profile";
        private static final String[] ALL_COLUMNS = {
                _ID,
                COLUMN_NAME,
                COLUM_START,
                COLUM_END,
                ORIGNAL_PROFILE
        };
    }
    public static final String DELETE_PRAYERS =
            "DROP TABLE IF EXISTS " + FeedPrayer.TABLE_NAME;

    public PrayersDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    //*/
    public void updatePrayer (Bean bean){
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedPrayer.COLUMN_NAME, bean.getName());
        contentValues.put(FeedPrayer.COLUM_START, bean.getStartTime());
        contentValues.put(FeedPrayer.COLUM_END, bean.getEndTime());
        long id = bean.getId();
        database.update(FeedPrayer.TABLE_NAME, contentValues, "_id="+ id, null);

    }

    public void createPrayer(Bean bean) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedPrayer.COLUMN_NAME, bean.getName());
        contentValues.put(FeedPrayer.COLUM_START, bean.getStartTime());
        contentValues.put(FeedPrayer.COLUM_END, bean.getEndTime());
        database.insert(FeedPrayer.TABLE_NAME, null, contentValues);
    }

    public void deletePrayer(Bean bean) {
        long id = bean.getId();
        database.delete(FeedPrayer.TABLE_NAME, FeedPrayer._ID + " = " + id, null);
    }

    public void deletePrayerById(long id) {
        database.delete(FeedPrayer.TABLE_NAME, FeedPrayer._ID + " = " + id, null);
    }

    public ArrayList<Bean> getAllPrayers() {
        ArrayList<Bean> listPrayers = null;
        Cursor cursor = database.query(FeedPrayer.TABLE_NAME, FeedPrayer.ALL_COLUMNS, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            listPrayers = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                Bean bean = cursorToPrayer(cursor);
                listPrayers.add(bean);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return listPrayers;
    }

    @SuppressLint("Range")
    private Bean cursorToPrayer(Cursor cursor) {
        Bean bean = new Bean();
        bean.setId(cursor.getLong(cursor.getColumnIndex(FeedPrayer._ID)));
        bean.setName(cursor.getString(cursor.getColumnIndex(FeedPrayer.COLUMN_NAME)));
        bean.setStartTime(cursor.getLong(cursor.getColumnIndex(FeedPrayer.COLUM_START)));
        bean.setEndTime(cursor.getLong(cursor.getColumnIndex(FeedPrayer.COLUM_END)));
        return bean;
    }
}
