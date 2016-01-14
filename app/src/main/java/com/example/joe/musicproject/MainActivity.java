package com.example.joe.musicproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.app.FragmentTransaction;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class MainActivity extends AppCompatActivity implements MediaPlayerControl {

    //Main Activity

    static final String[] data = {"All Music", "My Library", "Spotify", "Soundcloud"};
    //final ArrayList<String> data = new ArrayList<>()
    static final int[] ICONS = {R.drawable.ic_action, R.drawable.ic_spotify, R.drawable.ic_soundcloud};
    static final int[] COLORS = {0xcc00cc, 0x66ff33, 0xff9933};

    final private String[] mFragments = {
            "com.example.joe.musicproject.FragmentAll",
            "com.example.joe.musicproject.FragmentOne",
            "com.example.joe.musicproject.FragmentTwo",
            "com.example.joe.musicproject.FragmentThree"
    };

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mData;
    private int mCurrentFragment;

    private MusicController mController;

    //public mMasterSongs<IOClass.Song>
    public static HashMap<Long, Bitmap> mArtworkMap = new HashMap<>();

    public static HashMap<Long, Bitmap> getArtworkMap() { return mArtworkMap; }

    public static LinkedHashMap<Long, IOClass.Artist> mLibArtists = new LinkedHashMap<>();
    public static LinkedHashMap<Long, IOClass.Album> mLibAlbums = new LinkedHashMap<>();
    public static LinkedHashMap<Long, IOClass.Song> mLibSongs = new LinkedHashMap<>();

    /** Media Player Control Section **/
    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    private void setController() {
        mController = new MusicController(this);

        mController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playPrev();
            }
        });

        mController.setMediaPlayer(this);
        mController.setAnchorView(findViewById(R.id.main));
        mController.setEnabled(true);
    }


    /**
     * Drawer Adapter for handling the drawer navigation
     */
    private static class DrawerAdapter extends BaseAdapter {

        private View view;
        private int mSelectedItem;

        public DrawerAdapter(final View view) {
            this.view = view;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
            }

            TextView tv = (TextView) view.findViewById(R.id.rowText);
            ImageView iv =  (ImageView) view.findViewById(R.id.rowIcon);

            tv.setText(data[position]);
            //iv.setImageResource(ICONS[position]);

            if(position == 0) {
                view.setBackgroundColor(COLORS[position]);
            }

            return view;
        }

        public void setSelectedItem(int position) {
            mSelectedItem = position;
        }
    }

    /**
     * Listener for the selection of drawer items
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        int initFrag = 0;

        String thing = "not bundled";
        Intent intent = getIntent();
        if(intent != null) {
            Bundle bundle = intent.getExtras();
            //thing = (String) bundle.getString("THING");

            HashMap<Long, IOClass.Artist> artistsMap = (HashMap<Long, IOClass.Artist>) bundle.getSerializable("ARTISTS");
            HashMap<Long, IOClass.Album> albumsMap = (HashMap<Long, IOClass.Album>) bundle.getSerializable("ALBUMS");
            HashMap<Long, IOClass.Song> songsMap = (HashMap<Long, IOClass.Song>) bundle.getSerializable("SONGS");

            mLibArtists.putAll(artistsMap); //= (LinkedHashMap<Long, IOClass.Artist>) artistsMap;
            mLibAlbums.putAll(albumsMap); //= (LinkedHashMap<Long, IOClass.Album>) albumsMap;
            mLibSongs.putAll(songsMap); // = (LinkedHashMap<Long, IOClass.Song>) songsMap;


        }

        //Log.i("BUNDLE", thing);
        //IOClass ioClass = new IOClass();
        //mArtworkMap = ioClass.initAlbumArtwork(this);

        mData = getResources().getStringArray(R.array.music_type);
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            // Called when drawer is closed
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            // Called when drawer is opened
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }

        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        DrawerAdapter adapter = new DrawerAdapter(mDrawerList);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setController();

        selectItem(initFrag);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    // Called on invalidateOptionsMenu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.menu.menu_main).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ApplicationSettings.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.account_settings) {
            Intent intent = new Intent(this, AccountSettings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private void selectItem(int position) {
        // Create a new fragment based on item selected
        Fragment fragment = Fragment.instantiate(MainActivity.this, mFragments[position]);
        mCurrentFragment = position;

        // Replace existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main, fragment).commit();

        // Highlight selected item, update title, close drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        setTitle(mData[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("FRAGMENT", mCurrentFragment);
    }
}
