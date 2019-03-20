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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainPage extends AppCompatActivity implements LocationListener {

    FirebaseAuth Auth;
    final Context context = this;
    private Button ghatButton;
    private Button getLocation;
    private Button gotomap;

    TextView locationText;
    TextView destlocationtext;
    TextView distance;
    TextView example;
    LocationManager locationManager;

    public double currentLat;
    public double currentLon;
    Location loc1, loc2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);


        ghatButton = (Button) findViewById(R.id.ghat);
        getLocation = (Button) findViewById(R.id.getLocationBtn) ;
        locationText = (TextView)findViewById(R.id.locationText);
        destlocationtext = (TextView)findViewById(R.id.destlocationtext);
        distance = (TextView)findViewById(R.id.distance);
        example = (TextView)findViewById(R.id.example);
        gotomap = (Button)findViewById(R.id.getmap);


        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainPage.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        // add button listener
        ghatButton.setOnClickListener(new View.OnClickListener() {

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
                Button joingroup = (Button)dialog.findViewById(R.id.joingroup);
               // startActivity(new Intent(ProfileActivity.this, Login.class));

                joingroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  startActivity(new Intent(MainPage.this, GroupChat.class));
                        Intent myIntent = new Intent(MainPage.this, GroupChat.class);
                        startActivity(myIntent);
                    }
                });
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
                        Auth= FirebaseAuth.getInstance();
                        final FirebaseUser userf = Auth.getCurrentUser();
                        final String displayname = userf.getDisplayName();
                        Log.w("display name", displayname);
                        if(userf!=null)
                        {
                            String url = "https://costoptimized.firebaseio.com/users.json";
                            final ProgressDialog pd = new ProgressDialog(MainPage.this);
                            pd.setMessage("Loading...");
                            pd.show();

                            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    if (s.equals("null")) {
                                        Toast.makeText(MainPage.this, "user not found", Toast.LENGTH_LONG).show();
                                    } else {
                                        try {
                                            JSONObject obj = new JSONObject(s);

                                            if (!obj.has(displayname)) {
                                                Toast.makeText(MainPage.this, "inside user not found", Toast.LENGTH_LONG).show();
                                            }  else {
                                                UserDetails.username = displayname;
                                                ///UserDetails.password = pass;
                                                startActivity(new Intent(MainPage.this, Users.class));
                                                //Toast.makeText(Login.this, "incorrect password", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    pd.dismiss();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    System.out.println("" + volleyError);
                                    pd.dismiss();
                                }
                            });

                            RequestQueue rQueue = Volley.newRequestQueue(MainPage.this);
                            rQueue.add(request);
                            //startActivity(new Intent(MainPage.this, Login.class));
                        }
                        else{
                            Log.w("User not logged in","bad");
                        }
                        //startActivity(new Intent(MainPage.this, Login.class));
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


// get distance between two points
                Location loc1 = new Location("");
                loc1.setLatitude(currentLat);
                loc1.setLongitude(currentLon);

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