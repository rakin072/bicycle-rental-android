package com.example.bicyclerentalsystem.utils;

import com.example.bicyclerentalsystem.models.Bicycle;
import com.example.bicyclerentalsystem.models.User;
import com.example.bicyclerentalsystem.models.Rental;
import com.example.bicyclerentalsystem.models.Message;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHelper {

    private DatabaseReference databaseReference;

    public DatabaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addBicycle(Bicycle bicycle) {
        String bicycleId = databaseReference.child("bicycles").push().getKey();
        databaseReference.child("bicycles").child(bicycleId).setValue(bicycle);
    }

    public void addUser(User user) {
        String userId = databaseReference.child("users").push().getKey();
        databaseReference.child("users").child(userId).setValue(user);
    }

    public void addRental(Rental rental) {
        String rentalId = databaseReference.child("rentals").push().getKey();
        databaseReference.child("rentals").child(rentalId).setValue(rental);
    }

    public void addMessage(Message message) {
        String messageId = databaseReference.child("messages").push().getKey();
        databaseReference.child("messages").child(messageId).setValue(message);
    }

    public DatabaseReference getBicyclesReference() {
        return databaseReference.child("bicycles");
    }

    public DatabaseReference getUsersReference() {
        return databaseReference.child("users");
    }

    public DatabaseReference getRentalsReference() {
        return databaseReference.child("rentals");
    }

    public DatabaseReference getMessagesReference() {
        return databaseReference.child("messages");
    }
}