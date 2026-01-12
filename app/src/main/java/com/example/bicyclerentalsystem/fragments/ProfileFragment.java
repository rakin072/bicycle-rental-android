package com.example.bicyclerentalsystem.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.activities.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private DatabaseReference usersRef;
    private DatabaseReference bicyclesRef;
    private DatabaseReference rentalsRef;
    private DatabaseReference messagesRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private String currentUserEmail;
    
    private TextView emailText, userIdText, memberSinceText;
    private TextView totalBicyclesValue, activeRentalsValue, completedRentalsValue, overdueChargesValue;
    private TextView totalRentalsText, overdueAmountText;
    private TextInputEditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private MaterialButton updatePasswordBtn, deleteAccountBtn, logoutBtn;
    
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        context = requireContext();
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        if (currentUser == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        
        currentUserId = currentUser.getUid();
        currentUserEmail = currentUser.getEmail();
        
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        bicyclesRef = FirebaseDatabase.getInstance().getReference("bicycles");
        rentalsRef = FirebaseDatabase.getInstance().getReference("rentals");
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");
        
        // Initialize views
        emailText = view.findViewById(R.id.emailText);
        userIdText = view.findViewById(R.id.userIdText);
        memberSinceText = view.findViewById(R.id.memberSinceText);
        
        totalBicyclesValue = view.findViewById(R.id.totalBicyclesValue);
        activeRentalsValue = view.findViewById(R.id.activeRentalsValue);
        completedRentalsValue = view.findViewById(R.id.completedRentalsValue);
        overdueChargesValue = view.findViewById(R.id.overdueChargesValue);
        
        totalRentalsText = view.findViewById(R.id.totalRentalsText);
        overdueAmountText = view.findViewById(R.id.overdueAmountText);
        
        currentPasswordInput = view.findViewById(R.id.currentPasswordInput);
        newPasswordInput = view.findViewById(R.id.newPasswordInput);
        confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput);
        
        updatePasswordBtn = view.findViewById(R.id.updatePasswordBtn);
        deleteAccountBtn = view.findViewById(R.id.deleteAccountBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        
        // Setup functionality
        loadUserProfile();
        setupChangePassword();
        setupDeleteAccount();
        setupLogout();
    }
    
    private void loadUserProfile() {
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double overdueCharges = snapshot.child("overdueCharges").getValue(Double.class);
                    if (overdueCharges == null) overdueCharges = 0.0;
                    
                    Long createdAt = snapshot.child("createdAt").getValue(Long.class);
                    if (createdAt == null) createdAt = System.currentTimeMillis();
                    
                    // Display basic info
                    emailText.setText(currentUserEmail);
                    String shortId = currentUserId.length() > 8 ? 
                        currentUserId.substring(0, 8) + "..." : currentUserId;
                    userIdText.setText("ID: " + shortId);
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.US);
                    memberSinceText.setText("Member Since: " + sdf.format(new Date(createdAt)));
                    
                    // Display overdue charges
                    overdueChargesValue.setText(String.format(Locale.US, "%.2f Taka", overdueCharges));
                    overdueAmountText.setText(String.format(Locale.US, "%.2f Taka", overdueCharges));
                    
                    if (overdueCharges > 0) {
                        overdueChargesValue.setTextColor(getResources().getColor(R.color.error));
                        overdueAmountText.setTextColor(getResources().getColor(R.color.error));
                    } else {
                        overdueChargesValue.setTextColor(getResources().getColor(R.color.success));
                        overdueAmountText.setTextColor(getResources().getColor(R.color.success));
                    }
                    
                    // Load statistics
                    loadStatistics();
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error loading profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadStatistics() {
        // Load total bicycles
        bicyclesRef.orderByChild("ownerId").equalTo(currentUserId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = (int) snapshot.getChildrenCount();
                    totalBicyclesValue.setText(String.valueOf(count));
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        
        // Load rental statistics
        rentalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int activeCount = 0;
                int completedCount = 0;
                int totalCount = 0;
                
                for (DataSnapshot child : snapshot.getChildren()) {
                    String renterId = child.child("renterId").getValue(String.class);
                    String status = child.child("status").getValue(String.class);
                    
                    if (currentUserId.equals(renterId)) {
                        totalCount++;
                        if ("Active".equals(status)) {
                            activeCount++;
                        } else if ("Completed".equals(status)) {
                            completedCount++;
                        }
                    }
                }
                
                activeRentalsValue.setText(String.valueOf(activeCount));
                completedRentalsValue.setText(String.valueOf(completedCount));
                totalRentalsText.setText("Total Rentals: " + totalCount);
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    
    private void setupChangePassword() {
        updatePasswordBtn.setOnClickListener(v -> {
            String currentPassword = currentPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();
            
            // Validate inputs
            if (currentPassword.isEmpty()) {
                currentPasswordInput.setError("Enter current password");
                currentPasswordInput.requestFocus();
                return;
            }
            
            if (newPassword.isEmpty()) {
                newPasswordInput.setError("Enter new password");
                newPasswordInput.requestFocus();
                return;
            }
            
            if (newPassword.length() < 6) {
                newPasswordInput.setError("Password must be at least 6 characters");
                newPasswordInput.requestFocus();
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords do not match");
                confirmPasswordInput.requestFocus();
                return;
            }
            
            showChangePasswordConfirmation(currentPassword, newPassword);
        });
    }
    
    private void showChangePasswordConfirmation(String currentPassword, String newPassword) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Change Password?")
            .setMessage("Your password will be updated")
            .setPositiveButton("Confirm", (dialog, which) -> {
                changePassword(currentPassword, newPassword);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void changePassword(String currentPassword, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;
        
        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Updating password...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Re-authenticate user first
        AuthCredential credential = EmailAuthProvider.getCredential(
            currentUserEmail, currentPassword);
        
        user.reauthenticate(credential)
            .addOnSuccessListener(v -> {
                // Update password in Firebase Auth
                user.updatePassword(newPassword)
                    .addOnSuccessListener(v2 -> {
                        // Update password in database (if storing hashed)
                        usersRef.child(currentUserId).child("password")
                            .setValue(hashPassword(newPassword));
                        
                        progressDialog.dismiss();
                        
                        // Clear inputs
                        currentPasswordInput.setText("");
                        newPasswordInput.setText("");
                        confirmPasswordInput.setText("");
                        
                        Snackbar.make(getView(), "Password updated successfully", 
                            Snackbar.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(context, 
                            "Error updating password: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    });
            })
            .addOnFailureListener(e -> {
                progressDialog.dismiss();
                currentPasswordInput.setError("Current password is incorrect");
                Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show();
            });
    }
    
    private String hashPassword(String password) {
        // Simple hashing (use BCrypt or similar in production)
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return password; // Fallback
        }
    }
    
    private void setupDeleteAccount() {
        deleteAccountBtn.setOnClickListener(v -> {
            showDeleteAccountDialog();
        });
    }
    
    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_delete_account, null);
        
        TextInputEditText confirmInput = dialogView.findViewById(R.id.confirmDeleteInput);
        
        AlertDialog dialog = builder.setView(dialogView)
            .setPositiveButton("Delete Account", null)
            .setNegativeButton("Cancel", null)
            .create();
        
        dialog.show();
        
        // Override positive button to validate
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String confirmation = confirmInput.getText().toString().trim();
            
            if (!"DELETE".equals(confirmation)) {
                confirmInput.setError("Type 'DELETE' to confirm");
                return;
            }
            
            deleteAccount();
            dialog.dismiss();
        });
    }
    
    private void deleteAccount() {
        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Deleting account...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Delete in order: messages -> rentals -> bicycles -> user -> auth
        deleteUserMessages(() -> {
            deleteUserRentals(() -> {
                deleteUserBicycles(() -> {
                    deleteUserData(() -> {
                        deleteAuthAccount(progressDialog);
                    });
                });
            });
        });
    }
    
    private void deleteUserMessages(Runnable onComplete) {
        messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> toDelete = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String senderId = child.child("senderId").getValue(String.class);
                    String receiverId = child.child("receiverId").getValue(String.class);
                    
                    if (currentUserId.equals(senderId) || currentUserId.equals(receiverId)) {
                        toDelete.add(child.getKey());
                    }
                }
                
                // Delete all user messages
                for (String msgId : toDelete) {
                    messagesRef.child(msgId).removeValue();
                }
                
                onComplete.run();
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    
    private void deleteUserRentals(Runnable onComplete) {
        rentalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> toDelete = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String renterId = child.child("renterId").getValue(String.class);
                    String ownerId = child.child("ownerId").getValue(String.class);
                    
                    if (currentUserId.equals(renterId) || currentUserId.equals(ownerId)) {
                        toDelete.add(child.getKey());
                    }
                }
                
                // Delete all user rentals
                for (String rentalId : toDelete) {
                    rentalsRef.child(rentalId).removeValue();
                }
                
                onComplete.run();
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    
    private void deleteUserBicycles(Runnable onComplete) {
        bicyclesRef.orderByChild("ownerId").equalTo(currentUserId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        child.getRef().removeValue();
                    }
                    onComplete.run();
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
    }
    
    private void deleteUserData(Runnable onComplete) {
        usersRef.child(currentUserId).removeValue()
            .addOnSuccessListener(v -> onComplete.run())
            .addOnFailureListener(e -> {
                Toast.makeText(context, "Error deleting user data", Toast.LENGTH_SHORT).show();
            });
    }
    
    private void deleteAuthAccount(ProgressDialog progressDialog) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            progressDialog.dismiss();
            return;
        }
        
        user.delete()
            .addOnSuccessListener(v -> {
                progressDialog.dismiss();
                Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                
                // Navigate to login screen
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            })
            .addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(context, 
                    "Error deleting account: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }
    
    private void setupLogout() {
        logoutBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    logout();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });
    }
    
    private void logout() {
        mAuth.signOut();
        
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        // Navigate to login screen
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}