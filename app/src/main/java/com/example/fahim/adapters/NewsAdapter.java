package com.example.fahim.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fahim.R;
import com.example.fahim.models.NewsModel;
import com.example.fahim.DatabaseHelper;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private Context context;
    private List<NewsModel> newsList;
    private boolean isAdmin;
    private DatabaseHelper dbHelper;

    public NewsAdapter(Context context, List<NewsModel> newsList, boolean isAdmin) {
        this.context = context;
        this.newsList = newsList;
        this.isAdmin = isAdmin;
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        NewsModel news = newsList.get(position);
        holder.title.setText(news.getTitle());
        holder.description.setText(news.getDescription());
        holder.date.setText(news.getDate());

        // Show delete button only for admin
        holder.btnDelete.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        if (isAdmin) {
            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete News")
                        .setMessage("Are you sure you want to delete this news?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (dbHelper.deleteNews((int)news.getId())) {
                                newsList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, newsList.size());
                                Toast.makeText(context, "News deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to delete news", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date;
        Button btnDelete;

        public NewsViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.newsTitle);
            description = itemView.findViewById(R.id.newsDescription);
            date = itemView.findViewById(R.id.newsDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
} 