package com.app.masjidmode.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.app.masjidmode.R;
import com.app.masjidmode.activity.MainActivity;
import com.app.masjidmode.bean.Bean;
import com.app.masjidmode.database.PrayersDataSource;
import com.app.masjidmode.publicData.PublicClass;

import java.util.ArrayList;
import java.util.Calendar;

public class Adapter extends ArrayAdapter<Bean> {

    private Context context;
    private ArrayList<Bean> bean;
    private PrayersDataSource prayersDataSource;

    public Adapter(Context c, ArrayList<Bean> b, PrayersDataSource p) {
        super(c, R.layout.list_item, b);
        this.context = c;
        this.bean = b;
        this.prayersDataSource = p;
    }

    private class Holder {
        private TextView name;
        private TextView startTime;
        private TextView endTime;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Holder holder = new Holder();
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, null);
            holder.name = (TextView) v.findViewById(R.id.namaz);
            holder.startTime = (TextView) v.findViewById(R.id.start_time);
            holder.endTime = (TextView) v.findViewById(R.id.end_time);

            v.setTag(holder);
        } else {
            holder = (Holder) v.getTag();
        }

        final Bean item = getItem(position);

        if (item != null) {
            holder.name.setText(item.getName());
            holder.startTime.setText(PublicClass.LongToString(item.getStartTime()));
            holder.endTime.setText(PublicClass.LongToString(item.getEndTime()));
        }
        holder.startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        long l = PublicClass.IntToLong(selectedHour, selectedMinute);
                        if(item != null) item.setStartTime(l);
                        ((TextView) v).setText(PublicClass.LongToString(l));
                        prayersDataSource.updatePrayer(item);
                        MainActivity.dataSetChanged = true;
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        holder.endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        long l = PublicClass.IntToLong(selectedHour, selectedMinute);
                        if(item != null) item.setEndTime(l);
                        ((TextView) v).setText(PublicClass.LongToString(l));
                        prayersDataSource.updatePrayer(item);
                        MainActivity.dataSetChanged = true;
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        return v;
    }

    @Override
    public int getCount() {
        return bean.size();
    }

    public void changeBean(ArrayList<Bean> b){
        this.bean = b;

    }
    @Override
    public Bean getItem(int position) {
        return bean.get(position);
    }

}