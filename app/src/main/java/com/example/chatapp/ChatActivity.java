package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String messagereceiverid,messagereceivername,visit_user_image,messagesenderid;
    private TextView username,lastseen;
    private CircleImageView profile_image;
    private Toolbar ChatToolbar;
    private EditText messagetext;
    private ImageButton sendbutton;
    private FirebaseAuth mAuth;
    private DatabaseReference Roorref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagereceiverid=getIntent().getStringExtra("visit_user_id");
        messagereceivername=getIntent().getStringExtra("visit_user_name");
        visit_user_image=getIntent().getStringExtra("visit_user_image");
        mAuth=FirebaseAuth.getInstance();
        Roorref= FirebaseDatabase.getInstance().getReference();
        messagesenderid=mAuth.getCurrentUser().getUid();

        InitializeControllers();

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessage();
            }
        });
    }

    private void sendmessage() {
        String Messagetext=messagetext.getText().toString();

        if(TextUtils.isEmpty(Messagetext))
        {
            Toast.makeText(ChatActivity.this,"Type Your Message First",Toast.LENGTH_SHORT).show();
        }
        else
        {
            String Messagesendref="Messages/" + messagesenderid + "/" + messagereceiverid;
            String MessageReceiveref="Messages/" + messagereceiverid + "/" + messagesenderid;

            DatabaseReference userMessagekeyref=Roorref.child("Messages").child(messagesenderid)
                    .child(messagereceiverid).push();

            String MessagePushid=userMessagekeyref.getKey();

            Map messagetextbody=new HashMap();
            messagetextbody.put("message",Messagetext);
            messagetextbody.put("type","text");
            messagetextbody.put("from",messagesenderid);


            Map messagebodydetails=new HashMap();
            messagebodydetails.put(Messagesendref+"/"+MessagePushid,messagetextbody);
            messagebodydetails.put(MessageReceiveref+"/"+MessagePushid,messagetextbody);

            Roorref.updateChildren(messagebodydetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                       Toast.makeText(ChatActivity.this,"Message Sent Successfully",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        String error=task.getException().getMessage();
                        Toast.makeText(ChatActivity.this,error,Toast.LENGTH_LONG).show();
                    }
                    messagetext.setText("");
                }
            });
        }
    }

    private void InitializeControllers() {

        ChatToolbar=(Toolbar)findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarview=layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarview);

        username=(TextView)actionBarview.findViewById(R.id.custom_profile_name);
        lastseen=(TextView)actionBarview.findViewById(R.id.custom_user_last_seen);
        profile_image=(CircleImageView) actionBarview.findViewById(R.id.custom_profile_image);
        messagetext=(EditText)findViewById(R.id.type_message_edit);
        sendbutton=(ImageButton)findViewById(R.id.snd_message_button);

        username.setText(messagereceivername);
        lastseen.setText("Last Seen");
        Picasso.get().load(visit_user_image).fit().placeholder(R.drawable.profile_image).into(profile_image);

    }
}
