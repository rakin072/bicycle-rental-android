package com.example.bicyclerentalsystem.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Rental {
    private String rentalId;
    private String bicycleId;
    private String bicycleModel;
    private String renterId;
    private String renterEmail;
    private String ownerId;
    private String ownerEmail;
    private String rentDate;
    private String dueDate;
    private String returnDate;
    private String status;
    private int durationDays;

    // Required empty constructor for Firebase
    public Rental() {
    }

    public Rental(String rentalId, String bicycleId, String bicycleModel, String renterId, String renterEmail, String ownerId, String ownerEmail, String rentDate, String dueDate, String returnDate, String status, int durationDays) {
        this.rentalId = rentalId;
        this.bicycleId = bicycleId;
        this.bicycleModel = bicycleModel;
        this.renterId = renterId;
        this.renterEmail = renterEmail;
        this.ownerId = ownerId;
        this.ownerEmail = ownerEmail;
        this.rentDate = rentDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.durationDays = durationDays;
    }

    // Getters
    public String getRentalId() {
        return rentalId;
    }

    public String getBicycleId() {
        return bicycleId;
    }

    public String getBicycleModel() {
        return bicycleModel;
    }

    public String getRenterId() {
        return renterId;
    }

    public String getRenterEmail() {
        return renterEmail;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getRentDate() {
        return rentDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public String getStatus() {
        return status;
    }

    public int getDurationDays() {
        return durationDays;
    }

    // Setters
    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public void setBicycleId(String bicycleId) {
        this.bicycleId = bicycleId;
    }

    public void setBicycleModel(String bicycleModel) {
        this.bicycleModel = bicycleModel;
    }

    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }

    public void setRenterEmail(String renterEmail) {
        this.renterEmail = renterEmail;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public void setRentDate(String rentDate) {
        this.rentDate = rentDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    // Business logic methods
    public boolean isOverdue() {
        if (returnDate != null || !"Active".equals(status)) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date due = sdf.parse(dueDate);
            Date today = new Date();
            return today.after(due);
        } catch (Exception e) {
            return false;
        }
    }

    public int getDaysLate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date due = sdf.parse(dueDate);
            Date compareDate;
            
            // For Active rentals, compare with today
            // For Completed rentals, compare with return date
            if ("Active".equals(status)) {
                compareDate = new Date();
            } else if ("Completed".equals(status) && returnDate != null) {
                compareDate = sdf.parse(returnDate);
            } else {
                return 0;
            }
            
            // Calculate difference
            long diff = compareDate.getTime() - due.getTime();
            int daysLate = (int) (diff / (1000 * 60 * 60 * 24));
            
            // Return days late only if positive (was actually late)
            return Math.max(0, daysLate);
        } catch (Exception e) {
            return 0;
        }
    }
    
    public boolean wasReturnedLate() {
        if ("Completed".equals(status) && returnDate != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date due = sdf.parse(dueDate);
                Date returned = sdf.parse(returnDate);
                return returned.after(due);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public double calculateOverdueCharges() {
        return getDaysLate() * 50.0; // 50 Taka per day
    }
}