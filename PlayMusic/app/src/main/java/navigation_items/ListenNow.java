package navigation_items;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pulkit.playmusic.R;

/**
 * Created by Pulkit on 8/1/2016.
 */
public class ListenNow extends Fragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_now, container, false);

        return rootView;

    }
}
