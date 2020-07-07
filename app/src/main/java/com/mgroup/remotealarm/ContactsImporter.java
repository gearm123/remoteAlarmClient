package com.mgroup.remotealarm;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactsImporter {

    private ArrayList<Contact> contacts;

    public ContactsImporter(){
        contacts = new ArrayList<>();
    }

    public void addContact(Contact tmp){
        contacts.add(tmp);
    }

    public Contact findContactByName(String name){
        for(int i = 0 ; i < contacts.size();i++){
            if(name.equals(contacts.get(i).getName())){
                return contacts.get(i);
            }
        }

        return null;
    }


    public void printToastContactIndex(Context context, int index){
        Toast.makeText(context,"name is "+contacts.get(index).getName()+" and number is "+contacts.get(index).getNumber(), Toast.LENGTH_LONG).show();

    }
}
