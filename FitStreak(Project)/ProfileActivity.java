package com.example.streak;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.streak.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.material.materialswitch.MaterialSwitch;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private DatabaseReference mDatabase;
    private String userId;

    private boolean isUserAction = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            finish();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId).child("profile");

        loadProfileData();

        binding.btnSaveProfile.setOnClickListener(v -> saveProfileData());

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUserAction) return;
            
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);
            
            // Save locally and to Firebase
            getSharedPreferences("FitStreakPrefs", MODE_PRIVATE).edit().putBoolean("darkMode", isChecked).apply();
            mDatabase.child("darkMode").setValue(isChecked);
        });

    }

    private void loadProfileData() {
        isUserAction = false;
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.etName.setText(snapshot.child("name").getValue(String.class));
                    binding.etGender.setText(snapshot.child("gender").getValue(String.class));
                    binding.etDob.setText(snapshot.child("dob").getValue(String.class));
                    binding.etEmail.setText(snapshot.child("email").getValue(String.class));
                    binding.etMobile.setText(snapshot.child("mobile").getValue(String.class));
                    binding.etAddress.setText(snapshot.child("address").getValue(String.class));
                    
                    Object weightObj = snapshot.child("weight").getValue();
                    if (weightObj != null) {
                        binding.etWeight.setText(String.valueOf(weightObj));
                    }
                    
                    Boolean dark = snapshot.child("darkMode").getValue(Boolean.class);
                    if (dark != null) {
                        binding.switchDarkMode.setChecked(dark);
                    }
                    
                }
                isUserAction = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isUserAction = true;
                Toast.makeText(ProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileData() {
        Map<String, Object> profile = new HashMap<>();
        profile.put("name", binding.etName.getText().toString());
        profile.put("gender", binding.etGender.getText().toString());
        profile.put("dob", binding.etDob.getText().toString());
        profile.put("email", binding.etEmail.getText().toString());
        profile.put("mobile", binding.etMobile.getText().toString());
        profile.put("address", binding.etAddress.getText().toString());
        String weightStr = binding.etWeight.getText().toString();
        if (!weightStr.isEmpty()) {
            profile.put("weight", Double.parseDouble(weightStr));
        }
        profile.put("darkMode", binding.switchDarkMode.isChecked());

        mDatabase.setValue(profile).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "Profile Saved to Cloud!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
