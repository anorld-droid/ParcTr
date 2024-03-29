package com.anorlddroid.parctr.ui.home.driver.trackinglist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anorlddroid.parctr.R;
import com.anorlddroid.parctr.model.TrackingItems;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class TrackingFragment extends Fragment {
    private ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tracking_list, container, false);


        progressBar = view.findViewById(R.id.progress_bar);
        Button mUpdateLocation = view.findViewById(R.id.update_location);

        progressBar.setVisibility(View.VISIBLE);

        List<TrackingItems> trackingItems = new ArrayList<TrackingItems>();

        RecyclerView recyclerView = view.findViewById(R.id.list);
        TrackingListAdapter trackingListAdapter = new TrackingListAdapter(getContext(), trackingItems);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());

        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser mCurrentUser = mAuth.getCurrentUser();

        assert mCurrentUser != null;
        Task<QuerySnapshot> collectionRef = mDatabase.collection("tracking_items").document(mCurrentUser.getUid()).collection("items").get();
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
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to retrieve items", Toast.LENGTH_LONG).show();
                    Log.d("TAG", "Error getting documents: ", task.getException());
                    progressBar.setVisibility(View.GONE);

                }
            }
        });

        // Set the adapter
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(trackingListAdapter);

        mUpdateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (trackingItems != null) {
                    for (TrackingItems trackingItem : trackingItems
                    ) {
                        trackingListAdapter.updateLocation(trackingItem);
                    }

                }
            }
        });

        return view;

    }
}