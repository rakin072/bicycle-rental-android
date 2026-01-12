package com.example.bicyclerentalsystem.fragments;

import android.app.AlertDialog;
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
import com.example.bicyclerentalsystem.adapters.OverdueAdapter;
import com.example.bicyclerentalsystem.models.OverduePaymentMessage;
import com.example.bicyclerentalsystem.models.Rental;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OverdueFragment extends Fragment {

    // Firebase references
    private DatabaseReference rentalsRef;
    private DatabaseReference usersRef;
    private DatabaseReference messagesRef;

    // Current user info
    private String currentUserId;
    private String currentUserEmail;

    // UI components
    private MaterialCardView summaryCard;
    private TextView totalChargesText;
    private ProgressBar progressBar;
    private TextView emptyView;
    private RecyclerView overdueRecyclerView;
    private OverdueAdapter adapter;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_overdue, container, false);

        // Initialize Firebase
        rentalsRef = FirebaseDatabase.getInstance().getReference("rentals");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");

        // Get current user info
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }

        // Initialize UI components
        initializeViews();

        // Setup RecyclerView
        adapter = new OverdueAdapter(rental -> onPayClick(rental));
        overdueRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        overdueRecyclerView.setAdapter(adapter);

        // Load data
        loadTotalOverdueCharges();
        loadOverdueRentals();

        return rootView;
    }

    private void initializeViews() {
        summaryCard = rootView.findViewById(R.id.summaryCard);
        totalChargesText = rootView.findViewById(R.id.totalChargesText);
        progressBar = rootView.findViewById(R.id.progressBar);
        emptyView = rootView.findViewById(R.id.emptyView);
        overdueRecyclerView = rootView.findViewById(R.id.overdueRecyclerView);
    }

    private void loadTotalOverdueCharges() {
        usersRef.child(currentUserId).child("overdueCharges")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Double charges = snapshot.getValue(Double.class);
                    if (charges == null) charges = 0.0;

                    if (charges > 0) {
                        summaryCard.setVisibility(View.VISIBLE);
                        totalChargesText.setText(String.format("%.2f Taka", charges));
                    } else {
                        summaryCard.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "Error loading charges", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void loadOverdueRentals() {
        progressBar.setVisibility(View.VISIBLE);

        rentalsRef.orderByChild("renterId").equalTo(currentUserId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Rental> overdueRentals = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date today = new Date();

                    for (DataSnapshot child : snapshot.getChildren()) {
                        Rental rental = child.getValue(Rental.class);
                        if (rental != null) {
                            rental.setRentalId(child.getKey());

                            // Check if overdue (Active and past due date)
                            if ("Active".equals(rental.getStatus()) && rental.isOverdue()) {
                                overdueRentals.add(rental);
                            }
                        }
                    }

                    // Sort by days late (most overdue first)
                    Collections.sort(overdueRentals, (r1, r2) ->
                        Integer.compare(r2.getDaysLate(), r1.getDaysLate()));

                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(overdueRentals.isEmpty() ? View.VISIBLE : View.GONE);
                    adapter.updateList(overdueRentals);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error loading overdue rentals", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void onPayClick(Rental rental) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_pay_overdue, null);

        TextView bicycleModelText = dialogView.findViewById(R.id.bicycleModelText);
        TextView daysLateText = dialogView.findViewById(R.id.daysLateText);
        TextView amountText = dialogView.findViewById(R.id.amountText);
        TextInputEditText transactionIdInput = dialogView.findViewById(R.id.transactionIdInput);
        TextInputEditText detailsInput = dialogView.findViewById(R.id.detailsInput);
        MaterialButton confirmButton = dialogView.findViewById(R.id.confirmButton);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);

        int daysLate = rental.getDaysLate();
        double charges = rental.calculateOverdueCharges();

        bicycleModelText.setText("Bicycle: " + rental.getBicycleModel());
        daysLateText.setText("Days Late: " + daysLate);
        amountText.setText(String.format("Amount: %.2f Taka", charges));

        AlertDialog dialog = builder.setView(dialogView).create();

        confirmButton.setOnClickListener(v -> {
            String txId = transactionIdInput.getText() != null ? transactionIdInput.getText().toString().trim() : "";
            String details = detailsInput.getText() != null ? detailsInput.getText().toString().trim() : "";

            if (txId.isEmpty()) {
                transactionIdInput.setError("Transaction ID required");
                transactionIdInput.requestFocus();
                return;
            }

            submitOverduePayment(rental, txId, details, charges);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void submitOverduePayment(Rental rental, String txId, String details, double amount) {
        String messageId = messagesRef.push().getKey();

        String messageText = "Overdue payment for " + rental.getBicycleModel() +
                ". Days late: " + rental.getDaysLate() +
                ". Amount: " + amount + " Taka.";

        if (!details.isEmpty()) {
            messageText += " Details: " + details;
        }

        OverduePaymentMessage payment = new OverduePaymentMessage();
        payment.setMessageId(messageId);
        payment.setSenderId(currentUserId);
        payment.setSenderEmail(currentUserEmail);
        payment.setReceiverId(rental.getOwnerId());
        payment.setReceiverEmail(rental.getOwnerEmail());
        payment.setMessageType("overdue_payment");
        payment.setTransactionId(txId);
        payment.setMessage(messageText);
        payment.setAmount(amount);
        payment.setRentalId(rental.getRentalId());
        payment.setStatus("Pending");
        payment.setTimestamp(System.currentTimeMillis());

        messagesRef.child(messageId).setValue(payment)
            .addOnSuccessListener(v -> {
                Snackbar.make(rootView,
                    "Payment submitted. Awaiting owner approval.",
                    Snackbar.LENGTH_LONG).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(requireContext(),
                    "Error submitting payment: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            });
    }
}
