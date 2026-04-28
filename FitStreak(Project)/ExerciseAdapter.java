package com.example.streak;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.streak.databinding.ItemExerciseBinding;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private List<Exercise> exerciseList;
    private OnExerciseStatusChangeListener listener;

    public interface OnExerciseStatusChangeListener {
        void onStatusChanged();
        void onDelete(int position);
    }

    public ExerciseAdapter(List<Exercise> exerciseList, OnExerciseStatusChangeListener listener) {
        this.exerciseList = exerciseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExerciseBinding binding = ItemExerciseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        
        holder.binding.tvExerciseName.setText(exercise.getName());
        holder.binding.cbExercise.setOnCheckedChangeListener(null);
        holder.binding.cbExercise.setChecked(exercise.isDone());

        // Important: Remove old watchers using the cached listener references
        if (holder.timeWatcher != null) holder.binding.etTime.removeTextChangedListener(holder.timeWatcher);
        if (holder.countWatcher != null) holder.binding.etCount.removeTextChangedListener(holder.countWatcher);

        holder.binding.etTime.setText(exercise.getTime() > 0 ? String.valueOf(exercise.getTime()) : "");
        holder.binding.etCount.setText(exercise.getCount() > 0 ? String.valueOf(exercise.getCount()) : "");

        View.OnClickListener clickListener = v -> {
            holder.binding.etTime.requestFocus();
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) 
                v.getContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(holder.binding.etTime, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            }
        };
        holder.itemView.setOnClickListener(clickListener);
        holder.binding.etTime.setOnClickListener(clickListener);
        holder.binding.etCount.setOnClickListener(clickListener);

        holder.binding.cbExercise.setOnCheckedChangeListener((buttonView, isChecked) -> {
            exercise.setDone(isChecked);
            listener.onStatusChanged();
        });

        holder.timeWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                try {
                    int val = s.toString().isEmpty() ? 0 : Integer.parseInt(s.toString());
                    if (exercise.getTime() != val) {
                        exercise.setTime(val);
                        listener.onStatusChanged();
                    }
                } catch (Exception e) {}
            }
        };

        holder.countWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                try {
                    int val = s.toString().isEmpty() ? 0 : Integer.parseInt(s.toString());
                    if (exercise.getCount() != val) {
                        exercise.setCount(val);
                        listener.onStatusChanged();
                    }
                } catch (Exception e) {}
            }
        };

        holder.binding.etTime.addTextChangedListener(holder.timeWatcher);
        holder.binding.etCount.addTextChangedListener(holder.countWatcher);

        holder.binding.btnDeleteExercise.setOnClickListener(v -> {
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                listener.onDelete(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemExerciseBinding binding;
        TextWatcher timeWatcher;
        TextWatcher countWatcher;
        public ViewHolder(ItemExerciseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

class Exercise {
    private String name;
    private boolean isDone;
    private int time; // minutes
    private int count;
    private float calories;

    public Exercise(String name) {
        this.name = name;
        this.isDone = false;
        this.time = 0;
        this.count = 0;
        this.calories = 0;
    }

    public String getName() { return name; }
    public boolean isDone() { return isDone; }
    public void setDone(boolean done) { isDone = done; }

    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public float getCalories() { return calories; }
    public void setCalories(float calories) { this.calories = calories; }
}
