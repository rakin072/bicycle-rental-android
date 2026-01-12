package com.example.bicyclerentalsystem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserProfile {
    private String userId;
    private String email;
    private double overdueCharges;
    private long createdAt;
    private int totalBicycles;
    private int activeRentals;
    private int completedRentals;
    private int totalRentals;
    
    public UserProfile() {
        // Required for Firebase
    }
    
    public UserProfile(String userId, String email, double overdueCharges, long createdAt) {
        this.userId = userId;
        this.email = email;
        this.overdueCharges = overdueCharges;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public double getOverdueCharges() {
        return overdueCharges;
    }
    
    public void setOverdueCharges(double overdueCharges) {
        this.overdueCharges = overdueCharges;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public int getTotalBicycles() {
        return totalBicycles;
    }
    
    public void setTotalBicycles(int totalBicycles) {
        this.totalBicycles = totalBicycles;
    }
    
    public int getActiveRentals() {
        return activeRentals;
    }
    
    public void setActiveRentals(int activeRentals) {
        this.activeRentals = activeRentals;
    }
    
    public int getCompletedRentals() {
        return completedRentals;
    }
    
    public void setCompletedRentals(int completedRentals) {
        this.completedRentals = completedRentals;
    }
    
    public int getTotalRentals() {
        return totalRentals;
    }
    
    public void setTotalRentals(int totalRentals) {
        this.totalRentals = totalRentals;
    }
    
    // Business logic methods
    public String getMemberSince() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.US);
        return sdf.format(new Date(createdAt));
    }
    
    public boolean hasOverdueCharges() {
        return overdueCharges > 0;
    }
}
