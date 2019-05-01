package com.example.foxcoparking;

import java.sql.Date;

public class activity {
    private int ActivityID;
    private String CustomerID;
    private String CarReg;
    private int CarParkID;
    private Date EnterTime;
    private Date ExitTime;
    private boolean SeasonPermit;
    private String EntryImage;
    private String ExitImage;
    private String InvoiceItem;

    public activity(){
        super();
    }

    public void setActivityID(int activityID){ActivityID = activityID;}
    public void setCustomerID(String customerID){CustomerID = customerID;}
    public void setCarReg(String carReg){CarReg = carReg;}
    public void setCarParkID(int carParkID){CarParkID = carParkID;}
    public void setEnterTime(Date enterTime){EnterTime = enterTime;}
    public void setExitTime(Date exitTime){ExitTime = exitTime;}
    public void setSeasonPermit(boolean seasonPermit){SeasonPermit = seasonPermit;}
    public void setEntryImage(String entryImage){EntryImage = entryImage;}
    public void setExitImage(String exitImage){ExitImage = exitImage;}
    public void setInvoiceItem(String invoiceItem){InvoiceItem = invoiceItem;}

    public int getActivityID(){return ActivityID;}
    public String getCustomerID(){return CustomerID;}
    public String getCarReg(){return CarReg;}
    public int getCarParkID(){return CarParkID;}
    public Date getEnterTime(){return EnterTime;}
    public Date getExitTime(){return ExitTime;}
    public boolean isSeasonPermit(){return SeasonPermit;}
    public String getEntryImage(){return EntryImage;}
    public String getExitImage(){return ExitImage;}
    public String getInvoiceItem(){return InvoiceItem;}
}
