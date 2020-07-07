package com.mgroup.remotealarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AlertDialog emailDialog;
    private AlertDialog nameDialog;
    public String myName;
    private SharedPreferences mPrefs;
    private ImageView wakeButton;
    private Button filterContactsButton;
    private Switch isCheckedSwitch;
    public static ContactsImporter myContacts;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContacts = new ContactsImporter();
        Log.v("remote_alarm", "about to sync contacts");
        Utilities.syncContacts(this,myContacts);
        Log.v("remote_alarm", "contacts synced");
        myContacts.printToastContactIndex(this,6);
        wakeButton = this.findViewById(R.id.wake_button);
        wakeButton.setOnClickListener(this);
        filterContactsButton = this.findViewById(R.id.filter_by_contact);
        filterContactsButton.setOnClickListener(this);
        isCheckedSwitch = this.findViewById(R.id.toggle);
        mPrefs = getSharedPreferences("remote_alarm", MODE_PRIVATE);
        editor = mPrefs.edit();
        checkAndGetName();
        if (Utilities.isGreaterThanOreo()) {
            checkDrawPer();
        }

        updateChecked();
    }

    private AlertDialog createEmailDialog() {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
        final EditText edittextEmail;
        Log.v("remote_alarm", "creating dialog");
        builder = new AlertDialog.Builder(this);

        edittextEmail = new EditText(this);
        edittextEmail.setHint(getString(R.string.question));
        edittextEmail.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(edittextEmail);
        builder.setTitle(getString(R.string.question));
        builder.setView(lay);
        builder.setCancelable(false);

        builder.setPositiveButton(getString(R.string.wake), null);

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                emailDialog.dismiss();
                finish();
            }
        });

        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button b = emailDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String nameToWake = edittextEmail.getText().toString();
                        emailDialog.dismiss();
                        WakeUpThread wake = new WakeUpThread(getApplicationContext(), nameToWake);
                        wake.start();
                    }
                });
            }
        });


        return alertDialog;
    }


    private AlertDialog createDialogName() {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
        final EditText edittextName;
        Log.v("remote_alarm", "creating dialog");
        builder = new AlertDialog.Builder(this);

        edittextName = new EditText(this);
        edittextName.setHint(getString(R.string.my_name));
        edittextName.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(edittextName);
        builder.setTitle(getString(R.string.my_name));
        builder.setView(lay);
        builder.setCancelable(false);

        builder.setPositiveButton(getString(R.string.submit), null);

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                nameDialog.dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button b = nameDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        myName = edittextName.getText().toString();
                        nameDialog.dismiss();
                        editor.putString("name", myName);
                        editor.commit();
                    }
                });
            }
        });


        return alertDialog;
    }


    public void checkAndGetName() {

        myName = mPrefs.getString("name", "");
        if (myName.equals("")) {
            nameDialog = createDialogName();
            nameDialog.show();
        }
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.wake_button) {
            Intent i = new Intent();
            i.setClassName("com.mgroup.remotealarm", "com.mgroup.remotealarm.ContactChooseActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);;
        }else if(view.getId() == R.id.filter_by_contact){
            Intent i = new Intent();
            i.setClassName("com.mgroup.remotealarm", "com.mgroup.remotealarm.ContactChooseActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);
        }
    }

    public void checkDrawPer() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        }
    }

    public void updateChecked(){
        editor.putBoolean("is_checked", isCheckedSwitch.isChecked());
        editor.commit();
    }
}
