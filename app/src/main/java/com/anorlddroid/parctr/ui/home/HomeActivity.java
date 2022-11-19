package com.anorlddroid.parctr.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.anorlddroid.parctr.R;
import com.anorlddroid.parctr.ui.MainActivity;
import com.anorlddroid.parctr.ui.home.archive.ArchiveActivity;
import com.anorlddroid.parctr.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity  {
    public String FRAGMENT = "FRAGMENT_DESTINATION";
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Parcel Tracking ");

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();





        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
        navController.setGraph(navGraph);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
//                if (navDestination.getId() == R.id.logout){
                    FirebaseAuth.getInstance().signOut();
                    finish();
//                }
            }
        });
        NavigationUI.setupWithNavController(navView, navController);
    }

}