package com.example.foxcoparking.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.foxcoparking.R;
import com.example.foxcoparking.model.jsonConversion;
import com.example.foxcoparking.model.webConnection;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String userName;
    private String password;
    private Button loginButtons;
    private Switch rememberButton;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButtons = this.findViewById(R.id.loginButton);
        loginButtons.setOnClickListener(this);
        rememberButton = this.findViewById(R.id.rememberSwitch);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if(!permissionCheck()){
            Toast.makeText(this, "No Internet Permission", Toast.LENGTH_SHORT).show();
        } else {

        }
        if(!"notCreated".equals(checkPreference())){
            moveToMainMenu(checkPreference());
        }
    }

    public boolean permissionCheck(){
        boolean result = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted

        } else {
            result = true;
        }

        return result;
    }

    @Override
    public void onClick(View v) {
        EditText editUsername = findViewById(R.id.emailAddressEnter);
        EditText editPassword = findViewById(R.id.passwordEnter);
        userName = editUsername.getText().toString();
        password = editPassword.getText().toString();

        if (userName.isEmpty()) {
            Toast.makeText(this, "No Username entered", Toast.LENGTH_SHORT).show();
        } else {
            if (password.isEmpty()) {
                Toast.makeText(this, "No Password entered", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    final String file = "/mobile/userLogin.php";
                    jsonConversion jsonString = new jsonConversion();
                    String json = jsonString.encodeJsonString("login", userName, password, "", "" ,"");
                    webConnection web = new webConnection();
                    if(web.checkNetworkConnection(context)){

                        String result = web.urlConnection(file, json);
                        if (result.equals("wrongPassword")){
                            Toast.makeText(this, "Login Failed. Try Again", Toast.LENGTH_LONG).show();
                        } else if(result.equals("noUser")) {
                            Toast.makeText(this, "No user found. Try again", Toast.LENGTH_LONG).show();
                        }else{
                            if(rememberButton.isChecked()){
                                createPreference(result);
                                Toast.makeText(this, "You won't need to login until you hit logout", Toast.LENGTH_SHORT).show();
                            }
                            moveToMainMenu(result);
                        }

                    } else {
                        Toast.makeText(this, "Please reconnect to the Internet and try again!", Toast.LENGTH_LONG).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createPreference(String custID){
        SharedPreferences logonFile = getApplicationContext().getSharedPreferences("logonFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = logonFile.edit();
        editor.putString("customerID", custID);
        editor.commit();
    }

    private String checkPreference(){
        String result = "";

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("logonFile", Context.MODE_PRIVATE);
        result = sharedPref.getString("customerID", "notCreated");

        return result;
    }

    private void moveToMainMenu(String userID){
            Intent intent = new Intent(this, MainMenu.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
    }
}
