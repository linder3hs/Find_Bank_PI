package com.linder.find_bank.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.linder.find_bank.R;

import org.json.JSONArray;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {
    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private EditText txtcorreo, txtcontra;
    private Button btningresar, btnregister;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtcorreo = (EditText) findViewById(R.id.txtCorreo);
        txtcontra = (EditText) findViewById(R.id.txtPass);
        btningresar = (Button) findViewById(R.id.btnIngresar);
        btnregister = (Button) findViewById(R.id.btnregister);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        // username remember
        final String username = sharedPreferences.getString("username", null);
        if(username != null){
            txtcorreo.setText(username);
            txtcontra.requestFocus();

        }


        // islogged remember
        if(sharedPreferences.getBoolean("islogged", false)){
            // Go to Dashboard
            goDashboard();
        }
        txtcorreo.setHintTextColor(Color.WHITE);
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btningresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtcorreo.getText().toString().isEmpty() || txtcontra.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Rellene los campos", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog();
                    Thread tr = new Thread() {
                        @Override
                        public void run() {

                            //Enviar los datos hacia el Web Service y
                            //Recibir los datos que me envia el Web Service
                            final String res = enviarPost(txtcorreo.getText().toString(), txtcontra.getText().toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    int r = objJSON(res);
                                    if (r > 0) {

                                        // Save to SharedPreferences
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        boolean success = editor
                                                .putString("username", username)
                                                .putBoolean("islogged", true)
                                                .commit();
                                        // Go to Dashboard
                                        goDashboard();

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Correo o Contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                    }



                                }
                            });
                        }
                    };
                    tr.start();
                }

            }
        });

    }

    private void goDashboard() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("correo",txtcorreo.getText().toString());
        startActivity(intent);

        finish();
    }

    //Metodo para consumir el WEB SERVICE
    public String enviarPost(String correo, String pass) {
        String urlparametros = "correo=" + correo + "&pass=" + pass;
        HttpURLConnection conection = null;
        String respuesta = "";
        try {
            //Se va a la Web y se envia los datos
            URL url = new URL("https://find-bank-roque363.c9users.io/WebService/valida.php");//Cambiar el ip - ya que no es estable por que es local
            conection = (HttpURLConnection) url.openConnection();
            conection.setRequestMethod("POST");
            conection.setRequestProperty("Content-Length", "" + Integer.toString(urlparametros.getBytes().length));

            conection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conection.getOutputStream());
            wr.writeBytes(urlparametros);
            wr.close();

            //Leer el dato que nos devuelve el Web Service
            Scanner inStream = new Scanner(conection.getInputStream());

            //Recorrer el dato que nos devolvio
            while (inStream.hasNextLine()) {
                respuesta += (inStream.nextLine());
            }

        } catch (Exception e) {

        }
        return respuesta.toString();
    }

    //Contar cuantos registros hay en la respuesta
    public int objJSON(String rspta) {
        int res = 0;//Si el usuario es incorrecto retorna 0
        try {
            JSONArray json = new JSONArray(rspta);
            if (json.length() > 0) {
                res = 1;//Si el usuario es correcto retorna 1
            }
        } catch (Exception e) {

        }
        return res;
    }

    public void progressDialog() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setIcon(R.drawable.ic_lock_black);
        progressDialog.setMessage("Validando Datos");
        progressDialog.setTitle("Find Bank");
        progressDialog.show();
    }
}
