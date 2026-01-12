package com.example.bicyclerentalsystem.fragments;

import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.example.bicyclerentalsystem.adapters.MyRentalAdapter;
import com.example.bicyclerentalsystem.models.Bicycle;
import com.example.bicyclerentalsystem.models.PaymentMessage;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RentalsFragment extends Fragment {

    // Firebase references
    private DatabaseReference rentalsRef;
    private DatabaseReference bicyclesRef;
    private DatabaseReference usersRef;
    private DatabaseReference messagesRef;
    
    // Current user info
    private String currentUserId;
    private String currentUserEmail;
    
    // Rent a Bicycle section
    private Spinner bicycleSpinner;
    private TextInputEditText durationEditText;
    private MaterialButton rentNowButton;
    private List<Bicycle> availableBicycles = new ArrayList<>();
    private Bicycle selectedBicycle;
    
    // UI components
    private ProgressBar progressBar;
    private TextView emptyView;
    private RecyclerView rentalsRecyclerView;
    private MyRentalAdapter rentalAdapter;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_rentals, container, false);
        
        // Initialize Firebase
        rentalsRef = FirebaseDatabase.getInstance().getReference("rentals");
        bicyclesRef = FirebaseDatabase.getInstance().getReference("bicycles");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");
        
        // Get current user info
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        
        // Initialize UI components
        initializeViews();
        
        // Setup rental adapter
        rentalAdapter = new MyRentalAdapter(this::onReturnClick);
        rentalsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        rentalsRecyclerView.setAdapter(rentalAdapter);
        
        // Load available bicycles for rent
        loadAvailableBicycles();
        
        // Load rental history
        loadMyRentals();
        
        return rootView;
    }
    
    private void initializeViews() {
        // Rent a Bicycle section
        bicycleSpinner = rootView.findViewById(R.id.bicycleSpinner);
        durationEditText = rootView.findViewById(R.id.durationEditText);
        rentNowButton = rootView.findViewById(R.id.rentNowButton);
        
        // Rental history section
        progressBar = rootView.findViewById(R.id.progressBar);
        emptyView = rootView.findViewById(R.id.emptyView);
        rentalsRecyclerView = rootView.findViewById(R.id.rentalsRecyclerView);
        
        // Setup rent button listener
        rentNowButton.setOnClickListener(v -> onRentNowClick());
        
        // Setup spinner listener
        bicycleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position <= availableBicycles.size()) {
                    selectedBicycle = availableBicycles.get(position - 1);
                } else {
                    selectedBicycle = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedBicycle = null;
            }
        });
    }
    
    private void loadAvailableBicycles() {
        // Load all bicycles and filter based on availability and ownership
        bicyclesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                availableBicycles.clear();
                List<String> bicycleModels = new ArrayList<>();
                bicycleModels.add("Select a Bicycle");
                
                for (DataSnapshot child : snapshot.getChildren()) {
                    Bicycle bicycle = child.getValue(Bicycle.class);
                    if (bicycle != null) {
                        bicycle.setBicycleId(child.getKey());
                        
                        // Check if bicycle is available and not owned by current user
                        // Use isAvailable() getter method which handles the boolean field properly
                        boolean isAvailableForRent = bicycle.isAvailable();
                        boolean isNotOwnedByUser = !currentUserId.equals(bicycle.getOwnerId());
                        
                        if (isAvailableForRent && isNotOwnedByUser) {
                            availableBicycles.add(bicycle);
                            bicycleModels.add(bicycle.getModel() + " (" + bicycle.getType() + ")");
                        }
                    }
                }
                
                // Update spinner adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                    android.R.layout.simple_spinner_item, bicycleModels);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                bicycleSpinner.setAdapter(adapter);
                
                // Show message if no bicycles available
                if (availableBicycles.isEmpty()) {
                    Toast.makeText(requireContext(), 
                        "No bicycles available for rent at the moment", 
                        Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error loading bicycles: " + error.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void onRentNowClick() {
        // Validate selection
        if (selectedBicycle == null) {
            Toast.makeText(requireContext(), "Please select a bicycle", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Validate duration
        String durationStr = durationEditText.getText() != null ? durationEditText.getText().toString().trim() : "";
        if (durationStr.isEmpty()) {
            durationEditText.setError("Enter rental duration");
            durationEditText.requestFocus();
            return;
        }
        
        int duration;
        try {
            duration = Integer.parseInt(durationStr);
            if (duration < 1) {
                durationEditText.setError("Duration must be at least 1 day");
                durationEditText.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            durationEditText.setError("Invalid duration");
            durationEditText.requestFocus();
            return;
        }
        
        // Check if user already has an active rental or pending rentals
        rentalsRef.orderByChild("renterId").equalTo(currentUserId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean hasActiveRental = false;
                    int pendingCount = 0;
                    
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Rental rental = child.getValue(Rental.class);
                        if (rental != null) {
                            if ("Active".equals(rental.getStatus())) {
                                hasActiveRental = true;
                                break;
                            } else if ("Pending".equals(rental.getStatus())) {
                                pendingCount++;
                            }
                        }
                    }
                    
                    if (hasActiveRental) {
                        Toast.makeText(requireContext(), 
                            "You already have an active rental. Please return it before requesting another.", 
                            Toast.LENGTH_LONG).show();
                        return;
                    }
                    
                    // Allow multiple pending requests (as per requirement)
                    // User can send multiple rental requests to different owners
                    
                    // Check for overdue charges
                    checkOverdueChargesAndRent(duration);
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "Error checking rentals", Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void checkOverdueChargesAndRent(int duration) {
        usersRef.child(currentUserId).child("overdueCharges")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Double overdueCharges = snapshot.getValue(Double.class);
                    if (overdueCharges != null && overdueCharges > 0) {
                        Toast.makeText(requireContext(), "You have overdue charges: " + overdueCharges + " Taka. Please pay first.", 
                            Toast.LENGTH_LONG).show();
                        return;
                    }
                    
                    // All validations passed, create rental
                    createRental(duration);
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "Error checking overdue charges", Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void createRental(int duration) {
        // Show dialog to enter transaction ID
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_payment_details, null);
        
        TextView titleText = dialogView.findViewById(R.id.dialogTitle);
        TextView bicycleText = dialogView.findViewById(R.id.bicycleText);
        TextView durationText = dialogView.findViewById(R.id.durationText);
        TextView ownerText = dialogView.findViewById(R.id.ownerText);
        TextInputEditText transactionIdInput = dialogView.findViewById(R.id.transactionIdInput);
        MaterialButton sendButton = dialogView.findViewById(R.id.sendButton);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);
        
        titleText.setText("Send Payment Details");
        bicycleText.setText("Bicycle: " + selectedBicycle.getModel());
        durationText.setText("Duration: " + duration + " days");
        ownerText.setText("Owner: " + selectedBicycle.getOwnerEmail());
        
        AlertDialog dialog = builder.setView(dialogView).create();
        
        sendButton.setOnClickListener(v -> {
            String transactionId = transactionIdInput.getText() != null ? 
                transactionIdInput.getText().toString().trim() : "";
            
            if (transactionId.isEmpty()) {
                transactionIdInput.setError("Enter Transaction ID");
                return;
            }
            
            dialog.dismiss();
            createRentalWithPayment(duration, transactionId);
        });
        
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private void createRentalWithPayment(int duration, String transactionId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String rentDate = sdf.format(new Date());
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, duration);
        String dueDate = sdf.format(calendar.getTime());
        
        String rentalId = rentalsRef.push().getKey();
        Rental rental = new Rental();
        rental.setRentalId(rentalId);
        rental.setBicycleId(selectedBicycle.getBicycleId());
        rental.setBicycleModel(selectedBicycle.getModel());
        rental.setRenterId(currentUserId);
        rental.setRenterEmail(currentUserEmail);
        rental.setOwnerId(selectedBicycle.getOwnerId());
        rental.setOwnerEmail(selectedBicycle.getOwnerEmail());
        rental.setRentDate(rentDate);
        rental.setDueDate(dueDate);
        rental.setStatus("Pending");
        rental.setDurationDays(duration);
        
        rentalsRef.child(rentalId).setValue(rental)
            .addOnSuccessListener(v -> {
                // Make bicycle unavailable
                bicyclesRef.child(selectedBicycle.getBicycleId()).child("isAvailable").setValue(false);
                
                // Create payment message
                createPaymentMessage(rentalId, selectedBicycle, duration, transactionId);
                
                // Reset form
                bicycleSpinner.setSelection(0);
                durationEditText.setText("");
                selectedBicycle = null;
                
                Snackbar.make(rootView, "Rental request sent! Payment details shared with owner.", 
                    Snackbar.LENGTH_LONG).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Error creating rental: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }
    
    private void createPaymentMessage(String rentalId, Bicycle bicycle, int duration, String transactionId) {
        String messageId = messagesRef.push().getKey();
        
        PaymentMessage paymentMessage = new PaymentMessage();
        paymentMessage.setMessageId(messageId);
        paymentMessage.setSenderId(currentUserId);
        paymentMessage.setSenderEmail(currentUserEmail);
        paymentMessage.setReceiverId(bicycle.getOwnerId());
        paymentMessage.setReceiverEmail(bicycle.getOwnerEmail());
        paymentMessage.setMessageType("payment");
        paymentMessage.setTransactionId(transactionId);
        paymentMessage.setMessage("Payment for " + duration + " days rental");
        paymentMessage.setAmount(duration * 100.0); // 100 Taka per day
        paymentMessage.setRentalId(rentalId);
        paymentMessage.setBicycleId(bicycle.getBicycleId());
        paymentMessage.setBicycleModel(bicycle.getModel());
        paymentMessage.setStatus("Pending");
        paymentMessage.setTimestamp(System.currentTimeMillis());
        
        messagesRef.child(messageId).setValue(paymentMessage)
            .addOnSuccessListener(aVoid -> {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Payment details sent to owner", 
                        Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Error sending payment details", 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void loadMyRentals() {
        progressBar.setVisibility(View.VISIBLE);
        
        rentalsRef.orderByChild("renterId").equalTo(currentUserId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Rental> rentals = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Rental rental = child.getValue(Rental.class);
                        if (rental != null) {
                            rental.setRentalId(child.getKey());
                            rentals.add(rental);
                        }
                    }
                    
                    // Sort by rent date (newest first)
                    Collections.sort(rentals, (r1, r2) -> {
                        if (r2.getRentDate() == null) return -1;
                        if (r1.getRentDate() == null) return 1;
                        return r2.getRentDate().compareTo(r1.getRentDate());
                    });
                    
                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(rentals.isEmpty() ? View.VISIBLE : View.GONE);
                    rentalAdapter.updateList(rentals);
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error loading rentals", Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void onReturnClick(Rental rental) {
        if (!isAdded() || getContext() == null || rental == null) {
            return;
        }
        
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_return_bicycle, null);
            
            TextView modelText = dialogView.findViewById(R.id.bicycleModelText);
            TextView rentDateText = dialogView.findViewById(R.id.rentDateText);
            TextView dueDateText = dialogView.findViewById(R.id.dueDateText);
            TextView daysLateText = dialogView.findViewById(R.id.daysLateText);
            TextView overdueChargesText = dialogView.findViewById(R.id.overdueChargesText);
            MaterialButton confirmButton = dialogView.findViewById(R.id.confirmButton);
            MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);
            
            if (modelText == null || confirmButton == null || cancelButton == null) {
                Toast.makeText(requireContext(), "Error loading dialog", Toast.LENGTH_SHORT).show();
                return;
            }
            
            modelText.setText("Bicycle: " + (rental.getBicycleModel() != null ? rental.getBicycleModel() : "Unknown"));
            rentDateText.setText("Rent Date: " + (rental.getRentDate() != null ? rental.getRentDate() : "N/A"));
            dueDateText.setText("Due Date: " + (rental.getDueDate() != null ? rental.getDueDate() : "N/A"));
            
            int daysLate = 0;
            double overdueCharges = 0.0;
            
            try {
                daysLate = rental.getDaysLate();
                overdueCharges = rental.calculateOverdueCharges();
            } catch (Exception e) {
                daysLate = 0;
                overdueCharges = 0.0;
            }
            
            if (daysLate > 0) {
                daysLateText.setVisibility(View.VISIBLE);
                daysLateText.setText("Days Late: " + daysLate);
                overdueChargesText.setVisibility(View.VISIBLE);
                overdueChargesText.setText("Overdue Charges: " + overdueCharges + " Taka");
            } else {
                daysLateText.setVisibility(View.GONE);
                overdueChargesText.setVisibility(View.GONE);
            }
            
            AlertDialog dialog = builder.setView(dialogView).create();
            
            final double finalOverdueCharges = overdueCharges;
            
            confirmButton.setOnClickListener(v -> {
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    returnBicycle(rental, finalOverdueCharges);
                } catch (Exception e) {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Error processing return", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            
            cancelButton.setOnClickListener(v -> {
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    // Ignore dismiss errors
                }
            });
            
            if (isAdded() && !requireActivity().isFinishing()) {
                dialog.show();
            }
        } catch (Exception e) {
            if (isAdded()) {
                Toast.makeText(requireContext(), "Error showing return dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void returnBicycle(Rental rental, double overdueCharges) {
        if (!isAdded() || rental == null) {
            return;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String returnDate = sdf.format(new Date());
            
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", "Completed");
            updates.put("returnDate", returnDate);
            
            rentalsRef.child(rental.getRentalId()).updateChildren(updates)
                .addOnSuccessListener(v -> {
                    if (!isAdded()) return;
                    
                    bicyclesRef.child(rental.getBicycleId()).child("isAvailable").setValue(true);
                    
                    if (overdueCharges > 0) {
                        usersRef.child(currentUserId).child("overdueCharges")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!isAdded()) return;
                                    
                                    Double currentCharges = snapshot.getValue(Double.class);
                                    if (currentCharges == null) currentCharges = 0.0;
                                    double newCharges = currentCharges + overdueCharges;
                                    
                                    usersRef.child(currentUserId).child("overdueCharges").setValue(newCharges);
                                    
                                    if (isAdded() && rootView != null) {
                                        Snackbar.make(rootView, "Bicycle returned. Overdue charges: " + overdueCharges + " Taka", 
                                            Snackbar.LENGTH_LONG).show();
                                    }
                                }
                                
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    if (isAdded() && rootView != null) {
                                        Snackbar.make(rootView, "Bicycle returned successfully", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    } else {
                        if (isAdded() && rootView != null) {
                            Snackbar.make(rootView, "Bicycle returned successfully", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Error returning bicycle: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        } catch (Exception e) {
            if (isAdded()) {
                Toast.makeText(requireContext(), "Error processing return: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
