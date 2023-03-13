package com.app.masjidmode.dialog;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.app.masjidmode.R;
import com.app.masjidmode.activity.MainActivity;
import com.app.masjidmode.publicData.PublicClass;

import java.util.Calendar;

public class AddNewItemDialog extends Activity implements View.OnClickListener {

    private EditText NewName;
    private TextView NewStartTime, NewEndTime;
    private Button Done;
    private Context context;
    private long startTime, endTime;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new);
        context = this;
        init();
        actions();
    }

    private void init() {
        NewName = (EditText) findViewById(R.id.new_name);
        NewStartTime = (TextView) findViewById(R.id.new_start_time);
        NewEndTime = (TextView) findViewById(R.id.new_end_time);
        Done = (Button) findViewById(R.id.done);
    }

    private void actions() {
        NewStartTime.setOnClickListener(this);
        NewEndTime.setOnClickListener(this);
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFieldValidate()) {
                    Intent i = new Intent(AddNewItemDialog.this, MainActivity.class);
                    name = NewName.getText().toString();
                    i.putExtra("new_name", name);
                    i.putExtra("new_start_time", startTime);
                    i.putExtra("new_end_time", endTime);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        });
    }

    @Override
    public void onClick(final View v) {

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;

        switch (v.getId()) {
            case R.id.new_start_time:
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startTime = PublicClass.IntToLong(selectedHour, selectedMinute);
                        ((TextView) v).setText(PublicClass.LongToString(startTime));
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                break;
            case R.id.new_end_time:
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endTime = PublicClass.IntToLong(selectedHour, selectedMinute);
                        ((TextView) v).setText(PublicClass.LongToString(endTime));
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                break;
        }
    }

    private boolean isFieldValidate() {
        boolean result;
        result = isEmpty(NewName);
        result = result && isEmpty(NewStartTime);
        result = result && isEmpty(NewEndTime);
        return result;
    }

    private boolean isEmpty(EditText edit) {
        edit.setError(null);
        if (edit.getText().toString().isEmpty()) {
            edit.setError(" " + getString(R.string.is_required));
            return false;
        }
        return true;
    }

    private boolean isEmpty(TextView edit) {
        edit.setError(null);
        if(edit.getText().toString().equals("Set Time")) {
            edit.setError(" " + getString(R.string.is_required));
            return false;
        }
        return true;
    }
}
