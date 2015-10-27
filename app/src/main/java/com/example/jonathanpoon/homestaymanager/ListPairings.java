package com.example.jonathanpoon.homestaymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ExpandableListView;


/** Hosts can find student who paired with them**/
public class ListPairings extends Activity {

    private String user_email;
    private ArrayList<UserProfile> profileArrayList;
    private ExpandableListAdapter profileListAdapter;
    private ExpandableListView profileListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_profile);

        Intent intent = getIntent();
        user_email = intent.getStringExtra(MainActivity.EXTRA_EMAIL).toString();

        profileListView = (ExpandableListView) findViewById(R.id.host_profile);
        profileArrayList = new ArrayList<UserProfile>();

        profileListAdapter = new ExpandableListAdapter(this, populateList(), populateList2());
        profileListView.setAdapter(profileListAdapter);

        profileListAdapter.setUserEmail(user_email);
    }

    public List<String> populateList(){
        List<String> profileList = new ArrayList<String>();
        ProfileDatabase dbHelper = new ProfileDatabase(this);
        SQLiteDatabase sqlprofile = dbHelper.getReadableDatabase();

        String[] whereArg = new String[1];
        whereArg[0] = String.valueOf(user_email);
        System.out.println("WhereArg: " + whereArg[0]);
        Cursor cursor = sqlprofile.query(ProfileDatabase.table_name, null, ProfileDatabase.pairedWith+"=?", whereArg,
                null, null, null);

        if(cursor.moveToFirst()){
            do{
                String c_name = cursor.getString(cursor.getColumnIndex(ProfileDatabase.name));
                String c_fsize = cursor.getString(cursor.getColumnIndex(ProfileDatabase.familySize));

                // But we need a List of String to display in the ListView also.
                profileList.add(c_name + "\nFamily Size: " + c_fsize);
            }while(cursor.moveToNext());
        }
        cursor.close();
        sqlprofile.close();
        return profileList;
    }// list name

    public HashMap<String, List<String>> populateList2(){
        HashMap<String, List<String>> profile = new HashMap<String, List<String>>();
        ProfileDatabase dbHelper = new ProfileDatabase(this);
        SQLiteDatabase sqlprofile = dbHelper.getReadableDatabase();

        String[] whereArg = new String[1];
        whereArg[0] = String.valueOf(user_email);

        Cursor cursor = sqlprofile.query(ProfileDatabase.table_name, null, ProfileDatabase.pairedWith+"=?", whereArg,
                null, null, null);

        if(cursor.moveToFirst()){
            do{
                String c_name = cursor.getString(cursor.getColumnIndex(ProfileDatabase.name));
                String c_s_date = cursor.getString(cursor.getColumnIndex(ProfileDatabase.startDate));
                String c_e_date = cursor.getString(cursor.getColumnIndex(ProfileDatabase.endDate));
                String c_fsize = cursor.getString(cursor.getColumnIndex(ProfileDatabase.familySize));
                String c_smoke = cursor.getString(cursor.getColumnIndex(ProfileDatabase.smoke));
                String c_pets = cursor.getString(cursor.getColumnIndex(ProfileDatabase.pets));
                String c_distance = cursor.getString(cursor.getColumnIndex(ProfileDatabase.distance));

                // store data
                UserProfile user_pro = new UserProfile();
                user_pro.setName(c_name);
                user_pro.setStartDate(c_s_date);
                user_pro.setEndDate(c_e_date);
                user_pro.setAddress(c_fsize);
                user_pro.setPhone(c_smoke);
                user_pro.setPhone(c_pets);
                user_pro.setPhone(c_distance);

                profileArrayList.add(user_pro);

                // But we need a List of String to display in the ListView also.
                List<String> profileList = new ArrayList<String>();
                profileList.add("Start Data: " + c_s_date);
                profileList.add("End Data: " + c_e_date);
                profileList.add("Distance: " + c_distance);
                if(c_smoke.equals("1"))
                    profileList.add("Smoking");
                else
                    profileList.add("Non-Smoking");
                if(c_pets.equals("1"))
                    profileList.add("Have Pets");
                else
                    profileList.add("No Pets");
                profile.put(c_name + "\nFamily Size: " + c_fsize, profileList);
            }while(cursor.moveToNext());
        }
        cursor.close();
        sqlprofile.close();
        return profile;
    }// list details profile
} //main
