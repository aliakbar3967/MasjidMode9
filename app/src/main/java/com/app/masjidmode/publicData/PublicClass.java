package com.app.masjidmode.publicData;

import java.text.DecimalFormat;

public class PublicClass {

    public static String LongToString(long l){
        String string ;
        Boolean PmFlag ;
        DecimalFormat hr= new DecimalFormat("00");
        DecimalFormat min = new DecimalFormat("00");

        if (l == -1) return "Start Time" ;
        if (l == -2) return "End Time";

        long minute = (l / (1000 * 60)) % 60;
        long hour = (l / (1000 * 60 * 60)) % 24;

        if(hour == 0){
            PmFlag = false;
            hour = 12;
        } else if (hour == 12){
            PmFlag = true;
        } else if (hour > 12) {
            hour = hour -12;
            PmFlag = true;
        } else {
            PmFlag = false;
        }

        string = ((hr.format(hour)) + ":" + (min.format(minute)) + ((PmFlag ? " PM" : " AM")));
        return string;
    }

    public static long IntToLong(int hour, int min){
        long l;
        long minMilliSec = min * 60 * 1000;
        long hrMilliSec = hour * 60 * 60 * 1000;
        l = minMilliSec + hrMilliSec;
        return l;
    }
}