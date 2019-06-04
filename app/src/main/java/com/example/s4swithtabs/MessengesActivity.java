package com.example.s4swithtabs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessengesActivity extends AppCompatActivity {
    ListView listOfChats;
    TextView nameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenges);

        listOfChats = findViewById(R.id.ChatsList);
        String user= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        /*FirebaseListAdapter<EventModel> adapter;

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
        listOfChats.setAdapter(adapter);*/

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child(user);
        FirebaseListAdapter<EventModel> visitorsAdapter= new FirebaseListAdapter<EventModel>(this, EventModel.class, R.layout.chatlist_item, reference) {
            @Override
            protected void populateView(View v, EventModel model, int position) {
                TextView ctv= v.findViewById(R.id.ctv);
                ctv.setText(model.getEventName());
            }
        };
        listOfChats.setAdapter(visitorsAdapter);

        listOfChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                nameView = itemClicked.findViewById(R.id.ctv);
                ChatRoomActivity chat;
                String name = nameView.getText().toString();
                Intent intent=new Intent(MessengesActivity.this,ChatRoomActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

    }
}
