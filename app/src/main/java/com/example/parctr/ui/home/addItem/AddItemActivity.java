package com.example.parctr.ui.home.addItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.parctr.R;
import com.example.parctr.model.TrackingItems;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {
    private EditText mParcelID;
    private EditText mParcelType;
    private EditText mSender;
    private EditText mReceiver;
    private  EditText mReceiverPhoneNumber;
    private  EditText mReceiverIDNumber;
    private EditText mDestination;
    private TextView mDateSendTxv;
    private Switch mStatus;
    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;

    private Button mSave;
    private Button mScanCode;
    final Calendar mDateSend = Calendar.getInstance();
    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().setTitle("New Tracking Item");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mParcelID = findViewById(R.id.parcel_id);
        mParcelType = findViewById(R.id.type_parcel);
        mSender = findViewById(R.id.sender);
        mReceiver = findViewById(R.id.receiver);
        mReceiverIDNumber = findViewById(R.id.receiver_id);
        mReceiverPhoneNumber = findViewById(R.id.receiver_phone_number);
        mDestination = findViewById(R.id.pick_up_destination);
        mDateSendTxv = findViewById(R.id.date_send);
        mStatus = findViewById(R.id.parcelPaidSwitch);
        mSave = findViewById(R.id.save);
        mScanCode = findViewById(R.id.scan_code);
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
                    mReceiverIDNumber.getText().toString(),
                    mReceiverPhoneNumber.getText().toString(),
                    date,
                    time,
                    "*****",
                    "*****",
                    mDestination.getText().toString(),
                    mStatus.isChecked()
            );

            mDatabase.collection("tracking_items").document(mCurrentUser.getUid()).collection("items").add(trackingItems)
                    .addOnSuccessListener(documentReference -> Toast.makeText(this, "Item added",
                            Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
            finish();
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
            if (result.getContents() != null ) {
                Toast.makeText(this, getString(R.string.main_menu_scanned_text) + " " + result.getContents(), Toast.LENGTH_SHORT).show();
                mParcelID.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}