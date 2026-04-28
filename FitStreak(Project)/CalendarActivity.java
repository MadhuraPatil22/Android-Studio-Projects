package com.example.streak;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.streak.databinding.ActivityCalendarBinding;
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

public class CalendarActivity extends AppCompatActivity {

    private ActivityCalendarBinding binding;
    private List<CalendarDay> calendarDays;
    private CalendarAdapter calendarAdapter;
    private String userId;
    private String currentMonthYear;
    private SimpleDateFormat dayKeyFormat = new SimpleDateFormat("MM_yyyy_d", Locale.getDefault());
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM_yyyy", Locale.getDefault());
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId).child("history");

        // Set up toolbar back button
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupCalendarBase();
        fetchStreakData();
        fetchChartData();
    }


    private void setupCalendarBase() {
        calendarDays = new ArrayList<>();
        String currentMonthName = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault()).format(new Date());
        binding.monthYearText.setText(currentMonthName);

        // Fill with correct number of days
        Calendar cal = Calendar.getInstance();
        int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= maxDays; i++) {
            calendarDays.add(new CalendarDay(i, CalendarAdapter.DayStatus.NONE));
        }

        binding.calendarRecycler.setLayoutManager(new GridLayoutManager(this, 7));
        
        currentMonthYear = monthYearFormat.format(new Date());
        
        calendarAdapter = new CalendarAdapter(calendarDays, day -> {
            if (day.getStatus() == CalendarAdapter.DayStatus.COMPLETED || day.getStatus() == CalendarAdapter.DayStatus.PARTIAL) {
                showDayDetails(day);
            } else if (day.getStatus() == CalendarAdapter.DayStatus.MISSED) {
                Toast.makeText(this, "You haven't done exercise for this day", Toast.LENGTH_SHORT).show();
            }
        });
        binding.calendarRecycler.setAdapter(calendarAdapter);
        binding.btnCloseDetails.setOnClickListener(v -> binding.dayDetailsCard.setVisibility(View.GONE));
    }

    private void showDayDetails(CalendarDay day) {
        String dayKey = currentMonthYear + "_" + day.getDay();
        binding.dayDetailsCard.setVisibility(View.VISIBLE);
        binding.tvSelectedDay.setText(new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(RecordDate(day.getDay())) + " Details");
        
        String node = (day.getStatus() == CalendarAdapter.DayStatus.PARTIAL) ? "partial_progress" : "history";
        
        FirebaseDatabase.getInstance().getReference("users").child(userId)
                .child(node).child(dayKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isFinishing() || isDestroyed() || binding == null) return;
                List<String> exerciseDetails = new ArrayList<>();
                int totalDuration = 0;
                float totalCalories = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);
                    Integer time = ds.child("time").getValue(Integer.class);
                    Integer count = ds.child("count").getValue(Integer.class);
                    
                    if (name != null) {
                        int t = (time != null) ? time : 0;
                        int c = (count != null) ? count : 0;
                        totalDuration += t;
                        totalCalories += calculateCalories(name, t);
                        
                        String detail = "• " + name;
                        if (t > 0 || c > 0) {
                            detail += " (" + (t > 0 ? t + "m " : "") + (c > 0 ? c + " reps" : "") + ")";
                        }
                        exerciseDetails.add(detail);
                    }
                }

                binding.tvDayCalories.setText(String.format(Locale.getDefault(), "%.0f kcal", totalCalories));
                binding.tvDayDuration.setText(totalDuration + " min");
                
                setupDayExercisesList(exerciseDetails);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private Date RecordDate(int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }

    private float calculateCalories(String name, int mins) {
        float met = 5.0f; // Default for general exercise
        String n = name.toLowerCase();
        if (n.contains("run")) met = 8.0f;
        else if (n.contains("push") || n.contains("pull")) met = 4.0f;
        else if (n.contains("squat") || n.contains("leg")) met = 5.0f;
        else if (n.contains("cardio")) met = 7.0f;
        // Assume avg weight 70kg: kcal = MET * 3.5 * weight / 200 * mins
        return met * 3.5f * 70f / 200f * mins;
    }

    private void setupDayExercisesList(List<String> details) {
        binding.dayExerciseRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.dayExerciseRecycler.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                TextView tv = new TextView(parent.getContext());
                tv.setPadding(8, 8, 8, 8);
                tv.setTextSize(14f);
                tv.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.primary_dark));
                return new RecyclerView.ViewHolder(tv) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((TextView)holder.itemView).setText(details.get(position));
            }

            @Override
            public int getItemCount() {
                return details.size();
            }
        });
    }

    private void fetchStreakData() {
        String currentMonthYear = new SimpleDateFormat("MM_yyyy", Locale.getDefault()).format(new Date());
        int todayDay = Integer.parseInt(new SimpleDateFormat("d", Locale.getDefault()).format(new Date()));
        Calendar cal = Calendar.getInstance();

        mDatabase.getParent().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    for (int i = 1; i <= maxDays; i++) {
                        calendarDays.set(i - 1, new CalendarDay(i, i < todayDay ? CalendarAdapter.DayStatus.MISSED : CalendarAdapter.DayStatus.NONE));
                    }
                    calendarAdapter.notifyDataSetChanged();
                    return;
                }
                DataSnapshot historySnap = snapshot.child("history");
                DataSnapshot partialSnap = snapshot.child("partial_progress");

                int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = 1; i <= maxDays; i++) {
                    String dayKey = currentMonthYear + "_" + i;
                    if (historySnap.hasChild(dayKey)) {
                        calendarDays.set(i - 1, new CalendarDay(i, CalendarAdapter.DayStatus.COMPLETED));
                    } else if (partialSnap.hasChild(dayKey)) {
                        calendarDays.set(i - 1, new CalendarDay(i, CalendarAdapter.DayStatus.PARTIAL));
                    } else if (i < todayDay) {
                        calendarDays.set(i - 1, new CalendarDay(i, CalendarAdapter.DayStatus.MISSED));
                    } else {
                        calendarDays.set(i - 1, new CalendarDay(i, CalendarAdapter.DayStatus.NONE));
                    }
                }
                calendarAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CalendarActivity.this, "Error loading streaks", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchChartData() {
        DatabaseReference dailyRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("daily_totals");
        dailyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isFinishing() || isDestroyed() || binding == null) return;
                
                List<BarChartView.BarData> barData = new ArrayList<>();
                List<LineChartView.DataPoint> lineData = new ArrayList<>();
                
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat labelFormat = new SimpleDateFormat("E", Locale.getDefault()); // e.g. Mon, Tue
                SimpleDateFormat keyFormat = new SimpleDateFormat("MM_yyyy_d", Locale.getDefault());
                
                for (int i = 6; i >= 0; i--) {
                    Calendar tempCal = (Calendar) cal.clone();
                    tempCal.add(Calendar.DAY_OF_YEAR, -i);
                    String key = keyFormat.format(tempCal.getTime());
                    String label = labelFormat.format(tempCal.getTime());
                    
                    float mins = 0;
                    float cals = 0;
                    
                    DataSnapshot ds = snapshot.child(key);
                    if (ds.exists()) {
                        Object mObj = ds.child("totalMinutes").getValue();
                        Object cObj = ds.child("totalCalories").getValue();
                        if (mObj instanceof Number) mins = ((Number)mObj).floatValue();
                        if (cObj instanceof Number) cals = ((Number)cObj).floatValue();
                    }
                    
                    barData.add(new BarChartView.BarData(label, mins));
                    lineData.add(new LineChartView.DataPoint(label, cals));
                }
                
                binding.barChart.setData(barData);
                binding.lineChart.setData(lineData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

}
