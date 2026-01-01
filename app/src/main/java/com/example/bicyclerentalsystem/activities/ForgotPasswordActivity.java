package com.example.bicyclerentalsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bicyclerentalsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editTextHint;
    private Button buttonVerifyHint;
    private TextView textViewCancel;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextHint = findViewById(R.id.editTextHint);
        buttonVerifyHint = findViewById(R.id.buttonVerifyHint);
        textViewCancel = findViewById(R.id.textViewCancel);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        buttonVerifyHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyHint();
            }
        });

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void verifyHint() {
        final String enteredHint = editTextHint.getText().toString().trim();

        if (enteredHint.isEmpty()) {
            Toast.makeText(this, "Please enter your password hint", Toast.LENGTH_SHORT).show();
            return;
        }

        // Search through all users to find matching hint
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hintFound = false;
                String foundUserId = "";

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String storedHint = userSnapshot.child("passwordHint").getValue(String.class);
                    
                    if (storedHint != null && storedHint.equals(enteredHint)) {
                        hintFound = true;
                        foundUserId = userSnapshot.getKey();
                        break;
                    }
                }

                if (hintFound) {
                    // Navigate to reset password page
                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("userId", foundUserId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, 
                        "Hint not found. Please try again or contact support.", 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ForgotPasswordActivity.this, 
                    "Database error: " + databaseError.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
}
