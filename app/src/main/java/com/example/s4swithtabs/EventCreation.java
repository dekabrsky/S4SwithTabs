package com.example.s4swithtabs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class EventCreation extends AppCompatActivity {

    TextView currentDate;
    TextView currentTime;
    Calendar dateAndTime=Calendar.getInstance();
    EventModel event;
    EditText eventName;
    EditText eventAdress;
    EditText eventInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);

        currentDate = findViewById(R.id.test);
        currentTime = findViewById(R.id.test2);
        eventName = findViewById(R.id.editText);
        eventAdress = findViewById(R.id.editText2);
        eventInfo = findViewById(R.id.editText3);
        setInitialDateTime();
    }
    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(EventCreation.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(EventCreation.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    public void setEventData(View v){
       event = new EventModel();
       event.setEventName(eventName.getText().toString());
       event.setEventAdress(eventAdress.getText().toString());
       event.setEventInfo(eventInfo.getText().toString());
        event.setEventTime(dateAndTime.getTimeInMillis());
        event.setEventCreator(FirebaseAuth.getInstance()
                .getCurrentUser()
                .getDisplayName());
        FirebaseDatabase.getInstance()
                .getReference()
                .child("Events")
                .push()
                .setValue(event);

        Intent intent = new Intent(EventCreation.this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    public void creationOut(View v){
        Intent intent = new Intent(EventCreation.this, MainActivity.class);
        startActivity(intent);
    }

    // установка начальных даты и времени
    private void setInitialDateTime() {

        currentDate.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        currentTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };
}
