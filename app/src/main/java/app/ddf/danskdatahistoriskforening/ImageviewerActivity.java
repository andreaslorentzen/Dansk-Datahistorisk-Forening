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
    ViewPager viewPager;

    Intent result;

    ArrayList<Integer> removedImages;
    ArrayList<Uri> imageUris;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageviewer);

        result = new Intent();
        removedImages = new ArrayList<>();

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

        viewPager = (ViewPager) findViewById(R.id.imageview_viewpager);
        ImageviewerPageAdapter pageAdapter = new ImageviewerPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
    }

    @Override
    public void onClick(View v) {
        if(v == backButton){
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        result.putExtra("removedImages", removedImages);
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
