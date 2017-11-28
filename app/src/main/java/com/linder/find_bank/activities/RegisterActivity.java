package com.linder.find_bank.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.linder.find_bank.R;
import com.linder.find_bank.model.Hash;
import com.linder.find_bank.network.ApiService;
import com.linder.find_bank.network.ApiServiceGenerator;
import com.linder.find_bank.network.ResponseMessage;

import org.json.JSONArray;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText txtnombre, txtemail, txtpassword, txtpasswordAgain;
    Button finaRegister;
    private ProgressDialog progressDialog;

    private static final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtnombre = (EditText) findViewById(R.id.nameRegister);
        txtemail = (EditText) findViewById(R.id.emailRegister);
        txtpassword = (EditText) findViewById(R.id.passwordRegister);
        txtpasswordAgain = (EditText) findViewById(R.id.passwordRegisteragain);

        finaRegister = (Button) findViewById(R.id.finalRegister);
        finaRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String nombre = txtnombre.getText().toString();
                final String email = txtemail.getText().toString();
                final String passwordAgain = txtpasswordAgain.getText().toString();
                final String password = txtpassword.getText().toString();
                final String hpassword = Hash.sha1(password);
                final String tipo = "cliente";

                if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()  || passwordAgain.isEmpty()) { // Comprobar que los campos esten completos
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.error_completar_campos), Snackbar.LENGTH_LONG);// Snackbar message
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    View snaView1 = snackbar.getView();
                    snaView1.setBackgroundColor(getResources().getColor(R.color.bgsnack2));
                    snackbar.show();

                } else if (nombre.matches("[0-9]")) { // Comprobar los caracteres de nombre
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.error_caracteres_numericos), Snackbar.LENGTH_LONG);// Snackbar message
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    View snaView1 = snackbar.getView();
                    snaView1.setBackgroundColor(getResources().getColor(R.color.bgsnack2));
                    snackbar.show();

                } else if (email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) { // Comprobar que sea un email valido

                    if( password.length() < 6 ){ // Comprobar que el password sea mayor a 5
                        Snackbar snackbar = Snackbar.make(view, getString(R.string.error_contraseña_demasiado_corta), Snackbar.LENGTH_LONG);// Snackbar message
                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                        View snaView1 = snackbar.getView();
                        snaView1.setBackgroundColor(getResources().getColor(R.color.bgsnack2));
                        snackbar.show();

                    } else if ( password.equals(passwordAgain) ){ // Comprobar que el password coincide
                        progressDialog();
                        ApiService service = ApiServiceGenerator.createService(ApiService.class);

                        Call<ResponseMessage> call;
                        call = service.registrarUsuario(nombre, email, hpassword, tipo);

                        call.enqueue(new Callback<ResponseMessage>() {
                            @Override
                            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                                try {
                                    int statusCode = response.code();
                                    Log.d(TAG, "HTTP status code: " + statusCode);
                                    if (response.isSuccessful()) {
                                        ResponseMessage responseMessage = response.body();
                                        Log.d(TAG, "responseMessage: " + responseMessage);
                                        Toast.makeText(RegisterActivity.this, responseMessage.getMessage(), Toast.LENGTH_LONG).show();
                                        // Go to Login
                                        progressDialog.dismiss();
                                        goLogin();
                                    } else {
                                        progressDialog.dismiss();
                                        Log.e(TAG, "onError: " + response.errorBody().string());
                                        Snackbar snackbar = Snackbar.make(view, "Error: Dirección de correo electrónico ya registrada!", Snackbar.LENGTH_LONG);// Snackbar message
                                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                                        View snaView1 = snackbar.getView();
                                        snaView1.setBackgroundColor(getResources().getColor(R.color.bgsnack2));
                                        snackbar.show();
                                        //throw new Exception();
                                    }

                                } catch (Throwable t) {
                                    try {
                                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                                        Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                    } catch (Throwable x) {
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                                Log.e(TAG, "onFailure: " + t.toString());
                                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        });

                    }else{
                        Snackbar snackbar = Snackbar.make(view, getString(R.string.error_contraseñas_diferentes), Snackbar.LENGTH_LONG);// Snackbar message
                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                        View snaView1 = snackbar.getView();
                        snaView1.setBackgroundColor(getResources().getColor(R.color.bgsnack2));
                        snackbar.show();
                    }

                }else{
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.error_ingresar_correo_valido), Snackbar.LENGTH_LONG);// Snackbar message
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    View snaView1 = snackbar.getView();
                    snaView1.setBackgroundColor(getResources().getColor(R.color.bgsnack2));
                    snackbar.show();

                }
            }
        });

    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        //Intent intent = new Intent(this, PerfilActivity.class);
        intent.putExtra("correo",txtemail.getText().toString());
        startActivity(intent);
        finish();
    }

    public void progressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setIcon(R.drawable.ic_lock_black);
        progressDialog.setMessage("Validando Datos");
        progressDialog.setTitle("Find Bank");
        progressDialog.show();
    }

}
