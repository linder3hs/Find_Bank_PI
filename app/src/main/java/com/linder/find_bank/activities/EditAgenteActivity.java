package com.linder.find_bank.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.Toast;

import com.linder.find_bank.R;
import com.linder.find_bank.network.ApiService;
import com.linder.find_bank.network.ApiServiceGenerator;
import com.linder.find_bank.network.ResponseMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAgenteActivity extends AppCompatActivity {
    private static final String TAG = EditAgenteActivity.class.getSimpleName();

    private RatingBar newSecure;
    private Switch newState;
    private String switchOn = "Agente Abierto";
    private String switchOff = "Agente Cerrado";
    private String seguridad, sistema;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_agente);

        newSecure = (RatingBar) findViewById(R.id.newSecureAgente);
        newState = (Switch) findViewById(R.id.newStateAgent);
        id = getIntent().getIntExtra("id_agente_edt",0);
        Toast.makeText(this, String.valueOf(id), Toast.LENGTH_SHORT).show();
        String ratingValue = String.valueOf(newSecure.getRating());
        Toast.makeText(getApplicationContext(), "Rate: " + ratingValue, Toast.LENGTH_LONG).show();

    }
    private void initialize(){
        newState.setChecked(true);

        newState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    seguridad = "1";
                } else {
                    seguridad = "0";
                }

            }
        });
        if (newState.isChecked()) {
            seguridad = "1";
        } else {
            seguridad = "0";
        }
        sistema = String.valueOf(newSecure.getRating());
        Toast.makeText(getApplicationContext(), "Rate: " + sistema, Toast.LENGTH_LONG).show();
        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<ResponseMessage> call;
        call = service.updateAgente(id, seguridad, sistema);

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
                        Toast.makeText(EditAgenteActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(EditAgenteActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    public void updateAgente(View view){
        initialize();
        Log.d(TAG, String.valueOf(id));
        Log.d(TAG, String.valueOf(seguridad));
        Log.d(TAG, String.valueOf(sistema));
        goHome();
    }

    public void goHome(){
        Intent intent = new Intent(EditAgenteActivity.this, HomeActivity.class);
        startActivity(intent);
    }



}
