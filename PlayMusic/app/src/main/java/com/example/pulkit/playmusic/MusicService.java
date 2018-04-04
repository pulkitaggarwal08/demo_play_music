package com.example.pulkit.playmusic;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import model.Song;

/**
 * Responsible for music playback. This is the main controller that handles all user actions
 * regarding song playback
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    // Media player
    private MediaPlayer player;
    // Song list
    private ArrayList<Song> songs;
    // Current position
    private int songPos;
    // Our binder
    //title of current song
    private String songTitle = "";
    private String songArtist = "";

    private final IBinder musicBind = new MusicBinder();
    private OnSongChangedListener onSongChangedListener;

    private static final int NOTIFY_ID = 1;

    public static final int STOPPED = 0;
    public static final int PAUSED = 1;
    public static final int PLAYING = 2;
    private int playerState = STOPPED;
    private SeekBar mSeekBar;
    private TextView mCurrentPosition;
    private TextView mTotalDuration;
    private int mInterval = 1000;

    // Async thread to update progress bar every second
    private Runnable mProgressRunner = new Runnable() {
        @Override
        public void run() {
            if (mSeekBar != null) {
                mSeekBar.setProgress(player.getCurrentPosition());
                if (player.isPlaying()) {
                    mSeekBar.postDelayed(mProgressRunner, mInterval);
                }
            }
        }
    };

    public void onCreate() {
        // Create the service
        super.onCreate();
        // Initialize position
        songPos = 0;
        // Create player
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        // Set player properties
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // Set player event listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // Stop media player

//        player.start();
//        player.getCurrentPosition();

//        player.stop();
//        player.reset();
//        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        int newPos = songPos + 1;
        if (newPos == songs.size())
            newPos = 0;
        setSong(newPos);
        togglePlay();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPrepared(MediaPlayer mp) {
        // Start playback
        mp.start();
        int duration = mp.getDuration();
        mSeekBar.setMax(duration);
        mSeekBar.postDelayed(mProgressRunner, mInterval);

        // Set our duration text view to display total duration in format 0:00
        mTotalDuration.setText(String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        ));

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle(songTitle)
                .setContentText(songArtist);

        Notification not = builder.build();
        startForeground(NOTIFY_ID, not);
    }

    public void setSong(int songIndex) {
        if (songs.size() <= songIndex || songIndex < 0) // if the list is empty... just return
            return;
        songPos = songIndex;
        playerState = STOPPED;
        onSongChangedListener.onSongChanged(songs.get(songPos));
    }

    /**
     * Toggles on/off song playback
     */
    public void togglePlay() {
        switch (playerState) {
            case STOPPED:
                playSong();
                break;
            case PAUSED:
                player.start();
                onSongChangedListener.onPlayerStatusChanged(playerState = PLAYING);
                mProgressRunner.run();
                break;
            case PLAYING:
                player.pause();
                onSongChangedListener.onPlayerStatusChanged(playerState = PAUSED);
                mSeekBar.removeCallbacks(mProgressRunner);
                break;
        }
    }

    private void playSong() {
        if (songs.size() <= songPos) // if the list is empty... just return
            return;
        // Play a song
        player.reset();
        // Get song
        Song playSong = songs.get(songPos);

        songTitle = playSong.getTitle();
        songArtist = playSong.getArtist();
        long currSongID = playSong.getID();

        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSongID);
        // Try playing the track... but it might be missing so try and catch
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
        mProgressRunner.run();
        onSongChangedListener.onPlayerStatusChanged(playerState = PLAYING);
    }

    public interface OnSongChangedListener {
        void onSongChanged(Song song);

        void onPlayerStatusChanged(int status);
    }

    // Sets a callback to execute when we switch songs.. ie: update UI
    public void setOnSongChangedListener(OnSongChangedListener listener) {
        onSongChangedListener = listener;
    }

    /**
     * Sets seekBar to control while playing music
     *
     * @param seekBar - Seek bar instance that's already on our UI thread
     */
    public void setUIControls(SeekBar seekBar, TextView currentPosition, TextView totalDuration) {
        mSeekBar = seekBar;
        mCurrentPosition = currentPosition;
        mTotalDuration = totalDuration;
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Change current position of the song playback
                    player.seekTo(progress);
                }

                // Update our textView to display the correct number of second in format 0:00
                mCurrentPosition.setText(String.format(
                        "%d:%02d", TimeUnit.MILLISECONDS.toMinutes(progress),
                        TimeUnit.MILLISECONDS.toSeconds(progress) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress))
                ));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public int getCurrentIndex() {
        return songPos;
    }
}