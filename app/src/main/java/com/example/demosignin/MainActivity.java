package com.example.demosignin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    String displayname,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        signInButton = (SignInButton)findViewById(R.id.googleBtn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });

        Firebase.setAndroidContext(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w("Intent data : ",data.getExtras().toString());
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("MYAPP", "exception", e);
                Log.w("error", "google signin failed");
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {


        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information



                            FirebaseUser user = auth.getCurrentUser();
                            displayname = user.getDisplayName();

                            Toast.makeText(MainActivity.this, displayname.toString(), Toast.LENGTH_SHORT).show();

                            pass="thispwd";
                            final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                            pd.setMessage("Loading...");
                            pd.show();

                            String url = "https://costoptimized.firebaseio.com/users.json";

                            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                                @Override
                                public void onResponse(String s) {
                                    Firebase reference = new Firebase("https://costoptimized.firebaseio.com/users");

                                    if(s.equals("null")) {
                                        reference.child(displayname).child("password").setValue(pass);
                                        Toast.makeText(MainActivity.this, "registration successful", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(MainActivity.this, MainPage.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        try {
                                            JSONObject obj = new JSONObject(s);

                                            if (!obj.has(displayname)) {
                                                reference.child(displayname).child("password").setValue(pass);
                                                Toast.makeText(MainActivity.this, "registration successful", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(MainActivity.this, MainPage.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(MainActivity.this, "username already exists", Toast.LENGTH_LONG).show();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    pd.dismiss();
                                }

                            },new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    System.out.println("" + volleyError );
                                    pd.dismiss();
                                }
                            });

                            RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
                            rQueue.add(request);

                        }

                        else {
                            // If sign in fails, display a message to the user.
                            // Log.w("error", "not working");
                        }

                        // ...
                    }
                });
    }


}
