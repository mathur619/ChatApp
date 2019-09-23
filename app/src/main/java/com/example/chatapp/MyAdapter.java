package com.example.chatapp;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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

            case 3:
                return new RequestFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return Totaltabs;
    }
}
