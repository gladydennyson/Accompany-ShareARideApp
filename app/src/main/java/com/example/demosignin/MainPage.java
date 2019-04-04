package com.example.demosignin;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainPage extends AppCompatActivity implements LocationListener {

    FirebaseAuth Auth;
    private DatabaseReference dataref;
    private String name;
    private String statusget;
    final Context context = this;
    private Button ghatButton;
    private Button getLocation;
    private Button gotomap;
    ImageButton sharerickshawspots;

   // TextView locationText;
   // TextView destlocationtext;
    TextView distance;
    TextView number;
    LocationManager locationManager;

    public double currentLat;
    public double currentLon;
    Location loc1, loc2;
    boolean userdistance = false;
    int count = 0;
    public ProgressBar spinner;
    TextView gettingloc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);


        ghatButton = (Button) findViewById(R.id.ghat);
       // getLocation = (Button) findViewById(R.id.getLocationBtn) ;
       // locationText = (TextView)findViewById(R.id.locationText);
        //destlocationtext = (TextView)findViewById(R.id.destlocationtext);
        distance = (TextView)findViewById(R.id.distance);
       // gotomap = (Button)findViewById(R.id.getmap);
        number = (TextView)findViewById(R.id.number);
        spinner = (ProgressBar)findViewById(R.id.location_progress);
        gettingloc = (TextView)findViewById(R.id.getingloctext);
      //  sharerickshawspots = (ImageButton)findViewById(R.id.sharerickshawspots);

        final Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);


        Thread t = new Thread(){
            @Override
            public void run(){
                while (!isInterrupted()){
                    try{
                        Thread.sleep(1000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

//                                count++;
                                LatLng dest = new LatLng(19.1233238,72.8842004);
                                getLocation();
                                loc1 = new Location("");
                                loc1.setLatitude(currentLat);
                                loc1.setLongitude(currentLon);

                                loc2 = new Location("");
                                loc2.setLatitude(dest.latitude);
                                loc2.setLongitude(dest.longitude);


                                float distanceInMeters = loc1.distanceTo(loc2);
                                int distance_int = (int) distanceInMeters;
                                if(distanceInMeters<=12000){
                                    userdistance = true;
                                }
                                else{
                                    userdistance = false;


                                }
                                if(distanceInMeters == 8220823.0)
                                {
                                number.setText("Distance: calculating..");
                                }
                                else{
                                    number.setText(String.valueOf("Distance: " + distance_int + " meters"));
                            }
                            }
                        });

                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }

                }
            }
        };
        t.start();


        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainPage.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        Auth = FirebaseAuth.getInstance();
        name = Auth.getCurrentUser().getDisplayName();
        // add button listener
        ghatButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (userdistance == true) {


                            final String status = "online";


//
//                        startActivity(myIntent);
                            // startActivity(new Intent(MainPage.this, UserStatus.class));


                            final Firebase reference = new Firebase("https://costoptimized.firebaseio.com/users");
                            reference.child(name).child("status").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {

                                    if (snapshot.getValue() != null) {
                                        statusget = snapshot.getValue().toString();
                                        Log.w("status of user", statusget);

                                        //setting to online only if it is offline
                                        if (snapshot.getValue().equals("offline")) {
                                            Log.w("hello", "helo");
                                            reference.child(name).child("status").setValue(status);
                                            startActivity(new Intent(MainPage.this, UserStatus.class));
                                        }
                                        //if not, as of now do nothing, go to the userstatus page,
                                        // here we have to tell that distance is more than 2km
                                        else {
                                            Log.w("status", "did not set");
                                            startActivity(new Intent(MainPage.this, UserStatus.class));
                                        }

                                    } else {
                                        Log.w("TAG", " it's null.");
                                    }

                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    Log.e("onCancelled", " cancelled");
                                }
                            });




            }
            }
        });



//        gotomap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainPage.this, RoutesForUser.class));
//            }
//        });




//        sharerickshawspots.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, (LocationListener) this);

        }
        catch(SecurityException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
       // locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();
        spinner.setVisibility(View.GONE);
        gettingloc.setVisibility(View.GONE);
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(MainPage.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }



}