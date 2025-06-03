package com.example.fahim;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.fahim.controllers.AuthController;

public class MainActivity extends AppCompatActivity {
    private AuthController authController;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize AuthController
        authController = new AuthController(this);
        
        // Check if user is logged in
        if (!authController.isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Get admin status after confirming user is logged in
        isAdmin = authController.isCurrentUserAdmin();
        Log.d("MainActivity", "User is admin: " + isAdmin);

        setupNavigation();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_events) {
                selectedFragment = new EventsFragment();
            } else if (id == R.id.nav_news) {
                selectedFragment = new NewsFragment();
            } else if (id == R.id.nav_teams) {
                selectedFragment = new TeamsFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Set default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh admin status when returning to MainActivity
        isAdmin = authController.isCurrentUserAdmin();
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
