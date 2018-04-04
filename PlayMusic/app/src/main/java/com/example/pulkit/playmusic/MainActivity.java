package com.example.pulkit.playmusic;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.IBinder;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.IconTextView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.shamanland.fab.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import adapter.SongAdapter;
import model.Song;
import navigation_items.ListenNow;
import navigation_items.MusicLibrary;


public class MainActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    ActionBarDrawerToggle mDrawerToggle;

    private ArrayList<Song> songList;
    private MusicService musicService;
    private boolean musicBound = false;

    private FloatingActionButton fab;
    private SeekBar seekBar;
    private TextView currentPosition;
    private TextView totalDuration;
    private Intent playIntent;
    RenderScript rs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        // update the main content by replacing fragments
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.containerView, new MusicLibrary()).commit();


        fab = (FloatingActionButton) findViewById(R.id.playBtn);
        fab.setImageDrawable(new IconDrawable(this, Iconify.IconValue.fa_play)
                .colorRes(R.color.white));

        IconTextView previewPlayBtn = (IconTextView) findViewById(R.id.previewPlayBtn);
        previewPlayBtn.setOnClickListener(togglePlayBtn);
        fab.setOnClickListener(togglePlayBtn);

        songList = new ArrayList<Song>();

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        currentPosition = (TextView) findViewById(R.id.currentPosition);
        totalDuration = (TextView) findViewById(R.id.totalDuration);

        // Set up previous and next buttons
        IconTextView prevBtn = (IconTextView) findViewById(R.id.prevBtn);
        IconTextView nextBtn = (IconTextView) findViewById(R.id.nextBtn);

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = musicService.getCurrentIndex();
                int previous = current - 1;
                // If current was 0, then play the last song in the list
                if (previous < 0)
                    previous = songList.size() - 1;
                musicService.setSong(previous);
                musicService.togglePlay();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = musicService.getCurrentIndex();
                int next = current + 1;
                // If current was the last song, then play the first song in the list
                if (next == songList.size())
                    next = 0;
                musicService.setSong(next);
                musicService.togglePlay();
            }
        });

        final SlidingUpPanelLayout slidingUpPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        View dragPanel = findViewById(R.id.dragPanel);
        final TransitionDrawable transition = (TransitionDrawable) dragPanel.getBackground();
        transition.startTransition(1);

        drawerTab();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void drawerTab() {

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        getSupportActionBar().setTitle("Music Library");

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.nav_item_music_library) {

//                    mFragmentManager = getSupportFragmentManager();
                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.containerView, new MusicLibrary()).commit();

                    getSupportActionBar().setTitle("Music Library");

                } else if (menuItem.getItemId() == R.id.nav_item_listen_now) {

                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.containerView, new ListenNow()).commit();

                    getSupportActionBar().setTitle("Listen Now");

                }

                mDrawerLayout.closeDrawer(GravityCompat.START);
//                mDrawerLayout.closeDrawers();

                return true;
            }

        });
    }

    private View.OnClickListener togglePlayBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            musicService.togglePlay();
        }
    };

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;

            musicService = binder.getService();

            musicService.setSongs(songList);
            musicService.setUIControls(seekBar, currentPosition, totalDuration);
            musicBound = true;

            if (songList.size() == 0)
                findViewById(R.id.no_music).setVisibility(View.VISIBLE);

            musicService.setOnSongChangedListener(new MusicService.OnSongChangedListener() {
                ImageView artworkView = (ImageView) findViewById(R.id.playerArtwork);
                ImageView previewArtworkView = (ImageView) findViewById(R.id.previewArtwork);
                TextView previewSongTitle = (TextView) findViewById(R.id.previewSongTitle);
                TextView previewSongArtist = (TextView) findViewById(R.id.previewSongArtist);
                IconTextView previewPlayBtn = (IconTextView) findViewById(R.id.previewPlayBtn);

                @Override
                public void onSongChanged(Song song) {
                    Bitmap bitmap;
                    previewSongTitle.setText(song.getTitle());
                    previewSongArtist.setText(song.getArtist());
                    bitmap = song.getArtworkBitmap(getApplicationContext());
                    if (bitmap == null) return; // bitmap might be null.. if it is, dont do anything
                    artworkView.setImageBitmap(bitmap);
                    previewArtworkView.setImageBitmap(bitmap);

                    Bitmap blurredBitmap = bitmap.copy(bitmap.getConfig(), true);

                    applyBlur(25f, blurredBitmap);

//                     Scale the bitmap
                    Matrix matrix = new Matrix();
                    matrix.postScale(3f, 3f);
                    blurredBitmap = Bitmap.createBitmap(
                            blurredBitmap, 0, 0, blurredBitmap.getWidth(), blurredBitmap.getHeight(), matrix, true);
                    ((ImageView) findViewById(R.id.playerBg)).setImageBitmap(blurredBitmap);
                }

                @Override
                public void onPlayerStatusChanged(int status) {
                    switch (status) {
                        case MusicService.PLAYING:
                            previewPlayBtn.setText("{fa-pause}");
                            fab.setImageDrawable(new IconDrawable(getApplicationContext(), Iconify.IconValue.fa_pause)
                                    .colorRes(R.color.white));

                            break;
                        case MusicService.PAUSED:
                            previewPlayBtn.setText("{fa-play}");
                            fab.setImageDrawable(new IconDrawable(getApplicationContext(), Iconify.IconValue.fa_play)
                                    .colorRes(R.color.white));
                            break;
                    }
                }
            });

            musicService.setSong(0);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void applyBlur(float radius, Bitmap bitmap) {
        rs = RenderScript.create(this);
        // Use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
        final Allocation input = Allocation.createFromBitmap(rs, bitmap);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
//        stopService(playIntent);
        if (musicBound) {
            unbindService(musicConnection);
        }
        super.onDestroy();
    }

    public ArrayList<Song> getSongs() {
        return songList;
    }

    public void setSongs(ArrayList<Song> songList) {
        this.songList = songList;
    }

    public MusicService getMusicService() {
        return musicService;
    }

    private Menu mMenu;
    private MenuItem search;


    public static boolean mDefaultMenu = true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        mMenu = menu;
        //mDefaultMenu = true;
        search = menu.findItem(R.id.action_search);


        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mDefaultMenu) {
            search.setVisible(true);

        } else {
            search.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_search:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
