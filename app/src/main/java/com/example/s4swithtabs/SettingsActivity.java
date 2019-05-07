package com.example.s4swithtabs;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class SettingsActivity extends ListActivity{

    final String[] settingsList = new String[]{"Выйти", "Изменить профиль", "Уведомления"};

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

        if (position == 0)
            Toast.makeText(getApplicationContext(),
                "Вы выбрали " + l.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
        else if (position == 1)
            Toast.makeText(getApplicationContext(),
                    "ВЫ ВЫБРАЛИ " + l.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
        else Toast.makeText(getApplicationContext(),
                    "Вы урод и выбрали " + l.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
    }


}
