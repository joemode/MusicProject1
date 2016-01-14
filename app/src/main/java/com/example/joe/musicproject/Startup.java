package com.example.joe.musicproject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Joe on 1/3/2016.
 */
public class Startup extends Activity {

    //TODO: PROBABLY REMOVE THIS CLASS -- USELESS


    /* Possibly unneeded */
    HashMap<Long, Bitmap> mLibArtwork = new HashMap<>();
    ArrayList<IOClass.Album> mLibAlbums = new ArrayList<>();
    ArrayList<IOClass.Song> mLibSongs = new ArrayList<>();

    LinkedHashMap<Long, IOClass.Song> mLibSongsMap = new LinkedHashMap<>();
    LinkedHashMap<Long, IOClass.Album> mLibAlbumsMap = new LinkedHashMap<>();
    LinkedHashMap<Long, IOClass.Artist> mLibArtistsMap = new LinkedHashMap<>();

    Bitmap mArtwork;// = BitmapFactory.decodeResource(getResources(), R.drawable.ic_music);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mArtwork = BitmapFactory.decodeResource(getResources(), R.drawable.ic_music);

        /**
         * Show splashscreen while loading music objects
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean ok = loadObjects();

                Intent intent = new Intent(Startup.this, MainActivity.class);
                if(ok) {
                    //Start Main Activity
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ARTISTS", mLibArtistsMap);
                    bundle.putSerializable("ALBUMS", mLibAlbumsMap);
                    bundle.putSerializable("SONGS", mLibSongsMap);
                    //bundle.putString("THING", "THING");

                    //intent.putExtra("ARTISTS", mLibArtistsMap);
                    //intent.putExtra("ALBUMS", mLibAlbumsMap);
                    //intent.putExtra("SONGS", mLibSongsMap);
                    intent.putExtras(bundle);

                }
                startActivity(intent);
            }
        }).start();

    }

    private boolean loadObjects() {
        IOClass ioClass = new IOClass();
        ContentResolver res = this.getContentResolver();

        getArtworkMap(res);
        getArtists(res);
        getAlbums(res);
        getSongs(res);

        return true;


    }

    private void getArtists(ContentResolver res) {

        Bitmap tempArtwork = mArtwork;
        String[] projection = new String[] {MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_ALBUMS};
        String sortOrder = MediaStore.Audio.Media.ARTIST + " ASC";
        Cursor artistsCursor = res.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);

        if(artistsCursor != null && artistsCursor.moveToFirst()) {

            int idColumn = artistsCursor.getColumnIndex(MediaStore.Audio.Artists._ID);
            int nameColumn = artistsCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
            int numAlbumsColumn = artistsCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);

            do {
                long tempId = artistsCursor.getLong(idColumn);
                String tempName = artistsCursor.getString(nameColumn);
                String tempNumAlbums = artistsCursor.getString(numAlbumsColumn);

                mLibArtistsMap.put(tempId, new IOClass.Artist(tempId, tempName, tempNumAlbums));
                //newItems.add(new Artist(tempId, tempName, tempNumAlbums, tempArtwork));
            } while (artistsCursor.moveToNext());
            artistsCursor.close();
        }

    }

    private void getAlbums(ContentResolver res) {
        //ArrayList<IOClass.Album> newItems = new ArrayList<>();
        Bitmap tempArtwork = mArtwork;
        Uri uri = Uri.parse("content://media/external/audio/albumart");

        String[] projection = new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Media.ARTIST_ID};
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";
        Cursor albumsCursor = res.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);

        if(albumsCursor != null && albumsCursor.moveToFirst()) {
            int idColumn = albumsCursor.getColumnIndex(projection[0]);
            int nameColumn = albumsCursor.getColumnIndex(projection[1]);
            int artistColumn = albumsCursor.getColumnIndex(projection[2]);
            int artistIdColumn = albumsCursor.getColumnIndex(projection[3]);

            do {
                long tempId = albumsCursor.getLong(idColumn);
                long tempArtistId = albumsCursor.getLong(artistIdColumn);
                String tempName = albumsCursor.getString(nameColumn);
                String tempArtist = albumsCursor.getString(artistColumn);

                if(getArtwork(tempId, uri, res) != null) {
                    tempArtwork = getArtwork(tempId, uri, res);
                }

                IOClass.Album album = new IOClass.Album(tempId, tempArtistId, tempName, tempArtist);
                mLibAlbumsMap.put(tempId, album);
                mLibArtistsMap.get(tempArtistId).getAlbums().add(album);
//                if(mLibArtistsMap.get(tempArtistId).getArtwork().sameAs(mArtwork)) {
//                    mLibArtistsMap.get(tempArtistId).setArtwork(tempArtwork);
//                }

            } while (albumsCursor.moveToNext());
            albumsCursor.close();
        }
    }

    private void getSongs(ContentResolver res) {
        Bitmap tempArtwork  = mArtwork;
        String[] projection = new String[] {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION};
        String sortOrder = projection[2] + " ASC";
        Cursor songsCursor = res.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);

        if(songsCursor != null && songsCursor.moveToFirst()) {
            int idColumn = songsCursor.getColumnIndex(projection[0]);
            int albumIdColumn = songsCursor.getColumnIndex(projection[1]);
            int titleColumn = songsCursor.getColumnIndex(projection[2]);
            int artistColumn = songsCursor.getColumnIndex(projection[3]);
            int durationColumn = songsCursor.getColumnIndex(projection[4]);

            do {
                long tempId = songsCursor.getLong(idColumn);
                long tempAlbumId = songsCursor.getLong(albumIdColumn);
                String tempTitle = songsCursor.getString(titleColumn);
                String tempArtist = songsCursor.getString(artistColumn);
                String tempDuration = songsCursor.getString(durationColumn);

//                if(mLibAlbumsMap.get(tempAlbumId) != null) {
//                    if (mLibAlbumsMap.get(tempAlbumId).getArtwork() != null) {
//                        tempArtwork = mLibAlbumsMap.get(tempAlbumId).getArtwork();
//                    }
//                }

                IOClass.Song song = new IOClass.Song(tempId, tempAlbumId, tempTitle, tempArtist, tempDuration);
                mLibSongsMap.put(tempId, song);
                if(mLibAlbumsMap.get(tempAlbumId) != null) {
                    mLibAlbumsMap.get(tempAlbumId).getTracklist().add(song);
                }

            } while (songsCursor.moveToNext());
            songsCursor.close();
        }
    }

    private void getArtworkMap(ContentResolver res) {
        String[] projection = {MediaStore.Audio.Albums._ID};
        Cursor artworkCursor = res.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, null, null, null);
        Uri uri = Uri.parse("content://media/external/audio/albumart");

        if(artworkCursor != null && artworkCursor.moveToFirst()) {
            int idColumn = artworkCursor.getColumnIndex(projection[0]);

            do {
                long id = artworkCursor.getLong(idColumn);
                Bitmap bm = getArtwork(id, uri, res);
                mLibArtwork.put(id, bm);

            } while (artworkCursor.moveToNext());
        }

    }

    private Bitmap getArtwork(long id, Uri u, ContentResolver res) {
        Uri uri = ContentUris.withAppendedId(u, id);
        ParcelFileDescriptor fd = null;

        if(uri != null) {
            try {
                fd = res.openFileDescriptor(uri, "r");
                if(fd != null) {
                    Bitmap bm = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
                    //Scaled bitmap to 4x the size of the ic_music png
                    Bitmap scaledBM = Bitmap.createScaledBitmap(bm, 256, 256, false);
                    bm.recycle();
                    return scaledBM;
                } else {
                    return null;
                }
            } catch (FileNotFoundException ex) {
                return null;
            }
        }

        return null;
    }

}
