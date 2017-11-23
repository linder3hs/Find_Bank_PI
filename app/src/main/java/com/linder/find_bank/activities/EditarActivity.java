package com.linder.find_bank.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.linder.find_bank.R;
import com.linder.find_bank.model.User;
import com.linder.find_bank.network.ApiService;
import com.linder.find_bank.network.ApiServiceGenerator;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String email, nombre, pwd;

    private static final String TAG = EditarActivity.class.getSimpleName();
    private EditText nombreNew, emailNew, passwordNew, passWordNewRepeat;
    private Button btnUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nombreNew = (EditText) findViewById(R.id.nombreNew);
        emailNew = (EditText) findViewById(R.id.emailNew);
        email = getIntent().getStringExtra("emailSend");
        pwd = getIntent().getStringExtra("passwordSend");
        nombre = getIntent().getStringExtra("nombreSend");
        emailNew.setText(email);
        nombreNew.setText(nombre);
        Log.d("Nombre", nombre);

    }

}
