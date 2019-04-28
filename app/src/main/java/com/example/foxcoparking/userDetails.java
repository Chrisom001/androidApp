package com.example.foxcoparking;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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
                String json = convertToJson(firstNameEntered, lastNameEntered, passwordEntered, carRegEntered);
                if(checkNetworkConnection()){

                } else {
                    Toast.makeText(this, "Web connection unavaliable. Please reconnect.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
        }


        String result = userDetails.toString();
        return result;
    }
}
