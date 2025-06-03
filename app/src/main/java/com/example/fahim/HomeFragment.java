package com.example.fahim;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {

    private TextView welcomeText;
    private String currentUserEmail;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        welcomeText = view.findViewById(R.id.welcomeText);
        GridLayout gridLayout = view.findViewById(R.id.gridLayout);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", "").toLowerCase().trim();

        // Set welcome message
        welcomeText.setText("Welcome to BAUST Sports Arena");

        // Handle card clicks
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View card = gridLayout.getChildAt(i);
            int index = i;
            card.setOnClickListener(v -> {
                Fragment selectedFragment = null;
                switch (index) {
                    case 0:
                        selectedFragment = new TeamsFragment();
                        break;
                    case 1:
                        selectedFragment = new EventsFragment();
                        break;
                    case 2:
                        selectedFragment = new ProfileFragment();
                        break;
                    case 3:
                        selectedFragment = new NewsFragment();
                        break;
                }

                if (selectedFragment != null) {
                    FragmentTransaction transaction = requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction();
                    transaction.replace(R.id.fragment_container, selectedFragment); // Use your actual fragment container ID
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }

        return view;
    }
}
