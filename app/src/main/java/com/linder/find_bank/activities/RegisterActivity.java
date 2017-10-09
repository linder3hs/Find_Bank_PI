package com.linder.find_bank.activities;

import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {
    EditText name, mail, contra, contaAgian;
    Button finaRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText) findViewById(R.id.nameRegister);
        mail = (EditText) findViewById(R.id.emailRegister);
        contra = (EditText) findViewById(R.id.passwordRegister);
        contaAgian = (EditText) findViewById(R.id.passwordRegisteragain);

        finaRegister = (Button) findViewById(R.id.finalRegister);
        finaRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nombre = name.getText().toString();
                final String email = mail.getText().toString();
                final String pass = contra.getText().toString();
                final String pasa = contaAgian.getText().toString();

                if (name.getText().toString().isEmpty() || mail.getText().toString().isEmpty() || contra.getText().toString().isEmpty()  || contaAgian.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Rellene los datos", Toast.LENGTH_SHORT).show();
                } else {
                    Thread tr = new Thread() {
                        @Override
                        public void run() {
                            //Enviar los datos hacia el Web Service y
                            //Recibir los datos que me envia el Web Service
                            enviarPost(nombre, email, pass);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);//Prueba del servicio
                                    startActivity(i);
                                    finish();

                                }
                            });
                        }
                    };
                    tr.start();
                }
            }
        });

    }

    //Metodo para consumir el WEB SERVICE
    public String enviarPost(String nombre, String correo, String pass) {
        String urlparametros = "nombre=" + nombre + "&correo=" + correo + "&pass=" + pass;
        HttpURLConnection conection = null;
        String respuesta = "";
        try {
            //Se va a la Web y se envia los datos
            //URL url=new URL("https://find-bank-roque363.c9users.io/WebService/valida.php");//Cambiar el ip - ya que no es estable por que es local
            URL url = new URL("https://find-bank-roque363.c9users.io/WebService/registra.php");
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
        int res = 1;//Si el usuario es incorrecto retorna 0
        try {
            JSONArray json = new JSONArray(rspta);
            if (json.length() > 0) {
                res = 1;//Si el usuario es correcto retorna 1
            }
        } catch (Exception e) {

        }
        return res;
    }
}
