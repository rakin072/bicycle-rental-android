package com.example.bicyclerentalsystem.services;

import com.example.bicyclerentalsystem.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseService {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    public FirebaseService() {
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void signOut() {
        auth.signOut();
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void createUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(listener);
    }

    public void signIn(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(listener);
    }

    public void addUserToDatabase(String userId, User user) {
        databaseReference.child("users").child(userId).setValue(user);
    }
}