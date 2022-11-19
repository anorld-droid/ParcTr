package com.anorlddroid.parctr.ui.home.client.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.anorlddroid.parctr.R;
import com.anorlddroid.parctr.model.TrackingItems;
import com.anorlddroid.parctr.ui.home.client.MapActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

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

    public ItemsAdapter(Context context, List<TrackingItems> items) {
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
    public ItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ItemsAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final ItemsAdapter.ViewHolder holder, int position) {
        holder.mIdView.setText(mValues.get(position).getId());
        generateBarCode(holder, mValues.get(position).getId());
        holder.mTypeOfParcel.setText(mValues.get(position).getTypeOfParcel());
        holder.mSender.setText(mValues.get(position).getSender());

        if (!Objects.equals(mValues.get(holder.getLayoutPosition()).getPickUpTime(), "*****")) {
            holder.mDelivered.setVisibility(View.GONE);
        }
        mCurrentUser = mAuth.getCurrentUser();
        holder.mDelivered.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapActivity.class);
                intent.putExtra("location", mValues.get(holder.getLayoutPosition()).getDriverLocation());
                context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }


    private void generateBarCode(final ItemsAdapter.ViewHolder holder, String id) {
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
        public final Button mDelivered;


        public ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.parcel_id);
            mBarcode = view.findViewById(R.id.bar_code);
            mTypeOfParcel = view.findViewById(R.id.type_parcel_value);
            mSender = view.findViewById(R.id.sender_value);


            mDelivered = view.findViewById(R.id.locate_driver);

            mDatabase = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTypeOfParcel.getText() + "'";
        }
    }


}