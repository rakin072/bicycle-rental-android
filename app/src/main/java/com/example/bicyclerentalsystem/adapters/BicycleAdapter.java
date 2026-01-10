package com.example.bicyclerentalsystem.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.models.Bicycle;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;

public class BicycleAdapter extends RecyclerView.Adapter<BicycleAdapter.BicycleViewHolder> {

    private List<Bicycle> bicycleList;
    private OnDeleteClickListener onDeleteClickListener;

    public BicycleAdapter(OnDeleteClickListener listener) {
        this.bicycleList = new ArrayList<>();
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public BicycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bicycle, parent, false);
        return new BicycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BicycleViewHolder holder, int position) {
        Bicycle bicycle = bicycleList.get(position);
        holder.bind(bicycle);
    }

    @Override
    public int getItemCount() {
        return bicycleList.size();
    }

    public void updateList(List<Bicycle> newBicycles) {
        this.bicycleList = newBicycles;
        notifyDataSetChanged();
    }

    class BicycleViewHolder extends RecyclerView.ViewHolder {

        private TextView bicycleIdText;
        private TextView modelText;
        private Chip typeChip;
        private TextView ownerEmailText;
        private MaterialButton deleteButton;

        public BicycleViewHolder(@NonNull View itemView) {
            super(itemView);
            bicycleIdText = itemView.findViewById(R.id.bicycleIdText);
            modelText = itemView.findViewById(R.id.modelText);
            typeChip = itemView.findViewById(R.id.typeChip);
            ownerEmailText = itemView.findViewById(R.id.ownerEmailText);
            deleteButton = itemView.findViewById(R.id.rentButton);

            deleteButton.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(bicycleList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Bicycle bicycle) {
            bicycleIdText.setText("ID: " + bicycle.getBicycleId());
            modelText.setText(bicycle.getModel());
            typeChip.setText(bicycle.getType());
            
            // Set chip color based on type
            if ("Electric".equals(bicycle.getType())) {
                typeChip.setChipBackgroundColorResource(R.color.stat_card_secondary);
            } else {
                typeChip.setChipBackgroundColorResource(R.color.status_active);
            }
            
            ownerEmailText.setText(bicycle.getOwnerEmail());
            
            // Change button text to Delete
            deleteButton.setText("DELETE");
            deleteButton.setBackgroundColor(Color.parseColor("#D32F2F")); // Red color for delete
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Bicycle bicycle);
    }
}