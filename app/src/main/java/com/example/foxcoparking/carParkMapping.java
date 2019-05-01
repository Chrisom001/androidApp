package com.example.foxcoparking;

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
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class carParkMapping extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context context = this;
    String[][] Cities;
    String[][] geoCodeCities;

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
            Cities = new String[cityJson.length()][5];
            for(int i = 0; i < cityJson.length(); i++){
                activityObject = cityJson.getJSONObject(i);
                Cities[i][0] = activityObject.getString("address1");
                Cities[i][1] = activityObject.getString("address2");
                Cities[i][2] = activityObject.getString("cityName");
                Cities[i][3] = activityObject.getString("postcode");
                Cities[i][4] = activityObject.getString("carParkName");
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
}
