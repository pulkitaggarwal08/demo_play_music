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

import com.example.pulkit.playmusic.R;

import tabs.Albums;
import tabs.Artists;
import tabs.Genres;
import tabs.PlayLists;
import tabs.Songs;

public class SampleActivityTabs extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_layout, null);
        return view;
    }

    @Override

    public void onViewCreated(View view, Bundle savedInstanceState) {
        setUpPager(view);
    }

    private void setUpPager(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new TabsPagerAdapter(getActivity()));

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }

    class TabsPagerAdapter extends PagerAdapter {

        Activity activity;

        public TabsPagerAdapter(Activity activity) {
            this.activity = activity;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "PlayLists";
                case 1:
                    return "Artists";
                case 2:
                    return "Songs";
                case 3:
                    return "Albums";
                case 4:
                    return "Genres";
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;

            if (position == 0) {
                view = activity.getLayoutInflater().inflate(R.layout.fragment_playlists, container, false);
                PlayLists playLists = new PlayLists();
                container.addView(view);
                return  view;

            } else if (position == 1) {
                view = activity.getLayoutInflater().inflate(R.layout.fragment_artists, container, false);
                Artists artists = new Artists();
                container.addView(view);
                return  view;

            } else if (position == 2) {
                view = activity.getLayoutInflater().inflate(R.layout.fragment_songs_list, container, false);
                Songs songs = new Songs();
                container.addView(view);
                return  view;

            } else if (position == 3) {
                view = activity.getLayoutInflater().inflate(R.layout.fragment_album, container, false);
                Albums albums = new Albums();
                container.addView(view);
                return view;

            } else{
                view = activity.getLayoutInflater().inflate(R.layout.fragment_genres, container, false);
                Genres genres = new Genres();
                container.addView(view);
                return  view;
            }

//            container.addView(view);

//            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//            container.removeView(container);
        }

    }

}
