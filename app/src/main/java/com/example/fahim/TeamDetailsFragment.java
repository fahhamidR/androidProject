package com.example.fahim;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fahim.adapters.PlayerAdapter;
import com.example.fahim.models.Player;
import com.example.fahim.models.Team;
import java.util.List;

public class TeamDetailsFragment extends Fragment {
    private static final String ARG_TEAM_ID = "team_id";
    private static final String ARG_SPORT_TYPE = "sport_type";

    private long teamId;
    private String sportType;
    private DatabaseHelper dbHelper;
    private RecyclerView playersRecyclerView;
    private TextView teamNameText, teamDescriptionText;

    public static TeamDetailsFragment newInstance(long teamId, String sportType) {
        TeamDetailsFragment fragment = new TeamDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TEAM_ID, teamId);
        args.putString(ARG_SPORT_TYPE, sportType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            teamId = getArguments().getLong(ARG_TEAM_ID);
            sportType = getArguments().getString(ARG_SPORT_TYPE);
        }
        dbHelper = new DatabaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_details, container, false);
        
        teamNameText = view.findViewById(R.id.teamName);
        teamDescriptionText = view.findViewById(R.id.teamDescription);
        playersRecyclerView = view.findViewById(R.id.playersRecyclerView);
        
        loadTeamDetails();
        
        return view;
    }

    private void loadTeamDetails() {
        Team team = dbHelper.getTeam(teamId);
        if (team != null) {
            teamNameText.setText(team.getName());
            teamDescriptionText.setText(team.getDescription());
            
            List<Player> players = dbHelper.getTeamPlayers(teamId);
            PlayerAdapter adapter = new PlayerAdapter(players, true);
            playersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            playersRecyclerView.setAdapter(adapter);
        }
    }
} 