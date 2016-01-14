package com.example.joe.musicproject;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Joe on 12/25/2015.
 */
public class PlaylistsFragment extends Fragment {

    //Fragment containing playlists

    public PlaylistsFragment() {
    }

    public static PlaylistsFragment newInstance(String text) {
        PlaylistsFragment playlistsFragment = new PlaylistsFragment();
        Bundle bundle = new Bundle();

        bundle.putString("MSG", text);
        playlistsFragment.setArguments(bundle);

        return playlistsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.playlists_fragment, container, false);

        TextView textView = (TextView) root.findViewById(R.id.playlistsText);
        textView.setText(getArguments().getString("MSG"));

        return root;
    }
}
