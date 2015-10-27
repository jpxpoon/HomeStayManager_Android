package com.example.jonathanpoon.homestaymanager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class CreateProfile extends Activity implements OnClickListener{

    private Spinner account_type;
    private TextView email;
    private EditText name;
    private EditText s_date;
    private EditText e_date;
    private EditText address;
    private EditText phone;
    private EditText distance;
    private EditText fsize;
    private Button btnsave;
    private Button btncancel;
    private RadioGroup smoke;
    private RadioGroup pets;
    private RadioGroup gender;
    private RadioButton btnsmoke;
    private RadioButton btnpets;
    private RadioButton btngender;

    String in_account_type = null;
    String in_name = null;
    String in_s_date = null;
    String in_e_date = null;
    String in_address = null;
    String in_phone = null;
    String in_email = null;
    String in_distance = null;
    String in_gender = null;
    boolean in_smoke = true;
    boolean in_pets = true;
    String in_fsize = null;

    private ArrayList<UserProfile> ProfileArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        account_type = (Spinner) findViewById(R.id.SpinnerAcccuntType);
        name = (EditText) findViewById(R.id.name);
        s_date = (EditText) findViewById(R.id.s_date);
        e_date = (EditText) findViewById(R.id.e_date);
        address = (EditText) findViewById(R.id.address);
        phone = (EditText) findViewById(R.id.phone);
        distance = (EditText) findViewById(R.id.distance);
        fsize = (EditText) findViewById(R.id.family_size);
        email = (TextView) findViewById(R.id.txtEmail);

        smoke = (RadioGroup) findViewById(R.id.smoke);
        pets = (RadioGroup) findViewById(R.id.pets);
        gender = (RadioGroup) findViewById(R.id.gender);

        btnsave = (Button) findViewById(R.id.save);
        btncancel = (Button) findViewById(R.id.cancel);

        ProfileArrayList = new ArrayList<UserProfile>();

        btnsave.setOnClickListener(this);
        btncancel.setOnClickListener(this);

        Intent intent = getIntent();
        String msg = intent.getStringExtra(MainActivity.EXTRA_EMAIL);
        email.setText(msg);
        in_email = msg;
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.cancel)
            finish(); // go back
        else if(v.getId() == R.id.save){
            btnsmoke = (RadioButton) findViewById(smoke.getCheckedRadioButtonId());
            btnpets = (RadioButton) findViewById(pets.getCheckedRadioButtonId());
            btngender = (RadioButton) findViewById(gender.getCheckedRadioButtonId());

            // Get the values provided by the user
            in_account_type = account_type.getSelectedItem().toString();
            in_name = name.getText().toString();
            in_s_date = s_date.getText().toString();
            in_e_date = e_date.getText().toString();
            in_address = address.getText().toString();
            in_phone = phone.getText().toString();
            in_distance = distance.getText().toString();
            in_fsize = fsize.getText().toString();
            in_gender = btngender.getText().toString();

            if(btnsmoke.getText().toString() == "Smoking")
                in_smoke = true;
            else
                in_smoke = false;
            if(btnpets.getText().toString() == "Pets")
                in_pets = true;
            else
                in_pets = false;
            System.out.println("smoke: " + btnsmoke.getText().toString());
            System.out.println("pets: " + btnpets.getText().toString());
            System.out.println("gender: " + btngender.getText().toString());

            // set user values in Profile function
            UserProfile user = new UserProfile();
            user.setAccountType(in_account_type);
            user.setName(in_name);
            user.setStartDate(in_s_date);
            user.setEndDate(in_e_date);
            user.setEmail(in_email);
            user.setAddress(in_address);
            user.setPhone(in_phone);
            user.setDistance(in_distance);
            user.setFamilySize(in_fsize);
            user.setSmoke(in_smoke);
            user.setPets(in_pets);
            user.setGender(in_gender);

            if(in_name != null && in_s_date != null && in_e_date != null &&
                    in_address != null && in_phone != null){
                // Add an user profile to array list
                ProfileArrayList.add(user);
                insertUser(user);
                System.out.println("Done");
                finish(); // go back
            }// reset
            else{
                Toast.makeText(this, "Sorry Some Fields are missing.\nPlease Fill up all", Toast.LENGTH_LONG).show();
                name.getText().clear();
                s_date.getText().clear();
                e_date.getText().clear();
                address.getText().clear();
                phone.getText().clear();
                distance.getText().clear();
                fsize.getText().clear();
            }
        }// save button
    }

    public void insertUser(UserProfile user){
        ProfileDatabase dbHelper = new ProfileDatabase(this);
        SQLiteDatabase sqlprofile = dbHelper.getWritableDatabase();
        ContentValues tmp = new ContentValues();

        tmp.put(ProfileDatabase.account_type, user.getAccountType());
        tmp.put(ProfileDatabase.name, user.getName());
        tmp.put(ProfileDatabase.startDate, user.getStartDate());
        tmp.put(ProfileDatabase.endDate, user.getEndDate());
        tmp.put(ProfileDatabase.email, user.getEmail());
        tmp.put(ProfileDatabase.address, user.getAddress());
        tmp.put(ProfileDatabase.phone, user.getPhone());
        tmp.put(ProfileDatabase.distance, user.getDistance());
        tmp.put(ProfileDatabase.familySize, user.getFamilySize());
        tmp.put(ProfileDatabase.smoke, user.getSmoke());
        tmp.put(ProfileDatabase.pets, user.getPets());
        tmp.put(ProfileDatabase.gender, user.getGender());
        tmp.put(ProfileDatabase.pairedWith, "none");

        long column = sqlprofile.insert(ProfileDatabase.table_name, null, tmp);

        sqlprofile.close();
        Toast.makeText(this, "User# : " + column, Toast.LENGTH_LONG).show();
    } //insert

} //main
