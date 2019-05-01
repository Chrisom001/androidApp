package com.example.foxcoparking;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class storeUserDetails {
    public String fillUserData(String customerID, Context context){
        String finalResult = "";
        try {
            final String file = "/mobile/userDetails.php";
            jsonConversion jsonConversion = new jsonConversion();
            String json = jsonConversion.encodeJsonString("customerID", customerID, "", "", "", "");
            webConnection web = new webConnection();

            if(web.checkNetworkConnection(context)){

                String result = web.urlConnection(file, json);

                if (result.equals("False")){
                    finalResult = "notUpdated";
                } else{
                    decodeJson(result);
                    finalResult = "updated";
                }
            } else {
                finalResult = "noInternet";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalResult;
    }

    private void decodeJson(String json){
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
