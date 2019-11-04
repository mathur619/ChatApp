package com.example.chatapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages>messages;
    private FirebaseAuth mAuth;
    private DatabaseReference userref;

    public MessageAdapter(List<Messages> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout,parent,false);
        mAuth=FirebaseAuth.getInstance();
        MessageViewHolder holder=new MessageViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

     String Messagesenderid=mAuth.getCurrentUser().getUid();
     Messages message=messages.get(position);

     String fromuserid=message.getFrom();
     String messagetype=message.getType();
     String t_message=message.getMessage();

     userref=FirebaseDatabase.getInstance().getReference().child("User").child(fromuserid);
     userref.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             if(dataSnapshot.hasChild("image"))
             {
                 Picasso.get().load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.profile_image).fit().into(holder.receiverprofileimage);
             }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
     });

     if(messagetype.equals("text"))
     {
         holder.receiverprofileimage.setVisibility(View.INVISIBLE);
         holder.ReceiverMessageText.setVisibility(View.INVISIBLE);
         holder.senderMessageText.setVisibility(View.INVISIBLE);

         if(fromuserid.equals(Messagesenderid))
         {
             holder.senderMessageText.setVisibility(View.VISIBLE);
             holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
             holder.senderMessageText.setText(t_message);
             holder.senderMessageText.setTextColor(Color.BLACK);
         }
         else
         {

             holder.receiverprofileimage.setVisibility(View.VISIBLE);
             holder.ReceiverMessageText.setVisibility(View.VISIBLE);
             holder.ReceiverMessageText.setText(t_message);

             holder.ReceiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
             holder.ReceiverMessageText.setTextColor(Color.BLACK);
         }
     }

    }

    @Override
    public int getItemCount() {
        if(messages==null)
            return 0;

        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText,ReceiverMessageText;
        public CircleImageView receiverprofileimage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText=(TextView)itemView.findViewById(R.id.sender_message_text);
            ReceiverMessageText=(TextView)itemView.findViewById(R.id.receiver_message_text);
            receiverprofileimage=(CircleImageView)itemView.findViewById(R.id.message_profile_image);
        }
    }
}
