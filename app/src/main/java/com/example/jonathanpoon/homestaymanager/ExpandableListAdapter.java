package com.example.jonathanpoon.homestaymanager;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private String user_email;
    private Button add;
    private Button remove;
    private Button hangout;

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public void setUserEmail(String email){
        this.user_email = email;
    }

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expand_host_profile, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.details_profile);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.show_host_profile_name, null);

            add = (Button) convertView.findViewById(R.id.add);
            remove = (Button) convertView.findViewById(R.id.remove);
            hangout = (Button) convertView.findViewById(R.id.hangout);

            final String[] name = headerTitle.split("\n");
            isInDatabase(name[0]);

            final AlertDialog.Builder adb = new AlertDialog.Builder(this._context);

            add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v){
                    adb.setTitle("Add Host");
                    adb.setMessage("Are you sure you want to add this Host?");
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    addPair(name[0]);
                                }
                            });
                    AlertDialog ad = adb.create();
                    ad.show();
                }// on click
            }); // add button clicked

            remove.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v){
                    adb.setTitle("Remove Host");
                    adb.setMessage("Are you sure you want to remove this Host?");
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    removePair(name[0]);
                                }
                            });
                    AlertDialog ad = adb.create();
                    ad.show();
                }// on click
            }); // remove button clicked

            hangout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v){
                    adb.setTitle("Hangout!");
                    adb.setMessage("Google Hangout is not working now!!!");
                    adb.setNeutralButton("Ok",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which){
                                }
                            });
                    AlertDialog ad = adb.create();
                    ad.show();
                }// on click
            }); // hangout button clicked
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.host_name);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    public void addPair(String name){
        ProfileDatabase dbHelper = new ProfileDatabase(this._context);
        SQLiteDatabase sqlprofile = dbHelper.getReadableDatabase();

        String[] whereArg = new String[1];
        whereArg[0] = String.valueOf(name);

        /** find host to add **/
        Cursor cursor = sqlprofile.query(ProfileDatabase.table_name, null, ProfileDatabase.name+"=?", whereArg,
                null, null, null);

        if(cursor.moveToFirst()){
            String[] whereArg2 = new String[2];
            whereArg2[0] = String.valueOf(user_email);
            whereArg2[1] = String.valueOf("none");
            String where = ProfileDatabase.email + "=? AND " + ProfileDatabase.pairedWith + "=?";
            Cursor user_cursor = sqlprofile.query(ProfileDatabase.table_name, null, where, whereArg2,
                    null, null, null);

            if(user_cursor.moveToFirst()){
                String h_email = cursor.getString(cursor.getColumnIndex(ProfileDatabase.email));
                UserProfile host = new UserProfile();
                host.setEmail(h_email);

                ContentValues tmp = new ContentValues();
                tmp.put(ProfileDatabase.pairedWith, host.getEmail());

                whereArg[0] = String.valueOf(user_email);
                sqlprofile.update(ProfileDatabase.table_name, tmp, ProfileDatabase.email+"=?", whereArg);
                Toast.makeText(this._context, "Added Host: " + name, Toast.LENGTH_SHORT).show();
            }// user have no pair
            else{
                Toast.makeText(this._context, "Sorry!!!\nYou already have a Host", Toast.LENGTH_SHORT).show();
            }// user already have pair
            user_cursor.close();
        }// have host

        cursor.close();
        sqlprofile.close();
    } // add pair

    public void removePair(String name){
        ProfileDatabase dbHelper = new ProfileDatabase(this._context);
        SQLiteDatabase sqlprofile = dbHelper.getReadableDatabase();

        String[] whereArg = new String[1];
        whereArg[0] = String.valueOf(name);

        /** find host to remove **/
        Cursor cursor = sqlprofile.query(ProfileDatabase.table_name, null, ProfileDatabase.name+"=?", whereArg,
                null, null, null);

        if(cursor.moveToFirst()){
            /** user have a host to remove **/
            String h_email = cursor.getString(cursor.getColumnIndex(ProfileDatabase.email));
            UserProfile host = new UserProfile();
            host.setEmail(h_email);

            String[] whereArg2 = new String[2];
            whereArg2[0] = String.valueOf(user_email);
            whereArg2[1] = String.valueOf(h_email);
            String where = ProfileDatabase.email + "=? AND " + ProfileDatabase.pairedWith + "=?";
            Cursor user_cursor = sqlprofile.query(ProfileDatabase.table_name, null, where, whereArg2,
                    null, null, null);

            if(user_cursor.moveToFirst()){
                ContentValues tmp = new ContentValues();
                tmp.put(ProfileDatabase.pairedWith, "none");

                whereArg[0] = String.valueOf(user_email);
                sqlprofile.update(ProfileDatabase.table_name, tmp, ProfileDatabase.email+"=?", whereArg);
                Toast.makeText(this._context, "Removed Host: " + name, Toast.LENGTH_SHORT).show();
            }// found pair to remove
            else{
                Toast.makeText(this._context, "Sorry!!!\nThis Host is not paired with you", Toast.LENGTH_SHORT).show();
            }// user have no pair
            user_cursor.close();
        }// have host to remove

        cursor.close();
        sqlprofile.close();
    } // remove pair

    public void isInDatabase(String name){
        ProfileDatabase dbHelper = new ProfileDatabase(this._context);
        SQLiteDatabase sqlprofile = dbHelper.getReadableDatabase();

        /** get host email**/
        String[] whereArg = new String[1];
        whereArg[0] = String.valueOf(name);
        Cursor cursor = sqlprofile.query(ProfileDatabase.table_name, null, ProfileDatabase.name+"=?", whereArg,
                null, null, null);

        if(cursor.moveToFirst()){
            if(cursor.getString(cursor.getColumnIndex(ProfileDatabase.account_type)).equals("Host")){
                /** found host **/
                String h_email = cursor.getString(cursor.getColumnIndex(ProfileDatabase.email));
                UserProfile host = new UserProfile();
                host.setEmail(h_email);

                String[] whereArg2 = new String[2];
                whereArg2[0] = String.valueOf(user_email);
                whereArg2[1] = String.valueOf(h_email);
                String where = ProfileDatabase.email + "=? AND " + ProfileDatabase.pairedWith + "=?";
                Cursor user_cursor = sqlprofile.query(ProfileDatabase.table_name, null, where, whereArg2,
                        null, null, null);

                if(user_cursor.moveToFirst()){
                    add.setVisibility(View.GONE);
                    remove.setVisibility(View.VISIBLE);
                    hangout.setVisibility(View.GONE);
                }// this host is paired with user
                else{
                    add.setVisibility(View.VISIBLE);
                    remove.setVisibility(View.GONE);
                    hangout.setVisibility(View.GONE);
                }// this host is not paired with user
                user_cursor.close();
            }// user student searching for host
            else{
                add.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
                hangout.setVisibility(View.VISIBLE);
            }// user host accept student
        } // found host to check

        cursor.close();
        sqlprofile.close();
    }// check if host pair with user

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}