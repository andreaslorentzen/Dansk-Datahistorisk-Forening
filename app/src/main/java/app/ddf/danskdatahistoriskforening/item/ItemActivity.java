package app.ddf.danskdatahistoriskforening.item;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import app.ddf.danskdatahistoriskforening.App;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.dal.BackgroundService;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.domain.Logic;
import app.ddf.danskdatahistoriskforening.helper.BitmapEncoder;
import app.ddf.danskdatahistoriskforening.image.ImageviewerDeleteActivity;

public class ItemActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int IMAGEVIEWER_REQUEST_CODE = 200;
    public static final int AUDIORECORDING_REQUEST_CODE = 300;

    ArrayList<Pair<ImageView, Uri>> imageViews;

    private boolean itemSaved;

    private WeakReference<ItemFragment> itemFragmentWeakReference;
    private WeakReference<ItemDetailsFragment> itemDetailsFragmentWeakReference;
    private WeakReference<ItemDescriptionFragment> itemDescriptionFragmentWeakReference;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ItemActivity.this.checkForErrors(intent.getIntExtra("status", 0));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.setCurrentActivity(this);
        setContentView(R.layout.activity_item);

        Toolbar registerToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        registerToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);

        if(Logic.instance.isNewRegistration()){
            registerToolbar.setTitle("Registrer genstand");
        }
        else{
            registerToolbar.setTitle("Rediger genstand");
        }

        setSupportActionBar(registerToolbar);

        /*
         * http://developer.android.com/training/animation/screen-slide.html
         */

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_strip);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onResume() {
        if (!App.isConnected()) {
            findViewById(R.id.internetConnBar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.internetConnBar).setVisibility(View.GONE);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(App.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        super.onResume();

        Item item = Logic.instance.editItem;

        imageViews = new ArrayList<>();
        ArrayList<Uri> uris = item.getPictures();
        if (uris != null) {
            for (int i = 0; i < uris.size(); i++) {
                generateImagePair(uris.get(i));
            }
        }
        uris = item.getAddedPictures();
        if (uris != null) {
            for (int i = 0; i < uris.size(); i++) {
                generateImagePair(uris.get(i));
            }
        }



        //     viewPager.setPageTransformer(false, new ZoomOutPageTransformer());
        //    ((LinearLayout.LayoutParams) viewPager.getLayoutParams()).weight = 1;

    }


    private void generateImagePair(Uri uri) {

        ImageView imageView = new ImageView(this);

        LinearLayout.LayoutParams sizeParameters = new LinearLayout.LayoutParams(App.MAX_THUMBNAIL_WIDTH, App.MAX_THUMBNAIL_HEIGHT);
        imageView.setLayoutParams(sizeParameters);

        BitmapEncoder.loadBitmapFromURI(imageView, uri, App.MAX_THUMBNAIL_WIDTH, App.MAX_THUMBNAIL_HEIGHT);

        imageView.setOnClickListener(this);

        imageViews.add(new Pair<>(imageView, uri));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //outState.putParcelable("item", item);
        //outState.putInt("index", viewPager.getCurrentItem());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_register, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        prompt();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_register_done:
                save();
                return true;
            case android.R.id.home:
                prompt();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void save() {
        updateItem();
        Item item = Logic.instance.editItem;
        if (item.getItemHeadline() == null || item.getItemHeadline().isEmpty()){
            Toast.makeText(this, "Der skal indtastes en titel", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!App.isConnected()){
            if(Logic.instance.isNewRegistration())
                Toast.makeText(this, "Genstanden kan ikke registreres uden internet", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Genstanden kan ikke opdateres uden internet", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent backgroundService = new Intent(this, BackgroundService.class);
    //    backgroundService.putExtra("event", Logic.instance.isNewRegistration() ? "create" : "update");
    //    backgroundService.putExtra("item", (Parcelable) item);
        startService(backgroundService);

        Logic.setListUpdated(false);

        Intent i = new Intent();
        i.putExtra("saved", true);
        setResult(Activity.RESULT_OK, i);

        itemSaved = true; // do not save draft if item is being sent to API

        finish();
    }

    private void checkForErrors(int responseCode) {
        switch (responseCode) {
            case -1:
                Toast.makeText(this, "Genstanden blev sendt til severen", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "Enheden er ikke forbundet til internettet!", Toast.LENGTH_LONG).show();
                break;
            case 3:
                Toast.makeText(this, "Server problem", Toast.LENGTH_LONG).show();
                break;
            case 4:
                Toast.makeText(this, "Kunne ikke forbinde til serveren", Toast.LENGTH_LONG).show();
                break;
            case 5:
                Toast.makeText(this, "Server problem", Toast.LENGTH_LONG).show(); // JSON problem
            default:
                Toast.makeText(this, "Noget gik galt", Toast.LENGTH_LONG).show();
        }
    }

    private void updateItem() {
        //update item if fragment is instantiated
        //destroyed fragments will have updated the item during onPause() already
        ItemFragment itemFragment = null;
        ItemDetailsFragment itemDetailsFragment = null;
        ItemDescriptionFragment itemDescriptionFragment = null;

        if (itemFragmentWeakReference != null) {
            itemFragment = itemFragmentWeakReference.get();
        }

        if (itemDetailsFragmentWeakReference != null) {
            itemDetailsFragment = itemDetailsFragmentWeakReference.get();
        }

        if (itemDescriptionFragmentWeakReference != null) {
            itemDescriptionFragment = itemDescriptionFragmentWeakReference.get();
        }

        Item item = Logic.instance.editItem;

        if (itemFragment != null) {
            itemFragment.updateItem(item);
        }

        if (itemDetailsFragment != null) {
            itemDetailsFragment.updateItem(item);
        }

        if (itemDescriptionFragment != null) {
            itemDescriptionFragment.updateItem(item);
        }
    }

    private void prompt() {
        setResult(Activity.RESULT_CANCELED, new Intent());
        finish();
    }

    @Override
    public void onClick(View v) {
        Log.d("DDF", "imageClick");

        int index = 0;
        ArrayList<Uri> uris = new ArrayList<>();
        for (int i = 0; i < imageViews.size(); i++) {
            Pair<ImageView, Uri> p = imageViews.get(i);
            uris.add(p.second);

            if (p.first == v)
                index = i;

        }

        Intent intent = new Intent(this, ImageviewerDeleteActivity.class);
        intent.putExtra("imageURIs", uris);
        intent.putExtra("index", index);
        startActivityForResult(intent, ItemActivity.IMAGEVIEWER_REQUEST_CODE);
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment;

            switch (position) {
                case 0:
                    fragment = new ItemFragment();
                    break;
                case 1:
                    fragment = new ItemDescriptionFragment();
                    break;
                case 2:
                    fragment = new ItemDetailsFragment();
                    break;
                default:
                    fragment = new ItemFragment();
            }

            return fragment;
        }

        //save weak references to fragments as they are instantiated for updating item
        //in case a reference becomes invalid, the fragment should have updated the item in onPause() anyways
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);

            switch (position) {
                case 0:
                    itemFragmentWeakReference = new WeakReference<>((ItemFragment) fragment);
                    break;
                case 1:
                    itemDescriptionFragmentWeakReference = new WeakReference<>((ItemDescriptionFragment) fragment);
                    break;
                case 2:
                    itemDetailsFragmentWeakReference = new WeakReference<>((ItemDetailsFragment) fragment);
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence title;

            switch (position) {
                case 0:
                    title = "Genstand";
                    break;
                case 1:
                    title = "Beskrivelse";
                    break;
                case 2:
                    title = "Oplysninger";
                    break;
                default:
                    title = "";
                    break;
            }

            return title;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("AJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ " + requestCode + " " + resultCode);
        Item item = Logic.instance.editItem;
        if (item == null)
            return;

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            //itemFragment.onActivityResult(requestCode, resultCode, data);
            Log.d("updateImage", "Result");

            if (resultCode == Activity.RESULT_OK) {
                item.addToAddedPictures(Logic.instance.tempUri);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture

            } else {
                // Image capture failed, advise user
                Toast.makeText(this, "Der opstod en fejl under brug af kameraet", Toast.LENGTH_LONG).show();
            }

            Logic.instance.tempUri = null;

        } else if (requestCode == ItemActivity.IMAGEVIEWER_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                ArrayList<Uri> remainingURIs = data.getParcelableArrayListExtra("remainingURIs");

                Log.d("updateImage", "remaining URIs: " + remainingURIs.size());

                List<Uri> tempDeleteUris = new ArrayList<>();
                if(item.getPictures() != null){
                    for (Uri uri: item.getPictures() ) {
                        if(!remainingURIs.contains(uri)){
                            item.addDeletedPicture(uri);
                            tempDeleteUris.add(uri);
                        }
                    }
                    for (Uri uri: tempDeleteUris) {
                        item.removeFromPictures(uri);
                    }
                }
                if(item.getAddedPictures() != null) {
                    tempDeleteUris.clear();
                    for (Uri uri : item.getAddedPictures()) {
                        if (!remainingURIs.contains(uri)) {
                            tempDeleteUris.add(uri);
                        }
                    }
                    for (Uri uri: tempDeleteUris) {
                        item.removeFromAddedPicture(uri);
                    }
                }

            }
        } else if (requestCode == ItemActivity.AUDIORECORDING_REQUEST_CODE) {
            System.out.println(".");
            if (resultCode == Activity.RESULT_OK) {
                System.out.println(". result ok");
                item.addToAddRecordings((Uri) data.getParcelableExtra("recordingUri"));
                Toast.makeText(this, "Audio file added!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static final int RECORD_PERMISSION_REQUEST = 2;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == RECORD_PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startRecording();

        }
    }

    public void startRecording() {
        if (App.hasRecordAudioPermission(this)) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        ItemActivity.RECORD_PERMISSION_REQUEST);

            } else {
                Toast.makeText(this, "Funktionen kræver adgang til mikrofonen. Gå til app indstillinger for at give adgang.", Toast.LENGTH_LONG).show();
            }
        } else {
            Intent i = new Intent(this, RecordingActivity.class);
            startActivityForResult(i, ItemActivity.AUDIORECORDING_REQUEST_CODE);
        }
    }

    public void updateInternet(boolean isConnected) {
        TextView iBar = (TextView) findViewById(R.id.internetConnBar);
        if (iBar != null) {
            if (isConnected)
                iBar.setVisibility(View.GONE);
            else
                iBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

        updateItem();
        Item item = Logic.instance.editItem;

        if (item.hasContent() && Logic.instance.isNewRegistration() && !itemSaved) {
            //save draft
            Log.d("draft", "Saving Draft");

            Logic.instance.draftManager.saveDraft();
        }
    }

    public interface ItemUpdater {
        void updateItem(Item item);
    }
}
