package com.example.datepickerapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.datepickerapp.models.ReminderItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ReminderAdapter extends ArrayAdapter<ReminderItem> {

    public ReminderAdapter(Context context, ArrayList<ReminderItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ReminderItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.reminder_item, parent, false);
        }
        // Lookup view for data population
        TextView textView = convertView.findViewById(R.id.text);
        TextView timeView = convertView.findViewById(R.id.text_time);
        Switch switchView = convertView.findViewById(R.id.switch_alarm);
        // Populate the data into the template view using the data object
        textView.setText(item.getText());
        switchView.setChecked(item.isAlarmOn());

        if (item.isAlarmOn()) {
            Calendar calender = Calendar.getInstance();
            calender.setTimeInMillis(item.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            String time = dateFormat.format(calender.getTime());
            timeView.setText("Alarm set for " + time);
        } else {
            timeView.setText("");
        }

        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setAlarmOn(isChecked);
                // update the alarm if the switch is turned on
                if (isChecked) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(item.getTime());
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    setAlarm(calendar, hour, minute, item.getText());
                    // Update the alarm time text
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
                    String time = dateFormat.format(calendar.getTime());
                    timeView.setText("Alarm set for " + time);
                } else {
                    timeView.setText("");
                }
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    private void setAlarm(Calendar calendar, int hour, int minute, String item) {
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("item", item);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }


}

