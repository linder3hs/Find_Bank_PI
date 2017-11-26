package com.linder.find_bank.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.linder.find_bank.R;
import com.linder.find_bank.model.User;
import com.linder.find_bank.network.ApiService;
import com.linder.find_bank.network.ApiServiceGenerator;
import com.linder.find_bank.network.ResponseMessage;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String TAG = EditarActivity.class.getSimpleName();
    private int id;
    private String email, nombre, password;
    private EditText nombreText, emailText, passwordText, passWordTextRepeat;
    private Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nombreText = (EditText) findViewById(R.id.nombreNew);
        emailText = (EditText) findViewById(R.id.emailNew);

        id = getIntent().getIntExtra("idSend",0);
        nombre = getIntent().getStringExtra("nombreSend");
        email = getIntent().getStringExtra("emailSend");
        password = getIntent().getStringExtra("passwordSend");

        nombreText.setText(nombre);
        emailText.setText(email);
        Log.d("Usuario","Id: "+ id);
        Log.d("Usuario","Nombre: "+ nombre);
        Log.d("Usuario","E-mail: "+ email);
    }

    private void initialize(){

        final String nombreNew = nombreText.getText().toString();
        final String emailNew = emailText.getText().toString();

        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<ResponseMessage> call;
        call = service.updateUsuario(id, nombreNew, emailNew);

        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {
                        ResponseMessage responseMessage = response.body();
                        Log.d(TAG, "user: " + responseMessage);

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(EditarActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(EditarActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    public void update(View view){
        initialize();
        goPerfil();
    }

    public void goPerfil(){
        Intent intent = new Intent(EditarActivity.this, PerfilActivity.class);
        startActivity(intent);
    }

}
