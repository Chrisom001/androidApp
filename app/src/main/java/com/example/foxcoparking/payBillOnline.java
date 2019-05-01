package com.example.foxcoparking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class payBillOnline extends AppCompatActivity {
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bill_online);
        getWebAddress();

        WebView billweb = findViewById(R.id.billWebs);

        WebSettings settings = billweb.getSettings();
        settings.setJavaScriptEnabled(true);
        billweb.loadUrl(address);
    }

    private void getWebAddress(){
        address = getIntent().getStringExtra("http");
    }
}
