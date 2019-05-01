package com.example.foxcoparking;

//https://www.tutorialspoint.com/java/java_using_singleton.htm - Used to work out how to build a simpleton
public class storedDetails {

    private String customerID;
    private String customerFirstName;
    private String customerLastName;
    private String customerCarReg;
    private String customerEmail;
    private String statusReason;
    private int status;


    private static storedDetails storedDetails = new storedDetails();

    private storedDetails(){}

    public static storedDetails getInstance(){
        return storedDetails;
    }

    public void setCustomerID(String custID){customerID = custID;}

    public void setCustomerFirstName(String custFirstName){
        customerFirstName = custFirstName;
    }

    public void setCustomerLastName(String custLastName){
        customerLastName = custLastName;
    }

    public void setCustomerCarReg(String custReg){
        customerCarReg = custReg;
    }

    public void setStatus (int bannedStatus){status = bannedStatus;}

    public void setCustomerEmail(String custEmail){
        customerEmail = custEmail;
    }

    public void setStatusReason(String custStatus){
        statusReason = custStatus;
    }

    public String getCustomerID() {return customerID;}

    public String getCustomerFirstName(){
        return customerFirstName;
    }

    public String getCustomerLastName(){
        return customerLastName;
    }

    public String getCustomerCarReg(){
        return customerCarReg;
    }

    public String getCustomerEmail(){
        return customerEmail;
    }

    public String getStatusReason(){
        return statusReason;
    }

    public int getStatus() {return status;}

    public void clearDetails(){
        customerFirstName = "";
        customerLastName = "";
        customerCarReg = "";
        customerEmail = "";
        statusReason = "";
        status = 0;
    }
}
