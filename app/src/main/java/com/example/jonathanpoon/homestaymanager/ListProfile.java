package com.example.jonathanpoon.homestaymanager;

import java.util.ArrayList;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/** not use **/
public class ListProfile extends Activity implements OnItemClickListener{

    private ListView profileListView;
    private ListAdapter profileListAdapter;
    private ArrayList<UserProfile> profileArrayList;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_profile);

        profileListView = (ListView) findViewById(R.id.profile_list);
        profileListView.setOnItemClickListener(this);
        profileArrayList = new ArrayList<UserProfile>();
        profileListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, populateList());

        profileListView.setAdapter(profileListAdapter);

        Intent intent = getIntent();
        email = intent.getStringExtra(MainActivity.EXTRA_EMAIL).toString();
    }

    public List<String> populateList(){
        List<String> profileList = new ArrayList<String>();

        ProfileDatabase dbHelper = new ProfileDatabase(this);
        SQLiteDatabase sqlprofile = dbHelper.getReadableDatabase();

        String[] whereArg = new String[1];
        whereArg[0] = String.valueOf(email);
        System.out.println("whereArg[0] is :" + whereArg[0]);

        Cursor cursor = sqlprofile.query(ProfileDatabase.table_name, null, ProfileDatabase.email+"=?", whereArg,
                null, null, null);
        //startManagingCursor(cursor);

        if(cursor.moveToFirst()){
            do{
                String c_name = cursor.getString(cursor.getColumnIndex(ProfileDatabase.name));
                String c_s_date = cursor.getString(cursor.getColumnIndex(ProfileDatabase.startDate));
                String c_e_date = cursor.getString(cursor.getColumnIndex(ProfileDatabase.endDate));
                String c_address = cursor.getString(cursor.getColumnIndex(ProfileDatabase.address));
                String c_phone = cursor.getString(cursor.getColumnIndex(ProfileDatabase.phone));

                // store data
                UserProfile user_pro = new UserProfile();
                user_pro.setName(c_name);
                user_pro.setStartDate(c_s_date);
                user_pro.setEndDate(c_e_date);
                user_pro.setAddress(c_address);
                user_pro.setPhone(c_phone);

                profileArrayList.add(user_pro);

                // But we need a List of String to display in the ListView also.
                profileList.add(c_name);
            }while(cursor.moveToNext());
        }
        cursor.close();
        sqlprofile.close();
        return profileList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        profileArrayList = new ArrayList<UserProfile>();
        profileListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, populateList());
        profileListView.setAdapter(profileListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        profileArrayList = new ArrayList<UserProfile>();
        profileListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, populateList());
        profileListView.setAdapter(profileListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(getApplicationContext(), "Clicked on :" + position, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, EditProfile.class);

        UserProfile clickedObject =  profileArrayList.get(position);

        Bundle dataBundle = new Bundle();
        dataBundle.putString("clickedName", clickedObject.getName());
        dataBundle.putString("clickedStartDate", clickedObject.getStartDate());
        dataBundle.putString("clickedEndDate", clickedObject.getEndDate());
        dataBundle.putString("clickedAddress", clickedObject.getAddress());
        dataBundle.putString("clickedPhone", clickedObject.getPhone());
        dataBundle.putString("clickedEmail", email);

        intent.putExtras(dataBundle);
        startActivity(intent);
    }
} //main
