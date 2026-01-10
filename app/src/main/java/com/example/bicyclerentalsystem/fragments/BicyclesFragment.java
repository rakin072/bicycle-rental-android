package com.example.bicyclerentalsystem.fragments;

import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.adapters.BicycleAdapter;
import com.example.bicyclerentalsystem.models.Bicycle;
import com.example.bicyclerentalsystem.models.Rental;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BicyclesFragment extends Fragment {

    // Firebase references
    private DatabaseReference bicyclesRef;
    private DatabaseReference rentalsRef;
    private DatabaseReference usersRef;
    
    // Current user info
    private String currentUserId;
    private String currentUserEmail;
    
    // UI components
    private TextInputEditText modelEditText;
    private Spinner typeSpinner;
    private MaterialButton addBicycleButton;
    private ProgressBar progressBar;
    private TextView emptyView;
    private RecyclerView bicyclesRecyclerView;
    private BicycleAdapter adapter;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bicycles, container, false);
        
        // Initialize Firebase
        bicyclesRef = FirebaseDatabase.getInstance().getReference("bicycles");
        rentalsRef = FirebaseDatabase.getInstance().getReference("rentals");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        
        // Get current user info
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        
        // Initialize UI components
        initializeViews();
        
        // Setup adapter
        adapter = new BicycleAdapter(this::onDeleteClick);
        bicyclesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        bicyclesRecyclerView.setAdapter(adapter);
        
        // Setup listeners
        setupListeners();
        
        // Load bicycles
        loadBicycles();
        
        return rootView;
    }
    
    private void initializeViews() {
        modelEditText = rootView.findViewById(R.id.modelEditText);
        typeSpinner = rootView.findViewById(R.id.typeSpinner);
        addBicycleButton = rootView.findViewById(R.id.addBicycleButton);
        progressBar = rootView.findViewById(R.id.progressBar);
        emptyView = rootView.findViewById(R.id.emptyView);
        bicyclesRecyclerView = rootView.findViewById(R.id.bicyclesRecyclerView);
    }
    
    private void setupListeners() {
        addBicycleButton.setOnClickListener(v -> addBicycle());
    }
    
    private void addBicycle() {
        String model = modelEditText.getText().toString().trim();
        String type = typeSpinner.getSelectedItem().toString();
        
        // Validate
        if (model.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter bicycle model", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create bicycle
        String bicycleId = bicyclesRef.push().getKey();
        Bicycle bicycle = new Bicycle();
        bicycle.setBicycleId(bicycleId);
        bicycle.setModel(model);
        bicycle.setType(type);
        bicycle.setOwnerId(currentUserId);
        bicycle.setOwnerEmail(currentUserEmail);
        bicycle.setIsAvailable(true);
        
        // Save to Firebase
        bicyclesRef.child(bicycleId).setValue(bicycle)
            .addOnSuccessListener(aVoid -> {
                Snackbar.make(rootView, "Bicycle added successfully", Snackbar.LENGTH_SHORT).show();
                modelEditText.setText("");
                typeSpinner.setSelection(0);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
    
    private void loadBicycles() {
        progressBar.setVisibility(View.VISIBLE);
        
        bicyclesRef.orderByChild("ownerId").equalTo(currentUserId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Bicycle> bicycles = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Bicycle bike = child.getValue(Bicycle.class);
                        if (bike != null) {
                            bike.setBicycleId(child.getKey());
                            bicycles.add(bike);
                        }
                    }
                    
                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(bicycles.isEmpty() ? View.VISIBLE : View.GONE);
                    adapter.updateList(bicycles);
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error loading bicycles", Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void onDeleteClick(Bicycle bicycle) {
        // Check if bicycle is currently rented
        rentalsRef.orderByChild("bicycleId").equalTo(bicycle.getBicycleId())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isRented = false;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String status = child.child("status").getValue(String.class);
                        if ("Active".equals(status) || "Pending".equals(status)) {
                            isRented = true;
                            break;
                        }
                    }
                    
                    if (isRented) {
                        showAlert("Cannot delete bicycle that is currently rented or has pending rental.");
                    } else {
                        showDeleteConfirmation(bicycle);
                    }
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "Error checking bicycle status", Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void showDeleteConfirmation(Bicycle bicycle) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Delete Bicycle")
            .setMessage("Are you sure you want to delete " + bicycle.getModel() + "?")
            .setPositiveButton("Delete", (dialog, which) -> deleteBicycle(bicycle))
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void deleteBicycle(Bicycle bicycle) {
        bicyclesRef.child(bicycle.getBicycleId()).removeValue()
            .addOnSuccessListener(aVoid -> {
                if (isAdded() && rootView != null) {
                    Snackbar.make(rootView, "Bicycle deleted successfully", Snackbar.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Error deleting bicycle: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void showAlert(String message) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Cannot Rent")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }
}
