package com.example.accompany;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RatingHandler extends AppCompatActivity {


    private RatingBar ratingBar;
    private Button submitrating;
    private TextView yourating;
    public int rateno = 0;
    public int usernumber;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ratings);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        submitrating = (Button)findViewById(R.id.submitrating);
        yourating = (TextView) findViewById(R.id.ratingtext);


        final Intent mIntent = getIntent();
        usernumber = mIntent.getIntExtra("user idnumber", 0);

        Log.w("got usernumber","msg"+usernumber);



        submitrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RatingHandler.this,"leave",Toast.LENGTH_SHORT).show();
                yourating.setText("Your rating is"+ratingBar.getRating());

                final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("groups").child("Ghat");
                final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("users");
                if(usernumber%2 == 1){


                    final Query query1 = reference1.orderByChild("userID").equalTo(usernumber+1);
                    //
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                String key=childSnapshot.getKey();
                                Log.w("geting key, evens",key);
                                String name = dataSnapshot.child(key).child("name").getValue().toString();
                                Log.w("opposite person name",name);

                                final DatabaseReference ref3 = reference2.child(name).child("ratingnumber");
                                ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String datastr = dataSnapshot.getValue().toString();
                                        rateno = Integer.parseInt(datastr);
                                        int ratenewno = rateno+1;
                                        ref3.setValue(rateno+1);
                                        Intent myIntent = new Intent(RatingHandler.this, MainPage.class);
                                        // myIntent.putExtra("rating number",ratenewno);
                                        startActivity(myIntent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            //  Log.w("geting key",dataSnapshot.getKey());
//                                    Log.w("datasnp","here"+dataSnapshot.toString());
//                                    String name = dataSnapshot.child("name").getValue().toString();
//                                    Log.w("opposite person name",name);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                else{
                    final Query query3 = reference1.orderByChild("userID").equalTo(usernumber-1);

                    query3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                String key=childSnapshot.getKey();
                                Log.w("geting key,odds",key);
                                String name = dataSnapshot.child(key).child("name").getValue().toString();
                                Log.w("opposite person name",name);

                                final DatabaseReference ref3 = reference2.child(name).child("ratingnumber");

                                ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                        String datastr = dataSnapshot.getValue().toString();
                                        rateno = Integer.parseInt(datastr);
                                        int ratenewno = rateno+1;
                                        ref3.setValue(rateno+1);
                                        Intent myIntent = new Intent(RatingHandler.this, MainPage.class);
                                        //myIntent.putExtra("rating number",ratenewno);
                                        startActivity(myIntent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

//

//                                Log.w("rating","submit"+rateno);
                            }




                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }


            }
        });
    }
}
