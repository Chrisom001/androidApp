package com.example.foxcoparking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class userDetails extends AppCompatActivity {
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        EditText firstName = findViewById(R.id.editTextFirstName);
        EditText lastName = findViewById(R.id.editTextLastName);
        EditText carReg = findViewById(R.id.editTextCarReg);

        firstName.setText(storedDetails.getInstance().getCustomerFirstName());
        lastName.setText(storedDetails.getInstance().getCustomerLastName());
        carReg.setText(storedDetails.getInstance().getCustomerCarReg());
    }

    public void saveUserDetails(View view){
        EditText firstName = findViewById(R.id.editTextFirstName);
        EditText lastName = findViewById(R.id.editTextLastName);
        EditText carReg = findViewById(R.id.editTextCarReg);
        EditText password = findViewById(R.id.editTextPassword);

        String firstNameEntered = firstName.getText().toString();
        String lastNameEntered = lastName.getText().toString();
        String carRegEntered = carReg.getText().toString();
        String passwordEntered = password.getText().toString();

        if(firstNameEntered.isEmpty()){
            Toast.makeText(this, "There is no first name entered", Toast.LENGTH_SHORT).show();
        } else if(lastNameEntered.isEmpty()){
            Toast.makeText(this, "There is no Last Name entered", Toast.LENGTH_SHORT).show();
        } else if(carRegEntered.isEmpty()){
            Toast.makeText(this, "There is no Car Reg entered", Toast.LENGTH_SHORT).show();
        } else {
            try {
                if(checkNetworkConnection()){
                    String json = convertToJson(firstNameEntered, lastNameEntered, passwordEntered, carRegEntered);
                    updateUser(json);
                } else {
                    Toast.makeText(this, "Web connection unavaliable. Please reconnect.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteUser(View view){
        CheckBox delete = findViewById(R.id.checkBoxConfirmDelete);
        if(delete.isChecked()){
            Toast.makeText(this, "Delete User", Toast.LENGTH_SHORT).show();
            deleteConfirm();
        } else {
            Toast.makeText(this, "Don't Delete", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteConfirm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete your account?")
                .setTitle("Delete Account");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finalDeleteUser();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void finalDeleteUser(){
        Toast.makeText(this, "User Deleted", Toast.LENGTH_LONG).show();
    }

    public void updateUser(String json){
        String result = "";
        try{
            webConnection web = new webConnection();
            final String host = "www.foxcoparkingsolution.co.uk";
            final String file = "/mobile/updateUserDetails.php";

            result = web.urlConnection(host, file, json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

    }

    private boolean checkNetworkConnection(){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private String convertToJson(String firstNameEntered, String lastNameEntered, String passwordEntered, String carRegEntered) throws JSONException {

        JSONObject userDetails = new JSONObject();

        userDetails.put("firstName", firstNameEntered);
        userDetails.put("lastName", lastNameEntered);
        userDetails.put("carReg", carRegEntered);
        if(!passwordEntered.isEmpty()){
            userDetails.put("password", passwordEntered);
        } else {
            userDetails.put("password", "none");
        }
        userDetails.put("emailAddress", storedDetails.getInstance().getCustomerEmail());


        String result = userDetails.toString();
        return result;
    }
}
