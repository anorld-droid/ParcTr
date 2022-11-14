package com.example.parctr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackingItems {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("doc_id")
    @Expose
    private String docID;
    @SerializedName("type_parcel")
    @Expose
    private String typeOfParcel;
    @SerializedName("sender")
    @Expose
    private String sender;
    @SerializedName("receiver")
    @Expose
    private String receiver;
    @SerializedName("receiver_phone_number")
    @Expose
    private String receiverPhoneNumber;
    @SerializedName("receiver_id_number")
    @Expose
    private String receiverIDNumber;
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
    private Boolean status;

    public TrackingItems(
            String id, String typeOfParcel, String sender, String receiver, String receiverIDNumber, String receiverPhoneNumber, String dateSend, String timeSend, String pickUpDate, String pickUpTime, String pickUpDestination, Boolean status) {
        this.id = id;
        this.typeOfParcel = typeOfParcel;
        this.sender = sender;
        this.receiver = receiver;
        this.receiverIDNumber = receiverIDNumber;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.dateSend = dateSend;
        this.timeSend = timeSend;
        this.pickUpDate = pickUpDate;
        this.pickUpTime = pickUpTime;
        this.pickUpDestination = pickUpDestination;
        this.status = status;
    }

    public TrackingItems() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
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

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public void setReceiverPhoneNumber(String receiverPhoneNumber) {
        this.receiverPhoneNumber = receiverPhoneNumber;
    }

    public void setReceiverIDNumber(String receiverIDNumber) {
        this.receiverIDNumber = receiverIDNumber;
    }

    public String getReceiverPhoneNumber() {
        return receiverPhoneNumber;
    }

    public String getReceiverIDNumber() {
        return receiverIDNumber;
    }
}
