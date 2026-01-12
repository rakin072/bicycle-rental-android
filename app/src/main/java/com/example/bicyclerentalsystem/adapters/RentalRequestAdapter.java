package com.example.bicyclerentalsystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.models.Rental;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;

public class RentalRequestAdapter extends RecyclerView.Adapter<RentalRequestAdapter.RentalRequestViewHolder> {

    private List<Rental> rentalList;
    private OnActionClickListener onActionClickListener;

    public RentalRequestAdapter(OnActionClickListener listener) {
        this.rentalList = new ArrayList<>();
        this.onActionClickListener = listener;
    }

    @NonNull
    @Override
    public RentalRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rental_request, parent, false);
        return new RentalRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalRequestViewHolder holder, int position) {
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

    class RentalRequestViewHolder extends RecyclerView.ViewHolder {

        private TextView bicycleModelText;
        private TextView renterEmailText;
        private TextView rentDateText;
        private TextView dueDateText;
        private Chip statusChip;
        private LinearLayout actionButtonsLayout;
        private MaterialButton approveButton;
        private MaterialButton rejectButton;

        public RentalRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            bicycleModelText = itemView.findViewById(R.id.bicycleModelText);
            renterEmailText = itemView.findViewById(R.id.renterEmailText);
            rentDateText = itemView.findViewById(R.id.rentDateText);
            dueDateText = itemView.findViewById(R.id.dueDateText);
            statusChip = itemView.findViewById(R.id.statusChip);
            actionButtonsLayout = itemView.findViewById(R.id.actionButtonsLayout);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);

            approveButton.setOnClickListener(v -> {
                if (onActionClickListener != null) {
                    onActionClickListener.onApprove(rentalList.get(getAdapterPosition()));
                }
            });

            rejectButton.setOnClickListener(v -> {
                if (onActionClickListener != null) {
                    onActionClickListener.onReject(rentalList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Rental rental) {
            bicycleModelText.setText("Bicycle: " + rental.getBicycleModel());
            renterEmailText.setText("Renter: " + (rental.getRenterEmail() != null ? rental.getRenterEmail() : "Unknown"));
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
            
            // Show approve/reject buttons only if status is Pending
            if ("Pending".equals(rental.getStatus())) {
                actionButtonsLayout.setVisibility(View.VISIBLE);
            } else {
                actionButtonsLayout.setVisibility(View.GONE);
            }
        }
    }

    public interface OnActionClickListener {
        void onApprove(Rental rental);
        void onReject(Rental rental);
    }
}
