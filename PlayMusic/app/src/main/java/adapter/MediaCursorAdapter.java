package adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.pulkit.playmusic.R;

import tabs.Songs;

/**
 * Created by Pulkit on 7/28/2016.
 */
public class MediaCursorAdapter extends SimpleCursorAdapter {

    public MediaCursorAdapter(Context context, int layout, Cursor c) {

        super(context, layout, c,
                new String[]{MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.TITLE, MediaStore.Audio.AudioColumns.DURATION},
//                    new int[]{R.id.display_name, R.id.title, R.id.duration});
                new int[]{R.id.title, R.id.duration});
//

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView title = (TextView) view.findViewById(R.id.title);
//            TextView name = (TextView) view.findViewById(R.id.display_name);
        TextView duration = (TextView) view.findViewById(R.id.duration);

//            name.setText(cursor.getString(
//                    cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)));

        title.setText(cursor.getString(
                cursor.getColumnIndex(MediaStore.MediaColumns.TITLE)));

        try {
            long durationInMs = Long.parseLong(cursor.getString(
                    cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)));

            int durationInTotalSec = (int) (durationInMs / 1000.0);

            int durationInMin = (int) ((durationInMs / 1000.0) / 60.0);

            int durationInSec = (durationInTotalSec % 60);

            if (durationInSec < 10) {
                duration.setText(durationInMin + ":" + "0" + durationInSec);
            } else {
                duration.setText(durationInMin + ":" + durationInSec);
            }

//            durationInMin = new BigDecimal(Double.toString(durationInMin)).setScale(2, BigDecimal.ROUND_UP).doubleValue();


        } catch (Exception e) {

        }

        view.setTag(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.listitem, parent, false);

        bindView(v, context, cursor);

        return v;
    }
}