package com.example.foxcoparking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class userDetails extends AppCompatActivity {
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        fillDetails();
    }

    public void fillDetails(){
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
                webConnection web = new webConnection();
                if(web.checkNetworkConnection(context)){
                    jsonConversion jsonConversion = new jsonConversion();

                    String json = jsonConversion.encodeJsonString("userDetails", firstNameEntered, lastNameEntered, passwordEntered, carRegEntered, "");
                    updateUser(json);
                } else {
                    Toast.makeText(this, "Web connection unavaliable. Please reconnect.", Toast.LENGTH_SHORT).show();
                }
            } catch(Exception e){
                Toast.makeText(this, "There has been an error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void deleteUser(View view){
        CheckBox delete = findViewById(R.id.checkBoxConfirmDelete);
        if(delete.isChecked()){
            deleteConfirm();
        } else {
            Toast.makeText(this, "Checkbox hasn't been ticked", Toast.LENGTH_SHORT).show();
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
        webConnection web = new webConnection();
        if(web.checkNetworkConnection(context)){
            try{
                final String file = "/mobile/deleteUser.php";
                jsonConversion jsonConversion = new jsonConversion();
                String json = jsonConversion.encodeJsonString("deleteUser", storedDetails.getInstance().getCustomerID(), "", "", "", "");
                String result = web.urlConnection(file, json);

                if(result.equals("True")){
                    SharedPreferences logonFile = getApplicationContext().getSharedPreferences("logonFile", Context.MODE_PRIVATE);
                    if (logonFile.edit().clear().commit()) {
                        storedDetails.getInstance().clearDetails();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Failed to Clear Settings", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "User Deletion Failed.", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e){
                Toast.makeText(this, "There has been an error", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "No network connection. Please try again later", Toast.LENGTH_LONG).show();
        }
    }

    public void updateUser(String json){
        String result = "";
        try{
            webConnection web = new webConnection();
            final String file = "/mobile/updateUserDetails.php";

            result = web.urlConnection(file, json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(result.equals("True")){
            storeUserDetails userDetails = new storeUserDetails();
            userDetails.fillUserData(storedDetails.getInstance().getCustomerID(), context);
            fillDetails();
            Toast.makeText(this, "User updated.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User not updated. Try again", Toast.LENGTH_SHORT).show();
        }

    }
}
