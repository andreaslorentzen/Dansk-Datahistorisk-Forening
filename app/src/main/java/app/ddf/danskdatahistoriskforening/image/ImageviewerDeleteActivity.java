package app.ddf.danskdatahistoriskforening.image;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;

import app.ddf.danskdatahistoriskforening.R;

public class ImageviewerDeleteActivity extends AbstractImageViewer implements View.OnClickListener, ConfirmDeletionDialogFragment.ConfirmDeletionListener{
    Button backButton;
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageviewer_delete);

        backButton = (Button) findViewById(R.id.imageview_back_button);
        backButton.setOnClickListener(this);

        deleteButton = (Button) findViewById(R.id.imageview_delete_button);
        deleteButton.setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.imageview_viewpager);


        if(savedInstanceState == null) {//avoid extra work and out of memory exceptions
            Intent intent = getIntent();
            if (intent.hasExtra("imageURIs")) {
                imageUris = intent.getParcelableArrayListExtra("imageURIs");
            } else {
                //should never happen... but just in case
                imageUris = new ArrayList<>();
            }

            int index;
            index = intent.getIntExtra("index", 0);


            viewPager.setAdapter(pageAdapter);

            viewPager.setCurrentItem(index);
        }
    }

    @Override
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

        (new FileDeleterAsyncTask()).execute(imageUris.get(index));

        imageUris.remove(index);

        if(imageUris.size() < 1){ //all images have been removed
            onBackPressed();
        }
        else {
            pageAdapter.notifyDataSetChanged();
            //workaround to force redraw of viewpager
            pageAdapter = new ImageviewerPageAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pageAdapter);

            viewPager.setCurrentItem(Math.min(index, imageUris.size() - 1));
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //no change required
    }

    private class FileDeleterAsyncTask extends AsyncTask<Uri, Void, Void>{

        @Override
        protected Void doInBackground(Uri... params) {
            Uri uri = params[0];

            File file = new File(uri.getPath());
            file.delete();

            return null;
        }
    }
}