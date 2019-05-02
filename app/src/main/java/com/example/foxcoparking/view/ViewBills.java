package com.example.foxcoparking.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.foxcoparking.R;
import com.example.foxcoparking.model.jsonConversion;
import com.example.foxcoparking.model.webConnection;
import com.example.foxcoparking.controller.storedDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ViewBills extends Activity implements AdapterView.OnItemSelectedListener {
    Context context = this;
    String[][] fullBills;
    String[] invoiceIDs;
    Spinner invoiceListing;
    String currentBill;
    ArrayAdapter<String> adapter;
    TextView invoiceID;
    TextView cost;
    TextView currency;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bills);
        //Text Views
        invoiceID = findViewById(R.id.textViewInvoiceID);
        cost = findViewById(R.id.textViewCost);
        currency = findViewById(R.id.textViewCurrency);
        status = findViewById(R.id.textViewPaymentStatus);

        invoiceListing = findViewById(R.id.spinnerInvoiceID);
        invoiceListing.setOnItemSelectedListener(this);
        getBills();
        fillInvoiceIDs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBills();
        fillInvoiceIDs();
    }

    private void fillInvoiceIDs(){
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, invoiceIDs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        invoiceListing.setAdapter(adapter);
    }

    private void getBills(){
        webConnection web = new webConnection();
        jsonConversion jsonConversion = new jsonConversion();
        String result = "";
        final String file = "/mobile/bills.php";
        if(web.checkNetworkConnection(context)){
            String json = jsonConversion.encodeJsonString("deleteUser", storedDetails.getInstance().getCustomerID(), "", "", "", "");
            try {
                result = web.urlConnection(file, json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        decodeJson(result);
    }

    private void decodeJson(String json){
        try{
            JSONArray bills = new JSONArray(json);
            invoiceIDs = new String[bills.length()];
            fullBills = new String[bills.length()][5];

            for(int i = 0; i < bills.length(); i++){
                JSONObject userObject = bills.getJSONObject(i);
                invoiceIDs[i] = userObject.getString("id");
                fullBills[i][0] = userObject.getString("id");
                fullBills[i][1] = convertToCurrency(userObject.getString("amount_due"));
                String status = userObject.getString("status");
                if(status.equals("open")){
                    fullBills[i][2] = "Unpaid";
                } else if(status.equals("void")){
                    fullBills[i][2] = "Voided";
                } else if(status.equals("draft")){
                    fullBills[i][2] = "Draft";
                } else if(status.equals("paid")){
                    fullBills[i][2] = "Paid";
                } else {
                        fullBills[i][2] = "Uncollectible";
                }
                fullBills[i][3] = userObject.getString("currency");
                fullBills[i][4] = userObject.getString("hosted_invoice_url");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String convertToCurrency(String value){
        String result;

        Double currency = Double.parseDouble(value);
        currency = currency / 100;

        result = currency.toString();

        return result;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        for (int i = 0; i < fullBills.length; i++) {
            if(fullBills[i][0].equals(item)){
                invoiceID.setText(fullBills[i][0]);
                cost.setText(fullBills[i][1]);
                currency.setText(fullBills[i][3]);
                status.setText(fullBills[i][2]);
                currentBill = fullBills[i][4];
                checkPaidState(fullBills[i][2]);

                break;
            } else {

            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void payBill(View view){
        Intent intent = new Intent(this, payBillOnline.class);
        intent.putExtra("http", currentBill);
        startActivity(intent);

    }

    public void checkPaidState(String status){
        TextView payLabel =findViewById(R.id.labelPayButton);
        Button payButton = findViewById(R.id.buttonPay);
        if(status.equals("Paid")){
            payLabel.setVisibility(View.INVISIBLE);
            payButton.setVisibility(View.INVISIBLE);
        } else if(status.equals("Voided")) {
            payLabel.setVisibility(View.INVISIBLE);
            payButton.setVisibility(View.INVISIBLE);
        } else if(status.equals("Draft")) {
            payLabel.setVisibility(View.INVISIBLE);
            payButton.setVisibility(View.INVISIBLE);
        }else {
            payLabel.setVisibility(View.VISIBLE);
            payButton.setVisibility(View.VISIBLE);
        }
    }
}
