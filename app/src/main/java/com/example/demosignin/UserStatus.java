package com.example.demosignin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.Thread.interrupted;

//This page is to show the online users
public class UserStatus extends AppCompatActivity {

    FirebaseAuth auth;
    ListView usersList,grid;
    TextView noUsersText;
    Button findpartner,matchwithpartner;
    ArrayList<String> al = new ArrayList<>();
    ArrayList<String> ride = new ArrayList<>();
    ArrayList<String> rideNames = new ArrayList<>();
    public DatabaseReference databaseReference1,databaseReference2, partnerchat;
    public int itemCount;

    public int dbid_counter;
    public int userID;

    public String displayname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);
        usersList = (ListView)findViewById(R.id.usersList);
        noUsersText = (TextView)findViewById(R.id.noUsersText);
        findpartner = (Button)findViewById(R.id.findpartner);
        //matchwithpartner = (Button)findViewById(R.id.matchwithpartner);

       //in FirebaseDatabase.getInstance().setPersistenceEnabled(false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users");
        Log.w("reference",reference.toString());
        //searching based on the status=nline
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
                String vals = dataSnapshot.toString();
                Log.w("values",dataSnapshot.toString());
                al.add(vals);
                usersList.setAdapter(new ArrayAdapter<String>(UserStatus.this, android.R.layout.simple_list_item_1, al));
                usersList.setVisibility(View.VISIBLE);
                noUsersText.setText("No of users online"+noofonline);
                noUsersText.setVisibility(View.VISIBLE);
            }

            public void onCancelled(DatabaseError databaseError) { }
        });







        findpartner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                final FirebaseUser userf = auth.getCurrentUser();
                 displayname = userf.getDisplayName();

//                databaseReference1 = FirebaseDatabase.getInstance().getReference("groups").child("ghat_rides").child("partnerride1");
               // databaseReference2 = FirebaseDatabase.getInstance().getReference().child("groups").child("ghat_rides");

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



                        String key = databaseReference2.push().getKey();
                        databaseReference2.child(key).child("name").setValue(displayname);
                        databaseReference2.child(key).child("userID").setValue(userID);


                        if(userID%2==1){
                            Log.w("odd user!",user);

                            Thread t = new Thread(){
                                @Override
                                public void run(){
                                    while (!isInterrupted())
                                    {
                                        try{
                                            Thread.sleep(3000);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    final Query nextuser = databaseReference2.orderByChild("userID").equalTo(userID+1);

                                                    nextuser.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()){

                                                                Log.w("next user present","show");
                                                                Intent myIntent = new Intent(UserStatus.this, Partner_Chat.class);
                                                                myIntent.putExtra("user id", userID+1);
                                                                startActivity(myIntent);

                                                            }
                                                            else{
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
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            };
                            t.start();



                        }

                        else{
                            Log.w("even user!",user);

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
                            startActivity(myIntent);



                        }

                    }
                    public void onCancelled(DatabaseError databaseError) { }
                });






            }
        });



//        matchwithpartner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                auth = FirebaseAuth.getInstance();
//                final FirebaseUser userf = auth.getCurrentUser();
//                displayname = userf.getDisplayName();
//                databaseReference1 = FirebaseDatabase.getInstance().getReference("groups").child("ghat_rides").child("partnerride1");
//                databaseReference1.addValueEventListener(new ValueEventListener() {
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()){
//                            Long noofpeople = dataSnapshot.getChildrenCount();
//
//                            if (noofpeople%2==0){
//                                for(DataSnapshot datas: dataSnapshot.getChildren()) {
//                                    Object value = datas.getValue();
//                                    rideNames.add(value.toString());
//
//                                    itemCount = rideNames.size();
//
//
//                                }
//                                    for (int i = 1; i < itemCount; i=i+2)
//                                    {
//                                        if(rideNames.get(i-1).contains(displayname)|| rideNames.get(i).contains(displayname)){
//                                            startActivity(new Intent(UserStatus.this, Partner_Chat.class));
//                                        }
//                                        else {
//                                            Log.w("you","not happening");
//                                        }
//
//                                    }
//
//
//                            }
//                            else {
//                                Toast.makeText(UserStatus.this, "Wait for partner", Toast.LENGTH_SHORT).show();
//
//                            }
//
//                        }
//
//
//                    }
//
//                    public void onCancelled(DatabaseError databaseError) { }
//                });
//
//
//
//
//
//
//            }
//        });



    }




}