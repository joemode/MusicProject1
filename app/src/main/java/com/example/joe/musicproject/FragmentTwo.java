package com.example.joe.musicproject;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Joe on 12/20/2015.
 */
public class FragmentTwo extends FragmentAll {

    //Fragment containing spotify music

    private static String[] mTitles = {"Tracks", "Artists", "Albums", "Playlists"};

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        PagerTabStrip mPagerStrip = (PagerTabStrip) view.findViewById(R.id.pagerStrip);
        mPagerStrip.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorSpotify));
        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position) {
                case 0: return TracksFragment.newInstance("Spotify Tracks");
                case 1: return ArtistsFragment.newInstance("Spotify Artists");
                case 2: return AlbumsFragment.newInstance("Spotify Albums");
                case 3: return PlaylistsFragment.newInstance("Spotify Playlists");
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }
}
