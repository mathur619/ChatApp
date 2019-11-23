package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

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
    private RecyclerView privatemessages;

    private List<Messages>messagesList=new ArrayList<>();
    private MessageAdapter messageAdapter;

    private byte encryptionKey[]= {9,115,51,86,105,4,-31,-23,-68,88,17,20,3,-105,119,-53};
    private Cipher cipher,decipher;
    private SecretKeySpec secretKeySpec;



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

        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        secretKeySpec= new SecretKeySpec(encryptionKey, "AES");


        InitializeControllers();

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> lst=new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Roorref.child("Messages").child(messagesenderid).child(messagereceiverid).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String actual_message= dataSnapshot.child("message").getValue().toString();
                        actual_message= actual_message.substring(1,actual_message.length()-1);
                        String decryptedMessage=AESDecryptionMethod(actual_message);
                        Log.d("ChatActivity",decryptedMessage);

                        Messages messages=new Messages(dataSnapshot.child("from").getValue().toString(),decryptedMessage,dataSnapshot.child("type").getValue().toString());

                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        privatemessages.smoothScrollToPosition(privatemessages.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }
        };

        lst.execute();

    }

    @Override
    protected void onPause() {
        super.onPause();
        messagesList.clear();
    }

    private String AESEncryptionMethod(String string){
        byte[] stringByte= string.getBytes();
        byte[] encryptedByte= new byte[stringByte.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            encryptedByte = cipher.doFinal(stringByte);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        String returnString= null;
        try {
            returnString= new String(encryptedByte,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnString;
    }

    private String AESDecryptionMethod(String string){
        byte[] EncryptedByte= string.getBytes(StandardCharsets.UTF_8);
        String decryptedString= null;

        byte[] decryption = new byte[EncryptedByte.length];

        try {
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try{
            decryption= decipher.doFinal(EncryptedByte);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        decryptedString= new String(decryption).substring(string.length());

        return string;
    }


    private void sendmessage() {
        String Messagetext = AESEncryptionMethod(messagetext.getText().toString());

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

        messageAdapter=new MessageAdapter(messagesList);

        privatemessages=(RecyclerView)findViewById(R.id.private_message_list);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        privatemessages.setLayoutManager(linearLayoutManager);
        privatemessages.setAdapter(messageAdapter);

    }
}
