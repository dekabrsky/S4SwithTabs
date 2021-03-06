package com.example.s4swithtabs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Extensions").child(user);
        FirebaseListAdapter<chat_class> visitorsAdapter = new FirebaseListAdapter<chat_class>(this, chat_class.class, R.layout.chatlist_item, reference) {
            @Override
            protected void populateView(View v, chat_class model, int position) {
                TextView ctv = v.findViewById(R.id.ctv);
                ctv.setText(model.getChatName());
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
                nameView = view.findViewById(R.id.ctv);
                //Toast.makeText(MessengesActivity.this, "lll", Toast.LENGTH_LONG).show();
                dialog = new Dialog(MessengesActivity.this);
                name = nameView.getText().toString();
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Extensions").child(user).orderByChild("chatName").equalTo(name);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "onCancelled", databaseError.toException());
            }
        });

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
        Query applesQuery2 = ref2.child("Extensions").child(name).child("EventVisitors").orderByChild("chatName").equalTo(user);

        applesQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "onCancelled", databaseError.toException());
            }
        });

        dialog.dismiss();
        Toast.makeText(MessengesActivity.this, "Вы покинули чат. Мы уведомили ваших собеседников об этом.", Toast.LENGTH_LONG).show();
    }

    public void NoDeleteChat(View v) {
        dialog.dismiss();
    }
}
