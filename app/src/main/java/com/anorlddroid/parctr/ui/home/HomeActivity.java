package com.anorlddroid.parctr.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;

import com.anorlddroid.parctr.R;
import com.anorlddroid.parctr.model.TrackingItems;
import com.anorlddroid.parctr.model.User;
import com.anorlddroid.parctr.ui.home.addItem.AddItemActivity;
import com.anorlddroid.parctr.ui.home.archive.ArchiveActivity;
import com.anorlddroid.parctr.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private TextView nav_fullnames, nav_email, nav_phonenumber;
    private FirebaseFirestore mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Parcel Tracking ");

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navView = findViewById(R.id.nav_view);

        mDatabase = FirebaseFirestore.getInstance();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView.setNavigationItemSelectedListener(this);

        nav_fullnames = navView.getHeaderView(0).findViewById(R.id.nav_user_fullnames);
        nav_email = navView.getHeaderView(0).findViewById(R.id.nav_user_email);
        nav_phonenumber = navView.getHeaderView(0).findViewById(R.id.nav_phone_number);
        fetchUserData();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
        if (item.getItemId() == R.id.navigation_home) {
            navGraph.setStartDestination(R.id.navigation_home);
            navController.setGraph(navGraph);
        }
        if (item.getItemId() == R.id.navigation_tracklist) {
            navGraph.setStartDestination(R.id.navigation_tracklist);
            navController.setGraph(navGraph);
        }
        if (item.getItemId() == R.id.add_parcel) {
            Intent intent = new Intent(this, AddItemActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.archive) {
            Intent intent = new Intent(this, ArchiveActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fetchUserData(){
        mDatabase.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("userDetails").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()){
                            User user = document.toObject(User.class);
                            nav_fullnames.setText(user.getName());
                            nav_email.setText(user.getEmail());
                            nav_phonenumber.setText(user.getPhone());
                        }
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to retrieve items", Toast.LENGTH_LONG).show();
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });

    }


}