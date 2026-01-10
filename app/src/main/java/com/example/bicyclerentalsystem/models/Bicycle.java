package com.example.bicyclerentalsystem.models;

public class Bicycle {
    private String bicycleId;
    private String model;
    private String type;
    private String ownerId;
    private String ownerEmail;
    private boolean isAvailable;

    // Required empty constructor for Firebase
    public Bicycle() {
    }

    public Bicycle(String bicycleId, String model, String type, String ownerId, String ownerEmail, boolean isAvailable) {
        this.bicycleId = bicycleId;
        this.model = model;
        this.type = type;
        this.ownerId = ownerId;
        this.ownerEmail = ownerEmail;
        this.isAvailable = isAvailable;
    }

    // Getters
    public String getBicycleId() {
        return bicycleId;
    }

    public String getModel() {
        return model;
    }

    public String getType() {
        return type;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    // Setters
    public void setBicycleId(String bicycleId) {
        this.bicycleId = bicycleId;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}