package com.noah.japantripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button packingListButton = findViewById(R.id.packingListButton);
        Button budgetButton = findViewById(R.id.budgetButton);
        Button spotsButton = findViewById(R.id.spotsButton);
        Button signOutButton = findViewById(R.id.signOutButton);

        packingListButton.setOnClickListener(v ->
                startActivity(new Intent(this, PackingListActivity.class)));

        budgetButton.setOnClickListener(v ->
                startActivity(new Intent(this, BudgetActivity.class)));

        spotsButton.setOnClickListener(v ->
                startActivity(new Intent(this, SpotsActivity.class)));

        signOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
