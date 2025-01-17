package com.example.app.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.DatabaseHelper;
import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.adapters.UserAdapter;
import com.example.app.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {
    private static final String TAG = "AdminHomeActivity";

    private RecyclerView recyclerView;
    private DatabaseHelper db;
    private List<User> userList;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

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

        Log.d(TAG, "AdminHomeActivity started");

        recyclerView = findViewById(R.id.recycler_view);
        db = new DatabaseHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the user list and adapter
        userList = new ArrayList<>();
        adapter = new UserAdapter(userList, this::navigateToCustomerDetail, this::confirmAndDeleteUser);

        recyclerView.setAdapter(adapter);

        // Enable swipe-to-delete functionality
        enableSwipeToDelete();

        // Fetch and display users
        fetchUsers();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchUsers() {
        Log.d(TAG, "Fetching users from the database...");
        userList.clear(); // Clear the existing list before adding new users
        Cursor cursor = db.fetchUsers(false);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String userPhone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE));
                String processStatus = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROCESS_STATUS));

                Log.d(TAG, "User found: ID=" + userId + ", Name=" + userName);

                // Create User object and add it to the list
                User user = new User();
                user.setId(userId);
                user.setName(userName);
                user.setPhone(userPhone);
                user.setProcessStatus(processStatus);

                userList.add(user);
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Log.d(TAG, "No users found in the database.");
            Toast.makeText(this, "No users available to display.", Toast.LENGTH_SHORT).show();
        }

        // Notify the adapter about data changes
        adapter.notifyDataSetChanged();
    }

    private void navigateToCustomerDetail(User user) {
        Intent intent = new Intent(this, CustomerDetailActivity.class);
        intent.putExtra("USER_ID", user.getId());
        startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void confirmAndDeleteUser(User user) {
        // Show confirmation dialog before deleting the user
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", (dialog, which) -> deleteUser(user))
                .setNegativeButton("No", (dialog, which) -> {
                    // Refresh the adapter to restore the swiped item
                    adapter.notifyDataSetChanged();
                })
                .show();
    }

    private void deleteUser(User user) {
        Log.d(TAG, "Deleting user: ID=" + user.getId());
        db.deleteUser(user.getId());
        Toast.makeText(this, "User deleted successfully.", Toast.LENGTH_SHORT).show();
        fetchUsers(); // Refresh the user list after deletion
    }

    private void enableSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // No need to handle move
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                User user = userList.get(position);

                // Confirm deletion with the user
                confirmAndDeleteUser(user);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
        finish(); // Optionally close the current activity
    }

    // Navigate to Admin Home Activity
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        finish(); // Optionally close the current activity
    }
}
