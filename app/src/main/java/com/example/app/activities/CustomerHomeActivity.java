package com.example.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.DatabaseHelper;
import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerHomeActivity extends AppCompatActivity {
    private EditText nameEditText, streetEditText, suiteEditText, cityEditText, zipcodeEditText, phoneEditText;
    private TextView usernameTextView, emailTextView, latTextView, lngTextView, processStatusTextView;
    private DatabaseHelper db;
    private int userId;

    private String name;
    private String username;
    private String email;
    private String street;
    private String suite;
    private String city;
    private String zipcode;
    private String lat;
    private String lng;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        // BottomNavigationView setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                navigateToHome();
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

        // Initialize views
        nameEditText = findViewById(R.id.name_edit_text);
        usernameTextView = findViewById(R.id.username_text_view);
        emailTextView = findViewById(R.id.email_text_view);
        streetEditText = findViewById(R.id.street_edit_text);
        suiteEditText = findViewById(R.id.suite_edit_text);
        cityEditText = findViewById(R.id.city_edit_text);
        zipcodeEditText = findViewById(R.id.zipcode_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        latTextView = findViewById(R.id.lat_text_view);
        lngTextView = findViewById(R.id.lng_text_view);
        processStatusTextView = findViewById(R.id.process_status_text_view);

        // Initialize database helper
        db = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);

        // Fetch customer details
        fetchCustomerDetails(userId);
    }

    private void fetchCustomerDetails(int userId) {
        try (Cursor cursor = db.fetchUserById(userId)) {
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME));
                email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL));
                street = cursor.getString(cursor.getColumnIndexOrThrow("street"));
                suite = cursor.getString(cursor.getColumnIndexOrThrow("suite"));
                city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
                zipcode = cursor.getString(cursor.getColumnIndexOrThrow("zipcode"));
                lat = cursor.getString(cursor.getColumnIndexOrThrow("lat"));
                lng = cursor.getString(cursor.getColumnIndexOrThrow("lng"));
                phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE));

                // Populate UI with data
                nameEditText.setText(name);
                usernameTextView.setText(username);
                emailTextView.setText(email);
                streetEditText.setText(street);
                suiteEditText.setText(suite);
                cityEditText.setText(city);
                zipcodeEditText.setText(zipcode);
                phoneEditText.setText(phone);
                latTextView.setText(lat);
                lngTextView.setText(lng);
                processStatusTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROCESS_STATUS)));

                processStatusTextView.setBackgroundColor(Utils.getColorForStatus(
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROCESS_STATUS)), this));
            } else {
                Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateDetails(View view) {
        String updatedName = nameEditText.getText().toString().trim();
        String updatedStreet = streetEditText.getText().toString().trim();
        String updatedSuite = suiteEditText.getText().toString().trim();
        String updatedCity = cityEditText.getText().toString().trim();
        String updatedZipcode = zipcodeEditText.getText().toString().trim();
        String updatedPhone = phoneEditText.getText().toString().trim();

        if (updatedName.isEmpty() || updatedStreet.isEmpty() || updatedCity.isEmpty() || updatedZipcode.isEmpty() || updatedPhone.isEmpty()) {
            Toast.makeText(this, "All fields except username, email, lat, and lng must be filled!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.updateCustomerDetails(userId, updatedName, updatedStreet + ", " + updatedSuite + ", " + updatedCity + ", " + updatedZipcode, updatedPhone);
        Toast.makeText(this, "Details updated successfully", Toast.LENGTH_SHORT).show();
    }

    // Logout Method
    private void logout() {
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Navigate to Admin Home Activity
    private void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Navigate to Registration Activity
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        finish(); // Optionally close the current activity
    }
}
