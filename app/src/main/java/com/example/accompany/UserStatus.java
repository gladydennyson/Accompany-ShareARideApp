package com.example.accompany;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//This page is to show the online users
public class UserStatus extends AppCompatActivity  {

    private static final String TAG = UserStatus.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    LocationManager locationManager;
    FirebaseAuth auth;
    ListView usersList,grid;
    TextView noUsersText;
    Button findpartner,matchwithpartner;
    ArrayList<String> al = new ArrayList<>();
    ArrayList<String> ride = new ArrayList<>();
    ArrayList<String> rideNames = new ArrayList<>();
    public DatabaseReference databaseReference1,databaseReference2, partnerchat;
    public int itemCount;

    TextView distancenumber;
    public Location loc1, loc2;
    boolean userdistance = false;
    public int dbid_counter;
    public int userID,usernumber;

    public String displayname;
    public double loc1lat,loc1long;
    public static boolean stopThread =false;
    public String key;
    ImageView onlineimage;

    public double currentLat;
    public double currentLon;
    boolean flag = true;
    private boolean mAlreadyStartedService = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);
        usersList = (ListView)findViewById(R.id.usersList);
        noUsersText = (TextView)findViewById(R.id.noUsersText);
        onlineimage = (ImageView)findViewById(R.id.onlineimage);
       // findpartner = (Button)findViewById(R.id.findpartner);
        distancenumber = (TextView)findViewById(R.id.distancenumber);


       //in FirebaseDatabase.getInstance().setPersistenceEnabled(false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users");
        Log.w("reference",reference.toString());
        //searching based on the status=nline

        stopThread = false;
        auth = FirebaseAuth.getInstance();
        final FirebaseUser userf = auth.getCurrentUser();
        displayname = userf.getDisplayName();


        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);


                        if (latitude != null && longitude != null) {
                            loc1lat = Double.parseDouble(latitude);
                            loc1long = Double.parseDouble(longitude);
                            Log.w("double","value"+loc1lat);
                            loc1 = new Location("");
                            loc1.setLatitude(loc1lat);
                            loc1.setLongitude(loc1long);

                        }
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );

        Thread t = new Thread(){
            @Override
            public void run(){

                    try{
                        Thread.sleep(3000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.w("msg1","running thread");
                                LatLng dest = new LatLng(19.0868109,72.9058244);
                                loc2 = new Location("");
                                loc2.setLatitude(dest.latitude);
                                loc2.setLongitude(dest.longitude);
                                Log.w("SOURCE","loc"+loc1);
                                Log.w("dest","loc"+loc2);

                                float distanceInMeters = loc1.distanceTo(loc2);
                                int distance_int = (int) distanceInMeters;
                                Log.w("distance","dist"+distanceInMeters);

                                if(distanceInMeters<=12000){
                                    Log.w("msg2","running thread inside dist");
                                    userdistance= true;
                                    databaseReference1 = FirebaseDatabase.getInstance().getReference("groups").child("Ghat").child("dbid");
                                    databaseReference2 = FirebaseDatabase.getInstance().getReference("groups").child("Ghat");

                                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Long count = dataSnapshot.getChildrenCount();


                                            //Log.w("Db","Inserted");
                                            String value = dataSnapshot.getValue().toString();
                                            dbid_counter = Integer.parseInt(value);
                                            userID = dbid_counter;
                                            Log.w("reading",value);

                                            databaseReference1.setValue(dbid_counter+1);
                                            String user=String.valueOf(userID);
                                            Log.w("counter",user);



                                            key = databaseReference2.push().getKey();
                                            databaseReference2.child(key).child("name").setValue(displayname);
                                            databaseReference2.child(key).child("userID").setValue(userID);


                                            if(userID%2==1){
                                                Log.w("odd user!",user);

                                                usernumber = userID;
                                                Thread t = new Thread(){
                                                    @Override
                                                    public void run(){
                                                        while (!stopThread)
                                                        {
                                                            try{
                                                                Thread.sleep(3000);

                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Log.w("msg3","running thread inside 3rd thread");
                                                                        final Query nextuser = databaseReference2.orderByChild("userID").equalTo(userID+1);

                                                                        nextuser.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                if (dataSnapshot.exists()){

                                                                                    stopThread=true;
                                                                                    Log.w("msg4","running thread");
                                                                                    Log.w("next user present","show");
                                                                                    Intent myIntent = new Intent(UserStatus.this, Partner_Chat.class);

                                                                                    myIntent.putExtra("user exact number",usernumber);
                                                                                    myIntent.putExtra("user id", userID+1);
                                                                                    myIntent.putExtra("user push key",key);
                                                                                    startActivity(myIntent);


                                                                                }
                                                                                else{

                                                                                    Log.w("msg5","running thread");
                                                                                    Log.w("user not present","show");
                                                                                    Toast.makeText(UserStatus.this,"User not present",Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }
                                                                });

                                                            } catch (InterruptedException e){
                                                                Thread.currentThread().interrupt();
                                                                e.printStackTrace();
                                                                Log.e("print",e.toString());
                                                            }

                                                        }
                                                    }
                                                };
                                                t.start();




                                            }

                                            else{
                                                Log.w("msg6","running thread");
                                                Log.w("even user!",user);

                                                usernumber = userID;

                                                partnerchat = FirebaseDatabase.getInstance().getReference("groups").child("Ghat").child("partnerchat"+userID);

                                                partnerchat.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        partnerchat.push().setValue(new Partner_Chat_Message(displayname+"has joined",
                                                                FirebaseAuth.getInstance()
                                                                        .getCurrentUser()
                                                                        .getDisplayName()));

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });



                                                Intent myIntent = new Intent(UserStatus.this, Partner_Chat.class);
                                                myIntent.putExtra("user id", userID);
                                                myIntent.putExtra("user exact number",usernumber);
                                                myIntent.putExtra("user push key",key);

                                                startActivity(myIntent);



                                            }

                                        }
                                        public void onCancelled(DatabaseError databaseError) { }
                                    });

                                }
                                else{
                                   Toast.makeText(UserStatus.this,"far from loc",Toast.LENGTH_SHORT).show();


                                }
                                if(distanceInMeters == 8220823.0)
                                {
                                    distancenumber.setText("Distance: calculating..");
                                }
                                else{
                                    distancenumber.setText(String.valueOf("Distance: " + distance_int + " meters"));
                                }
                                //Toast.makeText(MainPage.this,"Thread wrks",Toast.LENGTH_SHORT).show();


