package com.linder.find_bank.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.linder.find_bank.R;
import com.linder.find_bank.model.Agente;
import com.linder.find_bank.network.ApiService;
import com.linder.find_bank.network.ApiServiceGenerator;
import com.linder.find_bank.respository.AgenteAdapter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView agentesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        //Flecha atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        agentesList = (RecyclerView) findViewById(R.id.recyclerview);
        agentesList.setLayoutManager(new LinearLayoutManager(this));

        agentesList.setAdapter(new AgenteAdapter(this));

        initialize();

    }

    private void initialize() {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Agente>> call = service.getAgentes();

        call.enqueue(new Callback<List<Agente>>() {
            @Override
            public void onResponse(Call<List<Agente>> call, Response<List<Agente>> response) {
                try {
                    int statusCode = response.code();
                    Log.d("Agent", "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {
                        List<Agente> agentes = response.body();
                        Log.d("Agent2", "Agentes: "+ agentes);

                        AgenteAdapter adapter = (AgenteAdapter) agentesList.getAdapter();
                        adapter.setAgentes(agentes);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("Error", "onError: " + response.errorBody().string());
                        throw new Exception("Error del servidor");
                    }
                } catch (Throwable t) {
                    try {
                        Log.e("onThrowable", "onThrowable: " + t.toString(), t);
                        Toast.makeText(FavoriteActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Agente>> call, Throwable t) {
                Log.e("onFailure", "onFailure: " + t.toString());
                Toast.makeText(FavoriteActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
