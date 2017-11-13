package com.example.home.superprayer.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.home.superprayer.Fragment.TimesFragment;

/**
 * Created by Home on 11/5/2017.
 */

public class MyViewPagerAdapter extends FragmentPagerAdapter{

    private final int FRAG_TAB = 3;

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                Log.d("TAG", "position zero");
                return new TimesFragment();
            case 1:
                Log.d("TAG", "position 2");

                return new TimesFragment();
            case 2:
                Log.d("TAG", "position 2");

                return new TimesFragment();

                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return FRAG_TAB;
    }
}
