package com.example.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private EditText username,userstatus;
    private Button SavePreference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference Rootref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Initializing Firebase

        firebaseAuth=FirebaseAuth.getInstance();
        Rootref=FirebaseDatabase.getInstance().getReference();

        //Initializing views
        Initializeviews();

        //setonclicklistener

        onclicklistener();

        SetOldData();

    }

    private void SetOldData() {
        String CurrentUserId=firebaseAuth.getCurrentUser().getUid();
         Rootref.child("User").child(CurrentUserId).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.exists() && dataSnapshot.child("name").exists() && dataSnapshot.child("image").exists())
                 {
                    username.setText(dataSnapshot.child("name").getValue().toString());
                    userstatus.setText(dataSnapshot.child("status").getValue().toString());
                 }
                 else if(dataSnapshot.exists() && dataSnapshot.child("name").exists())
                 {
                     username.setText(dataSnapshot.child("name").getValue().toString());
                     userstatus.setText(dataSnapshot.child("status").getValue().toString());
                 }
                 else
                 {
                   Toast.makeText(SettingsActivity.this,getString(R.string.settings_update_request),Toast.LENGTH_LONG).show();
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
    }

    private void onclicklistener() {
        SavePreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name=username.getText().toString();
                String Status=userstatus.getText().toString();
                String CurrentUserId=firebaseAuth.getCurrentUser().getUid();

                if(TextUtils.isEmpty(Name) && TextUtils.isEmpty(Status))
                {
                    Toast.makeText(SettingsActivity.this,getString(R.string.warning),Toast.LENGTH_SHORT).show();
                }
                else
                {
                    HashMap<String,String>Data=new HashMap<>();
                    Data.put("name",Name);
                    Data.put("status",Status);
                    Data.put("uid",CurrentUserId);

                    Rootref.child("User").child(CurrentUserId).setValue(Data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String Error=task.getException().getMessage();
                                Toast.makeText(SettingsActivity.this,Error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }

    private void SendUserToMainActivity()
    {
        Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void Initializeviews() {
        username=(EditText)findViewById(R.id.set_user_name);
        userstatus=(EditText)findViewById(R.id.set_user_status);
        SavePreference=(Button)findViewById(R.id.save_preference);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.right_slide_in,R.anim.left_slide_in);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
