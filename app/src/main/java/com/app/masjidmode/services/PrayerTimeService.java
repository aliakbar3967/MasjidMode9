package com.app.masjidmode.services;

import static android.app.Service.START_STICKY;
import static com.app.masjidmode.publicData.PublicClass.IntToLong;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;

import com.app.masjidmode.bean.Bean;
import com.app.masjidmode.broadcast.MatchTimeReceiver;
import com.app.masjidmode.database.PrayersDataSource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class PrayerTimeService extends Service {

    public static int previousProfile = AudioManager.RINGER_MODE_SILENT;

    private MatchTimeReceiver time = new MatchTimeReceiver();
    private PrayersDataSource prayers;
    private ArrayList<Bean> beanArray = null;
    private Bean currentBean = null;

    @Override
    public void onCreate() {
        prayers = new PrayersDataSource(this);
        prayers.open();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        previousProfile = AudioManager.RINGER_MODE_SILENT;
        beanArray = prayers.getAllPrayers();
        Collections.sort(beanArray);
        currentBean = null;
        MoveToNextAndStart();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void MoveToNextAndStart() {
        Calendar calendar = Calendar.getInstance();
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        long currentTime = IntToLong(h, m);
        int count = 0;

        for(int i = 0; i < beanArray.size(); i++){
            if(currentTime >= beanArray.get(i).getStartTime()){
                count++;
                if(currentBean == null && beanArray.get(i).getStartTime() != -1){
                    currentBean = beanArray.get(i);
                }
                if(currentBean != null && count == beanArray.size())
                {
                    setMobileSilent(currentBean, day+1);
                }
            } else {
                currentBean = beanArray.get(i);
                setMobileSilent(currentBean, day);
                break;
            }
        }
    }

    private void setMobileSilent(Bean bean, int day){
        String name = bean.getName();
        Calendar sc = setOnCalender(bean.getStartTime(), day);
        time.handleTime(this, sc, name, 0);
        Log.i("AlarmSet","Start Time " + sc.get(Calendar.HOUR_OF_DAY)+ " : " + sc.get(Calendar.MINUTE) + " :: " + sc.get(Calendar.DAY_OF_YEAR));

        if ( bean.getStartTime() > bean.getEndTime() ) {
            day += 1;
        }

        name = "";
        Calendar ec = setOnCalender(bean.getEndTime(), day);
        time.handleTime(this, ec, name, 1);
        Log.i("AlarmSet","End Time " + ec.get(Calendar.HOUR_OF_DAY)+ " : " + ec.get(Calendar.MINUTE) + " :: " + ec.get(Calendar.DAY_OF_YEAR));
    }

    private Calendar setOnCalender(long time, int day){
        int minute = (int) ((time / (1000 * 60)) % 60);
        int hour = (int) ((time / (1000 * 60 * 60)) % 24);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_YEAR, day);
        return calendar;
    }
}