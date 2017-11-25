package com.linder.find_bank.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.linder.find_bank.R;
import com.linder.find_bank.model.Hash;
import com.linder.find_bank.model.User;
import com.linder.find_bank.network.ApiService;
import com.linder.find_bank.network.ApiServiceGenerator;
import com.linder.find_bank.network.ResponseMessage;

import org.json.JSONArray;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Scanner;

import okio.HashingSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private EditText txtemail, txtpassword;
    private Button btningresar, btnregister;
    private ProgressDialog progressDialog;
    String nameuser;

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtemail = (EditText) findViewById(R.id.txtCorreo);
        txtpassword = (EditText) findViewById(R.id.txtPass);
        btningresar = (Button) findViewById(R.id.btnIngresar);
        btnregister = (Button) findViewById(R.id.btnregister);
        //rootLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // username remember
        final String email = sharedPreferences.getString("email", null);
        if(email != null){
            txtemail.setText(email);
            txtpassword.requestFocus();
        }

        // islogged remember
        if(sharedPreferences.getBoolean("islogged", false)){
            // Go to Dashboard
            goDashboard();
        }
        txtemail.setHintTextColor(Color.WHITE);
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btningresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String email = txtemail.getText().toString();
                final String password = txtpassword.getText().toString();
                final String hpassword = Hash.sha1(password);
                final String tipo = "cliente";

                // Comprobar que los campos esten completos
                if (email.isEmpty() || hpassword.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.error_completar_campos), Snackbar.LENGTH_LONG);// Snackbar message
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    View snaView1 = snackbar.getView();
                    snaView1.setBackgroundColor(getResources().getColor(R.color.bgsnack2));
                    snackbar.show();

                }else{

                    // Comprobar que sea un email valido
                    if (email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")){
                        progressDialog();
                        ApiService service = ApiServiceGenerator.createService(ApiService.class);
                        Call<ResponseMessage> call = null;
                        call = service.loginUsuario(email, hpassword, tipo);

                        call.enqueue(new Callback<ResponseMessage>() {
                            @Override
                            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                                try {
                                    int statusCode = response.code();
                                    Log.d(TAG, "HTTP status code: " + statusCode);
                                    if (response.isSuccessful()) {
                                        ResponseMessage responseMessage = response.body();
                                        Log.d(TAG, "responseMessage: " + responseMessage);
                                        Toast.makeText(LoginActivity.this, responseMessage.getMessage(), Toast.LENGTH_LONG).show();
                                        // Save to SharedPreferences
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        boolean success = editor
                                                .putString("email", email)
                                                .putBoolean("islogged", true)
                                                .commit();
                                        // Go to Dashboard
                                        progressDialog.dismiss();
                                        goDashboard();
                                    } else {
                                        progressDialog.dismiss();
                                        Log.e(TAG, "onError: " + response.errorBody().string());
                                        Snackbar snackbar = Snackbar.make(view, getString(R.string.error_correo_contraseña_incorrectos), Snackbar.LENGTH_LONG);// Snackbar message
                                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                                        View snaView1 = snackbar.getView();
                                        snaView1.setBackgroundColor(getResources().getColor(R.color.bgsnack2));
                                        snackbar.show();
                                        //throw new Exception();
                                    }

                                } catch (Throwable t) {
                                    try {
                                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                                        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                    } catch (Throwable x) {
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                                Log.e(TAG, "onFailure: " + t.toString());
                                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        });
                    }else{
                        Snackbar snackbar = Snackbar.make(view, getString(R.string.error_ingresar_correo_valido), Snackbar.LENGTH_LONG);// Snackbar message
                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                        View snaView1 = snackbar.getView();
                        snaView1.setBackgroundColor(getResources().getColor(R.color.bgsnack2));
                        snackbar.show();
                    }
                }

            }
        });

    }

    private void goDashboard() {
        Intent intent = new Intent(this, HomeActivity.class);
        //Intent intent = new Intent(this, PerfilActivity.class);
        intent.putExtra("correo",txtemail.getText().toString());
        startActivity(intent);
        finish();
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
