package com.linder.find_bank.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.linder.find_bank.R;
import com.linder.find_bank.model.Agente;
import com.linder.find_bank.model.User;
import com.linder.find_bank.network.ApiService;
import com.linder.find_bank.network.ApiServiceGenerator;
import com.linder.find_bank.network.ResponseMessage;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    int PLACE_PICKER_REQUEST = 1;
    FloatingActionButton btnRefresh;
    private Marker markerAgente;
    private Marker markerInfo;
    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private GoogleMap mMap;
    private String nameSend;
    Location location;
    LocationManager locationManager;
    LocationListener locationListener;
    AlertDialog alertDialog = null;
    private String email;
    private ImageView fotoImage;
    Integer user_id;
    Integer agente_id;
    private FloatingActionButton newAgent;
    //Variales de permiso
    final private int REQUEST_CODE_ASK_PERMISON = 124;

    private void accsserPermison() {
        // Check permission (Api 22 check in Manifest, Api 23 check by requestPermissions)
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Dont have permission => request one or many permissions (String[])
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISON);
        }else {
            // Have permission
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_ASK_PERMISON: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso de ubicacion concedido", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        accsserPermison();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        btnRefresh = (FloatingActionButton) findViewById(R.id.btnRefresh);

        newAgent = (FloatingActionButton) findViewById(R.id.newAgent);
        newAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NewAgentActivity.class);
                startActivity(intent);
            }
        });


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        initialize();
                    }
                },2000);
            }
        });

        email = sharedPreferences.getString("email", null);
        Log.d(TAG, "email: " + email);

        NavigationView navigationView2 = (NavigationView) findViewById(R.id.nav_view);
        TextView emailText = (TextView) navigationView2.getHeaderView(0).findViewById(R.id.correoUser);
        emailText.setText(sharedPreferences.getString("email", null));

        fotoImage = (ImageView) navigationView2.getHeaderView(0).findViewById(R.id.profile_image);

        //Mejora para prender el gps automaticamente
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertNoGps();
        }

        //locationManager
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (status == ConnectionResult.SUCCESS) {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(HomeActivity.this, "Revise su conexión", Toast.LENGTH_LONG).show();
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity) getApplicationContext(), 10);
            dialog.setTitle("Sin conexión");
            dialog.show();

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Dialog dialogs = new Dialog(this);
            dialogs.setContentView(R.layout.activity_nosotros);
            dialogs.setTitle("Nosotros");
            dialogs.show();
            Button btnosotros = (Button) dialogs.findViewById(R.id.nosotros);
            btnosotros.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar snackbar = Snackbar.make(view,"Muy pronto se habilitara esta opcion",Snackbar.LENGTH_SHORT );
                    snackbar.show();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            Intent intent3 = new Intent(this, PerfilActivity.class);
            startActivity(intent3);
            Log.d("Intent", String.valueOf(intent3));

        } else if (id == R.id.nav_favoritos) {
            Log.d("Agent", "Usuario: "+ user_id);
            goFavoritos();

        } else if (id == R.id.nav_nosotros) {
            Dialog dialogs = new Dialog(this);
            dialogs.setContentView(R.layout.activity_nosotros);
            dialogs.setTitle("Nosotros");
            dialogs.show();
            Button btnosotros = (Button) dialogs.findViewById(R.id.nosotros);
            btnosotros.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar snackbar = Snackbar.make(view,"Muy pronto se habilitara esta opcion",Snackbar.LENGTH_SHORT );
                    snackbar.show();
                }
            });
        } else if (id == R.id.salir) {
            Intent intent1 = new Intent(this, LoginActivity.class);
            startActivity(intent1);
            callLogout();
        } else if (id == R.id.nav_agentes) {
           Intent intent = new Intent(HomeActivity.this, AgenteAllActivity.class);
           startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Llamado a los servicios Rest
    private void initialize() {
        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        //Muestra los datos del usuario
        Call<User> call2 = service.showUsuario(email);
        call2.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                try {
                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        User user = response.body();
                        Log.d(TAG, "user: " + user);

                        String url = ApiService.API_BASE_URL + "/images/" + user.getImagen();//Obtiene la imagen
                        Picasso.with(HomeActivity.this).load(url).into(fotoImage);//Guarda la imagen

                        user_id= user.getId();//Obtiene el Id de usuario

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){
                        Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

        //Muestra los datos de los Agentes
        Call<List<Agente>> call = service.getAgentes();
        call.enqueue(new Callback<List<Agente>>() {
            @Override
            public void onResponse(Call<List<Agente>> call, Response<List<Agente>> response) {
                try {
                    int statusCode = response.code();
                    Log.d("Agent", "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {
                        final List<Agente> agentes = response.body();
                        Log.d("Agent2", "Agentes: " + agentes);

                        for (Agente agente : agentes ) {
                            float lat = agente.getLat();
                            float lng = agente.getLng();
                            LatLng cosmo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .title(agente.getNombre())
                                    .snippet(agente.getDescripcion())
                                    .position(cosmo)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.compass)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(cosmo));
                            mMap.setOnMarkerClickListener(HomeActivity.this);
                            float zoon = 16;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cosmo, zoon));
                        }

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public boolean onMarkerClick(Marker marker) {

                                for (Agente agente : agentes){
                                    if (agente.getNombre().equals(marker.getTitle())){

                                        final Dialog dialogs = new Dialog(HomeActivity.this);
                                        dialogs.setContentView(R.layout.activity_detalle_banco);
                                        dialogs.closeOptionsMenu();
                                        dialogs.setCancelable(false);
                                        dialogs.show();

                                        agente_id = agente.getId();
                                        Log.d(TAG, "agente_id: " + agente_id );

                                        final LikeButton btnHeart = (LikeButton) dialogs.findViewById(R.id.btn_favorite);

                                        ApiService service = ApiServiceGenerator.createService(ApiService.class);
                                        Call<ResponseMessage> call = null;
                                        call = service.validarFavorito(user_id,agente_id);

                                        call.enqueue(new Callback<ResponseMessage>() {
                                            @Override
                                            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                                                try {
                                                    int statusCode = response.code();
                                                    Log.d(TAG, "HTTP status code: " + statusCode);
                                                    if (response.isSuccessful()) {
                                                        ResponseMessage responseMessage = response.body();
                                                        Log.d(TAG, "responseMessage: " + responseMessage);
                                                        //Toast.makeText(HomeActivity.this, responseMessage.getMessage(), Toast.LENGTH_LONG).show();
                                                        btnHeart.setLiked(true);
                                                    } else {
                                                        Log.d("Error", "onError: " + response.errorBody().string());
                                                        //throw new Exception("Error del servidor");
                                                    }
                                                } catch (Throwable t) {
                                                    try {
                                                        Log.e("onThrowable", "onThrowable: " + t.toString(), t);
                                                        Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                    } catch (Throwable x) {
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                                                Log.e(TAG, "onFailure: " + t.toString());
                                                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });

                                        TextView aSwitch = dialogs.findViewById(R.id.sistema);
                                        if (agente.getSistema().equals("1")) {

                                           aSwitch.setText(R.string.si);
                                           aSwitch.setBackgroundColor(Color.rgb(43, 174, 83  ));

                                        } else {
                                            aSwitch.setText(R.string.no);
                                            aSwitch.setBackgroundColor(Color.argb(255, 192, 57, 43));
                                        }

                                        TextView tipo = dialogs.findViewById(R.id.tipo);
                                        tipo.setText(agente.getTipo());

                                        TextView hora = dialogs.findViewById(R.id.horaA);
                                        hora.setText(agente.getHora_ini()+" am" + "-" + agente.getHora_fin()+ "pm");

                                        TextView direccion = dialogs.findViewById(R.id.direccionAgente);
                                        TextView nombre = dialogs.findViewById(R.id.nombreAgente);
                                        nombre.setText(agente.getNombre());
                                        direccion.setText(agente.getDireccion());

                                        btnHeart.setOnLikeListener(new OnLikeListener() {
                                            @Override
                                            public void liked(LikeButton likeButton) {
                                                agregarFavorito();
                                            }
                                            @Override
                                            public void unLiked(LikeButton likeButton) {
                                                Toast.makeText(HomeActivity.this, "Le diste unfollow", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        RatingBar ratingBar = (RatingBar) dialogs.findViewById(R.id.clalificacionAgente);
                                        if (agente.getSeguridad() == 1) {
                                            ratingBar.setNumStars(2);
                                            ratingBar.setRating(1);
                                        } else if (agente.getSeguridad() == 2) {
                                            ratingBar.setNumStars(3);
                                            ratingBar.setRating(2);
                                        } else if (agente.getSeguridad() == 3) {
                                            ratingBar.setNumStars(4);
                                            ratingBar.setRating(3);
                                        } else if (agente.getSeguridad() == 4) {
                                            ratingBar.setNumStars(5);
                                            ratingBar.setRating(4);
                                        } else  if (agente.getSeguridad() == 5) {
                                            ratingBar.setNumStars(5);
                                            ratingBar.setRating(5);
                                        } else {
                                            ratingBar.setNumStars(0);
                                        }

                                        ImageView btnClose = (ImageView) dialogs.findViewById(R.id.btnClose);
                                        btnClose.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogs.dismiss();
                                            }
                                        });

                                        Button btnEdiar = (Button) dialogs.findViewById(R.id.editarAgent);
                                        btnEdiar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                editarAgent();
                                            }
                                        });

                                    }
                                }
                                return false;
                            }
                        });

                    } else {
                        Log.d("Error", "onError: " + response.errorBody().string());
                        throw new Exception("Error del servidor");
                    }
                } catch (Throwable t) {
                    try {
                        Log.e("onThrowable", "onThrowable: " + t.toString(), t);
                        Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (Throwable x) {
                        Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Agente>> call, Throwable t) {
                Log.e("onFailure", "onFailure: " + t.toString());
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void goFavoritos(){
        Intent intent = new Intent(HomeActivity.this, FavoriteActivity.class);
        intent.putExtra("user_id",user_id);
        startActivity(intent);
    }

    public void agregarFavorito(){
        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<ResponseMessage> call;

        Log.d(TAG, ""+ user_id);
        call = service.registrarFavorito(user_id, agente_id);
        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                try {
                    int statusCode = response.code();
                    Log.d(TAG, "HTTP STATUS CODE" + statusCode);
                    if (response.isSuccessful()) {
                        ResponseMessage responseMessage = response.body();
                        Log.d(TAG, "response message" + responseMessage);
                        Toast.makeText(HomeActivity.this, "Agregado a favorito", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {
                        Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) ;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        initialize();
    }

    public void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                /* locationManager.removeUpdates(locationListener); */
            }
        } else {
            /* Da problemas con Api 21 */
            /* locationManager.removeUpdates(locationListener); */
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            } else {
                /* Da problemas con Api 21 */
                /* locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener); */
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void callLogout(){
        // remove from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean success = editor.putBoolean("islogged", false).commit();
        // boolean success = editor.clear().commit(); // not recommended
        finish();
    }

    @Override
    public void onRefresh() {
       // swipeLayout.setRefreshing(true);

        //Vamos a simular un refresco con un handle.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                initialize();
                //Se supone que aqui hemos realizado las tareas necesarias de refresco, y que ya podemos ocultar la barra de progreso
                //swipeLayout.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (marker.equals(markerAgente)) {
            final Dialog dialogs = new Dialog(this);
            dialogs.setContentView(R.layout.activity_detalle_banco);
            dialogs.setTitle("Agente");
            dialogs.closeOptionsMenu();
            dialogs.setCancelable(false);
            dialogs.show();

            ImageView btnClose = (ImageView) dialogs.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogs.dismiss();
                }
            });

        }
    }

    public void editarAgent() {
        Intent intent = new Intent(this, EditAgenteActivity.class);
        intent.putExtra("id_agente_edt", agente_id);
        startActivity(intent);
    }

}