//
                            }
                        });

                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }


            }
        };
        t.start();
        if (userdistance==true){
            t.interrupt();
        }




        final Query query = reference.orderByChild("status").equalTo("online");
//
        //Log.w("online users",query.toString());
        //if online displaying

        ride.clear();
        rideNames.clear();
       query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Long noofonline = dataSnapshot.getChildrenCount();
                //String vals = dataSnapshot.toString();
               // Log.w("values",dataSnapshot.toString());
               // al.add(vals);
                //usersList.setAdapter (new ArrayAdapter<String>(UserStatus.this, android.R.layout.simple_list_item_1, al));
                //usersList.setVisibility(View.VISIBLE);
                noUsersText.setText(" " + noofonline + " active users");
                noUsersText.setVisibility(View.VISIBLE);
                onlineimage.setVisibility(View.VISIBLE);

            }

            public void onCancelled(DatabaseError databaseError) { }
        });









    }



    @Override
    public void onResume() {
        super.onResume();

        startStep1();
    }


    /**
     * Step 1: Check Google Play services
     */
    private void startStep1() {

        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {

            //Passing null to indicate that it is executing for the first time.
            startStep2(null);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_google_playservice_available, Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Step 2: Check & Prompt Internet connection
     */
    private Boolean startStep2(DialogInterface dialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialog != null) {
            dialog.dismiss();
        }

        //Yes there is active internet connection. Next check Location is granted by user or not.

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }

    /**
     * Show A Dialog with button to refresh the internet state.
     */
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserStatus.this);
        builder.setTitle(R.string.title_alert_no_intenet);
        builder.setMessage(R.string.msg_alert_no_internet);

        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //Block the Application Execution until user grants the permissions
                        if (startStep2(dialog)) {

                            //Now make sure about location permission.
                            if (checkPermissions()) {

                                //Step 2: Start the Location Monitor Service
                                //Everything is there to start the service.
                                startStep3();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }

                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Step 3: Start the Location Monitor Service
     */
    private void startStep3() {

        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.

        if (!mAlreadyStartedService && distancenumber != null) {

            distancenumber.setText(R.string.msg_location_service_started);

            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationMonitoringService.class);
            startService(intent);

            mAlreadyStartedService = true;
            //Ends................................................
        }
    }

    /**
     * Return the availability of GooglePlayServices
     */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Start permissions requests.
     */
    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(UserStatus.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(UserStatus.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If img_user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startStep3();

            } else {
                // Permission denied.

                // Notify the img_user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the img_user for permission (device policy or "Never ask
                // again" prompts). Therefore, a img_user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


    @Override
    public void onDestroy() {


        //Stop location sharing service to app server.........

        stopService(new Intent(this, LocationMonitoringService.class));
        mAlreadyStartedService = false;
        //Ends................................................


        super.onDestroy();
    }


//        public void findpartner(){
//
//        }
}