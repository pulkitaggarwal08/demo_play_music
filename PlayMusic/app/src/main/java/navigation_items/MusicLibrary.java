package navigation_items;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pulkit.playmusic.MainActivity;
import com.example.pulkit.playmusic.R;

import tabs.Albums;
import tabs.Artists;
import tabs.Genres;
import tabs.PlayLists;
import tabs.Songs;

/**
 * Created by Pulkit on 8/1/2016.
 */
public class MusicLibrary extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_layout, null);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return view;

    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Songs();
                case 1:
                    return new PlayLists();
                case 2:
                    return new Artists();
                case 3:
                    return new Albums();
                case 4:
                    return new Genres();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Songs";
                case 1:
                    return "PlayLists";
                case 2:
                    return "Artists";
                case 3:
                    return "Albums";
                case 4:
                    return "Genres";
            }
            return null;
        }

    }

}


