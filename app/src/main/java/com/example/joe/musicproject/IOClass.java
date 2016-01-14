package com.example.joe.musicproject;

import android.animation.LayoutTransition;
import android.content.Context;
import android.media.MediaActionSound;
import android.os.ParcelFileDescriptor;
import android.widget.ProgressBar;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joe on 12/26/2015.
 */
public class IOClass {

    //Class maintaining all IO operations

    private static class MusicObject implements Serializable {
        private long id;
        private String title;
        private String artist;
        //private Bitmap artwork;

        public void setId(long id) {this.id = id;}
        public void setTitle(String title) {this.title = title;}
        public void setArtist(String artist) {this.artist = artist;}
        //public void setArtwork(Bitmap artwork) {this.artwork = artwork;}

        public long getId() { return id; }
        public String getTitle() { return title; }
        public String getArtist() { return artist; }
        //public Bitmap getArtwork() { return artwork; }

    }

    public static class Song extends MusicObject {

        private String duration;
        private long songID;

        public Song(long songID, long albumID, String songTitle, String songArtist, String duration) {
            setId(albumID);
            setTitle(songTitle);
            setArtist(songArtist);
            //setArtwork(songArtwork);

            this.duration = duration;
            this.songID = songID;
        }

    }

    public static class Album extends MusicObject {

        private Long artistId;
        private ArrayList<Song> tracklist = new ArrayList<>();
        public ArrayList<Song> getTracklist() { return tracklist; }

        public Album(long albumId, long artistId, String albumTitle, String albumArtist) {
            setId(albumId);
            setTitle(albumTitle);
            setArtist(albumArtist);

            this.artistId = artistId;
        }


    }

    public static class Artist implements Serializable {
        private String name;
        private long id;
        private String numAlbums;

        private ArrayList<Album> albums = new ArrayList<>();

        public ArrayList<Album> getAlbums() { return albums; }

        public Artist(long id, String name, String numAlbums) {
            this.id = id;
            this.name = name;
            this.numAlbums = numAlbums;
        }


    }

    public static class AlbumListAdapter extends BaseAdapter {

        private ArrayList<Album> albums = new ArrayList<>();
        private View view;
        private String message;
        private ProgressBar spinner;

        public AlbumListAdapter(final View view, final String message, final ProgressBar spinner) {
            this.view = view;
            this.message = message;
            this.spinner = spinner;
            update();
        }

        public void update() {
            //TODO: assemble these linkedhashmaps in this function so that they retain order/don't require bundling from startup class
            if(MainActivity.mLibAlbums != null) {
                spinner.setVisibility(view.GONE);
                for(Map.Entry<Long, Album> entry : MainActivity.mLibAlbums.entrySet()) {
                    albums.add(entry.getValue());
                }
            }

            /*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Album> newAlbums;

                    if(message.equals("Library Albums")) {
                        newAlbums = getAlbums(view);
                    } else {
                        newAlbums = null;
                    }

                    if(newAlbums != null) {
                        albums = newAlbums;
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                                spinner.setVisibility(view.GONE);
                            }
                        });
                    } else {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(), R.string.tracks_fail, Toast.LENGTH_SHORT).show();
                                spinner.setVisibility(view.GONE);
                            }
                        });
                    }
                }
            }).start(); */
        }

        @Override
        public int getCount() {
            return albums.size();
        }

        @Override
        public Object getItem(int position) {
            return albums.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false);
            }

            String albumsText = albums.get(position).getTitle();
            String artistsText = albums.get(position).getArtist();
            String blankStr = "";

