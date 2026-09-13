package com.noah.japantripplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    public interface Listener {
        void onDelete(Expense expense);
    }

    private final List<Expense> items = new ArrayList<>();
    private final Listener listener;

    public ExpenseAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<Expense> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = items.get(position);
        holder.categoryText.setText(expense.getCategory());
        holder.amountText.setText(String.format(Locale.US, "$%.2f", expense.getAmountUsd()));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(expense));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText;
        TextView amountText;
        ImageButton deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.categoryText);
            amountText = itemView.findViewById(R.id.amountText);
            deleteButton = itemView.findViewById(R.id.deleteExpenseButton);
        }
    }
}
