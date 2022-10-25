package com.example.parctr.ui.home.trackinglist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parctr.R;
import com.example.parctr.model.TrackingItems;
import com.example.parctr.ui.home.AddItemActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class TrackingFragment extends Fragment {
    private RecyclerView recyclerView;
    private TrackingListAdapter trackingListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tracking_list, container, false);

        FloatingActionButton mFABAddItem = view.findViewById(R.id.add_item);
        List<TrackingItems> trackingItems = new ArrayList<TrackingItems>();
        TrackingItems trackingItems1 = new TrackingItems("11189888757",
                "Laptop Hp Pavillion ", "Elijah Wanyama", "Paul, Brian", "Oct 22, Sun", "05:00am", "Oct 23, Mon", "05:00pm", "Nyeri, DeKUT", "Pending"
        );
        TrackingItems trackingItems2 = new TrackingItems("11112888757",
                "Samsung Galaxy A20s", "Elijah Wanyama", "Paul, Brian", "Oct 24, Sun", "03:00pm", "Oct 25, Mon", "05:00am", "Nyeri, DeKUT", "Pending"
        );
        trackingItems.add(trackingItems1);
        trackingItems.add(trackingItems2);
        // Set the adapter
        recyclerView = view.findViewById(R.id.list);
        trackingListAdapter = new TrackingListAdapter(trackingItems);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(trackingListAdapter);

        mFABAddItem.setOnClickListener(view1 -> {
            Intent transactionIntent = new Intent(getContext(), AddItemActivity.class);
            startActivity(transactionIntent);
        });
        return view;

    }
}