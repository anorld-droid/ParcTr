package com.anorlddroid.parctr.ui.home.driver.addItem;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anorlddroid.parctr.model.TrackingItems;
import com.anorlddroid.parctr.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 1;
    private static final int REQUEST_LOCATION = 1;
    final Calendar mDateSend = Calendar.getInstance();
    private EditText mParcelID;
    private EditText mParcelType;
    private EditText mSender;
    private EditText mReceiver;
    private EditText mReceiverEmail;
    private EditText mReceiverIDNumber;
    private EditText mDestination;
    private TextView mDateSendTxv;
    private Switch mStatus;
    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;
    private Button mSave;
    private Button mScanCode;

    private FirebaseUser mCurrentUser;


    private double lat;
    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Parcel Tracking ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mParcelID = findViewById(R.id.my_parcel_id);
        mParcelType = findViewById(R.id.type_parcel);
        mSender = findViewById(R.id.sender);
        mReceiver = findViewById(R.id.receiver);
        mReceiverIDNumber = findViewById(R.id.receiver_id);
        mReceiverEmail = findViewById(R.id.receiver_email);
        mDestination = findViewById(R.id.pick_up_destination);
        mDateSendTxv = findViewById(R.id.date_send);
        mStatus = findViewById(R.id.parcelPaidSwitch);
        mSave = findViewById(R.id.save);
        mScanCode = findViewById(R.id.scan_code);
        mCurrentUser= mAuth.getCurrentUser();

        getLocation();

        mSave.setOnClickListener(view -> {

            String parcelID = mParcelID.getText().toString();
            String parcelType = mParcelType.getText().toString();
            String sender = mSender.getText().toString();
            String receiver = mReceiver.getText().toString();
            String receiverEmail = mReceiverEmail.getText().toString();
            String receiverIDNo = mReceiverIDNumber.getText().toString();
            String destination = mDestination.getText().toString();
            if (!parcelID.isEmpty() || !parcelType.isEmpty() || !sender.isEmpty() || !receiver.isEmpty() || !receiverEmail.isEmpty() || !receiverIDNo.isEmpty() || !destination.isEmpty()) {
                String message = "Parcel ID " + parcelID + " has been sent to you by "
                        + sender + " from ParcTr. You will be notified when it is received at our offices in "
                        + destination + " for you to collect.";
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{mReceiverEmail.getText().toString()});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ParcTr, Parcel Delivery.");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);


                emailIntent.setType("message/rfc822");

                try {
                    startActivity(Intent.createChooser(emailIntent,
                            "Send email using..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this,
                            "No email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }


                assert mCurrentUser != null;
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, EEE");
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
                String date = formatter.format(mDateSend.getTime());
                String time = timeFormatter.format(mDateSend.getTime());
                TrackingItems trackingItems = new TrackingItems(
                        mParcelID.getText().toString(),
                        parcelType,
                        sender,
                        receiver,
                        receiverIDNo,
                        receiverEmail,
                        date,
                        time,
                        "*****",
                        "*****",
                        destination,
                        mStatus.isChecked(),
                        lat + " " +lng
                );

                mDatabase.collection("tracking_items").document(mCurrentUser.getUid()).collection("items").add(trackingItems)
                        .addOnSuccessListener(documentReference -> Toast.makeText(this, "Item added",
                                Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
                saveToFirestore(mParcelID.getText().toString(), trackingItems);
                finish();
            } else {
                Toast.makeText(this, "Fill in all the fields.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mScanCode.setOnClickListener(view12 -> {
            if (hasCameraPermission()) {
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setCaptureActivity(BarcodeCaptureActivity.class);
                integrator.setOrientationLocked(false);
                integrator.setPrompt("");
                integrator.initiateScan();
            }
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


    public boolean hasCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_RESULT);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                Toast.makeText(this, getString(R.string.main_menu_scanned_text) + " " + result.getContents(), Toast.LENGTH_SHORT).show();

                    mParcelID.setText(result.getContents());


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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

    private void saveToFirestore(String parcelID, TrackingItems trackingItems){
        String[] name = trackingItems.getReceiver().split(" ");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Parcels")
                .child(name[0]+name[1]);

        reference.child(parcelID).setValue(trackingItems);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                AddItemActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                AddItemActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                lat = locationGPS.getLatitude();
               lng = locationGPS.getLongitude();
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

