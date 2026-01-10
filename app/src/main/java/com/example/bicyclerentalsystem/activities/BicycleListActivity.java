package com.example.bicyclerentalsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.adapters.BicycleAdapter;
import com.example.bicyclerentalsystem.models.Bicycle;
import com.example.bicyclerentalsystem.repository.BicycleRepository;
import java.util.List;

public class BicycleListActivity extends AppCompatActivity {

    private ListView bicycleListView;
    private BicycleAdapter bicycleAdapter;
    private BicycleRepository bicycleRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bicycle_list);

        bicycleListView = findViewById(R.id.bicycleListView);
        bicycleRepository = new BicycleRepository();

        loadBicycles();

        Button addBicycleButton = findViewById(R.id.addBicycleButton);
        addBicycleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BicycleListActivity.this, AddBicycleActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadBicycles() {
        bicycleRepository.getAllBicycles(new BicycleRepository.BicycleCallback() {
            @Override
            public void onSuccess(List<Bicycle> bicycles) {
                // Note: BicycleAdapter needs to be updated to work with ListView properly
                // This is a placeholder
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }
}