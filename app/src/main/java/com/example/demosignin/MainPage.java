package com.example.demosignin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainPage extends AppCompatActivity implements LocationListener {

    final Context context = this;
    private Button button;
    private Button getLocation;
    private Button gotomap;
    TextView locationText;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);


        button = (Button) findViewById(R.id.ghat);
        getLocation = (Button) findViewById(R.id.getLocationBtn) ;
        locationText = (TextView)findViewById(R.id.locationText);

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
                getLocation();
            }
        });
        gotomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPage.this, RoutesForUser.class));
            }
        });

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