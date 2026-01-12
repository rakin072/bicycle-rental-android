package com.example.bicyclerentalsystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.models.Rental;
import java.util.List;

public class RentalAdapter extends RecyclerView.Adapter<RentalAdapter.RentalViewHolder> {

    private List<Rental> rentalList;

    public RentalAdapter(List<Rental> rentalList) {
        this.rentalList = rentalList;
    }

    @NonNull
    @Override
    public RentalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rental, parent, false);
        return new RentalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalViewHolder holder, int position) {
        Rental rental = rentalList.get(position);
        holder.bind(rental);
    }

    @Override
    public int getItemCount() {
        return rentalList.size();
    }

    static class RentalViewHolder extends RecyclerView.ViewHolder {
        private TextView rentalDetails;

        public RentalViewHolder(@NonNull View itemView) {
            super(itemView);
            rentalDetails = itemView.findViewById(R.id.rental_details);
        }

        public void bind(Rental rental) {
            rentalDetails.setText("Renter ID: " + rental.getRenterId() + "\nBicycle ID: " + rental.getBicycleId() + "\nRent Date: " + rental.getRentDate());
        }
    }
}