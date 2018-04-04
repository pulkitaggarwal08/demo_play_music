package model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

/**
 * Created by yasha on 12/12/14.
 */
public class Song {
    private long id;
    private String title;
    private String artist;
    private Uri artwork;
    private String song_time;

    public Song(long songID, String songTitle, String songArtist) {
        id = songID;
        title = songTitle;
        artist = songArtist;
    }

    public Song(long songID, String songTitle, String songArtist, Uri songArtwork) {
        id = songID;
        title = songTitle;
        artist = songArtist;
        artwork = songArtwork;
    }

    public Song(long songID, String songTitle, String songArtist, Uri songArtwork, String duration) {
        id = songID;
        title = songTitle;
        artist = songArtist;
        artwork = songArtwork;
        song_time = String.valueOf(duration);
    }

    public long getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Uri getArtwork() {
        return artwork;
    }

    public String getSong_time() {
        return song_time;
    }

    public Bitmap getArtworkBitmap(Context ctx) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), artwork);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
