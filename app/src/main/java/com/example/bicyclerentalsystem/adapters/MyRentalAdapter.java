package com.example.bicyclerentalsystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.models.Rental;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;

public class MyRentalAdapter extends RecyclerView.Adapter<MyRentalAdapter.MyRentalViewHolder> {

    private List<Rental> rentalList;
    private OnReturnClickListener onReturnClickListener;

    public MyRentalAdapter(OnReturnClickListener listener) {
        this.rentalList = new ArrayList<>();
        this.onReturnClickListener = listener;
    }

    @NonNull
    @Override
    public MyRentalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_rental, parent, false);
        return new MyRentalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRentalViewHolder holder, int position) {
        Rental rental = rentalList.get(position);
        holder.bind(rental);
    }

    @Override
    public int getItemCount() {
        return rentalList.size();
    }

    public void updateList(List<Rental> newRentals) {
        this.rentalList = newRentals;
        notifyDataSetChanged();
    }

    class MyRentalViewHolder extends RecyclerView.ViewHolder {

        private TextView bicycleModelText;
        private TextView ownerEmailText;
        private TextView rentDateText;
        private TextView dueDateText;
        private Chip statusChip;
        private MaterialButton returnButton;

        public MyRentalViewHolder(@NonNull View itemView) {
            super(itemView);
            bicycleModelText = itemView.findViewById(R.id.bicycleModelText);
            ownerEmailText = itemView.findViewById(R.id.ownerEmailText);
            rentDateText = itemView.findViewById(R.id.rentDateText);
            dueDateText = itemView.findViewById(R.id.dueDateText);
            statusChip = itemView.findViewById(R.id.statusChip);
            returnButton = itemView.findViewById(R.id.returnButton);

            returnButton.setOnClickListener(v -> {
                if (onReturnClickListener != null) {
                    onReturnClickListener.onReturnClick(rentalList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Rental rental) {
            bicycleModelText.setText(rental.getBicycleModel());
            ownerEmailText.setText("Owner: " + (rental.getOwnerEmail() != null ? rental.getOwnerEmail() : "Unknown"));
            rentDateText.setText("Rent Date: " + rental.getRentDate());
            dueDateText.setText("Due Date: " + rental.getDueDate());
            statusChip.setText(rental.getStatus());
            
            // Set chip color based on status
            if ("Pending".equals(rental.getStatus())) {
                statusChip.setChipBackgroundColorResource(R.color.status_pending);
            } else if ("Active".equals(rental.getStatus())) {
                statusChip.setChipBackgroundColorResource(R.color.status_active);
            } else if ("Completed".equals(rental.getStatus())) {
                statusChip.setChipBackgroundColorResource(R.color.status_completed);
            }
            
            // Show return button only if status is Active
            if ("Active".equals(rental.getStatus())) {
                returnButton.setVisibility(View.VISIBLE);
            } else {
                returnButton.setVisibility(View.GONE);
            }
        }
    }

    public interface OnReturnClickListener {
        void onReturnClick(Rental rental);
    }
}
