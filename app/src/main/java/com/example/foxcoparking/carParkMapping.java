package com.example.foxcoparking;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class carParkMapping extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    Context context = this;
    String[][] Cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_park_mapping);
        getCities();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void getCities() {
        String queryResult = "";
        final String file = "/mobile/address.php";
        webConnection web = new webConnection();
        if(web.checkNetworkConnection(this)){
            try {
                queryResult = web.urlConnection(file, "noDataRequired");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No Web Connection", Toast.LENGTH_LONG).show();
        }

        jsonDecodeCities(queryResult);
    }

    public void jsonDecodeCities(String json){
        try {
            JSONArray cityJson = new JSONArray(json);
            JSONObject activityObject;
            Cities = new String[cityJson.length()][13];
            for(int i = 0; i < cityJson.length(); i++){
                activityObject = cityJson.getJSONObject(i);
                Cities[i][0] = activityObject.getString("address1");
                Cities[i][1] = activityObject.getString("address2");
                Cities[i][2] = activityObject.getString("cityName");
                Cities[i][3] = activityObject.getString("postcode");
                Cities[i][4] = activityObject.getString("carParkName");
                Cities[i][5] = activityObject.getString("band0_2");
                Cities[i][6] = activityObject.getString("band2_3");
                Cities[i][7] = activityObject.getString("band3_4");
                Cities[i][8] = activityObject.getString("band4_5");
                Cities[i][9] = activityObject.getString("band5_6");
                Cities[i][10] = activityObject.getString("band6_12");
                Cities[i][11] = activityObject.getString("band12_24");
                Cities[i][12] = activityObject.getString("band24plus");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Json Failure", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng defaultLocation = new LatLng(56, -3);
        for(int i = 0; i < Cities.length; i++){
            LatLng city = geocode(Cities[i][0] + " " + Cities[i][1] + " " + Cities[i][2] + " " + Cities[i][3]);
            mMap.addMarker(new MarkerOptions().position(city).title(Cities[i][4]));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));
        mMap.setOnMarkerClickListener(this);
    }

    public LatLng geocode(String addressEntered) {
        LatLng city = null;
        Geocoder geocode = new Geocoder(this);

        if (geocode.isPresent()) {
            List<Address> list = null;
            try {
                list = geocode.getFromLocationName(addressEntered, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = list.get(0);
            double lat = address.getLatitude();
            double lng = address.getLongitude();

            city = new LatLng(lat, lng);
        }
        return city;
    }
//https://developers.google.com/maps/documentation/android-sdk/marker
    @Override
    public boolean onMarkerClick(final Marker marker){
        AlertDialog.Builder prices = new AlertDialog.Builder(this);
        double[] price = new double[8];
        for(int i = 0; i < Cities.length; i++){
            if(Cities[i][4].equals(marker.getTitle())){
                for(int j = 0; j < price.length; j++){
                    price[j] = (Double.parseDouble(Cities[i][5 + j])) / 100;
                }
                break;
            }
        }
        String prices1 = "0-2 Hours: " + price[0];
        String prices2 = "2-3 Hours: " + price[1];
        String prices3 = "3-4 Hours: " + price[2];
        String prices4 = "4-5 Hours: " + price[3];
        String prices5 = "5-6 Hours: " + price[4];
        String prices6 = "6-12 Hours: " + price[5];
        String prices7 = "12-24 Hours: " + price[6];
        String prices8 = "24+ Hours: " + price[7];
        prices.setMessage("These are the current prices \n "
                + "£" + prices1 + "\n" + "£" +prices2 + "\n"+ "£" +prices3 + "\n" + "£" +prices4 +
                "\n" + "£" +prices5 + "\n" + "£" + prices6 + "\n" + "£" + prices7 + "\n" + "£" + prices8).setTitle(marker.getTitle() + " Prices");
        AlertDialog dialog = prices.create();
        dialog.show();
        return false;
    }
}
