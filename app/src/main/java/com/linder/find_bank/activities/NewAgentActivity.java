package com.linder.find_bank.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.linder.find_bank.R;

import java.util.ArrayList;
import java.util.List;

public class NewAgentActivity extends AppCompatActivity {
private Spinner tipoAgente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_agent);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tipoAgente = (Spinner) findViewById(R.id.tipoAgente);

        tipoAgente.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0,View arg1,int arg2,long arg3){
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        // Elementos en Spinner
        List<String> values = new ArrayList<String>();
        values.add("Mercado");
        values.add("Botica");
        values.add("Bodega");
        values.add("Otro");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoAgente.setAdapter(dataAdapter);

    }

}
