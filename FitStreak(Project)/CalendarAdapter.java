package com.example.streak;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.streak.databinding.ItemCalendarDayBinding;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private List<CalendarDay> days;
    private OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(CalendarDay day);
    }

    public CalendarAdapter(List<CalendarDay> days, OnDayClickListener listener) {
        this.days = days;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCalendarDayBinding binding = ItemCalendarDayBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalendarDay day = days.get(position);
        holder.binding.tvDay.setText(String.valueOf(day.getDay()));
        
        switch (day.getStatus()) {
            case COMPLETED:
                holder.binding.tvStreak.setVisibility(View.VISIBLE);
                holder.binding.tvStreak.setText("🔥");
                break;
            case PARTIAL:
                holder.binding.tvStreak.setVisibility(View.VISIBLE);
                holder.binding.tvStreak.setText("✅");
                break;
            case MISSED:
                holder.binding.tvStreak.setVisibility(View.VISIBLE);
                holder.binding.tvStreak.setText("❌");
                break;
            default:
                holder.binding.tvStreak.setVisibility(View.INVISIBLE);
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDayClick(day);
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemCalendarDayBinding binding;
        public ViewHolder(ItemCalendarDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public enum DayStatus {
        NONE, COMPLETED, PARTIAL, MISSED
    }
}

class CalendarDay {
    private int day;
    private CalendarAdapter.DayStatus status;

    public CalendarDay(int day, CalendarAdapter.DayStatus status) {
        this.day = day;
        this.status = status;
    }

    public int getDay() { return day; }
    public CalendarAdapter.DayStatus getStatus() { return status; }
    public void setStatus(CalendarAdapter.DayStatus status) { this.status = status; }
}
