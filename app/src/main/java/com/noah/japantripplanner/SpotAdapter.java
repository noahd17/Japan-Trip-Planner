package com.noah.japantripplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.ViewHolder> {

    private final List<Spot> items = new ArrayList<>();

    public void setItems(List<Spot> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_spot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Spot spot = items.get(position);
        holder.nameText.setText(spot.getName());
        holder.coordsText.setText(String.format(Locale.US, "%.5f, %.5f", spot.getLat(), spot.getLng()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView coordsText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.spotNameText);
            coordsText = itemView.findViewById(R.id.spotCoordsText);
        }
    }
}
