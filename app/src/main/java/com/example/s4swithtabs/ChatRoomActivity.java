package com.example.s4swithtabs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;


public class ChatRoomActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 71;
    public String name;
    public String pathImg = "images/shedevr.jpg";
    public Uri imgUri;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        TextView chatName = findViewById(R.id.chatName);
        chatName.setText(name);
        FloatingActionButton fab;
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = findViewById(R.id.input);


                if (!input.getText().toString().isEmpty())
                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Chats")
                            .child(name)
                            .child("Messages")
                            .push()
                            .setValue(new ChatMessage(input.getText().toString(),
                                    FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getDisplayName(),
                                    pathImg)
                            );
                }

                // Clear the input
                input.setText("");
                pathImg = "images/shedevr.jpg";
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
            // Load chat room contents
            displayChatMessages();
        }

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
                displayChatMessages();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();
                // Close the app
                finish();
            }

        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadImage();
        }

    }

    private void displayChatMessages() {
        ListView listOfMessages = findViewById(R.id.list_of_messages);

        FirebaseListAdapter<ChatMessage> adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(name)
                .child("Messages").
                        orderByChild("messageTime")) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                if (model.getMessageText() != null) {
                    TextView messageText = v.findViewById(R.id.message_text);
                    TextView messageUser = v.findViewById(R.id.message_user);
                    TextView messageTime = v.findViewById(R.id.message_time);
                    TextView messageImage = v.findViewById(R.id.message_image);
                    ImageView imageView = v.findViewById(R.id.imageView);
                    // Set their text

                    messageText.setText(model.getMessageText());
                    //messageImage.setText(model.getPathToImage());
                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getMessageTime()));

                    if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals(model.getMessageUser())) {
                        messageUser.setText("Я");
                        messageUser.setTextColor(Color.CYAN);
                    } else {
                        messageUser.setText(model.getMessageUser());
                        messageUser.setTextColor(Color.GRAY);
                    }
                }
            }
        };

        listOfMessages.setAdapter(adapter);
    }

    public void attachImages(View view) {
        chooseImage();

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatRoomActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatRoomActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
            pathImg = ref.getPath();
        }

    }

    public void visitors2(View v) {
        Dialog dialog2 = new Dialog(ChatRoomActivity.this);
        dialog2.setTitle("Список участников");
        dialog2.setContentView(R.layout.visitors_dialog);
        ListView listView = dialog2.findViewById(R.id.listview);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Extensions").child(name).child("EventVisitors");
        FirebaseListAdapter<chat_class> visitorsAdapter = new FirebaseListAdapter<chat_class>(this, chat_class.class, R.layout.list_item, reference) {
            @Override
            protected void populateView(View v, chat_class model, int position) {
                TextView tv = v.findViewById(R.id.tv);
                tv.setText(model.getChatName());
            }
        };
        listView.setAdapter(visitorsAdapter);
        dialog2.show();
    }
}
