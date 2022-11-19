package com.anorlddroid.parctr.ui.home.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anorlddroid.parctr.R;
import com.anorlddroid.parctr.model.map.GetNearbyPlacesData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragment extends Fragment implements OnMapReadyCallback,

        GoogleApiClient.ConnectionCallbacks,

        GoogleApiClient.OnConnectionFailedListener,

        GoogleMap.OnMarkerClickListener,

        LocationListener {



    private GoogleMap mMap;


    private GoogleApiClient mGoogleApiClient;

    double latitude;

    double longitude;

    private int PROXIMITY_RADIUS = 10000;

    Location mLastLocation;

    Marker mCurrLocationMarker;

    LocationRequest mLocationRequest;

    View root;



    public MapFragment() {

        // Required empty public constructor

    }



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_map, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            checkLocationPermission();

        }

        //Check if Google Play Services Available or not

        if (!CheckGooglePlayServices()) {

            Log.d("onCreate", "Finishing test case since Google Play Services are not available");

        }

        else {

            Log.d("onCreate","Google Play Services available.");

        }



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        return root;

    }

    private boolean CheckGooglePlayServices() {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();

        int result = googleAPI.isGooglePlayServicesAvailable(getContext());

        if(result != ConnectionResult.SUCCESS) {

            if(googleAPI.isUserResolvableError(result)) {

                googleAPI.getErrorDialog(getActivity(), result,

                        0).show();

            }

            return false;

        }

        return true;

    }







    /** Called when the map is ready. */

    @Override

    public void onMapReady(GoogleMap map) {

        mMap = map;

        //Initialize Google Play Services

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getContext(),

                    Manifest.permission.ACCESS_FINE_LOCATION)

                    == PackageManager.PERMISSION_GRANTED) {

                buildGoogleApiClient();

                mMap.setMyLocationEnabled(true);

            }

        }

        else {

            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);

        }

        mMap.clear();




    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())

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

        if (ContextCompat.checkSelfPermission(getContext(),

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

        Log.d("onLocationChanged", "entered");



        mLastLocation = location;

        if (mCurrLocationMarker != null) {

            mCurrLocationMarker.remove();

        }



        //Place current location marker

        latitude = location.getLatitude();

        longitude = location.getLongitude();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(latLng);

        markerOptions.title("Current Position");

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        mCurrLocationMarker = mMap.addMarker(markerOptions);




        mMap.setOnMarkerClickListener(this);







        //move map camera

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));



        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f",latitude,longitude));



        //stop location updates

        if (mGoogleApiClient != null) {

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);


        }

        Log.d("onLocationChanged", "Exit");



    }







    @Override

    public void onConnectionFailed(ConnectionResult connectionResult) {



    }



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission(){

        if (ContextCompat.checkSelfPermission(getContext(),

                Manifest.permission.ACCESS_FINE_LOCATION)

                != PackageManager.PERMISSION_GRANTED) {



            // Asking user if explanation is needed

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),

                    Manifest.permission.ACCESS_FINE_LOCATION)) {



                // Show an explanation to the user *asynchronously* -- don't block

                // this thread waiting for the user's response! After the user

                // sees the explanation, try again to request the permission.



                //Prompt the user once explanation has been shown

                ActivityCompat.requestPermissions(getActivity(),

                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},

                        MY_PERMISSIONS_REQUEST_LOCATION);





            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),

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

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_LOCATION: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0

                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                    // permission was granted. Do the

                    // contacts-related task you need to do.

                    if (ContextCompat.checkSelfPermission(getContext(),

                            Manifest.permission.ACCESS_FINE_LOCATION)

                            == PackageManager.PERMISSION_GRANTED) {



                        if (mGoogleApiClient == null) {

                            buildGoogleApiClient();

                        }

                        mMap.setMyLocationEnabled(true);

                    }



                } else {



                    // Permission denied, Disable the functionality that depends on this permission.

                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();

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