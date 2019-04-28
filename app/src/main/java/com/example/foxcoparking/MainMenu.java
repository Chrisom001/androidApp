package com.example.foxcoparking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity {
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        storedDetails sd = storedDetails.getInstance();

        String userID = getIntent().getStringExtra("userID");
        fillUserData(userID);
        //Toast.makeText(this, "Customer ID: " + userID, Toast.LENGTH_LONG).show();
    }

    public void logOut(View view){
        backToLogin();
    }

    public void userDetails(View view){
        Intent intent = new Intent(this, userDetails.class);
        startActivity(intent);
    }

    public void fillUserData(String customerID){
        try {
            final String host = "www.foxcoparkingsolution.co.uk";
            final String file = "/mobile/userDetails.php";
            String json = convertToJson(customerID);

            if(checkNetworkConnection()){
                webConnection web = new webConnection();

                String result = web.urlConnection(host, file, json);

                if (result.equals("False")){
                    Toast.makeText(this, "Login Failed. Try Again", Toast.LENGTH_LONG).show();
                } else{
                    //Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    decodeJson(result);
                }

            } else {
                Toast.makeText(this, "Please reconnect to the Internet and try again!", Toast.LENGTH_LONG).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkNetworkConnection(){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private String convertToJson(String customerID) throws JSONException {

        JSONObject userDetails = new JSONObject();

        userDetails.put("customerID", customerID);

        String result = userDetails.toString();

        return result;
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

    public void decodeJson(String json){
        try{
            JSONArray user = new JSONArray(json);
            JSONObject userObject = user.getJSONObject(0);

            storedDetails.getInstance().setCustomerFirstName(userObject.getString("firstName"));
            storedDetails.getInstance().setCustomerLastName(userObject.getString("lastName"));
            storedDetails.getInstance().setCustomerEmail(userObject.getString("email"));
            storedDetails.getInstance().setCustomerCarReg(userObject.getString("carReg"));
            storedDetails.getInstance().setStatus(userObject.getInt("status"));
            storedDetails.getInstance().setStatusReason(userObject.getString("statusReason"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
