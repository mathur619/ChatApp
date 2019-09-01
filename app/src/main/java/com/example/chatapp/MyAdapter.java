package com.example.chatapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyAdapter extends FragmentPagerAdapter {

    private Context context;
    private int Totaltabs;

    public MyAdapter(Context context, FragmentManager fragmentManager,int Totaltabs)
    {
        super(fragmentManager);
        this.context=context;
        this.Totaltabs=Totaltabs;
    }
    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new ChatFragment();
            case 1:
                return new GroupFragment();
            case 2:
                return new ContactsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return Totaltabs;
    }
}
