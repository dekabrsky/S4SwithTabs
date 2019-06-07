package com.example.s4swithtabs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class EventCreation extends AppCompatActivity {

    TextView currentDate;
    TextView currentTime;
    Calendar dateAndTime = Calendar.getInstance();
    EventModel event;
    EditText eventName;
    EditText eventAdress;
    EditText eventInfo;
    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };
    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

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

    public void setEventData(View v) {
        if (eventName.getText().toString().equals("") ||
                eventAdress.getText().toString().equals("") ||
                eventInfo.getText().toString().equals(""))
            Toast.makeText(this, "Все поля обязательны к заполнению", Toast.LENGTH_LONG).show();
        else {
            event = new EventModel();
            //event.setEventName(eventName.getText().toString());
            event.setEventAdress(eventAdress.getText().toString());
            event.setEventInfo(eventInfo.getText().toString());
            event.setEventTime(dateAndTime.getTimeInMillis());
            event.setEventCreator(FirebaseAuth.getInstance()
                    .getCurrentUser()
                    .getDisplayName());

            String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            event.eventVisitors.add(user);

            String eventNameToBase = eventName.getText().toString();
            eventNameToBase = eventNameToBase.replaceAll("[\\[\\](){}]", " ");
            eventNameToBase = eventNameToBase.replaceAll("[.,#,$]", " ");
            event.setEventName(eventNameToBase);

            FirebaseDatabase.getInstance().getReference().child(user).push().setValue(event);
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Events")
                    .push()
                    .setValue(event);

            //новый способ
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Extensions")
                    .child(event.getEventName());
            SendStringsToFirebase("EventName", eventNameToBase);
            SendStringsToFirebase("EventAdress", event.getEventAdress());
            SendStringsToFirebase("EventInfo", event.getEventInfo());
            SendLongToFirebase("EventTime", event.getEventTime());
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Extensions")
                    .child(event.getEventName())
                    .child("EventVisitors")
                    .push()
                    .setValue(new chat_class(user));
            // конец нового спосособа


            FirebaseDatabase.getInstance().getReference()
                    .child("Extensions")
                    .child(user)
                    .push()
                    .setValue(new chat_class(eventNameToBase));

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Chats")
                    .child(event.getEventName())
                    .child("Messages")
                    .push()
                    .setValue(new ChatMessage("Добро пожаловать в чат события " + event.getEventName(),
                            FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getDisplayName()));


            finish();
        }
    }

    public void SendStringsToFirebase(String target, String value) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("Extensions")
                .child(event.getEventName())
                .child(target)
                .push()
                .setValue(value);
    }

    public void SendLongToFirebase(String target, long value) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("Extensions")
                .child(event.getEventName())
                .child(target)
                .push()
                .setValue(value);
    }

    public void SendListToFirebase(String target, ArrayList<String> value) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("Extensions")
                .child(event.getEventName())
                .child(target)
                .push()
                .setValue(value);
    }

    public void creationOut(View v) {
        finish();
    }

    // установка начальных даты и времени
    private void setInitialDateTime() {

        currentDate.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        currentTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
    }
}
