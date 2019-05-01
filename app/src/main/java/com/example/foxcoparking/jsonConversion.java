package com.example.foxcoparking;

import org.json.JSONException;
import org.json.JSONObject;

public class jsonConversion {
    public String encodeJsonString(String reason, String option1, String option2, String option3, String option4, String option5){
        String json = "";

        JSONObject userDetails = new JSONObject();
        try{
            if(reason.equals("login")){
                userDetails.put("userName", option1);
                userDetails.put("password", option2);
            } else if (reason.equals("customerID")){
                userDetails.put("customerID", option1);
            } else if (reason.equals("userDetails")){
                userDetails.put("firstName", option1);
                userDetails.put("lastName", option2);
                userDetails.put("carReg", option4);
                if(!option4.isEmpty()){
                    userDetails.put("password", option3);
                } else {
                    userDetails.put("password", "none");
                }
                userDetails.put("emailAddress", storedDetails.getInstance().getCustomerEmail());
            } else if(reason.equals("deleteUser")) {
                userDetails.put("customerID", option1);
            }else{
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        json = userDetails.toString();

        return json;
    }
}
