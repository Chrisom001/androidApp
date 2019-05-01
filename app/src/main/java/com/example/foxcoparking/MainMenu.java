package com.example.foxcoparking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity {
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        fillUserDetails();
        checkBannedState();
    }

    public void checkBannedState(){
        int status = storedDetails.getInstance().getStatus();
        if(status == 0){
            TextView statusText =findViewById(R.id.textViewBanned);
            statusText.setVisibility(View.VISIBLE);
        }
    }

    public void fillUserDetails(){
        storedDetails sd = storedDetails.getInstance();
        sd.setCustomerID(getIntent().getStringExtra("userID"));

        storeUserDetails user = new storeUserDetails();
        String result = user.fillUserData(sd.getCustomerID(), context);
        if(result.equals("notUpdated")){
            Toast.makeText(this, "User does not exist", Toast.LENGTH_LONG).show();
            backToLogin();
        } else if(result.equals("noInternet")){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    public void goToMap(View view){
            Intent intent = new Intent(this, carParkMapping.class);
            startActivity(intent);
    }

    public void goToActivities(View view){
        Intent intent = new Intent(this, viewActivities.class);
        startActivity(intent);
    }

    public void logOut(View view){
        backToLogin();
    }

    public void userDetails(View view){
        Intent intent = new Intent(this, userDetails.class);
        startActivity(intent);
    }

    public void goToBills(View view){
        Intent intent = new Intent(this, ViewBills.class);
        startActivity(intent);
    }

    public void backToLogin() {
        SharedPreferences logonFile = getApplicationContext().getSharedPreferences("logonFile", Context.MODE_PRIVATE);

        if (logonFile.edit().clear().commit()) {
            storedDetails.getInstance().clearDetails();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Failed to Clear Settings", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        Toast.makeText(this, "Back button is disabled on this page", Toast.LENGTH_SHORT).show();
    }
}