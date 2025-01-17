package com.example.app.utils;

import android.content.Context;
import android.graphics.Color;

public class Utils {
    public static int getColorForStatus(String status, Context context) {
        switch (status) {
            case "AWAITED":
                return Color.YELLOW;
            case "FAILEDTOREACH":
                return Color.parseColor("#FFCCCB"); // Light red
            case "ONBOARDED":
                return Color.parseColor("#CCFFCC"); // Light green
            case "INPROCESS":
                return Color.parseColor("#32CD32"); // Medium green
            case "COMPLETED":
                return Color.parseColor("#006400"); // Dark green
            case "DENIED":
                return Color.RED;
            default:
                return Color.WHITE;
        }
    }
}
