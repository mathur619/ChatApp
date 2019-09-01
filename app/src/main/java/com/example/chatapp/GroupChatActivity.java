package com.example.chatapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SimpleTimeZone;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton imageButton;
    private EditText editText;
    private TextView displaymessage;
    private ScrollView scrollView;
    private String Groupname;
    private String CurrentUserName,CurrenUserId,CurrentDate,CurrentTime;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference UserRef,GroupNameRef,GroupMessageKeyRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

         Groupname=getIntent().getExtras().get("groupname").toString();
        Toast.makeText(GroupChatActivity.this,Groupname,Toast.LENGTH_SHORT).show();

        initializeviews();

        firebaseAuth=FirebaseAuth.getInstance();
        CurrenUserId=firebaseAuth.getCurrentUser().getUid();

        UserRef= FirebaseDatabase.getInstance().getReference().child("User").child(CurrenUserId);
        GroupNameRef=FirebaseDatabase.getInstance().getReference().child("Group").child(Groupname);

        GetUserInfo();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveMessageInfoDatabase();
                editText.setText("");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void initializeviews() {
        toolbar=(Toolbar)findViewById(R.id.group_chat_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Groupname);
        imageButton=(ImageButton)findViewById(R.id.message_button);
        editText=(EditText)findViewById(R.id.type_message);
        displaymessage=(TextView)findViewById(R.id.group_chat_display);
        scrollView=(ScrollView)findViewById(R.id.myscrollview);
    }

    private void SaveMessageInfoDatabase() {
        String MessageKey=GroupNameRef.push().getKey();

        String Message=editText.getText().toString();
        if(TextUtils.isEmpty(Message))
        {
            Toast.makeText(GroupChatActivity.this,"Please Write Message First....",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat currentDateTimeFormat=new SimpleDateFormat("MMM dd, yyyy");
            CurrentDate=currentDateTimeFormat.format(calendar.getTime());

            Calendar calendartime=Calendar.getInstance();
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
            CurrentTime=currentTimeFormat.format(calendar.getTime());

            HashMap<String,Object>groupMessageKey=new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef=GroupNameRef.child(MessageKey);

            HashMap<String,Object> messageinfomap=new HashMap<>();
            messageinfomap.put("name",CurrentUserName);
            messageinfomap.put("message",Message);
            messageinfomap.put("date",CurrentDate);
            messageinfomap.put("time",CurrentTime);

            GroupMessageKeyRef.updateChildren(messageinfomap);
        }
    }

    private void GetUserInfo() {
      UserRef.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if(dataSnapshot.exists())
              {
                  CurrentUserName=dataSnapshot.child("name").getValue().toString();
              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
    }

    @Override
    protected void onStart() {
        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()) {
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                 if(dataSnapshot.exists()) {
                     DisplayMessages(dataSnapshot);
                 }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator=dataSnapshot.getChildren().iterator();

        while (iterator.hasNext())
        {
            String chatdate=(String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage=(String)((DataSnapshot)iterator.next()).getValue();
            String chatname=(String)((DataSnapshot)iterator.next()).getValue();
            String chattime=(String)((DataSnapshot)iterator.next()).getValue();

            displaymessage.append(chatname + " :\n" + chatMessage + "\n" +chatdate + "\n" + chattime + "\n\n\n");

            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}
