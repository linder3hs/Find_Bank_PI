package com.linder.find_bank.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.Switch;
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
import com.linder.find_bank.R;
import com.linder.find_bank.model.Agente;
import com.linder.find_bank.model.User;
import com.linder.find_bank.network.ApiService;
import com.linder.find_bank.network.ApiServiceGenerator;
import com.linder.find_bank.respository.AgenteAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, SwipeRefreshLayout.OnRefreshListener {

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
    double speed;
    private String email;
    private ImageView fotoImage;

    //Variales de permiso
    final private int REQUEST_CODE_ASK_PERMISON = 124;
    int hasUbicationPermision;

    //Llamado a los servicios Rest

    private void accsserPermison() {
        hasUbicationPermision = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasUbicationPermision != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISON);
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
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
            Intent intent = new Intent(this, FavoriteActivity.class);
            startActivity(intent);

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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                            @Override
                            public boolean onMarkerClick(Marker marker) {

                                for (Agente agente : agentes){
                                    if (agente.getNombre().equals(marker.getTitle())){

                                        final Dialog dialogs = new Dialog(HomeActivity.this);
                                        dialogs.setContentView(R.layout.activity_detalle_banco);
                                        dialogs.closeOptionsMenu();
                                        dialogs.setCancelable(false);
                                        dialogs.show();
                                        Switch aSwitch = (Switch) dialogs.findViewById(R.id.estadoB);
                                        aSwitch.setEnabled(false);
                                        aSwitch.setClickable(false);
                                        TextView direccion = (TextView) dialogs.findViewById(R.id.direccionAgente);
                                        TextView nombre = (TextView) dialogs.findViewById(R.id.nombreAgente);
                                        nombre.setText(agente.getNombre());
                                        direccion.setText(agente.getDireccion());

                                        ImageView btnClose = (ImageView) dialogs.findViewById(R.id.btnClose);
                                        btnClose.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogs.dismiss();
                                            }
                                        });

                                        Toast.makeText(HomeActivity.this, agente.getNombre(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                return false;
                            }
                        });

                        // AgenteAdapter adapter = (AgenteAdapter) agentesList.getAdapter();
                        // adapter.setAgentes(agentes);
                        // adapter.notifyDataSetChanged();
                    } else {
                        Log.d("Error", "onError: " + response.errorBody().string());
                        throw new Exception("Error del servidor");
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
            public void onFailure(Call<List<Agente>> call, Throwable t) {
                Log.e("onFailure", "onFailure: " + t.toString());
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

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
                        String url = ApiService.API_BASE_URL + "/images/" + user.getImagen();
                        Picasso.with(HomeActivity.this).load(url).into(fotoImage);

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

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


        //Add a place with description
       LatLng cosmo2 = new LatLng(-12.134101, -77.0236405);

        //Valores
      //  Log.d("Lat", String.valueOf(lat));
      //  Log.d("Log", String.valueOf(lng));

       /* markerAgente = googleMap.addMarker(new MarkerOptions()
                .position(cosmo2)
                .title("cosmo2")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.compass)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(cosmo2));*/
        //googleMap.setOnMarkerClickListener(this);
        //  mMap.addMarker(new MarkerOptions().position(agenteSanta).title("Alondras").snippet("Agente BCP"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(cosmo));
       // float zoon = 16;
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cosmo, zoon));

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
                //locationManager.removeUpdates(locationListener);
            }

        } else {
            locationManager.removeUpdates(locationListener);
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            } else {
                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(markerAgente)) {
            final Dialog dialogs = new Dialog(this);
            dialogs.setContentView(R.layout.activity_detalle_banco);
            dialogs.setTitle("Agente");
            dialogs.closeOptionsMenu();
            dialogs.setCancelable(false);
            dialogs.show();
            Switch aSwitch = (Switch) dialogs.findViewById(R.id.estadoB);
            aSwitch.setEnabled(false);
            aSwitch.setClickable(false);

            ImageView btnClose = (ImageView) dialogs.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogs.dismiss();
                }
            });

            }
            return false;
        }

        public void callLogout(){
            // remove from SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            boolean success = editor.putBoolean("islogged", false).commit();
            //boolean success = editor.clear().commit(); // not recommended
            finish();
        }



    @Override
    public void onRefresh() {
       // swipeLayout.setRefreshing(true);

        //Vamos a simular un refresco con un handle.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //initialize();
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
            Switch aSwitch = (Switch) dialogs.findViewById(R.id.estadoB);
            aSwitch.setEnabled(false);
            aSwitch.setClickable(false);

            ImageView btnClose = (ImageView) dialogs.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogs.dismiss();
                }
            });

        }
    }
}
