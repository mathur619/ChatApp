package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {
    private RecyclerView privatechatview;
    private View privatechat;
    private FirebaseAuth mAuth;
    private DatabaseReference Contactsref;
    private String currentuserid;
    private DatabaseReference UsersRef;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privatechat=inflater.inflate(R.layout.fragment_chat, container, false);

        privatechatview=(RecyclerView)privatechat.findViewById(R.id.private_chat_recycler_view);
        privatechatview.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth=FirebaseAuth.getInstance();

        Contactsref=FirebaseDatabase.getInstance().getReference().child("Contacts");
        UsersRef=FirebaseDatabase.getInstance().getReference().child("User");

        currentuserid=mAuth.getCurrentUser().getUid();


        return privatechat;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts>options=
                new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(Contactsref.child(currentuserid),Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts,ChatViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder chatViewHolder, int i, @NonNull Contacts contacts) {
               final String userid=getRef(i).getKey();
                final String[] retimage = {"defauttImage"};
               UsersRef.child(userid).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if(dataSnapshot.hasChild("image"))
                       {
                           retimage[0] =dataSnapshot.child("image").getValue().toString();
                           Picasso.get().load(retimage[0]).placeholder(R.drawable.profile_image).fit().into(chatViewHolder.profile_photo);

                       }

                       final String name=dataSnapshot.child("name").getValue().toString();
                       final String status=dataSnapshot.child("status").getValue().toString();

                       chatViewHolder.username.setText(name);
                       chatViewHolder.status.setText("Last Seen: " + "\n" + "Date " +" Time");


                       chatViewHolder.itemview.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               Intent intent=new Intent(getContext(),ChatActivity.class);
                               intent.putExtra("visit_user_id",userid);
                               intent.putExtra("visit_user_name",name);
                               intent.putExtra("visit_user_image",retimage[0]);
                               startActivity(intent);

                           }
                       });
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });

            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_view,parent,false);
                ChatViewHolder holder=new ChatViewHolder(view);
                return holder;
            }
        };

        privatechatview.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private View itemview;
        private CircleImageView profile_photo;
        private TextView status;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemview=itemView;

            username = (itemView).findViewById(R.id.user_name);
            status = (itemView).findViewById(R.id.user_status);
            profile_photo = (itemView).findViewById(R.id.users_profile_image);

        }
    }
}
