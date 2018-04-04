package tabs;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pulkit.playmusic.MainActivity;
import com.example.pulkit.playmusic.R;
import com.example.pulkit.playmusic.RecyclerItemClickListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.SongAdapter;
import model.Song;

import com.example.pulkit.playmusic.MusicService;

/**
 * Created by Pulkit on 8/2/2016.
 */
public class Songs extends Fragment {

    private RecyclerView songView;
    private ArrayList<Song> songList;
    View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        if (rootView == null) {

        rootView = inflater.inflate(R.layout.fragment_songs_list, container, false);

        songList = ((MainActivity) getActivity()).getSongs();

        songView = (RecyclerView) rootView.findViewById(R.id.song_list);

        LinearLayoutManager grid = new LinearLayoutManager(getContext());
        grid.setOrientation(LinearLayoutManager.VERTICAL);
        songView.setLayoutManager(grid);

        getSongList();

        ((MainActivity) getActivity()).setSongs(songList);

        SongAdapter songAdt = new SongAdapter(songList, getContext());
        songView.setAdapter(songAdt);

        songView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                MusicService musicService = ((MainActivity) getActivity()).getMusicService();
                musicService.setSong(position);
                musicService.togglePlay();
            }
        }));

//        }
        return rootView;
    }

    public void getSongList() {
        //retrieve song info
        ContentResolver musicResolver = getContext().getContentResolver();
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String order = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        String text = "";
        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, order);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                long albumId = musicCursor.getLong(albumColumn);

//                long songTime = musicCursor.getColumnIndex(String.valueOf(duration));

                try {
                    long durationInMs = Long.parseLong(musicCursor.getString(
                            musicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)));

                    int durationInTotalSec = (int) ((durationInMs) / 1000.0);

                    int durationInMin = (int) ((int) (durationInMs) / 1000.0 / 60.0);

                    int durationInSec = (durationInTotalSec % 60);

                    if (durationInSec < 10) {
                        text = durationInMin + ":" + "0" + durationInSec;
                    } else {
                        text = durationInMin + ":" + durationInSec;
                    }
                } catch (Exception e) {

                }

                final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, albumId);

                songList.add(new Song(thisId, thisTitle, thisArtist, albumArtUri, text));
            }
            while (musicCursor.moveToNext());

            musicCursor.close();
        }

    }

}
