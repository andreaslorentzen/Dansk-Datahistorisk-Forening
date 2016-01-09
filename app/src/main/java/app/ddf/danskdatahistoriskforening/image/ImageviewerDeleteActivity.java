package app.ddf.danskdatahistoriskforening.image;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;

import app.ddf.danskdatahistoriskforening.R;

public class ImageviewerDeleteActivity extends AppCompatActivity implements View.OnClickListener, ConfirmDeletionDialogFragment.ConfirmDeletionListener{
    Button backButton;
    Button deleteButton;
    ViewPager viewPager;
    ImageviewerPageAdapter pageAdapter;

    ArrayList<Uri> imageUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageviewer_delete);

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

            ConfirmDeletionDialogFragment dialog = new ConfirmDeletionDialogFragment();
            dialog.setTitle(imageUris.get(index).getPath());
            dialog.setIndex(index);
            dialog.show(getSupportFragmentManager(), "ConfirmDeletionDialog");
        }
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra("remainingURIs", imageUris);
        setResult(Activity.RESULT_OK, result);

        super.onBackPressed();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        int index = ((ConfirmDeletionDialogFragment) dialog).getIndex();

        File file = new File(imageUris.get(index).getPath());
        file.delete();

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

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //no change required
    }

    private class ImageviewerPageAdapter extends FragmentStatePagerAdapter{
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
}