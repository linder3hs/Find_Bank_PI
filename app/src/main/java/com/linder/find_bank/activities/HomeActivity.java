package com.linder.find_bank.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.linder.find_bank.R;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    private static final String TAG = HomeActivity.class.getSimpleName();

    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private GoogleMap mMap;


    //Variales de permiso
    final private int REQUEST_CODE_ASK_PERMISON = 124;
    int hasUbicationPermision;

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
        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // get username from SharedPreferences
        String corroUser = getIntent().getExtras().getString("correo");
        String username = sharedPreferences.getString("username", null);
        Log.d(TAG, "username: " + username);
        Log.d(TAG, "correo: " + corroUser);
        NavigationView navigationView2 = (NavigationView) findViewById(R.id.nav_view);
        TextView correo = (TextView) navigationView2.getHeaderView(0).findViewById(R.id.correoUser);
        correo.setText(corroUser);


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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Dialog dialogs = new Dialog(this);
            dialogs.setContentView(R.layout.activity_nosotros);
            dialogs.setTitle("Nosotros");
            dialogs.show();
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
        } else if (id == R.id.nav_favoritos) {
            // Intent intent = new Intent(HomeActivity.this, DetalleBancoActivity.class);
            // startActivity(intent);

        } else if (id == R.id.nav_nosotros) {
            //Dialog dialogs = new Dialog(this);
            //dialogs.setContentView(R.layout.activity_nosotros);
            //  dialogs.setTitle("Nosotros");
            //dialogs.show();
        } else if (id == R.id.salir) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            boolean success = editor.putBoolean("islogged", false).commit();
            // boolean success = editor.clear().commit(); // not recommended
            Intent intent1 = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent1);
            finish();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

        // Add a place for me
        LatLng agenre = new LatLng(-12.0443305, -76.952573);
        LatLng agenteSanta = new LatLng(-12.0387409, -76.999248);
        //Add a place with description
        LatLng cosmo = new LatLng(-12.0443305, -76.999248);
        Marker cosmos = mMap.addMarker(new MarkerOptions()
                .position(cosmo)
                .title("Mi casa")
                .snippet("Mensajito: Aca vivo"));

        mMap.addMarker(new MarkerOptions().position(agenteSanta).title("Alondras").snippet("Agente BCP"));
        mMap.addMarker(new MarkerOptions().position(agenre).title("Lugar2").snippet("Mensaje nº2"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(agenre));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(agenteSanta));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cosmo));


        float zoon = 16;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(agenteSanta, zoon
        ));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cosmo, zoon));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(agenre, zoon));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cosmo, zoon));

    }
}
