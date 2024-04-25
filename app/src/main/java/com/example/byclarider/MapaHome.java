package com.example.byclarider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LocaleManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.byclarider.modelo.Reportes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.byclarider.modelo.AuthProvider;


import java.util.Arrays;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

public class MapaHome extends AppCompatActivity implements OnMapReadyCallback{
    FirebaseAuth mAuth;
    AuthProvider mAuthProvider;
    //Mapa
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocation;
    private final static int LOCATION_REQUEST_CODE=1;
    private final static int SETTINGS_REQUEST_CODE=2;

    //Boton buscar ruta origen y destino
    private Button btnOrigenDestino;
    private Button btnIrReportes;
    private LatLng mOriginLatLng;
    private String mDestination;
    private LatLng mDestinationLatLng;
    //Autocomplete
    private PlacesClient mPlaces;
    private AutocompleteSupportFragment mAutocomplete;
    private String mOrigin;

    //hecho 10/04/2024
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location: locationResult.getLocations()){
                if(getApplicationContext() != null){
                    //OBTENER LA LOCALIZACION DEL USUARIO EN TIEMPO REAL
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(15f)
                                    .build()
                    ));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_home);
        //Ubicación
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        //Mapa
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync( this);
        //autocomplete
        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(),getResources().getString(R.string.google_maps_key));
        }

        mPlaces = Places.createClient(this);
        mAutocomplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placeAutocompleteOrigin);
        mAutocomplete.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));
        mAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {

            }
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mOrigin = place.getName();
                mOriginLatLng = place.getLatLng();
                Log.d("PLACE","Name: "+mOrigin);
                Log.d("PLACE","Lat: "+mOriginLatLng.latitude);
                Log.d("PLACE","Lng: "+mOriginLatLng.longitude);
            }
        });

        //Boton ir actividad reportes
        FloatingActionButton btnIrReportes=(FloatingActionButton)findViewById(R.id.btnIrReportes);

        btnIrReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapaHome.this, Reportes.class);
                startActivity(intent);
                finish();
            }
        });

        //Autenticación
        mAuth = FirebaseAuth.getInstance();
        mAuthProvider = new AuthProvider();

        //Menú - Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Origen y destino
        FloatingActionButton btnOrigenDestino = (FloatingActionButton)findViewById(R.id.btnOrigenDestino);
        btnOrigenDestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDriver();
            }
        });

        //mButtonCerrarSecion = findViewById(R.id.btnCerrarSecion);
        /*
        mButtonCerrarSecion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });*/
    }
    /*
    origen y destino
    */
    private void requestDriver() {
        //Validar si los campos están vacios o no
        if(mOriginLatLng != null && mDestinationLatLng != null){
            Intent intent = new Intent(MapaHome.this, DetailRequestActivity.class);
            intent.putExtra("origin_lat",mOriginLatLng.latitude);
            intent.putExtra("origin_lng",mOriginLatLng.longitude);
            intent.putExtra("destination_lat",mDestinationLatLng.latitude);
            intent.putExtra("destination_lng",mDestinationLatLng.longitude);
            startActivity(intent);
        }else{
            Toast.makeText(this,"Seleccione una dirección",Toast.LENGTH_SHORT).show();
        }
    }

    //Menú de opciones de 3 puntos
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    //opciones
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout)
            logout();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            irMain();
        }
    }

    /*
    private void logout() {
        mAuth.signOut();
        irMain();
    }
    */
    private void irMain() {
        Intent intent = new Intent(MapaHome.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Visualización de mapa
    //Dentro del mapa muestra la ubicación del usuario
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //Geolocalizacion
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5);
        starLocation();
    }

    //Método necesario para la geolocalización
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    if(gpsActived()){
                        fusedLocation.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }else{
                        //showAlertDialogNOGPS();
                        System.out.println("Prueba");
                    }
                }else{
                    checkLocationPermissions();
                }
            }else{
                checkLocationPermissions();
            }
        }
    }

    @Override
    @SuppressLint("MissingPermission")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SETTINGS_REQUEST_CODE && gpsActived()){
            fusedLocation.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }else{
            //showAlertDialogNOGPS();
            System.out.println("Prueba");
        }
    }

    //Método para ir a la configuración para activar el GPS
    /*private void showAlertDialogNOGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa la ubicación para continuar")
                .setPositiveButton("Configuracion", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }*/

    //Método para validar si el GPS esta activo o no
    private boolean gpsActived(){
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActive=true;
        }
        return isActive;
    }

    //Permisos de localización
    private void checkLocationPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere los permisos de ubicación para ser utilizada")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                ActivityCompat.requestPermissions(MapaHome.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(MapaHome.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    //Ubicación del usuario, con y sin permisos de ubicación
    private void starLocation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                if(gpsActived()){
                    fusedLocation.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                    mMap.setMyLocationEnabled(true);
                }else{
                    //showAlertDialogNOGPS();
                    System.out.println("Prueba");
                }
            }
            else{
                checkLocationPermissions();
            }
        }else{
            if(gpsActived()){
                fusedLocation.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }else{
                //showAlertDialogNOGPS();
                System.out.println("Prueba");
            }
        }
    }

    void logout(){
        mAuthProvider.logout();
        Intent intent = new Intent(MapaHome.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}