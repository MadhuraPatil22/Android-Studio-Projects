package com.example.streak;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;
import com.example.streak.databinding.ActivityExerciseDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ExerciseDetailsActivity extends AppCompatActivity {

    private ActivityExerciseDetailsBinding binding;
    private DatabaseReference mDatabase;
    private List<RecordedExercise> recordedExercises;
    private DetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String dayKey = getIntent().getStringExtra("dayKey");
        int day = getIntent().getIntExtra("day", 0);
        boolean isPartial = getIntent().getBooleanExtra("isPartial", false);
        
        binding.toolbar.setTitle(isPartial ? "Current Progress" : "Recorded Exercises");
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.tvDateHeader.setText((isPartial ? "Working on Day " : "Completed on Day ") + day);

        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null || dayKey == null) {
            finish();
            return;
        }

        String node = isPartial ? "partial_progress" : "history";
        mDatabase = FirebaseDatabase.getInstance().getReference("users")
                .child(userId).child(node).child(dayKey);

        setupRecycler();
        fetchDetails();
    }

    private void setupRecycler() {
        recordedExercises = new ArrayList<>();
        adapter = new DetailsAdapter(recordedExercises);
        binding.detailsRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.detailsRecycler.setAdapter(adapter);
    }

    private void fetchDetails() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recordedExercises.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String name = ds.child("name").getValue(String.class);
                        Integer time = ds.child("time").getValue(Integer.class);
                        Integer count = ds.child("count").getValue(Integer.class);
                        if (name != null) {
                            recordedExercises.add(new RecordedExercise(name, 
                                time != null ? time : 0, 
                                count != null ? count : 0));
                        }
                    }
                } else {
                    recordedExercises.add(new RecordedExercise("No exercises found for this day.", 0, 0));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private static class RecordedExercise {
        String name;
        int time;
        int count;

        RecordedExercise(String name, int time, int count) {
            this.name = name;
            this.time = time;
            this.count = count;
        }
    }

    private static class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {
        private List<RecordedExercise> exercises;

        DetailsAdapter(List<RecordedExercise> exercises) {
            this.exercises = exercises;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RecordedExercise ex = exercises.get(position);
            String detail = "• " + ex.name;
            if (ex.time > 0 || ex.count > 0) {
                detail += " (" + (ex.time > 0 ? ex.time + "m " : "") + 
                                (ex.count > 0 ? ex.count + " reps" : "") + ")";
            }
            if (!ex.name.startsWith("No exercises") && !ex.name.contains("Workout completed")) {
                // If we're showing partial progress, we might want to show if it's done or not (optional)
            }
            holder.textView.setText(detail);
            holder.textView.setTextColor(parentContextColor(holder.itemView));
            holder.textView.setTextSize(16f);
        }

        private int parentContextColor(View v) {
            // Use the standard text color from theme
            android.util.TypedValue typedValue = new android.util.TypedValue();
            v.getContext().getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
            return ContextCompat.getColor(v.getContext(), typedValue.resourceId);
        }

        @Override
        public int getItemCount() {
            return exercises.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
