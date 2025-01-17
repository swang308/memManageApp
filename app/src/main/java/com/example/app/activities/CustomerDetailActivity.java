package com.example.app.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.DatabaseHelper;
import com.example.app.R;
import com.example.app.models.User;
import com.example.app.utils.Utils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CustomerDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView nameTextView, addressTextView, phoneTextView, processStatusTextView;
    private Spinner statusSpinner;
    private MapView mapView;
    private DatabaseHelper db;
    private int userId;
    private User user;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        // BottomNavigationView setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                navigateToAdminHome();
                return true;
            } else if (itemId == R.id.navigation_logout) {
                logout();
                return true;
            } else if (itemId == R.id.navigation_register) {
                navigateToRegister();
                return true;
            }
            return false;
        });

        // Initialize Views
        nameTextView = findViewById(R.id.name_text_view);
        addressTextView = findViewById(R.id.address_text_view);
        phoneTextView = findViewById(R.id.phone_text_view);
        processStatusTextView = findViewById(R.id.process_status_text_view);
        statusSpinner = findViewById(R.id.status_spinner);
        mapView = findViewById(R.id.map_view);

        // Initialize database helper
        db = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);

        // Set up MapView
        Bundle mapViewBundle = savedInstanceState != null ? savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY) : null;
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        // Populate the spinner with status options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.process_status_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        // Fetch and display customer details
        fetchCustomerDetails();
    }

    private void fetchCustomerDetails() {
        Cursor cursor = db.fetchUserById(userId);
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("street")) + ", " +
                    cursor.getString(cursor.getColumnIndexOrThrow("suite")) + ", " +
                    cursor.getString(cursor.getColumnIndexOrThrow("city")) + ", " +
                    cursor.getString(cursor.getColumnIndexOrThrow("zipcode")));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE)));
            user.setLat(cursor.getString(cursor.getColumnIndexOrThrow("lat")));
            user.setLng(cursor.getString(cursor.getColumnIndexOrThrow("lng")));
            user.setProcessStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROCESS_STATUS)));

            nameTextView.setText(user.getName());
            addressTextView.setText(user.getAddress());
            phoneTextView.setText(user.getPhone());
            processStatusTextView.setText(user.getProcessStatus());
            processStatusTextView.setBackgroundColor(
                    Utils.getColorForStatus(user.getProcessStatus(), this)
            );
        }
        if (cursor != null) cursor.close();

        if (user == null) {
            Toast.makeText(this, "User not found in the database.", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateStatus(View view) {
        String newStatus = statusSpinner.getSelectedItem().toString();

        if (!newStatus.equals(processStatusTextView.getText().toString())) {
            db.updateProcessStatus(userId, newStatus);
            processStatusTextView.setText(newStatus);
            processStatusTextView.setBackgroundColor(
                    Utils.getColorForStatus(newStatus, this)
            );
            Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No changes made to the status", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (user != null) {
            String lat = user.getLat();
            String lng = user.getLng();

            if (lat != null && !lat.isEmpty() && lng != null && !lng.isEmpty()) {
                LatLng customerLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                googleMap.addMarker(new MarkerOptions().position(customerLocation).title("Customer Location"));
                googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(customerLocation, 15));
            } else {
                handleGeocodingFallback(googleMap);
            }
        } else {
            Toast.makeText(this, "User data is not available.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleGeocodingFallback(@NonNull GoogleMap googleMap) {
        String address = addressTextView.getText().toString();
        if (!address.isEmpty()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address location = addresses.get(0);
                    LatLng customerLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(customerLocation).title("Customer Location"));
                    googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(customerLocation, 15));
                } else {
                    Toast.makeText(this, "Unable to locate address on map.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error fetching location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Address not provided.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void logout() {
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToAdminHome() {
        Intent intent = new Intent(this, AdminHomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToRegister() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }
}
