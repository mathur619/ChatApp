package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiveruserid;
    private CircleImageView User_profile_image;
    private TextView Username,UserStatus;
    private Button SendRequest;
    private DatabaseReference Ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Ref= FirebaseDatabase.getInstance().getReference();

        receiveruserid=getIntent().getExtras().get("visit_user_id").toString();

        User_profile_image=(CircleImageView)findViewById(R.id.visit_profile_image);
        Username=(TextView)findViewById(R.id.visit_user_name);
        UserStatus=(TextView)findViewById(R.id.visit_profile_status);
        SendRequest=(Button)findViewById(R.id.send_message_button);

        RetreiveUserInfo();
    }

    private void RetreiveUserInfo() {
        Ref.child("User").child(receiveruserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.hasChild("image"))
                {
                    Username.setText(dataSnapshot.child("name").getValue().toString());
                    UserStatus.setText(dataSnapshot.child("status").getValue().toString());
                    Picasso.get().load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.profile_image).into(User_profile_image);
                }
                else {
                    Username.setText(dataSnapshot.child("name").getValue().toString());
                    UserStatus.setText(dataSnapshot.child("status").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
