package com.example.android.customcamera;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.customcamera.Fragments.CameraFragment;
import com.example.android.customcamera.Fragments.GalleryFragment;
import com.example.android.customcamera.Fragments.VideoFragment;

import java.util.ArrayList;
import java.util.List;

public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_main);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_main);

        if (viewPager != null) {
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        TabsPagerAdapter pagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new GalleryFragment(), "GALLERY");
        pagerAdapter.addFragment(new CameraFragment(), "CAMERA");
        pagerAdapter.addFragment(new VideoFragment(), "VIDEO");
        viewPager.setAdapter(pagerAdapter);
    }

    public class TabsPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> FRAGMENTS_LIST = new ArrayList<>();
        private final List<String> FRAGMENTS_TITLES = new ArrayList<>();

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FRAGMENTS_LIST.get(position);
        }

        @Override
        public int getCount() {
            return FRAGMENTS_LIST.size() != 0 ? FRAGMENTS_LIST.size() : 0;
        }

        public void addFragment(Fragment fragment, String title) {
            FRAGMENTS_LIST.add(fragment);
            FRAGMENTS_TITLES.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return FRAGMENTS_TITLES.get(position);
        }
    }

}
