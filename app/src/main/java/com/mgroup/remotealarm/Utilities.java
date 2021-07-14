package com.mgroup.remotealarm;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Utilities {

    public static boolean isGreaterThanOreo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return true;
        }
        return false;
    }

    public  static void syncContacts(Context context,ContactsImporter contacts){
            ArrayList<String> nameList = new ArrayList<>();
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    String phoneNo = null;
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));
                    if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        pCur.moveToNext();
                                phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                        pCur.close();
                    }
                    contacts.addContact(new Contact(id,name,phoneNo));
                }
            }
            if (cur != null) {
                cur.close();
            }

    }


    public static ArrayList getAllContacts(Context context) {
        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                nameList.add(name);
                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return nameList;
    }

    public static void addContacttoFilterList(Context context,String name){
        SharedPreferences prefs = context.getSharedPreferences(
                "filter_list", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = prefs.getString("filter_array", "");
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> arrayList = gson.fromJson(json, type);
        if(arrayList!=null) {
            Log.v("remote_alarm","array list isnt null adding name and size is "+arrayList.size());
            arrayList.add(name);
        }else{
            Log.v("remote_alarm","array list is null adding first name");
            arrayList = new ArrayList<String>();
            arrayList.add(name);
        }
        String jsonUpdated = gson.toJson(arrayList);
        editor.putString("filter_array", jsonUpdated);
        editor.commit();
    }

    public static boolean isFilteredContact(Context context,String name){
        SharedPreferences prefs = context.getSharedPreferences(
                "filter_list", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("filter_array", "");
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> arrayList = gson.fromJson(json, type);
        if(arrayList!=null){
            Log.v("remote_alarm","array list is not null from contains size is "+arrayList.size());
        }
        if((arrayList!= null)&&(arrayList.contains(name))){
            Log.v("remote_alarm","does contain returning true");
            return true;
        }
        return false;
    }

    public static void removeFilteredContact(Context context,String name){
        SharedPreferences prefs = context.getSharedPreferences(
                "filter_list", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = prefs.getString("filter_array", "");
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> arrayList = gson.fromJson(json, type);
        if((arrayList!=null)&&(arrayList.size()>0)) {
            Log.v("remote_alarm","array list is fine and big removing name");
            arrayList.remove(name);
            String jsonUpdated = gson.toJson(arrayList);
            editor.putString("filter_array", jsonUpdated);
            editor.commit();
        }else{
            Log.v("remote_alarm","array list is either null or empty");
        }

    }
}
