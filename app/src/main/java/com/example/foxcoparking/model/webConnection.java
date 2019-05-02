package com.example.foxcoparking.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class webConnection {

    public String urlConnection(String file, String output) throws IOException {
        final String host = "www.foxcoparkingsolution.co.uk";
        URL urlConnection = new URL("http", host, file);
        URLConnection conn = urlConnection.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setAllowUserInteraction(true);
        conn.connect();

        //Send Data
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes(output);
        out.flush();
        out.close();

        //Recieve Reply
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );

        String line;
        while((line = in.readLine()) != null){
            sb.append(line);
        }

        in.close();

        //return "test";
        return sb.toString();
    }

    public boolean checkNetworkConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
