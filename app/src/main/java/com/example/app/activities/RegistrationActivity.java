package com.example.app.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.DatabaseHelper;
import com.example.app.MainActivity;
import com.example.app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {
    private EditText nameEditText, usernameEditText, passwordEditText, emailEditText;
    private EditText streetEditText, suiteEditText, cityEditText, zipcodeEditText;
    private EditText phoneEditText, websiteEditText, companyNameEditText;
    private EditText companyCatchPhraseEditText, companyBsEditText;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // BottomNavigationView setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                navigateToHome();
                return true;
            } else if (itemId == R.id.navigation_register) {
                navigateToRegister();
                return true;
            }
            return false;
        });

        // Initialize views
        nameEditText = findViewById(R.id.name_edittext);
        usernameEditText = findViewById(R.id.username_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        emailEditText = findViewById(R.id.email_edittext);
        streetEditText = findViewById(R.id.street_edittext);
        suiteEditText = findViewById(R.id.suite_edittext);
        cityEditText = findViewById(R.id.city_edittext);
        zipcodeEditText = findViewById(R.id.zipcode_edittext);
        phoneEditText = findViewById(R.id.phone_edittext);
        websiteEditText = findViewById(R.id.website_edittext);
        companyNameEditText = findViewById(R.id.company_name_edittext);
        companyCatchPhraseEditText = findViewById(R.id.company_catch_phrase_edittext);
        companyBsEditText = findViewById(R.id.company_bs_edittext);

        db = new DatabaseHelper(this);

        db.logAllUsers();
    }

    public void register(View view) {
        String name = nameEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String street = streetEditText.getText().toString();
        String suite = suiteEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String zipcode = zipcodeEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String website = websiteEditText.getText().toString();
        String companyName = companyNameEditText.getText().toString();
        String catchPhrase = companyCatchPhraseEditText.getText().toString();
        String companyBs = companyBsEditText.getText().toString();

        // Validation
        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() ||
                street.isEmpty() || city.isEmpty() || zipcode.isEmpty() || phone.isEmpty() ||
                website.isEmpty() || companyName.isEmpty() || catchPhrase.isEmpty() || companyBs.isEmpty()) {
            Toast.makeText(this, "All fields are required, expect suite", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() < 10 || phone.length() > 15) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform Geocoding on a background thread
        new Thread(() -> {
            String latitude = "0.0";
            String longitude = "0.0";

            try {
                Geocoder geocoder = new Geocoder(this);
                List<Address> addresses = geocoder.getFromLocationName(street + ", " + city + ", " + zipcode, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address location = addresses.get(0);
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Unable to fetch location. Using default coordinates.", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to fetch location due to network issues.", Toast.LENGTH_SHORT).show());
            }

            String finalLatitude = latitude;
            String finalLongitude = longitude;

            // Save user in the database on the UI thread
            runOnUiThread(() -> {
                long result = db.registerUser(
                        name, username, password, email, street, suite, city, zipcode,
                        finalLatitude, finalLongitude, phone, website, companyName, catchPhrase, companyBs, false
                );

                if (result != -1) {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToRegister() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }
}
