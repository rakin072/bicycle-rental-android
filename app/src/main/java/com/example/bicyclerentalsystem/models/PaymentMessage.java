package com.example.bicyclerentalsystem.models;

public class PaymentMessage {
    private String messageId;
    private String senderId;
    private String senderEmail;
    private String receiverId;
    private String receiverEmail;
    private String messageType; // "payment" or "overdue_payment"
    private String transactionId;
    private String message;
    private double amount;
    private String rentalId;
    private String bicycleId;
    private String bicycleModel;
    private String status; // "Pending", "Approved", "Rejected"
    private long timestamp;
    private String rejectionReason;

    // Required empty constructor for Firebase
    public PaymentMessage() {
    }

    public PaymentMessage(String messageId, String senderId, String senderEmail, 
                         String receiverId, String receiverEmail, String messageType, 
                         String transactionId, String message, double amount, 
                         String rentalId, String bicycleId, String bicycleModel, 
                         String status, long timestamp, String rejectionReason) {
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
        this.bicycleId = bicycleId;
        this.bicycleModel = bicycleModel;
        this.status = status;
        this.timestamp = timestamp;
        this.rejectionReason = rejectionReason;
    }

    // Business logic methods
    public String getTimeAgo() {
        long diff = System.currentTimeMillis() - timestamp;
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + " day" + (days > 1 ? "s" : "") + " ago";
        if (hours > 0) return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        if (minutes > 0) return minutes + " min" + (minutes > 1 ? "s" : "") + " ago";
        return "Just now";
    }

    public boolean isPaymentType() {
        return "payment".equals(messageType);
    }

    public boolean isOverduePaymentType() {
        return "overdue_payment".equals(messageType);
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

    public String getBicycleId() {
        return bicycleId;
    }

    public String getBicycleModel() {
        return bicycleModel;
    }

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getRejectionReason() {
        return rejectionReason;
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

    public void setBicycleId(String bicycleId) {
        this.bicycleId = bicycleId;
    }

    public void setBicycleModel(String bicycleModel) {
        this.bicycleModel = bicycleModel;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
