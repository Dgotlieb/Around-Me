package com.example.daniel.aroundme.activities_and_fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by daniel.gotlieb on 08/03/2016.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment frag=null;
        switch (position){
            case 0:
                frag=new PointOfIntrests();
                break;
            case 1:
                frag=new FragmentMaps();
                break;
            case 2:
                frag=new Favorites();
                break;

        }
        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title=" ";
        switch (position){
            case 0:
                title="Search";
                break;
            case 1:
                title="Map";
                break;

            case 2:
                title="Favourites";
                break;

        }

        return title;
    }
}