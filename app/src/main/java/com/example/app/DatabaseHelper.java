package com.example.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_IS_ADMIN = "isAdmin";
    public static final String COLUMN_PROCESS_STATUS = "processStatus";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_EMAIL + " TEXT, "
                + "street TEXT, "
                + "suite TEXT, "
                + "city TEXT, "
                + "zipcode TEXT, "
                + "lat TEXT, "
                + "lng TEXT, "
                + COLUMN_PHONE + " TEXT, "
                + "website TEXT, "
                + "company_name TEXT, "
                + "company_catch_phrase TEXT, "
                + "company_bs TEXT, "
                + COLUMN_IS_ADMIN + " INTEGER, "
                + COLUMN_PROCESS_STATUS + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN street TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN suite TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN city TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN zipcode TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN lat TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN lng TEXT");
        }
    }

    public long registerUser(String name, String username, String password, String email,
                             String street, String suite, String city, String zipcode,
                             String latitude, String longitude, String phone,
                             String website, String companyName, String catchPhrase, String companyBs, boolean isAdmin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_EMAIL, email);
        values.put("street", street);
        values.put("suite", suite);
        values.put("city", city);
        values.put("zipcode", zipcode);
        values.put("lat", latitude);
        values.put("lng", longitude);
        values.put(COLUMN_PHONE, phone);
        values.put("website", website);
        values.put("company_name", companyName);
        values.put("company_catch_phrase", catchPhrase);
        values.put("company_bs", companyBs);
        values.put(COLUMN_IS_ADMIN, isAdmin ? 1 : 0);
        values.put(COLUMN_PROCESS_STATUS, "AWAITED");
        return db.insert(TABLE_USERS, null, values);
    }

    public Cursor loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password});
    }

    public Cursor fetchUsers(boolean isAdmin) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_IS_ADMIN + "=?",
                new String[]{isAdmin ? "1" : "0"});
    }

    public Cursor fetchUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)});
    }

    public void updateProcessStatus(int userId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROCESS_STATUS, newStatus);
        db.update(TABLE_USERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
    }

    public void logAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);

        Log.d("DatabaseHelper", "Dumping all users:");
        while (cursor.moveToNext()) {
            Log.d("DatabaseHelper", "ID: " + cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)) +
                    ", Username: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)) +
                    ", Password: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)) +
                    ", IsAdmin: " + cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)));
        }
        cursor.close();
    }

    public void deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_USERS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        if (rowsDeleted > 0) {
            Log.d("DatabaseHelper", "User deleted successfully with ID: " + id);
        } else {
            Log.d("DatabaseHelper", "No user found with ID: " + id);
        }
        db.close();
    }

    public void checkDatabaseContent() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);

        if (cursor.moveToFirst()) {
            Log.d("DatabaseContent", "Users table content:");
            do {
                String record = "ID: " + cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)) + ", " +
                        "Name: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)) + ", " +
                        "Username: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)) + ", " +
                        "Email: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)) + ", " +
                        "Phone: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)) + ", " +
                        "IsAdmin: " + cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) + ", " +
                        "ProcessStatus: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROCESS_STATUS));

                Log.d("DatabaseContent", record);
            } while (cursor.moveToNext());
        } else {
            Log.d("DatabaseContent", "No data found in the users table.");
        }

        cursor.close();
    }

    public void updateCustomerDetails(int userId, String name, String address, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put("city", address); // Assuming "address" is stored in the "city" column
        values.put(COLUMN_PHONE, phone);

        int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
        if (rowsUpdated > 0) {
            Log.d("DatabaseHelper", "Customer details updated successfully for ID: " + userId);
        } else {
            Log.d("DatabaseHelper", "No customer found with ID: " + userId);
        }
    }
}
