package com.example.parctr.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.parctr.R;
import com.example.parctr.ui.home.HomeActivity;
import com.example.parctr.ui.home.addItem.AddItemActivity;
import com.example.parctr.ui.home.archive.ArchiveActivity;
import com.example.parctr.ui.home.map.Location;
import com.example.parctr.ui.home.map.MapFragment;
import com.example.parctr.ui.home.trackinglist.TrackingFragment;
import com.example.parctr.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    public  String FRAGMENT = "FRAGMENT_DESTINATION";
    public  String FRAGMENT_2 = "FRAGMENT_DESTINATION_2";
    private Button mAddParcel, mTracking, mUpdateParcelStatus, mArchive, mLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAddParcel = findViewById(R.id.add_parcel_btn);
        mTracking = findViewById(R.id.tracking_btn);
        mUpdateParcelStatus = findViewById(R.id.update_parcel_btn);
        mArchive = findViewById(R.id.archive_btn);
        mLogout = findViewById(R.id.log_out);


        mAddParcel.setOnClickListener(view -> {
            Intent transactionIntent = new Intent(MainActivity.this, AddItemActivity.class);
            startActivity(transactionIntent);
        });
        mTracking.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, HomeActivity.class);
            i.putExtra("frgToLoad", FRAGMENT_2);
            startActivity(i);
        });
        mUpdateParcelStatus.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, HomeActivity.class);
            i.putExtra("frgToLoad", FRAGMENT);
            startActivity(i);
        });
        mArchive.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, ArchiveActivity.class);
            startActivity(i);
        });
        mLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent transactionIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(transactionIntent);
            finish();
        });
    }
}