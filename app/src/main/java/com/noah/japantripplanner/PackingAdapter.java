package com.noah.japantripplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PackingAdapter extends RecyclerView.Adapter<PackingAdapter.ViewHolder> {

    public interface Listener {
        void onToggle(PackingItem item, boolean packed);
        void onDelete(PackingItem item);
    }

    private final List<PackingItem> items = new ArrayList<>();
    private final Listener listener;

    public PackingAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<PackingItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_packing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PackingItem item = items.get(position);
        holder.nameText.setText(item.getName());

        // Avoid re-triggering the listener while we set the initial state
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.isPacked());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                listener.onToggle(item, isChecked));

        holder.deleteButton.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView nameText;
        ImageButton deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.packedCheckBox);
            nameText = itemView.findViewById(R.id.itemNameText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
