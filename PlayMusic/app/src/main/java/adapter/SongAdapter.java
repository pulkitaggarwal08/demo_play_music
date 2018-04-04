package adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.pulkit.playmusic.R;

import java.util.ArrayList;
import java.util.List;

import model.Song;
import tabs.Songs;

/**
 * Created by yasha on 12/12/14.
 */
public class SongAdapter extends RecyclerView.Adapter<SongViewHolder> {

    private ArrayList<Song> songs;
    private Context ctx;

    public SongAdapter(ArrayList<Song> songs, Context ctx) {
        this.songs = songs;
        this.ctx = ctx;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.songslist, viewGroup, false);

        return new SongViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(SongViewHolder songViewHolder, int i) {

        Song song = songs.get(i);

        songViewHolder.vTitle.setText(song.getTitle());
        songViewHolder.vArtist.setText(song.getArtist());
        songViewHolder.duration.setText(song.getSong_time());

//        animate(songViewHolder);

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), song.getArtwork());
            songViewHolder.vArtwork.setImageBitmap(bitmap);
        } catch (Exception exception) {
            // log error
        }

    }

//    public void animate(RecyclerView.ViewHolder viewHolder) {
//        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(ctx, R.anim.bounce_interplator);
//        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
//    }


    @Override
    public int getItemCount() {
        return songs.size();
    }

}
