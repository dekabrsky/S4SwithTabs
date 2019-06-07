package com.example.s4swithtabs;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;


public class SettingsActivity extends ListActivity {

    final String[] settingsList = new String[]{"Выйти", "Справка", "Написать разработчикам"};
    public EditText editText;
    Dialog dialog;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> catNamesList = new ArrayList<>(Arrays.asList(settingsList));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, catNamesList);
        setListAdapter(mAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (position == 0) {
            AuthUI.getInstance().signOut(this);
            finish();
        } else if (position == 1)
            Toast.makeText(this, "Справка", Toast.LENGTH_LONG).show();
        else if (position == 2) {
            dialog = new Dialog(SettingsActivity.this);
            dialog.setContentView(R.layout.dialog_with_us);
            editText = dialog.findViewById(R.id.edit_to_us);
            dialog.show();
        }
    }

    public void clickSend(View v) {
        String string = editText.getText().toString();
        FirebaseDatabase.getInstance().getReference().child("About App").push().setValue(string + " , " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        dialog.dismiss();
        Toast.makeText(SettingsActivity.this, "Успешно! Ваше сообщение поступило разработчикам", Toast.LENGTH_LONG).show();
    }
}
