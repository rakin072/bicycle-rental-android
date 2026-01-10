package com.example.bicyclerentalsystem.viewmodel;

import com.example.bicyclerentalsystem.models.User;
import com.example.bicyclerentalsystem.repository.AuthRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AuthViewModel {
    private AuthRepository authRepository;

    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void login(String email, String password, AuthCallback callback) {
        authRepository.login(email, password, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess();
            } else {
                callback.onError(task.getException() != null ? 
                        task.getException().getMessage() : "Login failed");
            }
        });
    }

    public void registerUser(String email, String password, String username, String passwordHint, AuthCallback callback) {
        authRepository.register(email, password, task -> {
            if (task.isSuccessful()) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                User user = new User();
                user.setUserId(userId);
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setPasswordHint(passwordHint);
                FirebaseDatabase.getInstance().getReference("users")
                        .child(userId).setValue(user);
                callback.onSuccess();
            } else {
                callback.onError(task.getException() != null ? 
                        task.getException().getMessage() : "Registration failed");
            }
        });
    }

    public interface AuthCallback {
        void onSuccess();
        void onError(String error);
    }
}
