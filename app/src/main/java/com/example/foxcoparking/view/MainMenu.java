package com.example.foxcoparking.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foxcoparking.R;
import com.example.foxcoparking.controller.storeUserDetails;
import com.example.foxcoparking.controller.storedDetails;

public class MainMenu extends AppCompatActivity {
    private static final int REQUEST_CALL_PHONE = 1;
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
        TextView statusText = findViewById(R.id.textViewBanned);
        if(status == 0){
            statusText.setVisibility(View.VISIBLE);
        } else {
            statusText.setVisibility(View.INVISIBLE);
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

    //https://www.youtube.com/watch?v=SMrB97JuIoM - for checking permissions
    public void callFoxCo(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
            makeCall();
        } else {
            requestPhonePermission();
        }
    }

    private void requestPhonePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            final android.app.AlertDialog.Builder permission = new android.app.AlertDialog.Builder(this);
            permission.setTitle("Request Permission");
            permission.setMessage("This permission is required to call FoxCo Support Only");
            permission.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainMenu.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
                }
            });
            permission.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            permission.create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL_PHONE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makeCall();
            } else {
                Toast.makeText(this, "Permission Denied. Unable to make Call", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void makeCall(){
        String phoneNumber = "tel:07731486650";
        Intent intent = new Intent("android.intent.action.DIAL", Uri.parse(phoneNumber));
        startActivity(intent);
    }
}