package com.example.fahim.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fahim.R;
import com.example.fahim.TeamManagementActivity;
import com.example.fahim.models.Team;
import java.util.List;
import com.example.fahim.DatabaseHelper;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {
    private Context context;
    private List<Team> teams;
    private boolean isAdmin;
    private OnTeamClickListener listener;
    private DatabaseHelper dbHelper;

    public interface OnTeamClickListener {
        void onTeamClick(Team team);
    }

    public TeamAdapter(Context context, List<Team> teams, boolean isAdmin, OnTeamClickListener listener) {
        this.context = context;
        this.teams = teams;
        this.isAdmin = isAdmin;
        this.listener = listener;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teams.get(position);
        holder.teamNameText.setText(team.getName());
        holder.teamDescriptionText.setText(team.getDescription());
        holder.playerCountText.setText(team.getPlayers().size() + " Players");

        // Set up view details button
        holder.viewDetailsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTeamClick(team);
            }
        });

        // Set up edit button
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, TeamManagementActivity.class);
            intent.putExtra("team_id", team.getId());
            intent.putExtra("sport_type", team.getSportType());
            context.startActivity(intent);
        });

        // Set up delete button
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Team")
                    .setMessage("Are you sure you want to delete this team?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (dbHelper.deleteTeam(team.getId())) {
                            teams.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, teams.size());
                            Toast.makeText(context, "Team deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error deleting team", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    public void updateTeams(List<Team> newTeams) {
        this.teams = newTeams;
        notifyDataSetChanged();
    }

    public static class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView teamNameText, teamDescriptionText, playerCountText;
        Button viewDetailsButton, editButton, deleteButton;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            teamNameText = itemView.findViewById(R.id.teamName);
            teamDescriptionText = itemView.findViewById(R.id.teamDescription);
            playerCountText = itemView.findViewById(R.id.playerCount);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 