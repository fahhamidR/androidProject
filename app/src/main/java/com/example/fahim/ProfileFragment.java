package com.example.fahim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private static final String PREF_NAME = "login_pref";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_NAME_KEY = "name";

    private TextView nameTextView, emailTextView, roleTextView;
    private Button logoutButton;
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        nameTextView = view.findViewById(R.id.nameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        roleTextView = view.findViewById(R.id.roleTextView);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Load user data
        loadUserData();

        // Set up logout button
        logoutButton.setOnClickListener(v -> {
            SharedPreferences prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
            requireActivity().finish();
            startActivity(new Intent(requireContext(), LoginActivity.class));
        });

        return view;
    }

    private void loadUserData() {
        try {
            SharedPreferences prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String email = prefs.getString(PREF_EMAIL, "");
            String name = prefs.getString(PREF_NAME_KEY, "");
            String role = prefs.getString("role", "user"); // Get role from SharedPreferences

            Log.d("ProfileFragment", "Loading user data - Email: " + email + ", Name: " + name + ", Role: " + role);

            if (!email.isEmpty()) {
                // Set email
                emailTextView.setText("Email: " + email);
                
                // Set name if available
                if (!name.isEmpty()) {
                    nameTextView.setText("Name: " + name);
                } else {
                    nameTextView.setText("Name: Not set");
                }
                
                // Set role based on SharedPreferences
                roleTextView.setText("Role: " + role.substring(0, 1).toUpperCase() + role.substring(1));
                
                Log.d("ProfileFragment", "User data loaded successfully");
            } else {
                Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show();
                Log.e("ProfileFragment", "User data not found - Email is empty");
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error loading user data", Toast.LENGTH_SHORT).show();
            Log.e("ProfileFragment", "Error loading user data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
