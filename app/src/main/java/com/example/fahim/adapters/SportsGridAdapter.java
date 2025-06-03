package com.example.fahim.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.fahim.R;
import java.util.List;

public class SportsGridAdapter extends BaseAdapter {
    private Context context;
    private List<String> sportsList;

    public SportsGridAdapter(Context context, List<String> sportsList) {
        this.context = context;
        this.sportsList = sportsList;
    }

    @Override
    public int getCount() {
        return sportsList.size();
    }

    @Override
    public Object getItem(int position) {
        return sportsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sport, parent, false);
        }

        TextView sportName = convertView.findViewById(R.id.sportName);
        sportName.setText(sportsList.get(position));

        return convertView;
    }
} 