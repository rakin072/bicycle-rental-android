package com.example.bicyclerentalsystem.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.adapters.BicycleModelAdapter;
import com.example.bicyclerentalsystem.adapters.RecentRentalAdapter;
import com.example.bicyclerentalsystem.models.BicycleModel;
import com.example.bicyclerentalsystem.models.RecentRental;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardHomeFragment extends Fragment {

    // Statistics TextViews
    private TextView valueTotalBicycles;
    private TextView valueAvailable;
    private TextView valueCurrentlyRented;
    private TextView valueTotalRentals;

    // RecyclerViews and Adapters
    private RecyclerView recyclerBicycleModels;
    private RecyclerView recyclerRecentRentals;
    private BicycleModelAdapter bicycleModelAdapter;
    private RecentRentalAdapter recentRentalAdapter;

    // Empty state TextViews
    private TextView emptyBicycleModels;
    private TextView emptyRecentRentals;

    // Firebase
    private DatabaseReference databaseReference;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_home, container, false);

        // Initialize Firebase
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize statistics TextViews
        valueTotalBicycles = view.findViewById(R.id.value_total_bicycles);
        valueAvailable = view.findViewById(R.id.value_available);
        valueCurrentlyRented = view.findViewById(R.id.value_currently_rented);
        valueTotalRentals = view.findViewById(R.id.value_total_rentals);

        // Initialize empty state TextViews
        emptyBicycleModels = view.findViewById(R.id.empty_bicycle_models);
        emptyRecentRentals = view.findViewById(R.id.empty_recent_rentals);

        // Initialize RecyclerView for Bicycle Models
        recyclerBicycleModels = view.findViewById(R.id.recycler_bicycle_models);
        recyclerBicycleModels.setLayoutManager(new LinearLayoutManager(getContext()));
        bicycleModelAdapter = new BicycleModelAdapter();
        recyclerBicycleModels.setAdapter(bicycleModelAdapter);

        // Initialize RecyclerView for Recent Rentals
        recyclerRecentRentals = view.findViewById(R.id.recycler_recent_rentals);
        recyclerRecentRentals.setLayoutManager(new LinearLayoutManager(getContext()));
        recentRentalAdapter = new RecentRentalAdapter();
        recyclerRecentRentals.setAdapter(recentRentalAdapter);

        // Load dashboard data
        loadDashboardData();

        return view;
    }

    private void loadDashboardData() {
        loadStatistics();
        loadBicycleModels();
        loadRecentRentals();
    }

    private void loadStatistics() {
        // Load Total Bicycles - ALL bicycles added by all users
        databaseReference.child("bicycles")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int total = (int) snapshot.getChildrenCount();
                        valueTotalBicycles.setText(String.valueOf(total));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });

        // Load Available Bicycles - bicycles available for rent
        databaseReference.child("bicycles")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int available = 0;

                        for (DataSnapshot bicycleSnapshot : snapshot.getChildren()) {
                            Boolean isAvailable = bicycleSnapshot.child("isAvailable").getValue(Boolean.class);
                            if (isAvailable != null && isAvailable) {
                                available++;
                            }
                        }

                        valueAvailable.setText(String.valueOf(available));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });

        // Load Currently Rented - 1 if user has active rental, 0 if not
        databaseReference.child("rentals").orderByChild("renterId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int currentlyRented = 0;

                        for (DataSnapshot rentalSnapshot : snapshot.getChildren()) {
                            String status = rentalSnapshot.child("status").getValue(String.class);
                            if ("Active".equals(status)) {
                                currentlyRented = 1;
                                break;
                            }
                        }

                        valueCurrentlyRented.setText(String.valueOf(currentlyRented));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });

        // Load Total Rentals
        databaseReference.child("rentals").orderByChild("ownerId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int totalRentals = (int) snapshot.getChildrenCount();
                        valueTotalRentals.setText(String.valueOf(totalRentals));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    private void loadBicycleModels() {
        databaseReference.child("bicycles").orderByChild("ownerId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<BicycleModel> models = new ArrayList<>();

                        // Group bicycles by model and type
                        for (DataSnapshot bicycleSnapshot : snapshot.getChildren()) {
                            String model = bicycleSnapshot.child("model").getValue(String.class);
                            String type = bicycleSnapshot.child("type").getValue(String.class);

                            // Check if this model/type combination already exists
                            boolean found = false;
                            for (BicycleModel existingModel : models) {
                                if (existingModel.getModel().equals(model) && existingModel.getType().equals(type)) {
                                    existingModel.setCount(existingModel.getCount() + 1);
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) {
                                models.add(new BicycleModel(model, type, 1));
                            }
                        }

                        if (models.isEmpty()) {
                            recyclerBicycleModels.setVisibility(View.GONE);
                            emptyBicycleModels.setVisibility(View.VISIBLE);
                        } else {
                            recyclerBicycleModels.setVisibility(View.VISIBLE);
                            emptyBicycleModels.setVisibility(View.GONE);
                            bicycleModelAdapter.setBicycleModels(models);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    private void loadRecentRentals() {
        databaseReference.child("rentals").orderByChild("ownerId").equalTo(userId)
                .limitToLast(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<RecentRental> rentals = new ArrayList<>();

                        for (DataSnapshot rentalSnapshot : snapshot.getChildren()) {
                            String bicycleModel = rentalSnapshot.child("bicycleModel").getValue(String.class);
                            String rentDate = rentalSnapshot.child("rentDate").getValue(String.class);
                            String status = rentalSnapshot.child("status").getValue(String.class);

                            if (bicycleModel != null && rentDate != null && status != null) {
                                rentals.add(0, new RecentRental(bicycleModel, rentDate, status));
                            }
                        }

                        if (rentals.isEmpty()) {
                            recyclerRecentRentals.setVisibility(View.GONE);
                            emptyRecentRentals.setVisibility(View.VISIBLE);
                        } else {
                            recyclerRecentRentals.setVisibility(View.VISIBLE);
                            emptyRecentRentals.setVisibility(View.GONE);
                            recentRentalAdapter.setRecentRentals(rentals);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }
}
