package com.mgroup.remotealarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private AlertDialog emailDialog;
    public static String myName;
    private SharedPreferences mPrefs;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPrefs = getSharedPreferences("remote_alarm", MODE_PRIVATE);
        editor = mPrefs.edit();
        emailDialog = createEmailDialog();
        emailDialog.show();
    }

    private AlertDialog createEmailDialog() {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
        final EditText edittextEmail;
        final EditText edittextName;
        Log.v("remote_alarm", "creating dialog");
        builder = new AlertDialog.Builder(this);

        edittextName = new EditText(this);
        edittextName.setHint(getString(R.string.my_name));
        edittextName.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        edittextEmail = new EditText(this);
        edittextEmail.setHint(getString(R.string.question));
        edittextEmail.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(edittextName);
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
                        myName = edittextName.getText().toString();
                        String name = edittextEmail.getText().toString();
                        emailDialog.dismiss();
                        editor.putString("name", myName);
                        editor.commit();
                        WakeUpThread wake = new WakeUpThread(getApplicationContext(),name);
                        wake.start();
                        finish();
                    }
                });
            }
        });


        return alertDialog;
    }

}
