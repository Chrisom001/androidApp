package com.example.foxcoparking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class viewActivities extends Activity implements AdapterView.OnItemSelectedListener {
    Context context = this;
    String[][] activities;
    String[] activityInformation;
    Spinner activityListing;
    ArrayAdapter<String> adapter;
    TextView carText;
    TextView carParkText;
    TextView entryTimeText;
    TextView exitTimeText;
    TextView seasonPermitText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_activities);

        //Set all the text views
        carText = findViewById(R.id.textViewCarRegUsed);
        carParkText = findViewById(R.id.textViewCarParkUsed);
        entryTimeText = findViewById(R.id.textViewEntryTime);
        exitTimeText = findViewById(R.id.textViewExitTime);
        seasonPermitText = findViewById(R.id.textViewSeasonPermit);

        activityListing = findViewById(R.id.spinnerActivityList);
        activityListing.setOnItemSelectedListener(this);
        fillActivities();
        fillSpinner();
        Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
    }

    public void fillSpinner(){
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, activityInformation);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityListing.setAdapter(adapter);
    }

    public void fillActivities(){
        final String file = "/mobile/userActivities.php";
        final String carParkFile = "/mobile/carPark.php";
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
            activityInformation = new String[activityJson.length()];
            activities = new String[activityJson.length()][10];
            for(int i = 0; i < activityJson.length(); i++){
                activityObject = activityJson.getJSONObject(i);
                activityInformation[i] = activityObject.getString("activityID");
                activities[i][0] = activityObject.getString("activityID");
                activities[i][1] = activityObject.getString("customerID");
                activities[i][2] = activityObject.getString("carReg");
                String carParkjson = json.encodeJsonString("carParkName", activityObject.getString("carParkID"), "", "", "", "");
                String carParkName = web.urlConnection(carParkFile, carParkjson);
                activities[i][3] = carParkName;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        String item = parent.getItemAtPosition(position).toString();
        for (int i = 0; i < activities.length; i++) {
            if(activities[i][0].equals(item)){
                carText.setText(activities[i][2]);
                carParkText.setText(activities[i][3]);
                entryTimeText.setText(activities[i][4]);
                exitTimeText.setText(activities[i][5]);
                seasonPermitText.setText(activities[i][6]);
                break;
            } else {

            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
