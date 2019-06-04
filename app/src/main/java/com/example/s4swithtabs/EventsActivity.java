package com.example.s4swithtabs;

import android.app.Dialog;
import android.content.Intent;
import android.icu.util.DateInterval;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CalendarView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class EventsActivity extends AppCompatActivity {

    public TextView dName;
    public TextView dAdress;
    public TextView dInfo;
    public TextView dCreator;
    public TextView dTime;
    public Dialog dialog;
    public Dialog dialog2;
    public Dialog dialogAfterJoining;
    public boolean flag;
    public  ArrayList<String> listForChecking;
    TextView currentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        currentList = findViewById(R.id.current_list);
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

        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast

                Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            // Load events contents
            displayUserEvents();
        }

        CalendarView calendarView = findViewById(R.id.calendarView);
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
                //Toast.makeText(getApplicationContext(), selectedDate, Toast.LENGTH_LONG).show();
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
        currentList.setText("Все мероприятия, к которым Вы можете присоединиться");
        ListView listOfEvents = findViewById(R.id.EventsList);
        FirebaseListAdapter<EventModel> adapter;
        String user=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        adapter = new FirebaseListAdapter<EventModel>(this, EventModel.class,
                R.layout.event, FirebaseDatabase.getInstance().getReference().child("Events")) { //WARNING//
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



        listOfEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                TextView nameView = itemClicked.findViewById(R.id.eventName);
                String name = nameView.getText().toString();

                TextView adressView = itemClicked.findViewById(R.id.eventAdress);
                String adress = adressView.getText().toString();

                TextView infoView = itemClicked.findViewById(R.id.eventInfo);
                String info = infoView.getText().toString();

                TextView timeView = itemClicked.findViewById(R.id.eventTime);
                String timeString = timeView.getText().toString();
                Long time = Long.getLong(timeString);

                TextView creatorView = itemClicked.findViewById(R.id.eventCreator);
                String creator = creatorView.getText().toString();

                //Toast.makeText(getApplicationContext(), name + adress + info, Toast.LENGTH_LONG).show();

                dialog = new Dialog(EventsActivity.this);
                //dialog.setTitle(name);
                dialog.setContentView(R.layout.diaolog_event);
                dName = dialog.findViewById(R.id.dName);
                dName.setText(name);
                dAdress = dialog.findViewById(R.id.dAdress);
                dAdress.setText(adress);
                dInfo = dialog.findViewById(R.id.dInfo);
                dInfo.setText(info);
                dCreator = dialog.findViewById(R.id.dCreator);
                dCreator.setText(creator);
                dTime = dialog.findViewById(R.id.dTime);
                dTime.setText(timeString);
                dialog.show();


            }
        });

        listOfEvents.setAdapter(adapter);
    }

    private void displayEvents(long time, long nextday) {
        flag = true;
        currentList.setText("Ближайшие мероприятия");
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
    public void ViewUserEvents(View v)
    {
        flag = false;
        displayUserEvents();
    }

    public void ViewAllEvents(View v)
    {
        flag = true;
        displayEvents();
    }

    public void ViewHotEvents(View v){
        flag = true;
        displayEvents(Calendar.getInstance().getTimeInMillis(),
                Calendar.getInstance().getTimeInMillis() + 604800000);
    }

    public void join(View v) throws InterruptedException {
        if (!flag)
        {
            Toast.makeText(this, "Вы уже присоединились к событию", Toast.LENGTH_LONG).show();
        }
        else {
            EventModel event = new EventModel();

            String name = dName.getText().toString();
            event.setEventName(name);

            String adress = dAdress.getText().toString();
            event.setEventAdress(adress);

            String info = dInfo.getText().toString();
            event.setEventInfo(info);

            String timeString = dTime.getText().toString();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy (HH:mm)");
            Date date = null;
            try {
                date = formatter.parse(timeString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long time = date.getTime();
            event.setEventTime(time);

            String creator = dCreator.getText().toString();
            event.setEventCreator(creator);

            String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            FirebaseDatabase.getInstance().getReference().child(user).push().setValue(event);

            FirebaseDatabase.getInstance().getReference().child("Extensions").child(name).child("EventVisitors").push().setValue(user);

            Toast.makeText(this, user + " , Вы стали участником события " + name +"!", Toast.LENGTH_LONG).show();

            displayUserEvents();

            dialogAfterJoining = new Dialog(this);
            dialogAfterJoining.setContentView(R.layout.dialog_after_joining);
            dialogAfterJoining.show();
        }

        dialog.dismiss();
    }

    public void visitors(View v)
    {
        dialog2 = new Dialog(EventsActivity.this);
        dialog2.setTitle("Список участников");
        dialog2.setContentView(R.layout.visitors_dialog);
        ListView listView=dialog2.findViewById(R.id.listview);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Extensions").child(dName.getText().toString()).child("EventVisitors");
        FirebaseListAdapter<String> visitorsAdapter= new FirebaseListAdapter<String>(this,String.class,R.layout.list_item,reference) {
            @Override
            protected void populateView(View v, String model, int position) {
                TextView tv= v.findViewById(R.id.tv);
                tv.setText(model);
            }
        };
        listView.setAdapter(visitorsAdapter);
        dialog2.show();
    }

    private void displayUserEvents() {
        currentList.setText("Мероприятия, в которых вы участвуете");
        ListView listOfEvents = findViewById(R.id.EventsList);
        FirebaseListAdapter<EventModel> adapter;
        String user=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        //Toast.makeText(this,user,Toast.LENGTH_LONG).show();
        adapter = new FirebaseListAdapter<EventModel>(this, EventModel.class,
                R.layout.event, FirebaseDatabase.getInstance().getReference().child(user)) { //WARNING//
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



        listOfEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                TextView nameView = itemClicked.findViewById(R.id.eventName);
                String name = nameView.getText().toString();

                TextView adressView = itemClicked.findViewById(R.id.eventAdress);
                String adress = adressView.getText().toString();

                TextView infoView = itemClicked.findViewById(R.id.eventInfo);
                String info = infoView.getText().toString();

                TextView timeView = itemClicked.findViewById(R.id.eventTime);

                String timeString = timeView.getText().toString();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy (HH:mm)");
                Date date = null;
                try {
                    date = formatter.parse(timeString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long time = date.getTime();


                TextView creatorView = itemClicked.findViewById(R.id.eventCreator);
                String creator = infoView.getText().toString();

                //.makeText(getApplicationContext(), name + adress + info, Toast.LENGTH_LONG).show();

                dialog = new Dialog(EventsActivity.this);
                //dialog.setTitle(name);
                dialog.setContentView(R.layout.diaolog_event);
                dName = dialog.findViewById(R.id.dName);
                dName.setText(name);
                dAdress = dialog.findViewById(R.id.dAdress);
                dAdress.setText(adress);
                dInfo = dialog.findViewById(R.id.dInfo);
                dInfo.setText(info);
                dCreator = dialog.findViewById(R.id.dCreator);
                dCreator.setText(creator);
                dTime = dialog.findViewById(R.id.dTime);
                dTime.setText(timeString);
                dialog.show();

            }
        });

        listOfEvents.setAdapter(adapter);

    }

    public void goToChat(View v)
    {
        String name = dName.getText().toString();
        Intent intent=new Intent(EventsActivity.this,ChatRoomActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        intent.putExtras(bundle);
        startActivity(intent);
        dialogAfterJoining.dismiss();
    }

    public void goToMyEvents(View v)
    {
        displayUserEvents();
        dialogAfterJoining.dismiss();
    }
}
