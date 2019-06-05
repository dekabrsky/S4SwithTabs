package com.example.s4swithtabs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    String user;
    String name;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenges);

        listOfChats = findViewById(R.id.ChatsList);
        user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
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

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Extensions").child(user);
        FirebaseListAdapter<String> visitorsAdapter = new FirebaseListAdapter<String>(this, String.class, R.layout.chatlist_item, reference) {
            @Override
            protected void populateView(View v, String model, int position) {
                TextView ctv = v.findViewById(R.id.ctv);
                ctv.setText(model);
            }
        };
        listOfChats.setAdapter(visitorsAdapter);

        listOfChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                nameView = itemClicked.findViewById(R.id.ctv);
                ChatRoomActivity chat;
                name = nameView.getText().toString();
                Intent intent = new Intent(MessengesActivity.this, ChatRoomActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        listOfChats.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MessengesActivity.this, "lll", Toast.LENGTH_LONG).show();
                dialog = new Dialog(MessengesActivity.this);
                dialog.setContentView(R.layout.dialog_chat);
                dialog.show();
                return true;
            }
        });
    }

    public void DeleteChat(View v) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("Chats")
                .child(name)
                .child("Messages")
                .push()
                .setValue(new ChatMessage(user + " покинул чат.",
                        "Администрация",
                        "no"
                ));
        FirebaseDatabase.getInstance().getReference().child("Extensions").child(user).removeValue();
        dialog.dismiss();
        Toast.makeText(MessengesActivity.this, "Вы покинули чат. Мы уведомили ваших собеседников об этом.", Toast.LENGTH_LONG).show();
    }

    private void NoDeleteChat(View v) {
        dialog.dismiss();
    }
}
