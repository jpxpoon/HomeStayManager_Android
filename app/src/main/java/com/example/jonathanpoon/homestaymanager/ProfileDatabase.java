package com.example.jonathanpoon.homestaymanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ProfileDatabase extends SQLiteOpenHelper{
    // Database attributes
    public static final String DB_NAME = "User_Profile";
    public static final int DB_VERSION = 1;

    // Table attributes
    public static final String account_type = "Account_Type";
    public static final String table_name = "user_profile_table";
    public static final String name = "Name";
    public static final String startDate = "Start_Date";
    public static final String endDate = "End_Date";
    public static final String email = "Email";
    public static final String address = "Address";
    public static final String phone = "Phone";
    public static final String distance = "Distance";
    public static final String familySize = "Family_Size";
    public static final String smoke = "Smoke";
    public static final String pets = "Pets";
    public static final String gender = "Gender";

    public static final String pairedWith = "PairedWith";


    public ProfileDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlUserProfile = "create table if not exists " + table_name
                + " ( " + BaseColumns._ID + " integer primary key autoincrement, "
                + account_type + " text not null, "
                + email + " text not null, "
                + name + " text not null, "
                + smoke + " text not null, "
                + pets + " text not null, "
                + gender + " text not null, "
                + startDate + " text not null, "
                + endDate + " text not null, "
                + address + " text not null, "
                + phone + " text not null, "
                + distance + " text not null, "
                + familySize + " text not null, "
                + pairedWith + " TEXT);";
        db.execSQL(sqlUserProfile);
    } // create first database

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 1 && newVersion == 2){
            // Upgrade the database
        }
    }
} //main