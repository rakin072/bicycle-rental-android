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
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class OverdueAdapter extends RecyclerView.Adapter<OverdueAdapter.OverdueViewHolder> {

    private List<Rental> overdueRentals;
    private OnPayClickListener onPayClickListener;

    public interface OnPayClickListener {
        void onPayClick(Rental rental);
    }

    public OverdueAdapter(OnPayClickListener listener) {
        this.overdueRentals = new ArrayList<>();
        this.onPayClickListener = listener;
    }

    @NonNull
    @Override
    public OverdueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_overdue_rental, parent, false);
        return new OverdueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OverdueViewHolder holder, int position) {
        Rental rental = overdueRentals.get(position);
        holder.bind(rental);
    }

    @Override
    public int getItemCount() {
        return overdueRentals.size();
    }

    public void updateList(List<Rental> newRentals) {
        this.overdueRentals = newRentals;
        notifyDataSetChanged();
    }

    class OverdueViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cardView;
        private TextView bicycleModelText;
        private TextView ownerEmailText;
        private TextView rentDateText;
        private TextView dueDateText;
        private TextView daysLateText;
        private TextView overdueChargesText;
        private MaterialButton payButton;

        public OverdueViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            bicycleModelText = itemView.findViewById(R.id.bicycleModelText);
            ownerEmailText = itemView.findViewById(R.id.ownerEmailText);
            rentDateText = itemView.findViewById(R.id.rentDateText);
            dueDateText = itemView.findViewById(R.id.dueDateText);
            daysLateText = itemView.findViewById(R.id.daysLateText);
            overdueChargesText = itemView.findViewById(R.id.overdueChargesText);
            payButton = itemView.findViewById(R.id.payButton);

            payButton.setOnClickListener(v -> {
                if (onPayClickListener != null) {
                    onPayClickListener.onPayClick(overdueRentals.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Rental rental) {
            bicycleModelText.setText("Bicycle: " + rental.getBicycleModel());
            ownerEmailText.setText("Owner: " + (rental.getOwnerEmail() != null ? rental.getOwnerEmail() : "Unknown"));
            rentDateText.setText("Rent Date: " + rental.getRentDate());
            dueDateText.setText("Due Date: " + rental.getDueDate());

            int daysLate = rental.getDaysLate();
            double charges = rental.calculateOverdueCharges();

            daysLateText.setText("⏰ Days Late: " + daysLate);
            overdueChargesText.setText(String.format("💰 Overdue Charges: %.2f Taka", charges));

            // Apply red color to overdue elements
            int errorColor = itemView.getContext().getColor(R.color.error);
            dueDateText.setTextColor(errorColor);
            daysLateText.setTextColor(errorColor);
            overdueChargesText.setTextColor(errorColor);

            // Apply red border to card
            cardView.setStrokeColor(errorColor);
            cardView.setStrokeWidth(4);
        }
    }
}
