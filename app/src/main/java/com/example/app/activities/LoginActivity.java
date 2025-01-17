package com.example.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.app.DatabaseHelper;
import com.example.app.R;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // BottomNavigationView setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Navigate back to Admin Home Activity
                navigateToHome();
                return true;
            } else if (itemId == R.id.navigation_logout) {
                // Logout
                logout();
                return true;
            } else if (itemId == R.id.navigation_register) {
                navigateToRegister();
                return true;
            }
            return false;
        });

        usernameEditText = findViewById(R.id.username_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        db = new DatabaseHelper(this);

        db.logAllUsers();
        db.checkDatabaseContent();
    }

    public void login(View view) {
        String username = usernameEditText.getText().toString().trim(); // Trim whitespace
        String password = passwordEditText.getText().toString().trim(); // Trim whitespace

        Log.d("LoginActivity", "Entered username: " + username + ", Entered password: " + password);

        Cursor cursor = db.loginUser(username, password);

        if (cursor.moveToFirst()) {
            Log.d("LoginActivity", "Login successful for username: " + username);
            boolean isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_ADMIN)) == 1;
            Intent intent;

            if (isAdmin) {
                intent = new Intent(this, AdminHomeActivity.class);
            } else {
                intent = new Intent(this, CustomerHomeActivity.class);
                intent.putExtra("USER_ID", cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
            }

            startActivity(intent);
            finish();

        } else {
            Log.d("LoginActivity", "Invalid credentials for username: " + username);
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
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

    // Navigate to Admin Home Activity
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

}
