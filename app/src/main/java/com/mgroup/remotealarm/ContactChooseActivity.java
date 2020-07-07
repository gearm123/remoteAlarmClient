package com.mgroup.remotealarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactChooseActivity extends AppCompatActivity {
    public static final int REQUEST_READ_CONTACTS = 79;
    ListView list;
    ArrayList mobileArray;
    private SharedPreferences mPrefs;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_choose);
        mPrefs = getSharedPreferences("remote_alarm", MODE_PRIVATE);
        editor = mPrefs.edit();
        mobileArray = Utilities.getAllContacts(this);
        list = findViewById(R.id.list);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mobileArray);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String selectedFromList = (list.getItemAtPosition(arg2)).toString();
                Log.v("remote_alarm","contact selected is "+selectedFromList);
                Contact chosenContact = MainActivity.myContacts.findContactByName(selectedFromList);
                WakeUpThread wake = new WakeUpThread(getApplicationContext(), chosenContact.getNumber());
                wake.start();
                Log.v("remote_alarm","contact number is "+chosenContact.getNumber());
                finish();
            }
        });
    }

}
