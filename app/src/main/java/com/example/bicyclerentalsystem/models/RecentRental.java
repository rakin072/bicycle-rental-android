package com.example.bicyclerentalsystem.models;

public class RecentRental {
    private String bicycleModel;
    private String rentDate;
    private String status;

    public RecentRental() {
        // Default constructor for Firebase
    }

    public RecentRental(String bicycleModel, String rentDate, String status) {
        this.bicycleModel = bicycleModel;
        this.rentDate = rentDate;
        this.status = status;
    }

    public String getBicycleModel() {
        return bicycleModel;
    }

    public void setBicycleModel(String bicycleModel) {
        this.bicycleModel = bicycleModel;
    }

    public String getRentDate() {
        return rentDate;
    }

    public void setRentDate(String rentDate) {
        this.rentDate = rentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
