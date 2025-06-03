package com.example.fahim.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fahim.R;
import com.example.fahim.models.Player;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {
    private List<Player> players;
    private boolean isAdmin;
    private OnPlayerEditListener editListener;

    public interface OnPlayerEditListener {
        void onPlayerEdit(Player player, int position);
    }

    public PlayerAdapter(List<Player> players, boolean isAdmin) {
        this.players = players;
        this.isAdmin = isAdmin;
    }

    public void setOnPlayerEditListener(OnPlayerEditListener listener) {
        this.editListener = listener;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = players.get(position);
        holder.name.setText(player.getName());
        holder.position.setText(player.getPosition());
        
        // Show edit button only for admin users
        holder.editButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        
        holder.editButton.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onPlayerEdit(player, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public void updatePlayer(int position, Player updatedPlayer) {
        if (position >= 0 && position < players.size()) {
            players.set(position, updatedPlayer);
            notifyItemChanged(position);
        }
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView name, position;
        ImageButton editButton;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.playerName);
            position = itemView.findViewById(R.id.playerPosition);
            editButton = itemView.findViewById(R.id.editPlayerButton);
        }
    }
} 