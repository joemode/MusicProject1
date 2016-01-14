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
public class AlbumsFragment extends Fragment {

    //Fragment containing album list

    private IOClass.AlbumListAdapter adapter;


    public AlbumsFragment() {
    }

    public static AlbumsFragment newInstance(String text) {
        //return new AlbumsFragment();

        AlbumsFragment albumsFragment = new AlbumsFragment();
        Bundle bundle = new Bundle();

        bundle.putString("MSG", text);
        albumsFragment.setArguments(bundle);

        return albumsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.albums_fragment, container, false);
        String message = "";

        if(getArguments().getString("MSG") != null) {
            message = getArguments().getString("MSG");
        }

        TextView textView = (TextView) root.findViewById(R.id.albumsText);
        textView.setText(message);

        //TODO: Setup view for albums, rest of this func
        ProgressBar spinner = (ProgressBar) root.findViewById(R.id.progressSong);
        ListView tracksList = (ListView) root.findViewById(R.id.albumsList);

        adapter = new IOClass.AlbumListAdapter(tracksList, message, spinner);
        tracksList.setAdapter(adapter);

        return root;
    }
}
