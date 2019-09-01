package com.example.chatapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class GroupViewAdapter extends RecyclerView.Adapter<GroupViewAdapter.GroupViewHolder> {
    List<String>GroupTitle;
    final private ListItemClickListener listItemClickListener;

    public GroupViewAdapter(ListItemClickListener listItemClickListener) {
        this.listItemClickListener=listItemClickListener;
    }

    public interface ListItemClickListener
    {
        void onListItemClicked(int Position);
    }


    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context=viewGroup.getContext();
        int layout_id=R.layout.simple_list_item;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(layout_id,viewGroup,false);
        GroupViewHolder groupViewHolder=new GroupViewHolder(view);
        return groupViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder groupViewHolder, int i) {
         String Title=GroupTitle.get(i);
         groupViewHolder.groupname.setText(Title);

    }

    @Override
    public int getItemCount() {
        if (GroupTitle==null)
        {
            return 0;
        }
        return GroupTitle.size();
    }

    public void setdata(List<String>mgrouptitle)
    {
        GroupTitle=mgrouptitle;
        notifyDataSetChanged();
    }
    public String getitem(int Position)
    {
        return GroupTitle.get(Position);
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView groupname;
        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupname=(TextView)(itemView).findViewById(R.id.group_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            listItemClickListener.onListItemClicked(position);
        }
    }
}
