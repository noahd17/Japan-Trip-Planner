package com.noah.japantripplanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Tracks trip expenses in USD and shows the running total converted to
 * JPY using a live rate pulled from a third-party exchange-rate API.
 * This satisfies the "pull in data from a third party service" item.
 */
public class BudgetActivity extends AppCompatActivity {

    private static final String RATE_API_URL = "https://api.exchangerate-api.com/v4/latest/USD";

    private CollectionReference expensesRef;
    private ExpenseAdapter adapter;
    private TextView totalText;
    private double usdToJpyRate = 150.0; // fallback used if the API call fails
    private double runningTotalUsd = 0.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        String uid = FirebaseAuth.getInstance().getUid();
        expensesRef = FirebaseFirestore.getInstance()
                .collection("users").document(uid)
                .collection("expenses");

        totalText = findViewById(R.id.totalText);
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        RecyclerView recyclerView = findViewById(R.id.expenseRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ExpenseAdapter(expense -> expensesRef.document(expense.getId()).delete());
        recyclerView.setAdapter(adapter);

        EditText categoryInput = findViewById(R.id.categoryInput);
        EditText amountInput = findViewById(R.id.amountInput);
        Button addExpenseButton = findViewById(R.id.addExpenseButton);

        addExpenseButton.setOnClickListener(v -> {
            String category = categoryInput.getText().toString().trim();
            String amountStr = amountInput.getText().toString().trim();
            if (category.isEmpty() || amountStr.isEmpty()) return;

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            expensesRef.add(new Expense(category, amount, System.currentTimeMillis()));
            categoryInput.setText("");
            amountInput.setText("");
        });

        expensesRef.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Toast.makeText(this, "Error loading expenses: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            List<Expense> items = new ArrayList<>();
            double total = 0.0;
            if (snapshots != null) {
                for (QueryDocumentSnapshot doc : snapshots) {
                    Expense expense = doc.toObject(Expense.class);
                    expense.setId(doc.getId());
                    items.add(expense);
                    total += expense.getAmountUsd();
                }
            }
            adapter.setItems(items);
            runningTotalUsd = total;
            updateTotalText();
        });

        fetchExchangeRate();
    }

    /** Fetches the current USD->JPY rate on a background thread. */
    private void fetchExchangeRate() {
        new Thread(() -> {
            try {
                URL url = new URL(RATE_API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONObject rates = json.getJSONObject("rates");
                double jpyRate = rates.getDouble("JPY");

                runOnUiThread(() -> {
                    usdToJpyRate = jpyRate;
                    updateTotalText();
                });
            } catch (Exception e) {
                // Network unavailable, API down, etc. Fall back to the
                // hardcoded estimate rather than crashing the screen.
                runOnUiThread(() -> Toast.makeText(this,
                        "Couldn't fetch live exchange rate, using estimate",
                        Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void updateTotalText() {
        double totalJpy = runningTotalUsd * usdToJpyRate;
        totalText.setText(String.format(Locale.US, "Total: $%.2f (~¥%.0f)",
                runningTotalUsd, totalJpy));
    }
}
