package com.example.registrationapp;

import android.content.*;
import android.database.sqlite.*;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "StudentDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE students(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "age TEXT," +
                "dob TEXT," +
                "email TEXT," +
                "phone TEXT," +
                "gender TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS students");
        onCreate(db);
    }

    public boolean insertData(String name, String age, String dob, String email, String phone, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("age", age);
        cv.put("dob", dob);
        cv.put("email", email);
        cv.put("phone", phone);
        cv.put("gender", gender);

        long result = db.insert("students", null, cv);
        return result != -1;
    }
}