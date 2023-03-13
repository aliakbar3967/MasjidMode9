package com.app.masjidmode.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "Masjidmode.db";
    private final static int DATABASE_VERSION = 1;

    public MySQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(PrayersDataSource.CREATE_PRAYERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(PrayersDataSource.DELETE_PRAYERS);
        onCreate(database);
    }
}
