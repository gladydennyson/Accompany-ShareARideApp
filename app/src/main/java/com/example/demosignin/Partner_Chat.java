package com.example.demosignin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Partner_Chat extends AppCompatActivity {

    private int SIGN_IN_REQUEST_CODE=10;
    private FirebaseListAdapter<Partner_Chat_Message> adapter;
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partner_chat);

        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("groups")
                        .child("ghat_rides")
                        .child("partnerridechats")
                        .push()
                        .setValue(new Partner_Chat_Message(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );


               /* ChatMessage chatmsg = new
                        ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getDisplayName());
                mFirebaseDatabaseReference.child("groups")
                        .push().setValue(chatmsg); */
                // Clear the input
                input.setText("");
            }
        });

       displayChatMessages();
    }

    private void displayChatMessages() {
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child("ghat_rides").child("partnerridechats");
        Log.w("This is ref",mFirebaseDatabaseReference.toString());
        adapter = new FirebaseListAdapter<Partner_Chat_Message>(this, Partner_Chat_Message.class,
                R.layout.messages, mFirebaseDatabaseReference) {
            @Override
            protected void populateView(View v, Partner_Chat_Message model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);
        Log.w("End of disp","hrrfhr");
    }

}
