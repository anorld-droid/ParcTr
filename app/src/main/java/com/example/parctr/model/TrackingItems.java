package com.example.parctr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackingItems {
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("type_parcel")
        @Expose
        private String typeOfParcel;
        @SerializedName("sender")
        @Expose
        private String sender;
        @SerializedName("receiver")
        @Expose
        private String receiver;
        @SerializedName("date_send")
        @Expose
        private String dateSend;
        @SerializedName("time_send")
        @Expose
        private String timeSend;
        @SerializedName("pick_up_date")
        @Expose
        private String pickUpDate;
        @SerializedName("pick_up_time")
        @Expose
        private String pickUpTime;
        @SerializedName("pick_up_destination")
        @Expose
        private String pickUpDestination;
    @SerializedName("status")
    @Expose
    private String status;

    public TrackingItems(
            String id, String typeOfParcel, String sender, String receiver, String dateSend, String timeSend, String pickUpDate, String pickUpTime, String pickUpDestination, String status) {
        this.id = id;
        this.typeOfParcel = typeOfParcel;
        this.sender = sender;
        this.receiver = receiver;
        this.dateSend = dateSend;
        this.timeSend = timeSend;
        this.pickUpDate = pickUpDate;
        this.pickUpTime = pickUpTime;
        this.pickUpDestination = pickUpDestination;
        this.status = status;
    }

    public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }




    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPickUpDestination() {
        return pickUpDestination;
    }

    public void setPickUpDestination(String pickUpDestination) {
        this.pickUpDestination = pickUpDestination;
    }

    public String getPickUpTime() {
        return pickUpTime;
    }

    public void setPickUpTime(String pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    public String getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(String pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public String getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(String timeSend) {
        this.timeSend = timeSend;
    }

    public String getDateSend() {
        return dateSend;
    }

    public void setDateSend(String dateSend) {
        this.dateSend = dateSend;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTypeOfParcel() {
        return typeOfParcel;
    }

    public void setTypeOfParcel(String typeOfParcel) {
        this.typeOfParcel = typeOfParcel;
    }
}