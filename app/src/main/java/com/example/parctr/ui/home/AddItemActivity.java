package com.example.parctr.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.parctr.R;
import com.example.parctr.model.TrackingItems;
import com.example.parctr.ui.registration.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {
    private EditText mParcelID;
    private EditText mParcelType;
    private EditText mSender;
    private EditText mReceiver;
    private EditText mDestination;
    private TextView mDateSendTxv;
    private EditText mStatus;
    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;

    private Button mSave;
    final Calendar mDateSend = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().setTitle("Item");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mParcelID = findViewById(R.id.parcel_id);
        mParcelType = findViewById(R.id.type_parcel);
        mSender = findViewById(R.id.sender);
        mReceiver = findViewById(R.id.receiver);
        mDestination = findViewById(R.id.pick_up_destination);
        mDateSendTxv = findViewById(R.id.date_send);
        mStatus = findViewById(R.id.status);
        mSave = findViewById(R.id.save);
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();

        mSave.setOnClickListener(view -> {
            assert mCurrentUser != null;
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, EEE");
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
            String date = formatter.format(mDateSend.getTime());
            String time = timeFormatter.format(mDateSend.getTime());
            TrackingItems trackingItems = new TrackingItems(
                    mParcelID.getText().toString(),
                    mParcelType.getText().toString(),
                    mSender.getText().toString(),
                    mReceiver.getText().toString(),
                    date,
                    time,
                    "*****",
                    "*****",
                    mDestination.getText().toString(),
                    mStatus.getText().toString()
            );

            mDatabase.collection("tracking_items").document(mCurrentUser.getUid()).collection("items").add(trackingItems)
                    .addOnSuccessListener(documentReference -> Toast.makeText(this, "Item added",
                            Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
            finish();
        });


    }

    public void dateSend(View view) {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mDateSend.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(AddItemActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mDateSend.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mDateSend.set(Calendar.MINUTE, minute);

                        mDateSendTxv.setText(getFormattedDate(mDateSend));

                    }
                }, mDateSend.get(Calendar.HOUR_OF_DAY), mDateSend.get(Calendar.MINUTE), false).show();
            }
        }, mDateSend.get(Calendar.YEAR), mDateSend.get(Calendar.MONTH), mDateSend.get(Calendar.DATE)).show();
    }

    private String getFormattedDate(Calendar date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, EEE HH:mm");
        return formatter.format(date.getTime());
    }
}