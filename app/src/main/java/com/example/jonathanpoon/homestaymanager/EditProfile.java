package com.example.jonathanpoon.homestaymanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class EditProfile extends Activity implements View.OnClickListener {

    final Context context = this;

    private TextView email;
    private TextView account_type;
    private EditText name;
    private EditText s_date;
    private EditText e_date;
    private EditText address;
    private EditText phone;
    private EditText distance;
    private EditText fsize;
    private RadioGroup smoke;
    private RadioGroup pets;
    private RadioGroup gender;
    private RadioButton btnsmoke;
    private RadioButton btnpets;
    private RadioButton btngender;
    private Button btnsave;
    private Button btncancel;
    private Button btndelete;

    private String new_name;
    private String new_s_date;
    private String new_e_date;
    private String new_address;
    private String new_phone;
    private String new_distance;
    private String new_fsize;
    private String new_gender;
    private boolean new_smoke;
    private boolean new_pets;

    String user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        name = (EditText) findViewById(R.id.name);
        s_date = (EditText) findViewById(R.id.s_date);
        e_date = (EditText) findViewById(R.id.e_date);
        address = (EditText) findViewById(R.id.address);
        phone = (EditText) findViewById(R.id.phone);
        email = (TextView) findViewById(R.id.email);
        account_type = (TextView) findViewById(R.id.account_type);
        distance = (EditText) findViewById(R.id.distance);
        fsize = (EditText) findViewById(R.id.family_size);

        smoke = (RadioGroup) findViewById(R.id.smoke);
        pets = (RadioGroup) findViewById(R.id.pets);
        gender = (RadioGroup) findViewById(R.id.gender);

        btnsave = (Button) findViewById(R.id.save);
        btncancel = (Button) findViewById(R.id.cancel);
        btndelete = (Button) findViewById(R.id.delete);

        btnsave.setOnClickListener(this);
        btncancel.setOnClickListener(this);
        btndelete.setOnClickListener(this);

        Intent intent = getIntent();
        user_email = intent.getStringExtra(MainActivity.EXTRA_EMAIL).toString();

        /** find UserProfile from Database **/
        ProfileDatabase dbHelper = new ProfileDatabase(this);
        SQLiteDatabase sqlprofile = dbHelper.getReadableDatabase();

        String[] whereArg = new String[1];
        whereArg[0] = String.valueOf(user_email);

        Cursor cursor = sqlprofile.query(ProfileDatabase.table_name, null, ProfileDatabase.email+"=?", whereArg,
                null, null, null);

        /** set old data**/
        if(cursor.moveToFirst()){
            String c_name = cursor.getString(cursor.getColumnIndex(ProfileDatabase.name));
            String c_s_date = cursor.getString(cursor.getColumnIndex(ProfileDatabase.startDate));
            String c_e_date = cursor.getString(cursor.getColumnIndex(ProfileDatabase.endDate));
            String c_address = cursor.getString(cursor.getColumnIndex(ProfileDatabase.address));
            String c_phone = cursor.getString(cursor.getColumnIndex(ProfileDatabase.phone));
            String c_distance = cursor.getString(cursor.getColumnIndex(ProfileDatabase.distance));
            String c_fsize = cursor.getString(cursor.getColumnIndex(ProfileDatabase.familySize));
            String c_smoke = cursor.getString(cursor.getColumnIndex(ProfileDatabase.smoke));
            String c_pets = cursor.getString(cursor.getColumnIndex(ProfileDatabase.pets)) ;
            String c_gender = cursor.getString(cursor.getColumnIndex(ProfileDatabase.gender));
            String c_account_type = cursor.getString(cursor.getColumnIndex(ProfileDatabase.account_type));

            // print old data on screen
            email.setText(user_email);
            account_type.setText(c_account_type);
            name.setText(c_name);
            s_date.setText(c_s_date);
            e_date.setText(c_e_date);
            address.setText(c_address);
            phone.setText(c_phone);
            distance.setText(c_distance);
            fsize.setText(c_fsize);

            if(c_gender.equals("Male"))
                gender.check(R.id.male);
            else
                gender.check(R.id.female);
            if(c_smoke.equals("1"))
                smoke.check(R.id.yes_smoke);
            else
                smoke.check(R.id.no_smoke);
            if(c_pets.equals("1"))
                pets.check(R.id.yes_pets);
            else
                pets.check(R.id.no_pets);
        }
        else{
            Toast.makeText(this, "Sorry, no information to edit\nYour account information are not on out system!!!",
                    Toast.LENGTH_LONG).show();
        }
        cursor.close();
        sqlprofile.close();
    }

    @Override
    public void onClick(View v) {
        /** get new input **/
        new_name = name.getText().toString();
        new_s_date = s_date.getText().toString();
        new_e_date = e_date.getText().toString();
        new_address = address.getText().toString();
        new_phone = phone.getText().toString();
        new_distance = distance.getText().toString();
        new_fsize = fsize.getText().toString();

        btnsmoke = (RadioButton) findViewById(smoke.getCheckedRadioButtonId());
        btnpets = (RadioButton) findViewById(pets.getCheckedRadioButtonId());
        btngender = (RadioButton) findViewById(gender.getCheckedRadioButtonId());
        new_gender = btngender.getText().toString();

        if(btnsmoke.getText().toString().equals("Smoking"))
            new_smoke = true;
        else
            new_smoke = false;
        if(btnpets.getText().toString().equals("Pets"))
            new_pets = true;
        else
            new_pets = false;

        // store old data
        final UserProfile user_pro = new UserProfile();
        user_pro.setName(new_name);
        user_pro.setStartDate(new_s_date);
        user_pro.setEndDate(new_e_date);
        user_pro.setEmail(user_email);
        user_pro.setAddress(new_address);
        user_pro.setPhone(new_phone);
        user_pro.setDistance(new_distance);
        user_pro.setFamilySize(new_fsize);
        user_pro.setSmoke(new_smoke);
        user_pro.setPets(new_pets);
        user_pro.setGender(new_gender);

        if(v.getId() == R.id.cancel)
            finish(); // go back
        else if(v.getId() == R.id.save)
            updateProfile(user_pro);
        else if(v.getId() == R.id.delete){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Delete?");
            adb.setMessage("Are you sure you want to delete?");
            adb.setNegativeButton("Cancel", null);
            adb.setPositiveButton("Ok",
                    new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ProfileDatabase dbHelper = new ProfileDatabase(context);
                            SQLiteDatabase sqlprofile = dbHelper.getWritableDatabase();

                            String[] whereArg = new String[1];
                            whereArg[0] = user_pro.getEmail();
                            System.out.println("whereArg[0] delete is :" + whereArg[0]);

                            sqlprofile.delete(ProfileDatabase.table_name, ProfileDatabase.email+"=?", whereArg);
                            sqlprofile.close();
                            finish();
                        }
                    });
            AlertDialog ad = adb.create();
            ad.show();
        }// delete button
    }

    public void updateProfile(UserProfile user){
        ProfileDatabase dbHelper = new ProfileDatabase(this);
        SQLiteDatabase sqlprofile = dbHelper.getWritableDatabase();

        ContentValues tmp = new ContentValues();
        tmp.put(ProfileDatabase.name, user.getName());
        tmp.put(ProfileDatabase.startDate, user.getStartDate());
        tmp.put(ProfileDatabase.endDate, user.getEndDate());
        tmp.put(ProfileDatabase.address, user.getAddress());
        tmp.put(ProfileDatabase.phone, user.getPhone());
        tmp.put(ProfileDatabase.distance, user.getDistance());
        tmp.put(ProfileDatabase.familySize, user.getFamilySize());
        tmp.put(ProfileDatabase.smoke, user.getSmoke());
        tmp.put(ProfileDatabase.pets, user.getPets());
        tmp.put(ProfileDatabase.gender, user.getGender());

        String[] whereArg = new String[1];
        whereArg[0] = user.getEmail();
        System.out.println("whereArg[0] update is: " + whereArg[0]);

        sqlprofile.update(ProfileDatabase.table_name, tmp, ProfileDatabase.email+"=?", whereArg);
        sqlprofile.close();
        finish();
    }

    public void deleteProfile(UserProfile user_delete){
        ProfileDatabase dbHelper = new ProfileDatabase(this);
        SQLiteDatabase sqlprofile = dbHelper.getWritableDatabase();

        String[] whereArg = new String[1];
        whereArg[0] = user_delete.getEmail();
        System.out.println("whereArg[0] delete is :" + whereArg[0]);

        sqlprofile.delete(ProfileDatabase.table_name, ProfileDatabase.email+"=?", whereArg);
        sqlprofile.close();
        finish();
    }
} //main
