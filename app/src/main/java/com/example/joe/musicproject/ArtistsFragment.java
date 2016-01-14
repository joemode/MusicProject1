package com.example.joe.musicproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Joe on 12/25/2015.
 */
public class ArtistsFragment extends Fragment {

    //Fragment containing list of artists

    private IOClass.ArtistListAdapter adapter;

    public ArtistsFragment() {
    }

    public static ArtistsFragment newInstance(String text) {
        //return new ArtistsFragment();

        ArtistsFragment artistsFragment = new ArtistsFragment();
        Bundle bundle = new Bundle();

        bundle.putString("MSG", text);
        artistsFragment.setArguments(bundle);

        return artistsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.artists_fragment, container, false);
        String message = "";

        if(getArguments().getString("MSG") != null) {
            message = getArguments().getString("MSG");
        }

        TextView textView = (TextView) root.findViewById(R.id.artistsText);
        textView.setText(message);

        ProgressBar spinner = (ProgressBar) root.findViewById(R.id.progressArtist);
        ListView artistsList = (ListView) root.findViewById(R.id.artistsList);

        adapter = new IOClass.ArtistListAdapter(artistsList, message, spinner);
        artistsList.setAdapter(adapter);

        return root;
    }
}