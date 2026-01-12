package com.example.bicyclerentalsystem.repository;

import com.example.bicyclerentalsystem.models.Rental;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RentalRepository {
    private DatabaseReference databaseReference;

    public RentalRepository() {
        databaseReference = FirebaseDatabase.getInstance().getReference("rentals");
    }

    public void getRentalHistory(RentalHistoryCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        
        if (userId == null) {
            callback.onError("User not logged in");
            return;
        }

        databaseReference.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Rental> rentals = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Rental rental = snapshot.getValue(Rental.class);
                            if (rental != null) {
                                rentals.add(rental);
                            }
                        }
                        callback.onSuccess(rentals);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onError(databaseError.getMessage());
                    }
                });
    }

    public interface RentalHistoryCallback {
        void onSuccess(List<Rental> rentals);
        void onError(String error);
    }
}
