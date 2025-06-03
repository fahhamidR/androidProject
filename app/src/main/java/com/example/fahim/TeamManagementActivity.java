package com.example.fahim;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.fahim.adapters.PlayerAdapter;
import com.example.fahim.models.Player;
import com.example.fahim.models.Team;
import java.util.ArrayList;
import java.util.List;

public class TeamManagementActivity extends AppCompatActivity implements PlayerAdapter.OnPlayerEditListener {
    private static final String EXTRA_SPORT_TYPE = "sport_type";
    private static final String EXTRA_TEAM_ID = "team_id";

    private TextInputEditText teamNameInput;
    private AutoCompleteTextView sportTypeInput;
    private TextInputEditText teamDescriptionInput;
    private RecyclerView playersRecyclerView;
    private MaterialButton addPlayerButton;
    private MaterialButton saveTeamButton;
    
    private DatabaseHelper dbHelper;
    private PlayerAdapter playerAdapter;
    private List<Player> players;
    private long teamId = -1;
    private String selectedSport;

    public static void start(Context context, String sportType) {
        Intent intent = new Intent(context, TeamManagementActivity.class);
        intent.putExtra(EXTRA_SPORT_TYPE, sportType);
        context.startActivity(intent);
    }

    public static void start(Context context, long teamId) {
        Intent intent = new Intent(context, TeamManagementActivity.class);
        intent.putExtra(EXTRA_TEAM_ID, teamId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_management);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Team Management");

        // Initialize views
        teamNameInput = findViewById(R.id.teamNameInput);
        sportTypeInput = findViewById(R.id.sportTypeInput);
        teamDescriptionInput = findViewById(R.id.teamDescriptionInput);
        playersRecyclerView = findViewById(R.id.playersRecyclerView);
        addPlayerButton = findViewById(R.id.addPlayerButton);
        saveTeamButton = findViewById(R.id.saveTeamButton);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Get sport type or team ID from intent
        selectedSport = getIntent().getStringExtra(EXTRA_SPORT_TYPE);
        teamId = getIntent().getLongExtra(EXTRA_TEAM_ID, -1);

        if (selectedSport == null && teamId == -1) {
            Toast.makeText(this, "Error: Sport type or team ID not specified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup sport type dropdown
        String[] sportTypes = {"Football", "Cricket", "Badminton", "Volleyball"};
        ArrayAdapter<String> sportAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, sportTypes);
        sportTypeInput.setAdapter(sportAdapter);
        
        if (selectedSport != null) {
            // Set the sport type from the user's selection
            sportTypeInput.setText(selectedSport, false);
            sportTypeInput.setEnabled(false); // Disable editing for new team creation
            getSupportActionBar().setTitle("Create " + selectedSport + " Team");
        }

        // Setup RecyclerView
        players = new ArrayList<>();
        playerAdapter = new PlayerAdapter(players, true);
        playerAdapter.setOnPlayerEditListener(this);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playersRecyclerView.setAdapter(playerAdapter);

        // Load team data if editing existing team
        if (teamId != -1) {
            loadTeamData();
        }

        // Setup click listeners
        addPlayerButton.setOnClickListener(v -> showAddPlayerDialog());
        saveTeamButton.setOnClickListener(v -> saveTeam());
    }

    private void showAddPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Player");

        // Create the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_player, null);
        EditText playerNameInput = dialogView.findViewById(R.id.playerNameInput);
        EditText playerRoleInput = dialogView.findViewById(R.id.playerRoleInput);

        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String playerName = playerNameInput.getText().toString().trim();
            String playerRole = playerRoleInput.getText().toString().trim();

            if (playerName.isEmpty() || playerRole.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (players.size() >= 11) {
                Toast.makeText(this, "Maximum 11 players allowed per team", Toast.LENGTH_SHORT).show();
                return;
            }

            Player newPlayer = new Player(-1, (int)teamId, playerName, playerRole, selectedSport);
            players.add(newPlayer);
            playerAdapter.notifyItemInserted(players.size() - 1);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadTeamData() {
        Team team = dbHelper.getTeam(teamId);
        if (team != null) {
            teamNameInput.setText(team.getName());
            sportTypeInput.setText(team.getSportType(), false);
            teamDescriptionInput.setText(team.getDescription());
            selectedSport = team.getSportType();
            players.clear();
            players.addAll(team.getPlayers());
            playerAdapter.notifyDataSetChanged();
        }
    }

    private void saveTeam() {
        String teamName = teamNameInput.getText().toString().trim();
        String sportType = sportTypeInput.getText().toString().trim();
        String description = teamDescriptionInput.getText().toString().trim();

        if (teamName.isEmpty() || sportType.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (players.size() != 11) {
            Toast.makeText(this, "Please add exactly 11 players", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create or update team
        Team team = new Team((int)teamId, teamName, sportType, description);
        if (teamId == -1) {
            teamId = dbHelper.createTeam(team);
            if (teamId == -1) {
                Toast.makeText(this, "Error creating team", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (dbHelper.updateTeam(team) == 0) {
                Toast.makeText(this, "Error updating team", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Save players
        for (Player player : players) {
            player.setTeamId((int)teamId);
            if (player.getId() == -1) {
                dbHelper.createPlayer(player);
            } else {
                dbHelper.updatePlayer(player);
            }
        }

        Toast.makeText(this, "Team saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showEditPlayerDialog(Player player, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Player");

        // Create the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_player, null);
        EditText playerNameInput = dialogView.findViewById(R.id.playerNameInput);
        EditText playerRoleInput = dialogView.findViewById(R.id.playerRoleInput);

        // Pre-fill the fields with existing player data
        playerNameInput.setText(player.getName());
        playerRoleInput.setText(player.getPosition());

        builder.setView(dialogView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String playerName = playerNameInput.getText().toString().trim();
            String playerRole = playerRoleInput.getText().toString().trim();

            if (playerName.isEmpty() || playerRole.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the player object
            Player updatedPlayer = new Player(player.getId(), player.getTeamId(), playerName, playerRole, selectedSport);
            players.set(position, updatedPlayer);
            playerAdapter.notifyItemChanged(position);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    @Override
    public void onPlayerEdit(Player player, int position) {
        showEditPlayerDialog(player, position);
    }
} 