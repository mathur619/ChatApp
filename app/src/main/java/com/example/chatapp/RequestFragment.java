package com.example.chatapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RequestFragment extends Fragment {
    private View convert;
    private RecyclerView Requests_List;

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
        convert= inflater.inflate(R.layout.fragment_request, container, false);

        Requests_List=(RecyclerView)convert.findViewById(R.id.request_list);
        Requests_List.setLayoutManager(new LinearLayoutManager(getContext()));

        return convert;
    }

}
