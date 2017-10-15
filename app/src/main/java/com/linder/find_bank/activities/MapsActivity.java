package com.linder.find_bank.activities;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.linder.find_bank.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = MapsActivity.class.getSimpleName();
    // SharedPreferences
    private SharedPreferences sharedPreferences;
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
        setContentView(R.layout.activity_maps);

        accsserPermison();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (status == ConnectionResult.SUCCESS) {


            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(MapsActivity.this, "Revise su conexión", Toast.LENGTH_LONG).show();
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity) getApplicationContext(), 10);
            dialog.setTitle("Sin conexión");
            dialog.show();

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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
