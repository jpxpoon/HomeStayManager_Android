package com.example.jonathanpoon.homestaymanager;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/* not use */
public class MainActivity extends Activity implements View.OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener{

    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "MainActivity";
    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private SignInButton btnSignIn;
    private Button btnSignOut, btnRevokeAccess, btnCreateProfile, btnEditProfile, btnListHost, btnListPairings;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private LinearLayout llProfileLayout;

    public final static String EXTRA_EMAIL = "com.example.apps.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
        btnEditProfile = (Button) findViewById(R.id.btn_edit_profile);
        btnCreateProfile = (Button) findViewById(R.id.btn_create_profile);
        btnListHost = (Button) findViewById(R.id.btn_list_host);
        btnListPairings = (Button) findViewById(R.id.btn_list_pairings);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);

        // Button click listeners
        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        btnRevokeAccess.setOnClickListener(this);
        btnCreateProfile.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);
        btnListHost.setOnClickListener(this);
        btnListPairings.setOnClickListener(this);

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                signInWithGplus();
                break;
            case R.id.btn_sign_out:
                signOutFromGplus();
                break;
            case R.id.btn_revoke_access:
                revokeGplusAccess();
                break;
            case R.id.btn_create_profile:
                createProfile();
                break;
            case R.id.btn_edit_profile:
                editProfile();
                break;
            case R.id.btn_list_host:
                listHost();
                break;
            case R.id.btn_list_pairings:
                listPairings();
                break;
        }
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }// sign in

    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            updateUI(false);
        }
    }// sign out

    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked!");
                            mGoogleApiClient.connect();
                            updateUI(false);
                        }
                    });
        }
    }// revoke access

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        // Get user's information
        getProfileInformation();
        // Update the UI after signin
        isInDatabase();
        updateUI(true);
    }// connected

    public void isInDatabase(){
        ProfileDatabase dbHelper = new ProfileDatabase(this);
        SQLiteDatabase sqlprofile = dbHelper.getReadableDatabase();

        String[] whereArg = new String[1];
        whereArg[0] = String.valueOf(txtEmail.getText().toString());
        Cursor cursor = sqlprofile.query(ProfileDatabase.table_name, null, ProfileDatabase.email+"=?", whereArg,
                null, null, null);

        if(cursor.moveToFirst()){
            System.out.println(cursor.getString(cursor.getColumnIndex(ProfileDatabase.account_type)));
            if (cursor.getString(cursor.getColumnIndex(ProfileDatabase.account_type)).equals("Student")){
                btnCreateProfile.setVisibility(View.GONE);
                btnEditProfile.setVisibility(View.VISIBLE);
                btnListHost.setVisibility(View.VISIBLE);
                btnListPairings.setVisibility(View.GONE);
            }
            else if (cursor.getString(cursor.getColumnIndex(ProfileDatabase.account_type)).equals("Host")){
                btnCreateProfile.setVisibility(View.GONE);
                btnEditProfile.setVisibility(View.VISIBLE);
                btnListHost.setVisibility(View.GONE);
                btnListPairings.setVisibility(View.VISIBLE);
            }
            else if(cursor.getString(cursor.getColumnIndex(ProfileDatabase.account_type)).equals("Admin")){
                btnCreateProfile.setVisibility(View.GONE);
                btnEditProfile.setVisibility(View.VISIBLE);
                btnListHost.setVisibility(View.GONE);
                btnListPairings.setVisibility(View.GONE);
            }
        } // is on database
        else{
            btnCreateProfile.setVisibility(View.VISIBLE);
            btnEditProfile.setVisibility(View.GONE);
            btnListHost.setVisibility(View.GONE);
            btnListPairings.setVisibility(View.GONE);
        }// new account
        cursor.close();
    }// check if account in database

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;
            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }// if
    }// failed

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        updateUI(false);
    }// no connection

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    } //result

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                txtName.setText(personName);
                txtEmail.setText(email);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;
                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Background Async task to load user profile picture from url **/
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    /** Updating the UI, showing/hiding buttons and profile layout **/
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnRevokeAccess.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.GONE);
            btnCreateProfile.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
        }
    }

    public void createProfile(){
        Intent intent = new Intent(this, CreateProfile.class);
        TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
        String email = txtEmail.getText().toString();
        intent.putExtra(EXTRA_EMAIL, email);
        startActivity(intent);
    }

    public void editProfile(){
        Intent intent = new Intent(this, EditProfile.class);
        TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
        String email = txtEmail.getText().toString();
        intent.putExtra(EXTRA_EMAIL, email);
        startActivity(intent);
    }

    public void listHost(){
        Intent intent = new Intent(this, ListHost.class);
        TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
        String email = txtEmail.getText().toString();
        intent.putExtra(EXTRA_EMAIL, email);
        startActivity(intent);
    }

    public void listPairings(){
        Intent intent = new Intent(this, ListPairings.class);
        TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
        String email = txtEmail.getText().toString();
        intent.putExtra(EXTRA_EMAIL, email);
        startActivity(intent);
    }
}
