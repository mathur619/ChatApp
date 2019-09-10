package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference usersref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        recyclerView=(RecyclerView)findViewById(R.id.find_friends_recyclerlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersref= FirebaseDatabase.getInstance().getReference().child("User");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(usersref,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder>adapter=new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder findFriendViewHolder, final int i, @NonNull Contacts contacts) {
                findFriendViewHolder.username.setText(contacts.getName());
                findFriendViewHolder.userstatus.setText(contacts.getStatus());
                Picasso.get().load(contacts.getImage()).placeholder(R.drawable.profile_image).fit().into(findFriendViewHolder.userprofilephoto);

                findFriendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id=getRef(i).getKey();
                        Intent intent=new Intent(FriendsActivity.this,ProfileActivity.class);
                        intent.putExtra("visit_user_id",visit_user_id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_view,parent,false);
                FindFriendViewHolder viewHolder=new FindFriendViewHolder(view);
                return viewHolder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{

        TextView username,userstatus;
        CircleImageView userprofilephoto;
        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            username=(itemView).findViewById(R.id.user_name);
            userstatus=(itemView).findViewById(R.id.user_status);
            userprofilephoto=(itemView).findViewById(R.id.users_profile_image);
        }
    }
}
