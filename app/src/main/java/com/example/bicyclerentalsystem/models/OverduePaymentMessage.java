package com.example.bicyclerentalsystem.models;

public class OverduePaymentMessage {
    private String messageId;
    private String senderId;
    private String senderEmail;
    private String receiverId;
    private String receiverEmail;
    private String messageType;
    private String transactionId;
    private String message;
    private double amount;
    private String rentalId;
    private String status;
    private long timestamp;

    // Required empty constructor for Firebase
    public OverduePaymentMessage() {
    }

    public OverduePaymentMessage(String messageId, String senderId, String senderEmail, 
                                String receiverId, String receiverEmail, String messageType, 
                                String transactionId, String message, double amount, 
                                String rentalId, String status, long timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.senderEmail = senderEmail;
        this.receiverId = receiverId;
        this.receiverEmail = receiverEmail;
        this.messageType = messageType;
        this.transactionId = transactionId;
        this.message = message;
        this.amount = amount;
        this.rentalId = rentalId;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters
    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getMessage() {
        return message;
    }

    public double getAmount() {
        return amount;
    }

    public String getRentalId() {
        return rentalId;
    }

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
