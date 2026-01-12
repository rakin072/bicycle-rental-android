package com.example.bicyclerentalsystem.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.models.RecentRental;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class RecentRentalAdapter extends RecyclerView.Adapter<RecentRentalAdapter.ViewHolder> {

    private List<RecentRental> recentRentals;

    public RecentRentalAdapter() {
        this.recentRentals = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_rental, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentRental rental = recentRentals.get(position);
        holder.textBicycleModel.setText(rental.getBicycleModel());
        holder.textRentDate.setText(rental.getRentDate());
        holder.chipStatus.setText(rental.getStatus());

        // Set chip color based on status
        int chipColor;
        switch (rental.getStatus().toLowerCase()) {
            case "active":
                chipColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_active);
                break;
            case "pending":
                chipColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_pending);
                break;
            case "completed":
                chipColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_completed);
                break;
            default:
                chipColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.subtitle_gray);
                break;
        }
        holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(chipColor));
    }

    @Override
    public int getItemCount() {
        return recentRentals.size();
    }

    public void setRecentRentals(List<RecentRental> recentRentals) {
        this.recentRentals = recentRentals;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textBicycleModel;
        TextView textRentDate;
        Chip chipStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textBicycleModel = itemView.findViewById(R.id.text_bicycle_model);
            textRentDate = itemView.findViewById(R.id.text_rent_date);
            chipStatus = itemView.findViewById(R.id.chip_status);
        }
    }
}
