package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class GroupFragment extends Fragment implements GroupViewAdapter.ListItemClickListener {

    private View GroupFragmentView;
    private RecyclerView recyclerView;
    private List<String>grouptitle;
    private DatabaseReference GroupRef;
    private GroupViewAdapter groupViewAdapter;
    public GroupFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        GroupFragmentView= inflater.inflate(R.layout.fragment_group, container, false);

        GroupRef= FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_group));

        Initializefields();
        RetreiveandDisplayGroups();

        return GroupFragmentView;
    }

    private void RetreiveandDisplayGroups() {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String>set=new HashSet<>();
                Iterator iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext())
                {
                  set.add(((DataSnapshot)iterator.next()).getKey());
                }
                grouptitle.clear();
                grouptitle.addAll(set);
                groupViewAdapter.setdata(grouptitle);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Initializefields() {
        recyclerView=(RecyclerView)GroupFragmentView.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        groupViewAdapter=new GroupViewAdapter(this);
        recyclerView.setAdapter(groupViewAdapter);

        grouptitle=new ArrayList<>();
    }

    @Override
    public void onListItemClicked(int Position) {
        Intent intent=new Intent(getContext(),GroupChatActivity.class);
        String Groupname=groupViewAdapter.getitem(Position);
        intent.putExtra("groupname",Groupname);
        startActivity(intent);
    }
}
