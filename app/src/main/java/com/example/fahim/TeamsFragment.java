package com.example.fahim;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Arrays;
import java.util.List;
import com.example.fahim.adapters.TeamAdapter;
import com.example.fahim.models.Team;
import com.example.fahim.controllers.TeamController;

public class TeamsFragment extends Fragment {
    private RecyclerView teamsRecyclerView;
    private FloatingActionButton fabAddTeam;
    private List<String> sportsList = Arrays.asList("Football", "Cricket", "Badminton", "Volleyball");
    private TeamController teamController;
    private TeamAdapter teamAdapter;
    private List<Team> teams;
    private boolean isAdmin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAdmin = ((MainActivity) requireActivity()).isAdmin();
        teamController = new TeamController(requireContext(), isAdmin);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_teams, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        teamsRecyclerView = view.findViewById(R.id.teamsRecyclerView);
        fabAddTeam = view.findViewById(R.id.fabAddTeam);

        // Setup RecyclerView
        teamsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadTeams();

        // Setup FAB - only show for admin
        if (isAdmin) {
            fabAddTeam.setVisibility(View.VISIBLE);
            fabAddTeam.setOnClickListener(v -> showSportsSelectionDialog());
        } else {
            fabAddTeam.setVisibility(View.GONE);
        }
    }

    private void showSportsSelectionDialog() {
        if (!isAdmin) {
            Toast.makeText(requireContext(), "Admin access required", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] sports = sportsList.toArray(new String[0]);
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Select Sport")
                .setItems(sports, (dialog, which) -> {
                    String selectedSport = sports[which];
                    Intent intent = new Intent(requireActivity(), TeamManagementActivity.class);
                    intent.putExtra("sport_type", selectedSport);
                    startActivity(intent);
                })
                .show();
    }

    private void loadTeams() {
        try {
            teams = teamController.getAllTeams();
            teamAdapter = new TeamAdapter(requireContext(), teams, isAdmin, team -> {
                // Handle team click - show team details
                TeamDetailsFragment fragment = TeamDetailsFragment.newInstance(team.getId(), team.getSportType());
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            });
            teamsRecyclerView.setAdapter(teamAdapter);
        } catch (SecurityException e) {
            Toast.makeText(requireContext(), "Access denied", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error loading teams", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (teamsRecyclerView != null) {
            loadTeams(); // Refresh teams when returning to this fragment
        }
    }
}
