package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.activities.LoginActivity;
import com.example.app.activities.RegistrationActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
}
