package com.example.bicyclerentalsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.fragments.BicyclesFragment;
import com.example.bicyclerentalsystem.fragments.DashboardHomeFragment;
import com.example.bicyclerentalsystem.fragments.MessagesFragment;
import com.example.bicyclerentalsystem.fragments.OverdueFragment;
import com.example.bicyclerentalsystem.fragments.ProfileFragment;
import com.example.bicyclerentalsystem.fragments.RentalsFragment;
import com.example.bicyclerentalsystem.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        sessionManager = new SessionManager(this);

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Setup toolbar
        setSupportActionBar(toolbar);

        // Setup navigation drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        // Set hamburger (drawer) icon color to Instagram blue (primary)
        toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.primary));

        // Setup navigation view
        navigationView.setNavigationItemSelectedListener(this);

        // Load dashboard fragment as default
        if (savedInstanceState == null) {
            loadFragment(new DashboardHomeFragment());
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        String title = "";

        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_dashboard) {
            selectedFragment = new DashboardHomeFragment();
            title = "Dashboard";
        } else if (itemId == R.id.nav_bicycles) {
            selectedFragment = new BicyclesFragment();
            title = "Bicycles";
        } else if (itemId == R.id.nav_rentals) {
            selectedFragment = new RentalsFragment();
            title = "Rentals";
        } else if (itemId == R.id.nav_overdue) {
            selectedFragment = new OverdueFragment();
            title = "Overdue";
        } else if (itemId == R.id.nav_messages) {
            selectedFragment = new MessagesFragment();
            title = "Messages";
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
            title = "Profile";
        } else if (itemId == R.id.nav_logout) {
            logout();
            return true;
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            toolbar.setTitle(title);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        sessionManager.logout();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    public void navigateToOverdueTab() {
        loadFragment(new OverdueFragment());
        toolbar.setTitle("Overdue");
        navigationView.setCheckedItem(R.id.nav_overdue);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
