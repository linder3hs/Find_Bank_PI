package com.linder.find_bank.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.linder.find_bank.R;

public class SplashActivity extends AppCompatActivity {

    //Tiempo de splash
    public static final int TIME = 3000;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, TIME);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // islogged remember
        if(sharedPreferences.getBoolean("islogged", false)){
            // Go to Dashboard
            goDashboard();
        }


    }

    private void goDashboard() {
        Intent intent = new Intent(this, HomeActivity.class);
        //startActivity(intent);

        //finish();
    }
}
