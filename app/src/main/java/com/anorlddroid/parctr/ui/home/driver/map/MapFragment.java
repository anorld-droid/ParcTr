package com.anorlddroid.parctr.ui.home.driver.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anorlddroid.parctr.R;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback,

        GoogleApiClient.ConnectionCallbacks,

        GoogleApiClient.OnConnectionFailedListener,

        GoogleMap.OnMarkerClickListener,

        LocationListener {


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    double latitude;
    double longitude;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    View root;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private int PROXIMITY_RADIUS = 10000;
    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private TextView added, delivered, picked;

    public MapFragment() {

        // Required empty public constructor

    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_map, container, false);
        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = mAuth.getCurrentUser();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            checkLocationPermission();

        }

        added = root.findViewById(R.id.added);
        delivered = root.findViewById(R.id.delivered);
        picked = root.findViewById(R.id.picked_txt);
        getAddedItems();
        getDeliveredItems();
        getPickedItems();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        return root;

    }

    private int getAddedItems() {
        List<Integer> trackingItems = new ArrayList<Integer>();
        Task<QuerySnapshot> collectionRef = mDatabase.collection("tracking_items").document(mCurrentUser.getUid()).collection("items").get();
        collectionRef.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int sum = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            sum++;
                            Log.d("!!!!!", "1");
                        }

                    }
                    added.setText(String.valueOf(sum));

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to retrieve items", Toast.LENGTH_LONG).show();
                    Log.d("TAG", "Error getting documents: ", task.getException());

                }
            }
        });
        return trackingItems.size();
    }

    private int getDeliveredItems() {
        List<Integer> trackingItems = new ArrayList<Integer>();
        Task<QuerySnapshot> collectionRef = mDatabase.collection("tracking_items").document(mCurrentUser.getUid()).collection("delivered").get();
        collectionRef.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int sum = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            sum++;
                        }

                    }
                    delivered.setText(String.valueOf(sum));
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to retrieve items", Toast.LENGTH_LONG).show();
                    Log.d("TAG", "Error getting documents: ", task.getException());

                }
            }
        });
        return trackingItems.size();
    }

    private int getPickedItems() {
        List<Integer> trackingItems = new ArrayList<Integer>();
        Task<QuerySnapshot> collectionRef = mDatabase.collection("tracking_items").document(mCurrentUser.getUid()).collection("archive").get();
        collectionRef.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int sum = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            sum++;
                        }

                    }
                    picked.setText(String.valueOf(sum));
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to retrieve items", Toast.LENGTH_LONG).show();
                    Log.d("TAG", "Error getting documents: ", task.getException());

                }
            }
        });
        return trackingItems.size();
    }

    private boolean CheckGooglePlayServices() {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();

        int result = googleAPI.isGooglePlayServicesAvailable(getContext());

        if (result != ConnectionResult.SUCCESS) {

            if (googleAPI.isUserResolvableError(result)) {

                googleAPI.getErrorDialog(getActivity(), result,

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

        mMap = map;

        //Initialize Google Play Services

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getContext(),

                    Manifest.permission.ACCESS_FINE_LOCATION)

                    == PackageManager.PERMISSION_GRANTED) {

                buildGoogleApiClient();

                mMap.setMyLocationEnabled(true);

            }

        } else {

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


        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f", latitude, longitude));


        //stop location updates

        if (mGoogleApiClient != null) {

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);


        }

        Log.d("onLocationChanged", "Exit");


    }

    @Override

    public void onConnectionFailed(ConnectionResult connectionResult) {


    }

    public boolean checkLocationPermission() {

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