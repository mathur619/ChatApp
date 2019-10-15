package com.example.chatapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class RequestFragment extends Fragment {
    private View convert;
    private RecyclerView Requests_List;
    private DatabaseReference ChatRequesstRef;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, Contactsref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        convert = inflater.inflate(R.layout.fragment_request, container, false);

        Requests_List = (RecyclerView) convert.findViewById(R.id.request_list);
        Requests_List.setLayoutManager(new LinearLayoutManager(getContext()));

        ChatRequesstRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("User");
        Contactsref = FirebaseDatabase.getInstance().getReference().child("Contacts");

        return convert;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ChatRequesstRef.child(mAuth.getCurrentUser().getUid()), Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, RequestViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_view, parent, false);
                RequestViewHolder holder = new RequestViewHolder(view);
                return holder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder requestViewHolder, int i, @NonNull Contacts contacts) {
                requestViewHolder.Accept.setVisibility(View.VISIBLE);
                requestViewHolder.Cancel.setVisibility(View.VISIBLE);

                final String list_User_Id = getRef(i).getKey();

                DatabaseReference getTypeRef = getRef(i).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String type = dataSnapshot.getValue().toString();

                            if (type.equals("received")) {
                                UserRef.child(list_User_Id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child("image").exists()) {
                                            requestViewHolder.username.setText(dataSnapshot.child("name").getValue().toString());
                                            requestViewHolder.userstatus.setText("wants to connect with you");
                                            Picasso.get().load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.profile_image).fit().into(requestViewHolder.userprofilephoto);
                                        } else {
                                            requestViewHolder.username.setText(dataSnapshot.child("name").getValue().toString());
                                            requestViewHolder.userstatus.setText("wants to connect with you");
                                        }


                                        requestViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                                "Accept",
                                                                "Cancel"
                                                        };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(dataSnapshot.child("name").getValue().toString() + " Chat Request");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (which == 0) {
                                                            Contactsref.child(mAuth.getCurrentUser().getUid()).child(list_User_Id).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Contactsref.child(list_User_Id).child(mAuth.getCurrentUser().getUid()).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    ChatRequesstRef.child(mAuth.getCurrentUser().getUid()).child(list_User_Id).removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {
                                                                                                        ChatRequesstRef.child(list_User_Id).child(mAuth.getCurrentUser().getUid())
                                                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if (task.isSuccessful()) {
                                                                                                                    Toast.makeText(getContext(), "New Contact Saved", Toast.LENGTH_SHORT).show();

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
                                                        if (which == 1) {
                                                            ChatRequesstRef.child(mAuth.getCurrentUser().getUid()).child(list_User_Id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                ChatRequesstRef.child(list_User_Id).child(mAuth.getCurrentUser().getUid())
                                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Toast.makeText(getContext(), "New Contact Saved", Toast.LENGTH_SHORT).show();

                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        Requests_List.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView username, userstatus;
        CircleImageView userprofilephoto;
        Button Accept, Cancel;
        View ItemView;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ItemView = itemView;

            username = (itemView).findViewById(R.id.user_name);
            userstatus = (itemView).findViewById(R.id.user_status);
            userprofilephoto = (itemView).findViewById(R.id.users_profile_image);
            Accept = (itemView).findViewById(R.id.request_accept_btn);
            Cancel = (itemView).findViewById(R.id.request_cancel_btn);
        }
    }
}