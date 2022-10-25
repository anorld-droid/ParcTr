package com.example.parctr.ui.home.trackinglist;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parctr.databinding.FragmentTrackingBinding;
import com.example.parctr.model.TrackingItems;
import com.example.parctr.ui.home.placeholder.PlaceholderContent.PlaceholderItem;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link TrackingItems}.
 * TODO: Replace the implementation with code for your model type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<TrackingItems> mValues;

    public MyItemRecyclerViewAdapter(List<TrackingItems> items) {
        mValues = items;
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
        holder.mTypeOfParcel.setText(mValues.get(position).typeOfParcel);
        holder.mSender.setText(mValues.get(position).sender);
        holder.mReceiver.setText(mValues.get(position).receiver);
        holder.mDateSend.setText(mValues.get(position).dateSend);
        holder.mTimeSend.setText(mValues.get(position).timeSend);
        holder.mPickUpDate.setText(mValues.get(position).pickUpDate);
        holder.mPickUpTime.setText(mValues.get(position).pickUpTime);
        holder.mPickUpDestination.setText(mValues.get(position).pickUpDestination);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TrackingItems mItem;
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
        public final Button mChangeStatus;

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
            mChangeStatus = binding.btnChangeStatus;

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTypeOfParcel.getText() + "'";
        }
    }

    private void generateBarCode(final ViewHolder holder, String id){
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

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

}