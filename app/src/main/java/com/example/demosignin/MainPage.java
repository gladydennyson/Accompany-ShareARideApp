package com.example.demosignin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

public class MainPage extends AppCompatActivity implements LocationListener {

    final Context context = this;
    private Button button;
    private Button getLocation;
    private Button gotomap;
    TextView locationText;
    TextView destlocationtext;
    TextView distance;
    LocationManager locationManager;

    public double currentLat;
    public double currentLon;
    Location loc1, loc2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);


        button = (Button) findViewById(R.id.ghat);
        getLocation = (Button) findViewById(R.id.getLocationBtn) ;
        locationText = (TextView)findViewById(R.id.locationText);
        destlocationtext = (TextView)findViewById(R.id.destlocationtext);
        distance = (TextView)findViewById(R.id.distance);
        gotomap = (Button)findViewById(R.id.getmap);


        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainPage.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }




        // add button listener
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom);
                dialog.setTitle("Title...");

                // set the custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.text);
                text.setText("Android custom dialog example!");


                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                Button gochatroom = (Button) dialog.findViewById(R.id.gochatroom);
               // startActivity(new Intent(ProfileActivity.this, Login.class));

                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                gochatroom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainPage.this, Login.class));
                    }
                });
                dialog.show();
            }
        });


        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LatLng dest = new LatLng(19.0868058,72.9058244);
                destlocationtext.setText("Ghatkopar Latitude: " + dest.latitude + "\n Ghatkopar Longitude: " + dest.longitude);

                getLocation();


                //CalculationByDistance(currentLat, currentLon, dest.latitude, dest.longitude);
                Location loc1 = new Location("");
                loc1.setLatitude(19.0994779);
                loc1.setLongitude(72.9145689);

                Location loc2 = new Location("");
                loc2.setLatitude(dest.latitude);
                loc2.setLongitude(dest.longitude);


                float distanceInMeters = loc1.distanceTo(loc2);
                distance.setText("Distance: " + distanceInMeters);




            }
        });
        gotomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPage.this, RoutesForUser.class));
            }
        });

    }

// get distance between two points - formula method

    public void CalculationByDistance(double currLat, double currLon, double destlat, double destlon) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = currLat;
        double lat2 = destlat;
        double lon1 = currLon;
        double lon2 = destlon;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        distance.setText("Distance: " + Radius * c);

    }



    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, (LocationListener) this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainPage.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }
}