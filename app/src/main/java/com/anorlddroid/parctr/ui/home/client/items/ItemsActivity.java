package com.anorlddroid.parctr.ui.home.client.items;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anorlddroid.parctr.R;
import com.anorlddroid.parctr.model.TrackingItems;
import com.anorlddroid.parctr.model.User;
import com.anorlddroid.parctr.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemsActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private List<TrackingItems> trackingItems;
    private ItemsAdapter itemsAdapter;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore mDatabase;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Items");

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        mDatabase = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        fetchUserData();
        trackingItems = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.list);
        itemsAdapter = new ItemsAdapter(this, trackingItems);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);



        mCurrentUser = mAuth.getCurrentUser();

        assert mCurrentUser != null;

        // Set the adapter
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(itemsAdapter);
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
                Intent intent2 = new Intent(ItemsActivity.this, LoginActivity.class);
                startActivity(intent2);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchMyItems() {
        trackingItems.clear();
        String[] name = user.getName().split(" ");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Parcels").child(name[0] + name[1]);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    reference.child(Objects.requireNonNull(dataSnapshot.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            TrackingItems trackingItem = snapshot.getValue(TrackingItems.class);
                            trackingItems.add(trackingItem);
                            itemsAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void fetchUserData() {
        mDatabase.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("userDetails").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    user = document.toObject(User.class);
                                    fetchMyItems();
                                }
                            }
                        } else {
                            Toast.makeText(ItemsActivity.this, "Failed to retrieve items", Toast.LENGTH_LONG).show();
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}