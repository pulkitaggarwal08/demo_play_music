package adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pulkit.playmusic.R;

/**
 * Created by yasha on 12/12/14.
 */
public class SongViewHolder extends RecyclerView.ViewHolder {

    protected TextView vArtist;
    protected TextView vTitle;
    protected ImageView vArtwork;
    protected TextView duration;
    protected ImageView more_options;

    public SongViewHolder(View v) {
        super(v);
        vTitle = (TextView) v.findViewById(R.id.song_title);
        vArtist = (TextView) v.findViewById(R.id.song_artist);
        vArtwork = (ImageView) v.findViewById(R.id.song_art);
        duration = (TextView) v.findViewById(R.id.song_time);
        more_options= (ImageView) v.findViewById(R.id.more_options);

    }

}