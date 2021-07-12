package com.hrithik.hrithikadhikary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sanojpunchihewa.updatemanager.UpdateManager;
import com.sanojpunchihewa.updatemanager.UpdateManagerConstant;

import org.jsoup.Jsoup;

import java.io.IOException;

public class MainActivity extends AppCompatActivity    {
    FirebaseAuth Fauth;
    UpdateManager mUpdateManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar
       //Toolbar toolbar = findViewById(R.id.toolbar);
      //setSupportActionBar(toolbar);





        FirebaseMessaging.getInstance().subscribeToTopic("/topics/HrithikAdhikary");

        //toolbar
        // assigning ID of the toolbar to a variable
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // using toolbar as ActionBar
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(Color.WHITE);

        //bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);


        getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer, new FeedFragment()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        Fragment selectFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                selectFragment = new FeedFragment();
                                break;
                            case R.id.action_chat:
                                selectFragment = new ChatFragment();
                                break;
                            case R.id.action_voiceChat:
                                selectFragment = new VoiceFragment();
                                break;

                            case R.id.action_member:
                                selectFragment = new MembersFragment();
                                break;
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer, selectFragment).commit();

                        return true;
                    }
                });

        //end

        if (isOnline()) {

            load();
        } else {
            try {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage("Internet not available, Cross check your internet connectivity")
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                load();
                            }
                        }).show();
            } catch (Exception e) {

            }
        }

    }



    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            return false;
        }
        return true;
    }

    public void load(){

                Fauth = FirebaseAuth.getInstance();
                if (Fauth.getCurrentUser() != null) {


                    FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+Fauth.getCurrentUser().getUid());



                    //check update
                    CheckUpdate();





                } else {

                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();

                }



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                startSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void startSettings() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, Login.class));
    }













    @Override
    protected void onStop() {
        super.onStop();
    }



    //inapp update
    private void CheckUpdate() {

        mUpdateManager = UpdateManager.Builder(this).mode(UpdateManagerConstant.IMMEDIATE);
        mUpdateManager.start();

    }
}