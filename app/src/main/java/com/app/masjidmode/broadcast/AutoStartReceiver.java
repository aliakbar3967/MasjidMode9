package com.app.masjidmode.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.masjidmode.services.PrayerTimeService;

public class AutoStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, PrayerTimeService.class);
        context.startService(i);
    }
}