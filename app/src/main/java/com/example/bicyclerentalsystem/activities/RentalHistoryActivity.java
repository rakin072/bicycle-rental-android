package com.example.bicyclerentalsystem.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.adapters.RentalAdapter;
import com.example.bicyclerentalsystem.models.Rental;
import com.example.bicyclerentalsystem.repository.RentalRepository;
import java.util.List;

public class RentalHistoryActivity extends AppCompatActivity {

    private RecyclerView rentalRecyclerView;
    private RentalAdapter rentalAdapter;
    private RentalRepository rentalRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_history);

        rentalRecyclerView = findViewById(R.id.recycler_view_rental_history);
        rentalRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rentalRepository = new RentalRepository();
        loadRentalHistory();
    }

    private void loadRentalHistory() {
        rentalRepository.getRentalHistory(new RentalRepository.RentalHistoryCallback() {
            @Override
            public void onSuccess(List<Rental> rentals) {
                rentalAdapter = new RentalAdapter(rentals);
                rentalRecyclerView.setAdapter(rentalAdapter);
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }
}