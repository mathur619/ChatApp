package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiveruserid,Current_State,sendUserID;
    private CircleImageView User_profile_image;
    private TextView Username,UserStatus;
    private Button SendRequest,Cancel;
    private DatabaseReference Ref,ChatRequest,ContactsRef;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth=FirebaseAuth.getInstance();
        sendUserID=mAuth.getCurrentUser().getUid();

        Ref= FirebaseDatabase.getInstance().getReference();
        ChatRequest=FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef=FirebaseDatabase.getInstance().getReference().child("Contacts");

        receiveruserid=getIntent().getExtras().get("visit_user_id").toString();

        User_profile_image=(CircleImageView)findViewById(R.id.visit_profile_image);
        Username=(TextView)findViewById(R.id.visit_user_name);
        UserStatus=(TextView)findViewById(R.id.visit_profile_status);
        SendRequest=(Button)findViewById(R.id.send_message_button);
        Cancel=(Button)findViewById(R.id.decline_message_button);

        Current_State="new";

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

                    ManageChatRequest();
                }
                else {
                    Username.setText(dataSnapshot.child("name").getValue().toString());
                    UserStatus.setText(dataSnapshot.child("status").getValue().toString());
                    ManageChatRequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ManageChatRequest() {

        ChatRequest.child(sendUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(receiveruserid))
                {
                    String RequestType=dataSnapshot.child(receiveruserid).child("request_type").getValue().toString();

                    if(RequestType.equals("sent"))
                    {
                        Current_State="request_sent";
                        SendRequest.setText("Cancel Chat Request");
                    }
                    else if(RequestType.equals("received"))
                    {
                        Current_State="request_received";
                        SendRequest.setText("Accept Request");
                        Cancel.setVisibility(View.VISIBLE);
                        Cancel.setEnabled(true);
                        Cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CanceRequest();
                            }
                        });
                    }
                }
                else
                {
                    ContactsRef.child(sendUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(receiveruserid))
                            {
                                Current_State="friends";
                                SendRequest.setText("Unfriend");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(!sendUserID.equals(receiveruserid))
        {
           SendRequest.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   SendRequest.setEnabled(false);

                   if(Current_State.equals("new"))
                   {
                       SendChatRequest();
                   }
                   else if(Current_State.equals("request_sent"))
                   {
                     CanceRequest();
                   }
                   else if(Current_State.equals("request_received"))
                   {
                       AcceptChatRequest();
                   }
                   else if(Current_State.equals("friends"))
                   {
                       Unfriend();
                   }
               }
           });
        }
        else
        {
            SendRequest.setVisibility(View.INVISIBLE);
        }

    }

    private void Unfriend() {
        ContactsRef.child(sendUserID).child(receiveruserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    ContactsRef.child(receiveruserid).child(sendUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                SendRequest.setEnabled(true);
                                SendRequest.setText("Send Request");
                                Current_State="new";
                                Cancel.setVisibility(View.INVISIBLE);
                                Cancel.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void AcceptChatRequest() {
        ContactsRef.child(sendUserID).child(receiveruserid).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    ContactsRef.child(receiveruserid).child(sendUserID).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful())
                           {
                               ChatRequest.child(sendUserID).child(receiveruserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful())
                                       {
                                           ChatRequest.child(receiveruserid).child(sendUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if(task.isSuccessful())
                                                   {
                                                       SendRequest.setEnabled(true);
                                                       Current_State="friends";
                                                       SendRequest.setText("Unfriend");

                                                       Cancel.setVisibility(View.INVISIBLE);
                                                       Cancel.setEnabled(false);
                                                   }
                                               }
                                           });
                                       }
                                   }
                               });
                           }
                        }
                    });
                }
            }
        });
    }

    private void CanceRequest() {
        ChatRequest.child(sendUserID).child(receiveruserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    ChatRequest.child(receiveruserid).child(sendUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                SendRequest.setEnabled(true);
                                SendRequest.setText("Send Request");
                                Current_State="new";
                                Cancel.setVisibility(View.INVISIBLE);
                                Cancel.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void SendChatRequest() {
        ChatRequest.child(sendUserID).child(receiveruserid)
                .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                 if(task.isSuccessful())
                 {
                     ChatRequest.child(receiveruserid).child(sendUserID)
                             .child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful())
                             {
                                 SendRequest.setEnabled(true);
                                 Current_State="request_sent";
                                 SendRequest.setText("Cancel Chat Request");
                             }
                         }
                     });
                 }
            }
        });
    }
}
