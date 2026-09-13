package com.noah.japantripplanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Lets the user tag their current GPS location as a saved "spot".
 * This is the "pull data or interact with a sensor / native feature"
 * exceptional-work item, using the device's location sensor.
 */
public class SpotsActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 100;

    private FusedLocationProviderClient fusedLocationClient;
    private CollectionReference spotsRef;
    private SpotAdapter adapter;
    private EditText spotNameInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spots);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String uid = FirebaseAuth.getInstance().getUid();
        spotsRef = FirebaseFirestore.getInstance()
                .collection("users").document(uid)
                .collection("spots");

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.spotsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SpotAdapter();
        recyclerView.setAdapter(adapter);

        spotNameInput = findViewById(R.id.spotNameInput);
        Button tagSpotButton = findViewById(R.id.tagSpotButton);
        tagSpotButton.setOnClickListener(v -> tagCurrentLocation());

        spotsRef.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Toast.makeText(this, "Error loading spots: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            List<Spot> items = new ArrayList<>();
            if (snapshots != null) {
                for (QueryDocumentSnapshot doc : snapshots) {
                    Spot spot = doc.toObject(Spot.class);
                    spot.setId(doc.getId());
                    items.add(spot);
                }
            }
            adapter.setItems(items);
        });
    }

    private void tagCurrentLocation() {
        String name = spotNameInput.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Give this spot a name first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                Toast.makeText(this, "Location unavailable — move somewhere with GPS/network signal and try again",
                        Toast.LENGTH_LONG).show();
                return;
            }
            saveSpot(name, location);
        });
    }

    private void saveSpot(String name, Location location) {
        Spot spot = new Spot(name, location.getLatitude(), location.getLongitude(),
                System.currentTimeMillis());
        spotsRef.add(spot);
        spotNameInput.setText("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            tagCurrentLocation();
        } else {
            Toast.makeText(this, "Location permission is needed to tag a spot",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
