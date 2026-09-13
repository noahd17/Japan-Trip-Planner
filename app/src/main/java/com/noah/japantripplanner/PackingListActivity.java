package com.noah.japantripplanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the packing list for the signed-in user and keeps it in sync
 * with Firestore in real time via addSnapshotListener.
 *
 * Firestore path: users/{uid}/packingItems/{itemId}
 */
public class PackingListActivity extends AppCompatActivity {

    private CollectionReference itemsRef;
    private PackingAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packing_list);

        String uid = FirebaseAuth.getInstance().getUid();
        itemsRef = FirebaseFirestore.getInstance()
                .collection("users").document(uid)
                .collection("packingItems");

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.packingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PackingAdapter(new PackingAdapter.Listener() {
            @Override
            public void onToggle(PackingItem item, boolean packed) {
                itemsRef.document(item.getId()).update("packed", packed);
            }

            @Override
            public void onDelete(PackingItem item) {
                itemsRef.document(item.getId()).delete();
            }
        });
        recyclerView.setAdapter(adapter);

        EditText itemNameInput = findViewById(R.id.itemNameInput);
        Button addItemButton = findViewById(R.id.addItemButton);

        addItemButton.setOnClickListener(v -> {
            String name = itemNameInput.getText().toString().trim();
            if (name.isEmpty()) return;
            itemsRef.add(new PackingItem(name, false));
            itemNameInput.setText("");
        });

        // Real-time listener: any change in Firestore (from this device or
        // another) automatically refreshes the list on screen.
        itemsRef.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Toast.makeText(this, "Error loading list: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            List<PackingItem> items = new ArrayList<>();
            if (snapshots != null) {
                for (QueryDocumentSnapshot doc : snapshots) {
                    PackingItem item = doc.toObject(PackingItem.class);
                    item.setId(doc.getId());
                    items.add(item);
                }
            }
            adapter.setItems(items);
        });
    }
}
