package app.ddf.danskdatahistoriskforening;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class ImageviewerActivity extends AppCompatActivity implements View.OnClickListener {
    Button backButton;
    Button deleteButton;
    ViewPager viewPager;
    ImageviewerPageAdapter pageAdapter;

    Intent result;

    ArrayList<Uri> imageUris;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageviewer);

        result = new Intent();

        Intent intent = getIntent();

        if(intent.hasExtra("imageURIs")){
            imageUris = intent.getParcelableArrayListExtra("imageURIs");
        }
        else{
            //should never happen... but just in case
            imageUris = new ArrayList<>();
        }

        backButton = (Button) findViewById(R.id.imageview_back_button);
        backButton.setOnClickListener(this);

        deleteButton = (Button) findViewById(R.id.imageview_delete_button);
        deleteButton.setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.imageview_viewpager);
        pageAdapter = new ImageviewerPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);

        int index = intent.getIntExtra("index", 0);
        viewPager.setCurrentItem(index);
    }

    @Override
    public void onClick(View v) {
        if(v == backButton){
            onBackPressed();
        }
        else if(v == deleteButton){
            int index = viewPager.getCurrentItem();

            imageUris.remove(index);

            if(imageUris.size() < 1){ //all images have been removed
                onBackPressed();
            }
            else {
                pageAdapter.notifyDataSetChanged();
                //workaround to force redraw of viewpager
                viewPager.setAdapter(pageAdapter);
            }
        }
    }

    @Override
    public void onBackPressed() {
        result.putExtra("remainingUris", imageUris);
        setResult(Activity.RESULT_OK, result);

        super.onBackPressed();
    }

    private class ImageviewerPageAdapter extends FragmentStatePagerAdapter{
        public ImageviewerPageAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ImageviewerFragment fragment = new ImageviewerFragment();
            fragment.setImageUri(imageUris.get(position));

            return fragment;
        }

        @Override
        public int getCount() {
            return imageUris.size();
        }
    }
}
