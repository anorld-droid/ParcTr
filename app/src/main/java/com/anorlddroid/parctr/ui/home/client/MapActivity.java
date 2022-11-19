package com.anorlddroid.parctr.ui.home.client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anorlddroid.parctr.R;
import com.anorlddroid.parctr.ui.login.LoginActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static LatLng mDriverLocation = new LatLng(-0.42502836574115077, 36.955733708091024);

    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private int PROXIMITY_RADIUS = 10000;
    private Marker mDriverMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Driver Location");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            checkLocationPermission();

        }

        String location = getIntent().getExtras().getString("location");
        String[] mLatLng = location.split(" ");
        mDriverLocation = new LatLng(Double.parseDouble(mLatLng[0]), Double.parseDouble(mLatLng[1]));

        //Check if Google Play Services Available or not

        if (!CheckGooglePlayServices()) {

            Log.d("onCreate", "Finishing test case since Google Play Services are not available");

        } else {

            Log.d("onCreate", "Google Play Services available.");

        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent2 = new Intent(MapActivity.this, LoginActivity.class);
                startActivity(intent2);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean CheckGooglePlayServices() {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();

        int result = googleAPI.isGooglePlayServicesAvailable(this);

        if (result != ConnectionResult.SUCCESS) {

            if (googleAPI.isUserResolvableError(result)) {

                googleAPI.getErrorDialog(this, result,

                        0).show();

            }

            return false;

        }

        return true;

    }


    /**
     * Called when the map is ready.
     */

    @Override

    public void onMapReady(GoogleMap map) {

        map.addMarker(new MarkerOptions()
                .position(mDriverLocation)
                .title("Driver's Location"));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(mDriverLocation, 13));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mDriverLocation)      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)

                .addConnectionCallbacks(this)

                .addOnConnectionFailedListener(this)

                .addApi(LocationServices.API)

                .build();

        mGoogleApiClient.connect();

    }

    @Override

    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(1000);

        mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,

                Manifest.permission.ACCESS_FINE_LOCATION)

                == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }

    }

    @Override

    public void onConnectionSuspended(int i) {


    }

    @Override

    public void onLocationChanged(Location location) {


    }

    @Override

    public void onConnectionFailed(ConnectionResult connectionResult) {


    }

    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this,

                Manifest.permission.ACCESS_FINE_LOCATION)

                != PackageManager.PERMISSION_GRANTED) {


            // Asking user if explanation is needed

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,

                    Manifest.permission.ACCESS_FINE_LOCATION)) {


                // Show an explanation to the user *asynchronously* -- don't block

                // this thread waiting for the user's response! After the user

                // sees the explanation, try again to request the permission.


                //Prompt the user once explanation has been shown

                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},

                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},

                        MY_PERMISSIONS_REQUEST_LOCATION);

            }

            return false;

        } else {

            return true;

        }

    }


    @Override

    public void onRequestPermissionsResult(int requestCode,

                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_LOCATION: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0

                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted. Do the

                    // contacts-related task you need to do.

                    if (ContextCompat.checkSelfPermission(this,

                            Manifest.permission.ACCESS_FINE_LOCATION)

                            == PackageManager.PERMISSION_GRANTED) {


                        if (mGoogleApiClient == null) {

                            buildGoogleApiClient();

                        }

                        mMap.setMyLocationEnabled(true);

                    }


                } else {


                    // Permission denied, Disable the functionality that depends on this permission.

                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();

                }

                return;

            }


            // other 'case' lines to check for other permissions this app might request.

            // You can add here other case statements according to your requirement.

        }

    }

    @Override

    public boolean onMarkerClick(Marker marker) {

        return false;
    }

}
