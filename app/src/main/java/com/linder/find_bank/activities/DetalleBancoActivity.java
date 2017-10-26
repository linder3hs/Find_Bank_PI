package com.linder.find_bank.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;

import com.linder.find_bank.R;

public class DetalleBancoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_banco);

        //Flecha de atras Nota: Poner una clase padre en el manifest
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Switch aSwitch = (Switch) findViewById(R.id.estadoB);
        aSwitch.setEnabled(false);
        aSwitch.setClickable(false);

    }
}
