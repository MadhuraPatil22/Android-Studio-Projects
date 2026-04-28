package com.example.streak;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.streak.databinding.ActivityMainBinding;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private List<Exercise> exerciseList;
    private ExerciseAdapter exerciseAdapter;
    private DatabaseReference mDatabase;
    private String userId;
    private float userWeight = 70.0f; // Default weight in kg

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Initial UI Setup
        userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        
        // 2. Dark Mode Setup (from saved preference)
        android.content.SharedPreferences prefs = getSharedPreferences("FitStreakPrefs", MODE_PRIVATE);
        boolean isDarkLocal = prefs.getBoolean("darkMode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkLocal ? 
            AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // 3. Functional Logic
        setupExerciseList();
        fetchFirebaseData();
        checkNotificationPermission();
        NotificationHelper.scheduleReminders(this);

        binding.btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        binding.btnAddExercise.setOnClickListener(v -> {
            String name = binding.etExerciseName.getText().toString();
            if (!name.isEmpty()) {
                addExerciseToFirebase(name);
                binding.etExerciseName.setText("");
            }
        });

        binding.btnDone.setOnClickListener(v -> {
            markDayAsCompletedInFirebase();
            Toast.makeText(this, R.string.congrats_message, Toast.LENGTH_LONG).show();
        });

        setupAddExerciseUI();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        menu.add(0, 1, 0, "Calendar").setIcon(R.drawable.ic_calendar).setShowAsAction(android.view.MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == 1) {
            startActivity(new Intent(MainActivity.this, CalendarActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupAddExerciseUI() {
        String[] exercises = getResources().getStringArray(R.array.predefined_exercises);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, exercises);
        binding.etExerciseName.setAdapter(adapter);

        binding.btnTutorial.setOnClickListener(v -> {
            String name = binding.etExerciseName.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please select/type an exercise first", Toast.LENGTH_SHORT).show();
                return;
            }
            String query = name + " for beginners";
            Intent intent = new Intent(Intent.ACTION_VIEW, 
                android.net.Uri.parse("https://www.youtube.com/results?search_query=" + query));
            startActivity(intent);
        });
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void setupExerciseList() {
        exerciseList = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(exerciseList, new ExerciseAdapter.OnExerciseStatusChangeListener() {
            @Override
            public void onStatusChanged() {
                updateDoneButton();
                syncPartialProgressToFirebase();
            }

            @Override
            public void onDelete(int position) {
                removeExerciseFromFirebase(exerciseList.get(position).getName());
            }
        });

        binding.exerciseRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.exerciseRecycler.setAdapter(exerciseAdapter);
    }

    private void fetchFirebaseData() {
        String monthKey = new SimpleDateFormat("MM_yyyy_d", Locale.getDefault()).format(new Date());
        
        // Only listen to 'exercises' changes (to handle adding/removing exercises)
        // This prevents the 'focus reset' bug because typing writes to 'partial_progress'
        mDatabase.child("exercises").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot exercisesSnap) {
                if (isFinishing() || isDestroyed() || binding == null) return;

                // First, get the current partial progress once
                mDatabase.child("partial_progress").child(monthKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot partialSnap) {
                        if (isFinishing() || isDestroyed() || binding == null) return;
                        
                        Map<String, DataSnapshot> partialMap = new HashMap<>();
                        if (partialSnap.exists()) {
                            for (DataSnapshot ds : partialSnap.getChildren()) {
                                String name = ds.child("name").getValue(String.class);
                                if (name != null) partialMap.put(name, ds);
                            }
                        }

                        exerciseList.clear();
                        if (exercisesSnap.exists()) {
                            for (DataSnapshot postSnapshot : exercisesSnap.getChildren()) {
                                String name = postSnapshot.getKey();
                                if (name == null) continue;
                                Exercise ex = new Exercise(name);
                                if (partialMap.containsKey(name)) {
                                    DataSnapshot ds = partialMap.get(name);
                                    if (ds != null) {
                                        Boolean isDone = ds.child("isDone").getValue(Boolean.class);
                                        // Use Number to be safe with Firebase Long/Integer
                                        Object timeVal = ds.child("time").getValue();
                                        Object countVal = ds.child("count").getValue();
                                        
                                        if (isDone != null) ex.setDone(isDone);
                                        if (timeVal instanceof Number) ex.setTime(((Number)timeVal).intValue());
                                        if (countVal instanceof Number) ex.setCount(((Number)countVal).intValue());
                                    }
                                }
                                exerciseList.add(ex);
                            }
                        } else {
                            initDefaultExercises();
                        }
                        
                        if (exerciseAdapter != null) exerciseAdapter.notifyDataSetChanged();
                        updateDoneButton();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Check if already done today
        String todayKey = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        mDatabase.child("last_completion_date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String lastDate = snapshot.getValue(String.class);
                if (todayKey.equals(lastDate)) {
                    binding.btnDone.setEnabled(false);
                    // Optionally set exercises to checked, but for simplicity we'll just disable the button
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Fetch User Weight for accurate calorie calculation
        mDatabase.child("profile").child("weight").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object weightVal = snapshot.getValue();
                if (weightVal instanceof Number) {
                    userWeight = ((Number) weightVal).floatValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void initDefaultExercises() {
        String[] defaults = {
            "Push-ups", "Squats", "Plank", "Lunges", 
            "Burpees", "Jumping Jacks", "Mountain Climbers", 
            "Crunches", "Pull-ups", "Bench Press", 
            "Deadlifts", "Overhead Press"
        };
        for (String s : defaults) {
            mDatabase.child("exercises").child(s).setValue(true);
        }
    }

    private void addExerciseToFirebase(String name) {
        mDatabase.child("exercises").child(name).setValue(true);
    }

    private void removeExerciseFromFirebase(String name) {
        mDatabase.child("exercises").child(name).removeValue();
    }

    private void updateDoneButton() {
        if (binding == null) return;
        
        if (exerciseList == null || exerciseList.isEmpty()) {
            binding.btnDone.setEnabled(false);
            return;
        }

        int doneCount = 0;
        for (Exercise e : exerciseList) {
            if (e.isDone()) {
                doneCount++;
            }
        }

        boolean allDone = (doneCount == exerciseList.size() && !exerciseList.isEmpty());
        binding.btnDone.setEnabled(allDone);

        // Update Progress Circle (NEW)
        float percentage = 0f;
        if (!exerciseList.isEmpty()) {
            percentage = (float) doneCount / exerciseList.size() * 100f;
        }
        
        int progressColor = ContextCompat.getColor(this, R.color.primary);
        if (percentage == 100) progressColor = Color.parseColor("#4CAF50"); // Green
        else if (percentage > 50) progressColor = Color.parseColor("#FFC107"); // Amber
        
        binding.progressCircle.setProgress(percentage, progressColor, "Today");
        
        String status = "Not Started Yet";
        if (percentage == 100) status = "Perfect Day! 🔥";
        else if (percentage > 75) status = "Almost There!";
        else if (percentage > 0) status = "Keep Going!";
        
        binding.tvProgressStatus.setText(status);
        binding.tvProgressSubtitle.setText(doneCount + " of " + exerciseList.size() + " exercises completed");
    }


    private void syncPartialProgressToFirebase() {
        String monthKey = new SimpleDateFormat("MM_yyyy_d", Locale.getDefault()).format(new Date());
        List<Map<String, Object>> progress = new ArrayList<>();
        for (Exercise e : exerciseList) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", e.getName());
            map.put("isDone", e.isDone());
            map.put("time", e.getTime());
            map.put("count", e.getCount());
            progress.add(map);
        }
        mDatabase.child("partial_progress").child(monthKey).setValue(progress);
    }

    private void markDayAsCompletedInFirebase() {
        String todayKey = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String monthKey = new SimpleDateFormat("MM_yyyy_d", Locale.getDefault()).format(new Date());

        List<Map<String, Object>> completedExercises = new ArrayList<>();
        float totalCalories = 0;
        float totalMinutesFloat = 0;

        for (Exercise e : exerciseList) {
            if (e.isDone()) {
                Map<String, Object> exMap = new HashMap<>();
                exMap.put("name", e.getName());
                exMap.put("time", e.getTime());
                exMap.put("count", e.getCount());
                
                float exerciseMinutes = e.getTime() + (e.getCount() * 4.0f / 60.0f);
                float calories = calculateCalories(e.getName(), exerciseMinutes);
                
                exMap.put("calories", calories);
                totalCalories += calories;
                totalMinutesFloat += exerciseMinutes;
                
                completedExercises.add(exMap);
            }
        }

        mDatabase.child("history").child(monthKey).setValue(completedExercises);
        mDatabase.child("last_completion_date").setValue(todayKey);
        
        // Save daily totals for quick access in charts
        Map<String, Object> dayTotal = new HashMap<>();
        dayTotal.put("totalMinutes", (int)totalMinutesFloat);
        dayTotal.put("totalCalories", totalCalories);
        mDatabase.child("daily_totals").child(monthKey).setValue(dayTotal);
        binding.btnDone.setEnabled(false);
    }

    private float calculateCalories(String name, float minutes) {
        float met = 3.0f; // Default
        String n = name.toLowerCase();
        if (n.contains("running")) met = 9.8f;
        else if (n.contains("jogging")) met = 7.0f;
        else if (n.contains("skipping")) met = 8.0f;
        else if (n.contains("stretching")) met = 2.3f;
        else if (n.contains("neck")) met = 2.0f;
        else if (n.contains("push")) met = 8.0f;
        
        return met * userWeight * (minutes / 60.0f);
    }
}
