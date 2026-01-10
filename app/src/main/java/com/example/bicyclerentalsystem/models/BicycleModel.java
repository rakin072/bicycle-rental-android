package com.example.bicyclerentalsystem.models;

public class BicycleModel {
    private String model;
    private String type;
    private int count;

    public BicycleModel() {
        // Default constructor for Firebase
    }

    public BicycleModel(String model, String type, int count) {
        this.model = model;
        this.type = type;
        this.count = count;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
