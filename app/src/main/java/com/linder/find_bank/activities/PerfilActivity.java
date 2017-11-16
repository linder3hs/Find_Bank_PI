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

    private static final String TAG = PerfilActivity.class.getSimpleName();

    private String email;
    private ImageView fotoImage;
    private TextView emailText1,emailText2,nombreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailText1 = (TextView)findViewById(R.id.txtEmail1);
        emailText2 = (TextView)findViewById(R.id.txtEmail2);
        nombreText = (TextView)findViewById(R.id.txtNombre);
        fotoImage = (ImageView)findViewById(R.id.profile_image);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        email = sharedPreferences.getString("email", null);
        Log.d(TAG, "email: " + email);

        initialize();
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
                        emailText2.setText(user.getCorreo());


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



}
