package com.example.bicyclerentalsystem.fragments;

import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.adapters.MyPaymentAdapter;
import com.example.bicyclerentalsystem.adapters.MyRentalAdapter;
import com.example.bicyclerentalsystem.adapters.PaymentRequestAdapter;
import com.example.bicyclerentalsystem.adapters.RentalRequestAdapter;
import com.example.bicyclerentalsystem.models.PaymentMessage;
import com.example.bicyclerentalsystem.models.Rental;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesFragment extends Fragment {

    // Firebase references
    private DatabaseReference messagesRef;
    private DatabaseReference rentalsRef;
    private DatabaseReference usersRef;
    private DatabaseReference bicyclesRef;

    // Current user info
    private String currentUserId;
    private String currentUserEmail;

    // UI components
    private MaterialButtonToggleGroup toggleGroup;
    private ProgressBar progressBar;
    private TextView emptyView;
    private RecyclerView messagesRecyclerView;
    private View rootView;

    // Adapters
    private String currentTab = "payment_requests";
    private PaymentRequestAdapter paymentRequestAdapter;
    private MyPaymentAdapter myPaymentAdapter;
    private MyRentalAdapter myRentalAdapter;
    private RentalRequestAdapter rentalRequestAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_messages, container, false);

        // Initialize Firebase
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");
        rentalsRef = FirebaseDatabase.getInstance().getReference("rentals");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        bicyclesRef = FirebaseDatabase.getInstance().getReference("bicycles");

        // Get current user info
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }

        // Initialize UI components
        initializeViews();

        // Setup adapters
        paymentRequestAdapter = new PaymentRequestAdapter(new PaymentRequestAdapter.OnActionListener() {
            @Override
            public void onApprove(PaymentMessage message) {
                onApprovePaymentClick(message);
            }

            @Override
            public void onReject(PaymentMessage message) {
                onRejectPaymentClick(message);
            }
        });
        myPaymentAdapter = new MyPaymentAdapter();
        
        myRentalAdapter = new MyRentalAdapter(this::onReturnClick);
        rentalRequestAdapter = new RentalRequestAdapter(new RentalRequestAdapter.OnActionClickListener() {
            @Override
            public void onApprove(Rental rental) {
                onApproveRentalClick(rental);
            }

            @Override
            public void onReject(Rental rental) {
                onRejectRentalClick(rental);
            }
        });

        // Setup RecyclerView
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        messagesRecyclerView.setAdapter(paymentRequestAdapter);

        // Setup toggle listener
        setupToggleListener();

        // Load initial data (Payment Requests)
        toggleGroup.check(R.id.btnPaymentRequests);
        loadPaymentRequests();

        return rootView;
    }

    private void initializeViews() {
        toggleGroup = rootView.findViewById(R.id.bottomNavigation);
        progressBar = rootView.findViewById(R.id.progressBar);
        emptyView = rootView.findViewById(R.id.emptyView);
        messagesRecyclerView = rootView.findViewById(R.id.messagesRecyclerView);
    }

    private void setupToggleListener() {
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;

            if (checkedId == R.id.btnPaymentRequests) {
                currentTab = "payment_requests";
                messagesRecyclerView.setAdapter(paymentRequestAdapter);
                loadPaymentRequests();
            } else if (checkedId == R.id.btnMyPayments) {
                currentTab = "my_payments";
                messagesRecyclerView.setAdapter(myPaymentAdapter);
                loadMyPaymentMessages();
            } else if (checkedId == R.id.btnMyRentals) {
                currentTab = "my_rentals";
                messagesRecyclerView.setAdapter(myRentalAdapter);
                loadMyRentals();
            } else if (checkedId == R.id.btnRentalRequests) {
                currentTab = "rental_requests";
                messagesRecyclerView.setAdapter(rentalRequestAdapter);
                loadRentalRequestsToMe();
            }
        });
    }

    private void loadPaymentRequests() {
        progressBar.setVisibility(View.VISIBLE);

        messagesRef.orderByChild("receiverId").equalTo(currentUserId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<PaymentMessage> messages = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        PaymentMessage msg = child.getValue(PaymentMessage.class);
                        if (msg != null) {
                            msg.setMessageId(child.getKey());
                            messages.add(msg);
                        }
                    }

                    // Sort by timestamp (newest first)
                    Collections.sort(messages, (m1, m2) ->
                        Long.compare(m2.getTimestamp(), m1.getTimestamp()));

                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(messages.isEmpty() ? View.VISIBLE : View.GONE);
                    paymentRequestAdapter.updateList(messages);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error loading messages", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void loadMyPaymentMessages() {
        progressBar.setVisibility(View.VISIBLE);

        messagesRef.orderByChild("senderId").equalTo(currentUserId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<PaymentMessage> messages = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        PaymentMessage msg = child.getValue(PaymentMessage.class);
                        if (msg != null) {
                            msg.setMessageId(child.getKey());
                            messages.add(msg);
                        }
                    }

                    Collections.sort(messages, (m1, m2) ->
                        Long.compare(m2.getTimestamp(), m1.getTimestamp()));

                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(messages.isEmpty() ? View.VISIBLE : View.GONE);
                    myPaymentAdapter.updateList(messages);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error loading messages", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void onApprovePaymentClick(PaymentMessage message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_approve_payment, null);

        TextView senderText = dialogView.findViewById(R.id.senderEmailText);
        TextView bicycleText = dialogView.findViewById(R.id.bicycleModelText);
        TextView txIdText = dialogView.findViewById(R.id.transactionIdText);
        TextView amountText = dialogView.findViewById(R.id.amountText);
        MaterialButton confirmButton = dialogView.findViewById(R.id.confirmButton);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);

        senderText.setText("From: " + message.getSenderEmail());
        bicycleText.setText("Bicycle: " + (message.getBicycleModel() != null ? message.getBicycleModel() : "N/A"));
        txIdText.setText("Transaction ID: " + message.getTransactionId());
        amountText.setText(String.format("Amount: %.2f Taka", message.getAmount()));

        AlertDialog dialog = builder.setView(dialogView).create();

        confirmButton.setOnClickListener(v -> {
            approvePayment(message);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void approvePayment(PaymentMessage message) {
        // Update message status
        messagesRef.child(message.getMessageId()).child("status").setValue("Approved")
            .addOnSuccessListener(v -> {
                if (message.isPaymentType()) {
                    // Initial rental payment - Activate rental
                    rentalsRef.child(message.getRentalId()).child("status").setValue("Active");

                    Snackbar.make(rootView, "Payment approved. Rental activated.",
                        Snackbar.LENGTH_SHORT).show();

                } else if (message.isOverduePaymentType()) {
                    // Overdue payment - Clear user's overdue charges
                    usersRef.child(message.getSenderId()).child("overdueCharges")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Double currentCharges = snapshot.getValue(Double.class);
                                if (currentCharges == null) currentCharges = 0.0;

                                double newCharges = Math.max(0, currentCharges - message.getAmount());
                                usersRef.child(message.getSenderId()).child("overdueCharges")
                                    .setValue(newCharges);

                                Snackbar.make(rootView, "Overdue payment approved. Charges cleared.",
                                    Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(requireContext(), "Error updating charges", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Error approving payment", Toast.LENGTH_SHORT).show();
            });
    }

    private void onRejectPaymentClick(PaymentMessage message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_reject_payment, null);

        TextView senderText = dialogView.findViewById(R.id.senderEmailText);
        TextView txIdText = dialogView.findViewById(R.id.transactionIdText);
        TextInputEditText reasonInput = dialogView.findViewById(R.id.reasonInput);
        MaterialButton confirmButton = dialogView.findViewById(R.id.confirmButton);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);

        senderText.setText("From: " + message.getSenderEmail());
        txIdText.setText("Transaction ID: " + message.getTransactionId());

        AlertDialog dialog = builder.setView(dialogView).create();

        confirmButton.setOnClickListener(v -> {
            String reason = reasonInput.getText() != null ? reasonInput.getText().toString().trim() : "";
            rejectPayment(message, reason);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void rejectPayment(PaymentMessage message, String reason) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "Rejected");
        if (!reason.isEmpty()) {
            updates.put("rejectionReason", reason);
        }

        messagesRef.child(message.getMessageId()).updateChildren(updates)
            .addOnSuccessListener(v -> {
                if (message.isPaymentType()) {
                    // Initial payment rejected - Delete rental and free bicycle
                    rentalsRef.child(message.getRentalId()).removeValue();

                    if (message.getBicycleId() != null) {
                        bicyclesRef.child(message.getBicycleId()).child("isAvailable")
                            .setValue(true);
                    }

                    Snackbar.make(rootView, "Payment rejected. Rental cancelled.",
                        Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(rootView, "Overdue payment rejected.",
                        Snackbar.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Error rejecting payment", Toast.LENGTH_SHORT).show();
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
                    
                    Collections.sort(rentals, (r1, r2) -> {
                        if (r2.getRentDate() == null) return -1;
                        if (r1.getRentDate() == null) return 1;
                        return r2.getRentDate().compareTo(r1.getRentDate());
                    });
                    
                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(rentals.isEmpty() ? View.VISIBLE : View.GONE);
                    myRentalAdapter.updateList(rentals);
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error loading rentals", Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void loadRentalRequestsToMe() {
        progressBar.setVisibility(View.VISIBLE);
        
        bicyclesRef.orderByChild("ownerId").equalTo(currentUserId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> myBicycleIds = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        myBicycleIds.add(child.getKey());
                    }
                    
                    loadRentalsForBicycles(myBicycleIds);
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error loading bicycles", Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void loadRentalsForBicycles(List<String> bicycleIds) {
        if (bicycleIds.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            return;
        }
        
        rentalsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Rental> rentals = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Rental rental = child.getValue(Rental.class);
                    if (rental != null && bicycleIds.contains(rental.getBicycleId())) {
                        rental.setRentalId(child.getKey());
                        rentals.add(rental);
                    }
                }
                
                Collections.sort(rentals, (r1, r2) -> {
                    if (r2.getRentDate() == null) return -1;
                    if (r1.getRentDate() == null) return 1;
                    return r2.getRentDate().compareTo(r1.getRentDate());
                });
                
                progressBar.setVisibility(View.GONE);
                emptyView.setVisibility(rentals.isEmpty() ? View.VISIBLE : View.GONE);
                rentalRequestAdapter.updateList(rentals);
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error loading requests", Toast.LENGTH_SHORT).show();
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
                // If calculation fails, just use 0
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
        if (!isAdded() || rental == null || rental.getRentalId() == null || rental.getBicycleId() == null) {
            return;
        }
        
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
            String returnDate = sdf.format(new java.util.Date());
            
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", "Completed");
            updates.put("returnDate", returnDate);
            
            rentalsRef.child(rental.getRentalId()).updateChildren(updates)
                .addOnSuccessListener(v -> {
                    if (!isAdded() || rental.getBicycleId() == null) return;
                    
                    try {
                        bicyclesRef.child(rental.getBicycleId()).child("isAvailable").setValue(true);
                        
                        if (overdueCharges > 0) {
                            usersRef.child(currentUserId).child("overdueCharges")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!isAdded()) return;
                                        
                                        try {
                                            Double currentCharges = snapshot.getValue(Double.class);
                                            if (currentCharges == null) currentCharges = 0.0;
                                            double newCharges = currentCharges + overdueCharges;
                                            
                                            usersRef.child(currentUserId).child("overdueCharges").setValue(newCharges);
                                            
                                            if (isAdded() && rootView != null && getView() != null) {
                                                Snackbar.make(rootView, "Bicycle returned. Overdue charges: " + overdueCharges + " Taka", 
                                                    Snackbar.LENGTH_LONG).show();
                                            }
                                        } catch (Exception e) {
                                            // Handle any exception in inner callback
                                        }
                                    }
                                    
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        if (isAdded() && rootView != null && getView() != null) {
                                            Snackbar.make(rootView, "Bicycle returned successfully", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        } else {
                            if (isAdded() && rootView != null && getView() != null) {
                                Snackbar.make(rootView, "Bicycle returned successfully", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Bicycle returned but error updating status", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(requireContext(), "Error returning bicycle: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        } catch (Exception e) {
            if (isAdded() && getContext() != null) {
                Toast.makeText(requireContext(), "Error processing return: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void onApproveRentalClick(Rental rental) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Approve Rental")
            .setMessage("Approve rental for " + rental.getBicycleModel() + "?")
            .setPositiveButton("Approve", (dialog, which) -> {
                // First check if the renter already has an active rental
                rentalsRef.orderByChild("renterId").equalTo(rental.getRenterId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean hasActiveRental = false;
                            for (DataSnapshot child : snapshot.getChildren()) {
                                Rental existingRental = child.getValue(Rental.class);
                                if (existingRental != null && "Active".equals(existingRental.getStatus())) {
                                    hasActiveRental = true;
                                    break;
                                }
                            }
                            
                            if (hasActiveRental) {
                                if (isAdded()) {
                                    Toast.makeText(requireContext(), 
                                        "This user already has an active rental. Cannot approve.", 
                                        Toast.LENGTH_LONG).show();
                                }
                                return;
                            }
                            
                            // No active rental, proceed with approval
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("status", "Active");
                            
                            rentalsRef.child(rental.getRentalId()).updateChildren(updates)
                                .addOnSuccessListener(v -> {
                                    // Set bicycle as unavailable
                                    if (rental.getBicycleId() != null) {
                                        bicyclesRef.child(rental.getBicycleId()).child("isAvailable").setValue(false);
                                    }
                                    
                                    // After approving, reject all other pending rentals from this user
                                    rejectOtherPendingRentals(rental.getRenterId(), rental.getRentalId());
                                    
                                    if (isAdded() && rootView != null) {
                                        Snackbar.make(rootView, "Rental approved. Other pending requests rejected.", 
                                            Snackbar.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    if (isAdded()) {
                                        Toast.makeText(requireContext(), "Error approving rental", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        }
                        
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "Error checking rental status", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void rejectOtherPendingRentals(String renterId, String approvedRentalId) {
        rentalsRef.orderByChild("renterId").equalTo(renterId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Rental rental = child.getValue(Rental.class);
                        if (rental != null && rental.getRentalId() != null) {
                            // Skip the approved rental
                            if (rental.getRentalId().equals(approvedRentalId)) {
                                continue;
                            }
                            
                            // Reject pending rentals
                            if ("Pending".equals(rental.getStatus())) {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("status", "Rejected");
                                
                                rentalsRef.child(rental.getRentalId()).updateChildren(updates);
                                
                                // Make the bicycle available again
                                if (rental.getBicycleId() != null) {
                                    bicyclesRef.child(rental.getBicycleId()).child("isAvailable").setValue(true);
                                }
                            }
                        }
                    }
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Silently fail, main approval was successful
                }
            });
    }
    
    private void onRejectRentalClick(Rental rental) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Reject Rental")
            .setMessage("Reject rental for " + rental.getBicycleModel() + "? The bicycle will be available again.")
            .setPositiveButton("Reject", (dialog, which) -> {
                rentalsRef.child(rental.getRentalId()).removeValue()
                    .addOnSuccessListener(v -> {
                        bicyclesRef.child(rental.getBicycleId()).child("isAvailable").setValue(true);
                        if (isAdded() && rootView != null) {
                            Snackbar.make(rootView, "Rental rejected", Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Error rejecting rental", Toast.LENGTH_SHORT).show();
                        }
                    });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
