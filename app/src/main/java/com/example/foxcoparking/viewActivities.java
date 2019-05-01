package com.example.foxcoparking;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class viewActivities extends AppCompatActivity {
    Context context = this;
    String[][] activities;
    String[] activityInformation;
    String[] testArray;
    Spinner activityListing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_activities);
        testArray = new String[3];
        testArray[0] = "test";
        testArray[1] = "test2";
        testArray[2] = "test3";
        activityListing = findViewById(R.id.spinnerActivityList);
        fillActivities();
        fillSpinner();
    }

    public void fillSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, activities[0]);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityListing.setAdapter(adapter);
    }

    public void fillActivities(){
        final String file = "/mobile/userActivities.php";
        String queryResult = "";
        webConnection web = new webConnection();
        jsonConversion json = new jsonConversion();
        String jsonString = json.encodeJsonString("activities", storedDetails.getInstance().getCustomerID(), "", "", "", "");
        if(web.checkNetworkConnection(context)){
            try {
                queryResult = web.urlConnection(file, jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            JSONArray activityJson = new JSONArray(queryResult);
            JSONObject activityObject;
            Toast.makeText(this, "Json List Length is " + activityJson.length(), Toast.LENGTH_LONG).show();
            activityInformation = new String[activityJson.length()];
            activities = new String[activityJson.length()][10];
            for(int i = 0; i < activityJson.length(); i++){
                activityObject = activityJson.getJSONObject(i);

                activities[i][0] = activityObject.getString("activityID");
                activities[i][1] = activityObject.getString("customerID");
                activities[i][2] = activityObject.getString("carReg");
                activities[i][3] = activityObject.getString("carParkID");
                activities[i][4] = activityObject.getString("enterTimestamp");
                activities[i][5] = activityObject.getString("exitTimestamp");
                if(activityObject.equals("0")){
                    activities[i][6] = "No";
                } else {
                    activities[i][6] = "Yes";
                }
                activities[i][7] = activityObject.getString("entryImage");
                activities[i][8] = activityObject.getString("exitImage");
                activities[i][9] = activityObject.getString("invoiceItem");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Json Failure", Toast.LENGTH_SHORT).show();
        }
    }
}
