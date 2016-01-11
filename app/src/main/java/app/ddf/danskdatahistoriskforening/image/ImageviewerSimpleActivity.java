package app.ddf.danskdatahistoriskforening.image;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import app.ddf.danskdatahistoriskforening.R;

public class ImageviewerSimpleActivity extends AbstractImageViewer {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer_simple);

        Intent intent = getIntent();

        if(intent.hasExtra("imageURIs")){
            imageUris = intent.getParcelableArrayListExtra("imageURIs");
        }
        else{
            //should never happen... but just in case
            imageUris = new ArrayList<>();
        }

        viewPager = (ViewPager) findViewById(R.id.imageviewersimple_viewpager);
        viewPager.setAdapter(pageAdapter);

        int index = intent.getIntExtra("index", 0);
        viewPager.setCurrentItem(index);
    }
}
