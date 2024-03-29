package com.anorlddroid.parctr.ui.home.driver.trackinglist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.anorlddroid.parctr.databinding.FragmentTrackingBinding;
import com.anorlddroid.parctr.model.TrackingItems;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * {@link RecyclerView.Adapter} that can display a {@link TrackingItems}.
 * TODO: Replace the implementation with code for your model type.
 */
public class TrackingListAdapter extends RecyclerView.Adapter<TrackingListAdapter.ViewHolder> {

    /**************************************************************
     * getting from com.google.zxing.client.android.encode.QRCodeEncoder
     *
     * See the sites below
     * http://code.google.com/p/zxing/
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
     */

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private final List<TrackingItems> mValues;
    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private Context context;
    private double lat;
    private double lng;

    public TrackingListAdapter(Context context, List<TrackingItems> items) {
        this.context = context;
        mValues = items;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentTrackingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getId());
        generateBarCode(holder, mValues.get(position).getId());
        holder.mTypeOfParcel.setText(mValues.get(position).getTypeOfParcel());
        holder.mSender.setText(mValues.get(position).getSender());
        holder.mReceiver.setText(String.valueOf(mValues.get(position).getReceiver() + " " + mValues.get(position).getReceiverIDNumber()));
        holder.mDateSend.setText(mValues.get(position).getDateSend());
        holder.mTimeSend.setText(mValues.get(position).getTimeSend());
        holder.mPickUpDate.setText(mValues.get(position).getPickUpDate());
        holder.mPickUpTime.setText(mValues.get(position).getPickUpTime());
        holder.mPickUpDestination.setText(mValues.get(position).getPickUpDestination());
        holder.mParcelPickedSwitch.setChecked(!Objects.equals(mValues.get(position).getPickUpTime(), "*****"));

        if (!Objects.equals(mValues.get(holder.getLayoutPosition()).getPickUpTime(), "*****")) {
            holder.mDelivered.setVisibility(View.GONE);
        }
        mCurrentUser = mAuth.getCurrentUser();
        holder.mDelivered.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                sendEmail(mValues.get(holder.getLayoutPosition()).getId(), mValues.get(holder.getLayoutPosition()).getPickUpDestination(), mValues.get(holder.getLayoutPosition()).getReceiverPhoneNumber(), view.getContext());
                Toast.makeText(view.getContext(), "Receiver notified", Toast.LENGTH_LONG).show();
                deliveredItem(mValues.get(holder.getLayoutPosition()), context);
                notifyDataSetChanged();
            }
        });

        holder.mParcelPickedSwitch.setOnClickListener(view -> {
            if (Objects.equals(mValues.get(holder.getLayoutPosition()).getPickUpTime(), "*****")) {
                assert mCurrentUser != null;
                DocumentReference trackingItem = mDatabase.collection("tracking_items").document(mCurrentUser.getUid()).collection("items").document(mValues.get(holder.getLayoutPosition()).getDocID());
                trackingItem.get().addOnSuccessListener(documentSnapshot -> {
                    TrackingItems trIt = documentSnapshot.toObject(TrackingItems.class);
                    assert trIt != null;
                    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, EEE");
                    SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
                    Date now = new Date();
                    String date = formatter.format(now);
                    String time = timeFormatter.format(now);
                    Boolean status = !(trIt.getStatus());
                    trackingItem
                            .update("status", status, "pickUpDate", date, "pickUpTime", time)
                            .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully updated!"))
                            .addOnFailureListener(e -> Log.w("TAG", "Error updating document", e));
                    trIt.setDocID(mValues.get(holder.getLayoutPosition()).getDocID());
                    trIt.setStatus(status);
                    trIt.setPickUpDate(date);
                    trIt.setPickUpTime(time);
                    Toast.makeText(view.getContext(), "Pick up time updated", Toast.LENGTH_LONG).show();
                    archiveItem(trIt, view.getContext());
                    holder.mDelivered.setVisibility(View.GONE);
                    notifyDataSetChanged();
                });

            } else {
                Toast.makeText(view.getContext(), "You can't update an archived item.", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getLocation(Context context) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(context, "Location permission denied.", Toast.LENGTH_SHORT).show();
            return;
        }
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (locationGPS != null) {
            lat = locationGPS.getLatitude();
            lng = locationGPS.getLongitude();
        }
    }

    public void updateLocation(TrackingItems trackingItem) {
        getLocation(context);
        String[] name = trackingItem.getReceiver().split(" ");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Parcels")
                .child(name[0] + name[1]);
        reference.child(trackingItem.getId()).child("driverLocation").setValue(lat + " " + lng);
    }

    private void sendEmail(String parcelID, String destination, String receiverEmail, Context context) {
        String message = "Parcel ID " + parcelID + " has been received at ParcTr  "
                + destination + " office. You can now collect it anytime with your National ID.";
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{receiverEmail});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ParcTr, Parcel Delivery.");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        emailIntent.setType("message/rfc822");

        try {
            context.startActivity(Intent.createChooser(emailIntent,
                    "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context,
                    "No email clients installed.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void archiveItem(TrackingItems trackingItem, Context context) {
        mDatabase.collection("tracking_items").document(mCurrentUser.getUid()).collection("archive").add(trackingItem)
                .addOnSuccessListener(documentReference -> Toast.makeText(context, "Item archived",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w("TAG", "Error writing document"));
        mDatabase.collection("tracking_items").document(mCurrentUser.getUid()).collection("items").document(trackingItem.getDocID()).delete()
                .addOnSuccessListener(documentReference -> {
                })
                .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
    }

    private void deliveredItem(TrackingItems trackingItem, Context context) {
        mDatabase.collection("tracking_items").document(mCurrentUser.getUid()).collection("delivered").add(trackingItem)
                .addOnSuccessListener(documentReference -> Toast.makeText(context, "Item added to delivered",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w("TAG", "Error writing document"));
    }

    private void generateBarCode(final ViewHolder holder, String id) {
        // barcode model
        // barcode image
        Bitmap bitmap = null;
        try {
            bitmap = encodeAsBitmap(id);
            holder.mBarcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    Bitmap encodeAsBitmap(String contents) throws WriterException {
        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contents, BarcodeFormat.CODE_128, 275, 116, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final ImageView mBarcode;
        public final TextView mTypeOfParcel;
        public final TextView mSender;
        public final TextView mReceiver;
        public final TextView mDateSend;
        public final TextView mTimeSend;
        public final TextView mPickUpDate;
        public final TextView mPickUpTime;
        public final TextView mPickUpDestination;
        public final Switch mParcelPickedSwitch;
        public final Button mDelivered;

        public TrackingItems mItem;



        public ViewHolder(FragmentTrackingBinding binding) {
            super(binding.getRoot());
            mIdView = binding.parcelId;
            mBarcode = binding.barCode;
            mTypeOfParcel = binding.typeParcelValue;
            mSender = binding.senderValue;
            mReceiver = binding.receiverValue;
            mDateSend = binding.dateSend;
            mTimeSend = binding.timeSend;
            mPickUpDate = binding.pickUpDate;
            mPickUpTime = binding.pickUpTime;
            mPickUpDestination = binding.pickUpDestination;
            mParcelPickedSwitch = binding.parcelPaidSwitch;

            mDelivered = binding.delivered;


            mDatabase = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTypeOfParcel.getText() + "'";
        }
    }

}