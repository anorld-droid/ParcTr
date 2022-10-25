package com.example.parctr.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.parctr.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {
    private EditText mParcelID;
    private EditText mParcelType;
    private EditText mSender;
    private EditText mReceiver;
    private EditText mDestination;
    private TextView mDateSendTxv;

    private Button mSave;
    final Calendar mDateSend = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().setTitle("Item");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mParcelID = findViewById(R.id.parcel_id);
        mParcelType = findViewById(R.id.type_parcel);
        mSender = findViewById(R.id.sender);
        mReceiver = findViewById(R.id.receiver);
        mDestination = findViewById(R.id.pick_up_destination);
        mDateSendTxv = findViewById(R.id.date_send);
        mSave = findViewById(R.id.save);




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
    private  String getFormattedDate(Calendar date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, EEE HH:mm");
        return formatter.format(date.getTime());
    }
}