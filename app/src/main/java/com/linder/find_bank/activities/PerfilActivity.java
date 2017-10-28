package com.linder.find_bank.activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.linder.find_bank.R;

public class PerfilActivity extends AppCompatActivity {
    // SharedPreferences
    private SharedPreferences sharedPreferences;

    private static final String TAG = PerfilActivity.class.getSimpleName();

    private TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailText = (TextView)findViewById(R.id.txtemail);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String email = sharedPreferences.getString("email", null);
        Log.d(TAG, "email: " + email);

        emailText.setText(sharedPreferences.getString("email", null));

    }



}
