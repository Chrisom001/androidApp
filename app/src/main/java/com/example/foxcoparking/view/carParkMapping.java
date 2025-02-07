package com.example.foxcoparking.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.foxcoparking.R;
import com.example.foxcoparking.model.webConnection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private static final int REQUEST_LOCATION = 1;
    Context context = this;
    String[][] Cities;
    LocationManager lm;
    LocationListener listener;
    LatLng currentLocation;


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

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getCities() {
        String queryResult = "";
        final String file = "/mobile/address.php";
        webConnection web = new webConnection();
        if (web.checkNetworkConnection(this)) {
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

    public void jsonDecodeCities(String json) {
        try {
            JSONArray cityJson = new JSONArray(json);
            JSONObject activityObject;
            Cities = new String[cityJson.length()][13];
            for (int i = 0; i < cityJson.length(); i++) {
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
        LatLng currentLocation = null;
        if(checkPermissions()){
            if(gpsCheck()){
                currentLocation = getCurrentLocation();
            } else {
                Toast.makeText(this, "Unable to display Marker as GPS not enabled", Toast.LENGTH_SHORT).show();
            }
        } else {
            requestLocationPermission();
        }

        for (int i = 0; i < Cities.length; i++) {
            LatLng city = geocode(Cities[i][0] + " " + Cities[i][1] + " " + Cities[i][2] + " " + Cities[i][3]);
            mMap.addMarker(new MarkerOptions().position(city).title(Cities[i][4]));
        }
        if(currentLocation != null){
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        } else {
            Toast.makeText(this, "Marker not working", Toast.LENGTH_SHORT).show();
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));
        mMap.setOnMarkerClickListener(this);
    }

    private boolean checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private boolean gpsCheck(){
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gpsEnabled) {
            final android.app.AlertDialog.Builder gpsNotEnabled = new android.app.AlertDialog.Builder(this);
            gpsNotEnabled.setTitle("GPS Not Enabled");
            gpsNotEnabled.setMessage("You will only be able to see your current location if you turn on GPS");
            gpsNotEnabled.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            gpsNotEnabled.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            gpsNotEnabled.create().show();
        } else {
            return true;
        }
        return false;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            final android.app.AlertDialog.Builder permission = new android.app.AlertDialog.Builder(this);
            permission.setTitle("Request Permission");
            permission.setMessage("This permission is required to show your location on the map");
            permission.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(carParkMapping.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
                }
            });
            permission.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            permission.create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Permission Denied. Unable to place current marker", Toast.LENGTH_SHORT).show();
            }
        }
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
    public boolean onMarkerClick(final Marker marker) {
        AlertDialog.Builder prices = new AlertDialog.Builder(this);
        double[] price = new double[8];
        if(marker.getTitle().equals("Current Location")){

        } else {
            for (int i = 0; i < Cities.length; i++) {
                if (Cities[i][4].equals(marker.getTitle())) {
                    for (int j = 0; j < price.length; j++) {
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
                    + "£" + prices1 + "\n" + "£" + prices2 + "\n" + "£" + prices3 + "\n" + "£" + prices4 +
                    "\n" + "£" + prices5 + "\n" + "£" + prices6 + "\n" + "£" + prices7 + "\n" + "£" + prices8).setTitle(marker.getTitle() + " Prices");
            AlertDialog dialog = prices.create();
            dialog.show();
            return false;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public LatLng getCurrentLocation() {
        final LatLng[] current = new LatLng[1];
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(checkPermissions()){
            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    current[0] = new LatLng(longitude, latitude);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                    Toast.makeText(carParkMapping.this, "GPS is disabled", Toast.LENGTH_SHORT).show();
                }
            };
            lm.requestLocationUpdates("gps", 5000, 10, listener);

            if(current[0] == null){
                LatLng meh = new LatLng(0.0,0.0);
                return meh;
            } else {
                return current[0];
            }
        } else {
            return null;
        }
    }
}
