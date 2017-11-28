package com.linder.find_bank.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linder.find_bank.R;
import com.linder.find_bank.model.User;
import com.linder.find_bank.network.ApiService;
import com.linder.find_bank.network.ApiServiceGenerator;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {
    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private FloatingActionButton btnEditar;
    private static final String TAG = PerfilActivity.class.getSimpleName();

    private String email;
    private Integer idSend;
    private String emailSend, nombreSend, passwordSend;
    private ImageView fotoImage;
    private TextView emailText1,nombreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton btnEditar = (FloatingActionButton) findViewById(R.id.btnEditar);
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mandarDatos();
            }
        });

        emailText1 = (TextView)findViewById(R.id.txtEmail1);
        nombreText = (TextView)findViewById(R.id.txtNombre);
        fotoImage = (ImageView)findViewById(R.id.profile_image);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        email = sharedPreferences.getString("email", null);
        Log.d(TAG, "email: " + email);
        initialize();// Llamado a los servicios Rest
        emailText1.setText(sharedPreferences.getString("email", null));

    }

    private void initialize(){
        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<User> call = service.showUsuario(email);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        User user = response.body();
                        Log.d(TAG, "user: " + user);

                        String url = ApiService.API_BASE_URL + "/images/" + user.getImagen();
                        Picasso.with(PerfilActivity.this).load(url).into(fotoImage);
                        nombreText.setText(user.getNombre());

                        // Obtiene los datos del usuario
                        idSend = user.getId();
                        emailSend = user.getCorreo();
                        nombreSend = user.getNombre();
                        passwordSend = user.getPass();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(PerfilActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(PerfilActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    public void mandarDatos() {
        Intent intent = new Intent(PerfilActivity.this, EditarActivity.class);
        intent.putExtra("idSend", idSend);
        intent.putExtra("nombreSend", nombreSend);
        intent.putExtra("emailSend", emailSend);
        intent.putExtra("passwordSend", passwordSend);
        startActivity(intent);
    }

}
