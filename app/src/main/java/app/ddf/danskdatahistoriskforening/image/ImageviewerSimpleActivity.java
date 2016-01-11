package app.ddf.danskdatahistoriskforening.image;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import app.ddf.danskdatahistoriskforening.R;

public class ImageviewerSimpleActivity extends AbstractImageViewer {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer_simple);

        viewPager = (ViewPager) findViewById(R.id.imageviewersimple_viewpager);

        if(savedInstanceState == null) {//avoid extra work and out of memory exceptions
            Intent intent = getIntent();

            if (intent.hasExtra("imageURIs")) {
                imageUris = intent.getParcelableArrayListExtra("imageURIs");
            } else {
                //should never happen... but just in case
                imageUris = new ArrayList<>();
            }


            viewPager.setAdapter(pageAdapter);

            int index = intent.getIntExtra("index", 0);
            viewPager.setCurrentItem(index);
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        pageAdapter = new ImageviewerPageAdapter(getSupportFragmentManager());
        imageUris = savedInstanceState.getParcelableArrayList("imageURIs");
        int index = savedInstanceState.getInt("index");

        pageAdapter.notifyDataSetChanged();
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(index);

        Log.d("RestoreActivity", "" + pageAdapter.getCount());
    }
}
