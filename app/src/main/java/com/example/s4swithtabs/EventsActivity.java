package com.example.s4swithtabs;

import android.content.Intent;
import android.icu.util.DateInterval;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CalendarView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class EventsActivity extends AppCompatActivity {

    TextView testIteration = (TextView)findViewById(R.id.test_iteration);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        FloatingActionButton fab;
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventsActivity.this, EventCreation.class);
                startActivity(intent);
            }
        });


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    -1
            );
            testIteration.setText("1");
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            if (testIteration.getText().toString().equals("1"))
                Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            // Load events contents
            displayEvents();
        }

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        final Calendar dateAndTime=Calendar.getInstance();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                int mYear = year;
                int mMonth = month;
                int mDay = dayOfMonth;
                long longYear=year;
                TimeZone timeZone = dateAndTime.getTimeZone();
                int offset = timeZone.getRawOffset();
                String selectedDate = new StringBuilder().append(mMonth + 1)
                        .append("-").append(mDay).append("-").append(mYear)
                        .append(" ").toString();
                Toast.makeText(getApplicationContext(), selectedDate, Toast.LENGTH_LONG).show();
                dateAndTime.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                dateAndTime.set(Calendar.YEAR,year);
                dateAndTime.set(Calendar.MONTH,month);
                dateAndTime.set(Calendar.HOUR_OF_DAY,0);
                dateAndTime.set(Calendar.MINUTE,0);
                dateAndTime.set(Calendar.SECOND,0);
                long time=dateAndTime.getTimeInMillis()
                        ;
                dateAndTime.setTimeInMillis(0);
                dateAndTime.set(Calendar.DAY_OF_MONTH,dayOfMonth+1);
                dateAndTime.set(Calendar.YEAR,year);
                dateAndTime.set(Calendar.MONTH,month);
                dateAndTime.set(Calendar.HOUR_OF_DAY,0);
                dateAndTime.set(Calendar.MINUTE,0);
                dateAndTime.set(Calendar.SECOND,0);
                long nextDay=dateAndTime.getTimeInMillis();
                displayEvents(time,nextDay);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == -1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                displayEvents();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }

    }
    private void displayEvents() {
        ListView listOfEvents = findViewById(R.id.EventsList);

        FirebaseListAdapter<EventModel> adapter;
        adapter = new FirebaseListAdapter<EventModel>(this, EventModel.class,
                R.layout.event, FirebaseDatabase.getInstance().getReference().child("Events")) {
            @Override
            protected void populateView(View v, EventModel model, int position) {

                if (model.getEventName() != null) {
                    TextView eventName = v.findViewById(R.id.eventName);
                    TextView eventCreator = v.findViewById(R.id.eventCreator);
                    TextView eventAdress = v.findViewById(R.id.eventAdress);
                    TextView eventTime = v.findViewById(R.id.eventTime);
                    TextView eventInfo = v.findViewById(R.id.eventInfo);


                    eventName.setText(model.getEventName());
                    eventCreator.setText(model.getEventCreator());
                    eventAdress.setText(model.getEventAdress());
                    eventTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)",
                            model.getEventTime()));
                    eventInfo.setText(model.getEventInfo());
                }
            }
        };

        listOfEvents.setAdapter(adapter);
    }

    private void displayEvents(long time, long nextday) {
        ListView listOfEvents = findViewById(R.id.EventsList);

        FirebaseListAdapter<EventModel> adapter;
        adapter = new FirebaseListAdapter<EventModel>(this, EventModel.class,
                R.layout.event, FirebaseDatabase.getInstance().getReference().child("Events").orderByChild("eventTime").startAt(time).endAt(nextday)) {
            @Override
            protected void populateView(View v, EventModel model, int position) {

                if (model.getEventName() != null) {
                    TextView eventName = v.findViewById(R.id.eventName);
                    TextView eventCreator = v.findViewById(R.id.eventCreator);
                    TextView eventAdress = v.findViewById(R.id.eventAdress);
                    TextView eventTime = v.findViewById(R.id.eventTime);
                    TextView eventInfo = v.findViewById(R.id.eventInfo);


                    eventName.setText(model.getEventName());
                    eventCreator.setText(model.getEventCreator());
                    eventAdress.setText(model.getEventAdress());
                    eventTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)",
                            model.getEventTime()));
                    eventInfo.setText(model.getEventInfo());
                }
            }
        };

        listOfEvents.setAdapter(adapter);

    }

    public void ViewAllEvents(View v){
        displayEvents();
    }

    public void ViewHotEvents(View v){
        displayEvents(Calendar.getInstance().getTimeInMillis(),
                Calendar.getInstance().getTimeInMillis() + 604800000);
    }
}
