package com.example.joe.musicproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Joe on 12/25/2015.
 */
public class TracksFragment extends Fragment {

    //Fragment containing tracks

    private IOClass.SongListAdapter adapter;

    public TracksFragment() {
    }

    public static TracksFragment newInstance(String text) {
        TracksFragment tracksFragment = new TracksFragment();
        Bundle bundle = new Bundle();

        bundle.putString("MSG", text);
        tracksFragment.setArguments(bundle);

        return tracksFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.tracks_fragment, container, false);
        String message = "";

        if(getArguments().getString("MSG") != null) {
            message = getArguments().getString("MSG");
        }

        TextView textView = (TextView) root.findViewById(R.id.tracksText);
        textView.setText(message);

        //if(getArguments().getString("MSG").equals("Library Tracks"))

        ProgressBar spinner = (ProgressBar) root.findViewById(R.id.progressSong);
        ListView tracksList = (ListView) root.findViewById(R.id.tracksList);

        adapter = new IOClass.SongListAdapter(tracksList, message, spinner);
        tracksList.setAdapter(adapter);

        return root;
    }

}
