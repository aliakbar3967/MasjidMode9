package com.app.masjidmode.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.masjidmode.R;
import com.app.masjidmode.bean.Bean;
import com.app.masjidmode.database.PrayersDataSource;
import com.app.masjidmode.dialog.AddNewItemDialog;
import com.app.masjidmode.enums.Prayer;
import com.app.masjidmode.services.NotificationService;
import com.app.masjidmode.services.PrayerTimeService;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private boolean isNotificationServiceActive;
    public static boolean dataSetChanged = false;
    private Button addMore;
    private ListView list;
    private ArrayList<Bean> beanArray = null;
    private Context context;
    private PrayersDataSource prayersDataSource;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MobileAds.initialize(this, getResources().getString(R.string.app_id_for_admob));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setContentView(R.layout.activity_main);
            LinearLayout l = (LinearLayout) findViewById(R.id.under_21_layout);
            l.setVisibility(View.GONE);
            if (getActionBar() != null)
                getActionBar().setTitle("Prayers Timing");
        } else {
            setContentView(R.layout.activity_main);
        }
        dbHandle();
        init();
        actions();
        isNotificationServiceActive = isMyServiceRunning(NotificationService.class);
    }

    private void dbHandle() {
        prayersDataSource = new PrayersDataSource(this);
        prayersDataSource.open();
        beanArray = prayersDataSource.getAllPrayers();
    }

    private void init() {
        context = this;
        addMore = (Button) findViewById(R.id.add_more);
//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
        // This is for testing
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice("E714F9554B9F6AB6E940739C8D0704B8").build();
//        mAdView.loadAd(adRequest);
        list = (ListView) findViewById(R.id.list);
        if(beanArray == null) {
            beanArray = new ArrayList<>();
            for (Prayer p : Prayer.values()) {
                Bean b = new Bean(p, -1, -2);
                b.setName(b.getPrayer(context));
                prayersDataSource.createPrayer(b);
            }
            beanArray = prayersDataSource.getAllPrayers();
        }
    }

    private void actions() {
//        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, beanArray);
//        Adapter adapter = new Adapter(context, beanArray, prayersDataSource);
//        list.setAdapter(adapter);
        addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddNewItemDialog.class);
                startActivityForResult(i, 0);
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(MainActivity.this, AddNewItemDialog.class);
        startActivityForResult(i, 0);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        prayersDataSource.close();
        super.onDestroy();
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(context.getResources().getString(R.string.dialog_msg_delete));
        dialog.setPositiveButton(context.getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prayersDataSource.deletePrayer(beanArray.get(position));
                        beanArray.remove(position);
//                        adapter.changeBean(beanArray);
//                        adapter.notifyDataSetChanged();

                    }
                });
        dialog.setNegativeButton(context.getResources().getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            String name = data.getExtras().getString("new_name");
            long s = data.getExtras().getLong("new_start_time");
            long e = data.getExtras().getLong("new_end_time");
            Bean b = new Bean(name, s, e);
            prayersDataSource.createPrayer(b);
            dataSetChanged = true;
            beanArray = prayersDataSource.getAllPrayers();
//            adapter.changeBean(beanArray);
//            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {

        if (dataSetChanged || !isNotificationServiceActive) {
            Intent startServiceIntent = new Intent(MainActivity.this, PrayerTimeService.class);
            context.startService(startServiceIntent);
        }
        dataSetChanged = false;
        super.onStop();
    }
}