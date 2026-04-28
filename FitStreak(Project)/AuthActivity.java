package com.example.streak;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.streak.databinding.ActivityAuthBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    private boolean isLoginMode = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startDashboard();
        }

        updateUI();

        binding.toggleAuth.setOnClickListener(v -> {
            isLoginMode = !isLoginMode;
            updateUI();
        });

        binding.authButton.setOnClickListener(v -> handleAuth());

        binding.googleButton.setOnClickListener(v -> {
            Toast.makeText(this, "Google Sign-In coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleAuth() {
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isLoginMode) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startDashboard();
                    } else {
                        Toast.makeText(AuthActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startDashboard();
                    } else {
                        Toast.makeText(AuthActivity.this, "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    private void startDashboard() {
        startActivity(new Intent(AuthActivity.this, MainActivity.class));
        finish();
    }

    private void updateUI() {
        if (isLoginMode) {
            binding.authTitle.setText(R.string.login);
            binding.authButton.setText(R.string.login);
            binding.toggleAuth.setText(R.string.no_account);
        } else {
            binding.authTitle.setText(R.string.signup);
            binding.authButton.setText(R.string.signup);
            binding.toggleAuth.setText(R.string.have_account);
        }
    }
}
