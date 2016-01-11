package app.ddf.danskdatahistoriskforening.image;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public abstract class AbstractImageViewer extends AppCompatActivity {
    ViewPager viewPager;
    ImageviewerPageAdapter pageAdapter;

    ArrayList<Uri> imageUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){//avoid extra work and out of memory exceptions
            pageAdapter = new ImageviewerPageAdapter(getSupportFragmentManager());
        }
    }

    protected class ImageviewerPageAdapter extends FragmentStatePagerAdapter {
        public ImageviewerPageAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ImageviewerFragment fragment = new ImageviewerFragment();
            fragment.setImageUri(imageUris.get(position));
            fragment.setHeaderData(position + 1, imageUris.size());

            return fragment;
        }

        @Override
        public int getCount() {
            return imageUris.size();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("imageURIs", imageUris);
        outState.putInt("index", viewPager.getCurrentItem());
    }
}
