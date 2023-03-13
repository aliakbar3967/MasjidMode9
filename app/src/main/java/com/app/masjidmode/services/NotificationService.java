package com.app.masjidmode.services;

import static android.app.Service.START_NOT_STICKY;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import com.app.masjidmode.R;
import com.app.masjidmode.activity.MainActivity;

public class NotificationService extends Service {

    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 3333;

    private BroadcastReceiver profileChangeReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            hideNotification();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String name = intent.getStringExtra("activeSlot");
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wl.acquire();
        if(name.equals("")){
            hideNotification();
        } else {
            showNotification(name);
        }
        IntentFilter filter= new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
        registerReceiver(profileChangeReceiver, filter);
        wl.release();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("NotificationPermission")
    public void showNotification(String name) {

        mNotificationManager = (NotificationManager) this.
                getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
//
        /* Use Bellow commented code to enable notification using support library
         * Add google play services in dependency and this work for all Api levels upto API 4 (version 4)
         * */
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setContentTitle(getString(R.string.notification_title))
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(name + " time it is!"))
//                .setContentText(name + " time it is!");
//        mBuilder.setContentIntent(contentIntent);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            Notification noti = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(name + " time it is!")
                    .setSmallIcon(R.drawable.small_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                    .setContentIntent(contentIntent)
                    .getNotification();
            mNotificationManager.notify(NOTIFICATION_ID, noti);

        } else {
            Notification noti = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(name + " time it is!")
                    .setSmallIcon(R.drawable.small_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                    .setContentIntent(contentIntent)
                    .build();
            mNotificationManager.notify(NOTIFICATION_ID, noti);

        }
//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(profileChangeReceiver);
    }

    public void hideNotification() {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
        callPrayerTimeServiceAgain(this);
        stopSelf();
    }

    private void callPrayerTimeServiceAgain(Context context) {
        Intent startServiceAgain = new Intent(context, PrayerTimeService.class);
        context.startService(startServiceAgain);
    }

}