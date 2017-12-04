package com.linder.find_bank.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.linder.find_bank.R;
import com.linder.find_bank.network.ApiService;
import com.linder.find_bank.network.ApiServiceGenerator;
import com.linder.find_bank.network.ResponseMessage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAgenteActivity extends AppCompatActivity {
    private static final String TAG = EditAgenteActivity.class.getSimpleName();

   // private RatingBar newSecure;
    private Spinner spinner;
    private Switch newState;
    private String seguridad, sistema;
    private int id;
    private int ratingA;
    private String sisteA;
    private String sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_agente);

       // newSecure = (RatingBar) findViewById(R.id.newSecureAgente);
        spinner = (Spinner) findViewById(R.id.spinnersecure);
        newState = (Switch) findViewById(R.id.newStateAgent);
        id = getIntent().getIntExtra("id_agente_edt",0);
        ratingA = getIntent().getIntExtra("rating",0);
        sisteA = getIntent().getStringExtra("sistem");

      //  newSecure.setRating(ratingA);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3){
                sp = spinner.getSelectedItem().toString();
                Toast.makeText(EditAgenteActivity.this, spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        // Elementos en Spinner
        List<String> values = new ArrayList<String>();
        values.add("Seguro");
        values.add("Normal");
        values.add("Peligroso");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        if (sisteA.equals("1")) {
            newState.setChecked(true);
        } else {
            newState.setChecked(false);
        }


    }
    private void initialize(){

        if (newState.isChecked()) {
            sistema = "1";
        } else {
            sistema = "0";
        }

        if (sp.equals("Seguro")) {
            seguridad = "3";

        } else if (sp.equals("Normal")) {
            seguridad = "2";

        } else if (sp.equals("Peligroso")) {
            seguridad = "1";

        }
         ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<ResponseMessage> call;
        call = service.updateAgente(id, sistema, seguridad);

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
