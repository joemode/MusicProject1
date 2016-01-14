package com.example.joe.musicproject;


import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Joe on 12/24/2015.
 */
public class FragmentAll extends Fragment {

    //Fragment containing conglomerated music

    private static String[] mTitles = {"Tracks", "Artists", "Albums", "Playlists"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_all, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            //Activity activity = (Activity) .getContext();
            //Fragment fragment = Fragment.instantiate(FragmentAll.getActivity(), mFragments[position]);

            switch(position) {
                case 0: return TracksFragment.newInstance("All Tracks");
                case 1: return ArtistsFragment.newInstance("All Artists");
                case 2: return AlbumsFragment.newInstance("All Albums");
                case 3: return PlaylistsFragment.newInstance("All Playlists");
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
