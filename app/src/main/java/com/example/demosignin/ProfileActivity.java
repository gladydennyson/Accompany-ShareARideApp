package com.example.demosignin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    TextView username;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilepage);

        username = (TextView) findViewById(R.id.userdisplayname);

        mAuth= FirebaseAuth.getInstance();
        FirebaseUser userf = mAuth.getCurrentUser();
        String displayname = userf.getDisplayName();
        // Toast.makeText(ProfileActivity.this, displayname.toString(), Toast.LENGTH_SHORT).show();
        username.setText(displayname+" is logged in");

    }
    public void gotochatroom(View v){

        startActivity(new Intent(ProfileActivity.this, Login.class));
    }
    public void gotomap(View v){
        startActivity(new Intent(ProfileActivity.this, RoutesForUser.class));
    }
}