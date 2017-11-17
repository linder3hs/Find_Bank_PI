package com.linder.find_bank.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class FavoriteActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView agentesList;
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        //Flecha atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        agentesList = (RecyclerView) findViewById(R.id.recyclerview);
        agentesList.setLayoutManager(new LinearLayoutManager(this));

        agentesList.setAdapter(new AgenteAdapter(this));
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);

        //Indicamos que listener recogerá la retrollamada (callback), en este caso, será el metodo OnRefresh de esta clase.

        swipeLayout.setOnRefreshListener(this);
        //Podemos espeficar si queremos, un patron de colores diferente al patrón por defecto.
        swipeLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

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

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);

        //Vamos a simular un refresco con un handle.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                initialize();
                //Se supone que aqui hemos realizado las tareas necesarias de refresco, y que ya podemos ocultar la barra de progreso
                swipeLayout.setRefreshing(false);

            }
        }, 2000);

    }
}
