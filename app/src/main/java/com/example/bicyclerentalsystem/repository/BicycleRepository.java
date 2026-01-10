package com.example.bicyclerentalsystem.repository;

import com.example.bicyclerentalsystem.models.Bicycle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BicycleRepository {
    private DatabaseReference databaseReference;

    public BicycleRepository() {
        databaseReference = FirebaseDatabase.getInstance().getReference("bicycles");
    }

    public void getAllBicycles(BicycleCallback callback) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Bicycle> bicycles = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Bicycle bicycle = snapshot.getValue(Bicycle.class);
                    if (bicycle != null) {
                        bicycles.add(bicycle);
                    }
                }
                callback.onSuccess(bicycles);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    public void addBicycle(Bicycle bicycle) {
        String id = databaseReference.push().getKey();
        if (id != null) {
            bicycle.setBicycleId(id);
            databaseReference.child(id).setValue(bicycle);
        }
    }

    public interface BicycleCallback {
        void onSuccess(List<Bicycle> bicycles);
        void onError(String error);
    }
}