            String[] temp = new String[] {albumsText, artistsText, blankStr};
            Bitmap artwork = BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_music);
            popMusicItem(view, temp, artwork);

            return view;
        }
    }

    public static class ArtistListAdapter extends BaseAdapter {

        private ArrayList<Artist> artists = new ArrayList<>();
        private View view;
        private String message;
        private ProgressBar spinner;

        public ArtistListAdapter(final View view, final String message, final ProgressBar spinner) {
            this.view = view;
            this.message = message;
            this.spinner = spinner;
            update();
        }

        public void update() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Artist> newArtists;

                    if(message.equals("Library Artists")) {
                        newArtists = getArtists(view);
                    } else {
                        newArtists = null;
                    }

                    if(newArtists != null) {
                        artists = newArtists;
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                                spinner.setVisibility(view.GONE);
                            }
                        });
                    } else {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(), R.string.tracks_fail, Toast.LENGTH_SHORT).show();
                                spinner.setVisibility(view.GONE);
                            }
                        });
                    }
                }
            }).start();
        }

        @Override
        public int getCount() {
            return artists.size();
        }

        @Override
        public Object getItem(int position) {
            return artists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false);
            }

            String albumsText = view.getResources().getString(R.string.album_count);
            String endS = artists.get(position).numAlbums.equals("1") ? "" : "s";

            String artistText = artists.get(position).name;
            String albumsMsg = String.format(albumsText, artists.get(position).numAlbums, endS);
            String blankStr = "";

            String[] temp = new String[] {artistText, albumsMsg, blankStr};
            Bitmap artwork = BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_music);

            popMusicItem(view, temp, artwork);

            return view;
        }

    }

    public static class SongListAdapter extends BaseAdapter {

        private ArrayList<Song> songs = new ArrayList<>();
        private View view;
        private String message;
        private ProgressBar spinner;

        public SongListAdapter(final View view, final String message, final ProgressBar spinner) {
            this.view = view;
            this.message = message;
            this.spinner = spinner;
            update();
        }

        public void update() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Song> newSongs;

                    if(message.equals("Library Tracks")) {
                        //ProgressDialog.show(view.getContext(), "Loading", "Loading...");
                        newSongs = getSongs(view);
                    } else {
                        newSongs = null;
                    }

                    if(newSongs!=null) {
                        songs = newSongs;
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                                spinner.setVisibility(view.GONE);
                            }
                        });
                    } else {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(), R.string.tracks_fail, Toast.LENGTH_SHORT).show();
                                spinner.setVisibility(view.GONE);
                            }
                        });
                    }
                }
            }).start();

        }

        @Override
        public int getCount() {
            return songs.size();
        }

        @Override
        public Object getItem(int position) {
            return songs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false);
            }

            int durationMillis = Integer.parseInt(songs.get(position).duration);

            String songText = songs.get(position).getTitle();
            String artistText = songs.get(position).getArtist();
            String duration = String.format("%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(durationMillis),
                    TimeUnit.MILLISECONDS.toSeconds(durationMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMillis)));
            Bitmap artwork = BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_music);

            String[] strings = new String[] {songText, artistText, duration};
            popMusicItem(view, strings, artwork);

            return view;

        }

    }

    public static ArrayList<Song> getSongs(final View view) {
        ArrayList<Song> newItems = new ArrayList<>();

        ContentResolver songsResolver = view.getContext().getContentResolver();
        Uri songsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songsCursor = songsResolver.query(songsUri, null, null, null, MediaStore.Audio.Media.TITLE + " ASC");

        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");

        if(songsCursor != null && songsCursor.moveToFirst()) {
            //get columns
            int titleColumn = songsCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = songsCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = songsCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumId = songsCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int durationColumn = songsCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            //int artworkColumn = songsCursor.getColumnIndex(MediaStore.Audio.Media.);
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            //64x64 pixel default icon
            Bitmap tempArtwork = BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_music);

            //add songs to list
            do {
                long tempId = songsCursor.getLong(idColumn);
                String tempTitle = songsCursor.getString(titleColumn);
                String tempArtist = songsCursor.getString(artistColumn);
                String tempDuration = songsCursor.getString(durationColumn);
                long tempAlbumId = songsCursor.getLong(albumId);
                //String tempPath = songsCursor.getString(artworkColumn);

//                Bitmap artwork = loadAlbumArtwork(tempAlbumId, artworkUri, 150, 150, view);
//                if(artwork != null) {
//                    tempArtwork = artwork;
//                }

                newItems.add(new Song(tempId, tempAlbumId, tempTitle, tempArtist, tempDuration));

            } while (songsCursor.moveToNext());
        }

        return newItems;
    }

    public static ArrayList<Artist> getArtists(final View view) {
        ArrayList<Artist> newItems = new ArrayList<>();

        ContentResolver artistsResolver = view.getContext().getContentResolver();
        String[] projection = new String[] { MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_ALBUMS };
        String sortOrder = MediaStore.Audio.Media.ARTIST + " ASC";
        Cursor artistsCursor = artistsResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);

        if(artistsCursor != null && artistsCursor.moveToFirst()) {

            int idColumn = artistsCursor.getColumnIndex(MediaStore.Audio.Artists._ID);
            int nameColumn = artistsCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
            int numAlbumsColumn = artistsCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);

            Bitmap tempArtwork = BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_music);

            do {
                long tempId = artistsCursor.getLong(idColumn);
                String tempName = artistsCursor.getString(nameColumn);
                String tempNumAlbums = artistsCursor.getString(numAlbumsColumn);

                newItems.add(new Artist(tempId, tempName, tempNumAlbums));
            } while (artistsCursor.moveToNext());
            artistsCursor.close();
        }


        return newItems;
    }

    public static ArrayList<Album> getAlbums(final View view) {
        ArrayList<Album> newItems = new ArrayList<>();

        ContentResolver albumsResolver = view.getContext().getContentResolver();
        String[] projection = new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST};
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";
        Cursor albumsCursor = albumsResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);

        Bitmap tempArtwork = BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_music);

        if(albumsCursor != null && albumsCursor.moveToFirst()) {
            int idColumn = albumsCursor.getColumnIndex(projection[0]);
            int nameColumn = albumsCursor.getColumnIndex(projection[1]);
            int artistColumn = albumsCursor.getColumnIndex(projection[2]);

            do {
                long tempId = albumsCursor.getLong(idColumn);
                String tempName = albumsCursor.getString(nameColumn);
                String tempArtist = albumsCursor.getString(artistColumn);

                if(MainActivity.getArtworkMap().get(tempId) != null) {
                    tempArtwork = MainActivity.getArtworkMap().get(tempId);
                }

                //newItems.add(new Album(tempId, tempName, tempArtist, tempArtwork));

            } while (albumsCursor.moveToNext());
            albumsCursor.close();
        }

        /*
        ContentResolver artistsResolver = view.getContext().getContentResolver();
        String[] projection = new String[] { MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_ALBUMS };
        String sortOrder = MediaStore.Audio.Media.ARTIST + " ASC";
        Cursor artistsCursor = artistsResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);

        if(artistsCursor != null && artistsCursor.moveToFirst()) {

            int idColumn = artistsCursor.getColumnIndex(MediaStore.Audio.Artists._ID);
            int nameColumn = artistsCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
            int numAlbumsColumn = artistsCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);

            Bitmap tempArtwork = BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_music);

            do {
                long tempId = artistsCursor.getLong(idColumn);
                String tempName = artistsCursor.getString(nameColumn);
                String tempNumAlbums = artistsCursor.getString(numAlbumsColumn);

                newItems.add(new Artist(tempId, tempName, tempNumAlbums, tempArtwork));
            } while (artistsCursor.moveToNext());
            artistsCursor.close();
        }
         */
        return newItems;
    }

    public static Bitmap loadAlbumArtwork(long id, Uri aUri, int reqWidth, int reqHeight, View view) {
        ContentResolver res = view.getContext().getContentResolver();

        Uri uri = ContentUris.withAppendedId(aUri, id);
//        InputStream in;
//        try {
//            in = res.openInputStream(uri);
//            if(in == null) {
//                return null;
//            }
//        } catch (FileNotFoundException ex) {
//            return null;
//        }

        ParcelFileDescriptor in;
        if(uri != null) {

            try {
                in = res.openFileDescriptor(uri, "r");
            } catch (FileNotFoundException ex) {
                return null;
            }
        } else return null;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(in.getFileDescriptor(), null, options);
        //BitmapFactory.decodeStream(in, null, options);

        options.inSampleSize = calcInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        Bitmap ret = BitmapFactory.decodeFileDescriptor(in.getFileDescriptor(), null, options);
        //Bitmap ret = BitmapFactory.decodeStream(in, null, options);
        try {
            in.close();
        } catch(IOException ex) {

        }
        return ret;
    }

    public static int calcInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        //TODO: rest of function from http://developer.android.com/training/displaying-bitmaps/load-bitmap.html

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static void popMusicItem(View view, String[] strings, Bitmap artwork) {

        TextView primaryText = (TextView) view.findViewById(R.id.primaryText);
        TextView subText = (TextView) view.findViewById(R.id.subText);
        TextView rightText = (TextView) view.findViewById(R.id.rightText);
        ImageView artImage = (ImageView) view.findViewById(R.id.artworkView);

        primaryText.setText(strings[0]);
        subText.setText(strings[1]);
        rightText.setText(strings[2]);
        artImage.setImageBitmap(artwork);

    }

    public HashMap<Long, Bitmap> initAlbumArtwork(Context context) {
        HashMap<Long, Bitmap> scaledArtworkMap = new HashMap<>();
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        ParcelFileDescriptor fd = null;

        ContentResolver artworkResolver = context.getContentResolver();
        String[] projection = new String[] {MediaStore.Audio.Albums._ID};
        Cursor artworkCursor = artworkResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, null, null, null);


        if (artworkCursor != null && artworkCursor.moveToFirst()) {

            int idColumn = artworkCursor.getColumnIndex(projection[0]);

            do {
                long id = artworkCursor.getLong(idColumn);

                Uri uri = ContentUris.withAppendedId(artworkUri, id);
                if(uri != null) {

                    try {
                        fd = artworkResolver.openFileDescriptor(uri, "r");
                    } catch (FileNotFoundException ex) {

                    }
                }

                if(fd != null) {
                    Bitmap bm = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
                    //Scaled bitmap to 4x the size of the ic_music png
                    Bitmap scaledBM = Bitmap.createScaledBitmap(bm, 256, 256, false);
                    bm.recycle();
                    scaledArtworkMap.put(id, scaledBM);
                } else {
                    scaledArtworkMap.put(id, null);
                }



            } while (artworkCursor.moveToNext());

        }

        /*
        ContentResolver artistsResolver = view.getContext().getContentResolver();
        String[] projection = new String[] { MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_ALBUMS };
        String sortOrder = MediaStore.Audio.Media.ARTIST + " ASC";
        Cursor artistsCursor = artistsResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);

        if(artistsCursor != null && artistsCursor.moveToFirst()) {

            int idColumn = artistsCursor.getColumnIndex(MediaStore.Audio.Artists._ID);
            int nameColumn = artistsCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
            int numAlbumsColumn = artistsCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);

            Bitmap tempArtwork = BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_music);

            do {
                long tempId = artistsCursor.getLong(idColumn);
                String tempName = artistsCursor.getString(nameColumn);
                String tempNumAlbums = artistsCursor.getString(numAlbumsColumn);

                newItems.add(new Artist(tempId, tempName, tempNumAlbums, tempArtwork));
            } while (artistsCursor.moveToNext());
            artistsCursor.close();
        }
         */

        return scaledArtworkMap;
    }

}

/**
 Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
 Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
 ContentResolver res = context.getContentResolver();
 InputStream in = res.openInputStream(uri);
 Bitmap artwork = BitmapFactory.decodeStream(in);
 */
