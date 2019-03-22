package com.example.demosignin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
//This page is to show the online users
public class UserStatus extends AppCompatActivity {

    FirebaseAuth auth;
    ListView usersList;
    TextView noUsersText;
    Button findpartner;
    ArrayList<String> al = new ArrayList<>();
    public DatabaseReference databaseReference1,databaseReference2;
    public String displayname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);
        usersList = (ListView)findViewById(R.id.usersList);
        noUsersText = (TextView)findViewById(R.id.noUsersText);
        findpartner = (Button)findViewById(R.id.findpartner);

       //in FirebaseDatabase.getInstance().setPersistenceEnabled(false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users");
        Log.w("reference",reference.toString());
        //searching based on the status=nline
        final Query query = reference.orderByChild("status").equalTo("online");
//
        //Log.w("online users",query.toString());
        //if online displaying
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

                databaseReference1 = FirebaseDatabase.getInstance().getReference("groups").child("ghat_rides").child("partnerride1");
               // databaseReference2 = FirebaseDatabase.getInstance().getReference().child("groups").child("ghat_rides");

                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long count = dataSnapshot.getChildrenCount();
                        if (count<=1){
                            databaseReference1.push().child("name").setValue(displayname);
                            Log.w("Db","Inserted");
                        }
                        else {
                            chechdb();
                        }

                    }
                    public void onCancelled(DatabaseError databaseError) { }
                });


            }
        });
    }


    public void chechdb(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("groups").child("ghat_rides").child("partnerride1");
        Log.w("reference",ref.toString());
        final Query group = ref.orderByChild("name").equalTo(displayname);
        Log.w("query",group.toString());
        group.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()){
                                            String vals = dataSnapshot.toString();
                                            Log.w("values",dataSnapshot.toString());
                                            // Log.w("Inside this","yay");
                                             startActivity(new Intent(UserStatus.this, Partner_Chat.class));
                                        }

                                        else{
                                            Toast.makeText(UserStatus.this,"Waiting for a partner",Toast.LENGTH_SHORT).show();
                                        }
                                       // Log.w("Inside this","yay");
                                       //  startActivity(new Intent(UserStatus.this, Partner_Chat.class));

                                    }

                                    public void onCancelled(DatabaseError databaseError) { }
                                });
    }

}