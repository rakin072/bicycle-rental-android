package com.example.bicyclerentalsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.adapters.BicycleAdapter;
import com.example.bicyclerentalsystem.models.Bicycle;
import com.example.bicyclerentalsystem.repository.BicycleRepository;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BicycleAdapter bicycleAdapter;
    private BicycleRepository bicycleRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recycler_view_bicycles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bicycleRepository = new BicycleRepository();
        loadBicycles();
    }

    private void loadBicycles() {
        bicycleRepository.getAllBicycles(new BicycleRepository.BicycleCallback() {
            @Override
            public void onSuccess(List<Bicycle> bicycles) {
                bicycleAdapter = new BicycleAdapter(bicycle -> {
                    Intent intent = new Intent(HomeActivity.this, BicycleDetailActivity.class);
                    intent.putExtra("bicycle_id", bicycle.getBicycleId());
                    startActivity(intent);
                });
                bicycleAdapter.updateList(bicycles);
                recyclerView.setAdapter(bicycleAdapter);
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }
}