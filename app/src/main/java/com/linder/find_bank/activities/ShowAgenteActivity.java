package com.linder.find_bank.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.linder.find_bank.R;
import com.linder.find_bank.model.Agente;
import com.linder.find_bank.network.ApiService;
import com.linder.find_bank.network.ApiServiceGenerator;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAgenteActivity extends AppCompatActivity {
    private static final String TAG = ShowAgenteActivity.class.getSimpleName();
    private Integer id;
    private TextView name,direccion,sistema,seguridad,tipo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_agente);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getIntExtra("ID",0);
        name = (TextView) findViewById(R.id.showAgentName);
        direccion = (TextView) findViewById(R.id.showAgentAddress);
        sistema = (TextView) findViewById(R.id.showAgentSystem);
        seguridad = (TextView) findViewById(R.id.showAgentSecure);
        tipo = (TextView) findViewById(R.id.showAgentType);

        initialize();
    }

    private void initialize() {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<Agente> call = service.showAgente(id);

        call.enqueue(new Callback<Agente>() {
            @Override
            public void onResponse(Call<Agente> call, Response<Agente> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        Agente agente = response.body();
                        Log.d(TAG, "producto: " + agente);

                        name.setText(agente.getNombre());
                        direccion.setText("Dirección: "+agente.getDireccion());
                        String si = agente.getSistema();
                        String s;
                        if ( si == "1") {
                            s = "Activo";
                        } else if (si == "0") {
                            s = "Apagado";
                        } else {
                            s = "Error de SQL";
                        }
                        sistema.setText("Sistema " + s);
                        seguridad.setText("Nivel de Seguridad: " + agente.getSeguridad());
                        tipo.setText(("Tipo: " + agente.getTipo()));

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(ShowAgenteActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<Agente> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(ShowAgenteActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }
}
