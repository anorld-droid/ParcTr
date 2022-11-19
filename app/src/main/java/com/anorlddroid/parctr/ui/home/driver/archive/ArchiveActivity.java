package com.anorlddroid.parctr.ui.home.driver.archive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anorlddroid.parctr.R;
import com.anorlddroid.parctr.model.TrackingItems;
import com.anorlddroid.parctr.ui.home.driver.trackinglist.TrackingListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity {
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Archive");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = findViewById(R.id.loading_bar);
        progressBar.setVisibility(View.VISIBLE);

        List<TrackingItems> trackingItems = new ArrayList<TrackingItems>();

        RecyclerView recyclerView = findViewById(R.id.archived_list);
        TrackingListAdapter trackingListAdapter = new TrackingListAdapter(this, trackingItems);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser mCurrentUser = mAuth.getCurrentUser();

        assert mCurrentUser != null;
        Task<QuerySnapshot> collectionRef = mDatabase.collection("tracking_items").document(mCurrentUser.getUid()).collection("archive").get();
        collectionRef.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()){
                            TrackingItems trackingItem = document.toObject(TrackingItems.class);
                            trackingItem.setDocID(document.getId());
                            trackingItems.add(trackingItem);
                            trackingListAdapter.notifyDataSetChanged();
                            Log.d("TAG", document.getId() + " => " + document.toObject(TrackingItems.class));
                        }
                    }
                    progressBar.setVisibility(View.INVISIBLE);

                } else {
                    Toast.makeText(getApplicationContext(), "Failed to retrieve items", Toast.LENGTH_LONG).show();
                    Log.d("TAG", "Error getting documents: ", task.getException());
                    progressBar.setVisibility(View.GONE);

                }
            }
        });

        // Set the adapter
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(trackingListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}