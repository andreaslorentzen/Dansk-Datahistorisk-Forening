package app.ddf.danskdatahistoriskforening.item;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.os.Parcelable;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import app.ddf.danskdatahistoriskforening.App;
import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.dal.BackgroundService;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.helper.BitmapEncoder;
import app.ddf.danskdatahistoriskforening.image.ImageviewerDeleteActivity;

public class ItemActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int IMAGEVIEWER_REQUEST_CODE = 200;
    public static final int  AUDIORECORDING_REQUEST_CODE = 300;

    private Item item;
    private ArrayList<Pair<ImageView, Uri>> imageUris;
    ArrayList<Uri> audioUris;
    private Uri tempUri;

    public Item getItem() {
        return item;
    }

    public ArrayList<Pair<ImageView, Uri>> getImageUris() {
        return imageUris;
    }

    /**
     * http://developer.android.com/training/animation/screen-slide.html
     */

    private ViewPager viewPager;

    private WeakReference<ItemFragment> itemFragmentWeakReference;
    private WeakReference<ItemDetailsFragment> itemDetailsFragmentWeakReference;
    private WeakReference<ItemDescriptionFragment> itemDescriptionFragmentWeakReference;

    private boolean isNewRegistration;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ItemActivity.this.checkForErrors(intent.getIntExtra("status", 0));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Model.setCurrentActivity(this);
        setContentView(R.layout.activity_register);

        Toolbar registerToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        registerToolbar.setTitle("Registrer genstand");
        registerToolbar.setTitleTextColor(-1); // #FFF
        registerToolbar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(registerToolbar);

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_strip);
        tabLayout.setupWithViewPager(viewPager);
        
        if(savedInstanceState == null) {

            item = new Item();
            Intent intent = getIntent();
            if (intent.hasExtra("item")) {
                item = intent.getParcelableExtra("item");
            }

            if(intent.hasExtra("isNewRegistration")){
                isNewRegistration = intent.getBooleanExtra("isNewRegistration", false);
            }
            else{
                isNewRegistration = false;
            }

        } else {
            item = savedInstanceState.getParcelable("item");
            tempUri = savedInstanceState.getParcelable("tempUri");
            isNewRegistration = savedInstanceState.getBoolean("isNewRegistration");
        }

        //     viewPager.setPageTransformer(false, new ZoomOutPageTransformer());
        //    ((LinearLayout.LayoutParams) viewPager.getLayoutParams()).weight = 1;

    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Model.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        super.onResume();
        imageUris = new ArrayList<>();
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
        Pair<ImageView, Uri> uriImagePair = new Pair<>(new ImageView(this), uri);
        LinearLayout.LayoutParams sizeParameters = new LinearLayout.LayoutParams(App.MAX_THUMBNAIL_WIDTH, App.MAX_THUMBNAIL_HEIGHT);
        uriImagePair.first.setLayoutParams(sizeParameters);

        BitmapEncoder.loadBitmapFromURI(uriImagePair.first, uriImagePair.second, App.MAX_THUMBNAIL_WIDTH, App.MAX_THUMBNAIL_HEIGHT);

        uriImagePair.first.setOnClickListener(this);

        imageUris.add(uriImagePair);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("item", item);
        //outState.putInt("index", viewPager.getCurrentItem());
        outState.putParcelable("tempUri", tempUri);
        outState.putBoolean("isNewRegistration", isNewRegistration);
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

        if (item.getItemHeadline() == null || item.getItemHeadline().isEmpty())
            Toast.makeText(this, "Der skal indtastes en titel", Toast.LENGTH_SHORT).show();

        if(!Model.isConnected())
            Toast.makeText(this, "Kan ikke udføres uden internet", Toast.LENGTH_SHORT).show();

        //item.setItemHeadline(itemFragment.getItemTitle());
/*
        for (Pair<ImageView, Uri> pair : imageUris) {
            item.addToPictures(pair.second);
        }
*/

        /*item.setDonator(detailsFragment.donator == null ? null : detailsFragment.donator.getText().toString());
        item.setProducer(detailsFragment.producer == null ? null : detailsFragment.producer.getText().toString());
        item.setItemDescription(descriptionFragment.getItemDescription());*/
        if(Model.isConnected()) {
            if (item.getItemId() > 0) {
                Intent backgroundService = new Intent(this, BackgroundService.class);
                backgroundService.putExtra("event", "update");
                backgroundService.putExtra("item", (Parcelable) item);
                startService(backgroundService);
            } else {
                Intent backgroundService = new Intent(this, BackgroundService.class);
                backgroundService.putExtra("event", "create");
                backgroundService.putExtra("item", (Parcelable) item);
                startService(backgroundService);
            }
            Model.setListUpdated(false);
            isNewRegistration = false; //do not save draft if item is being sent to API
            finish();
        } else{
            Toast.makeText(this, "Genstanden kan ikke ændres uden internet", Toast.LENGTH_SHORT).show();
        }
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

    private void updateItem(){
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
        finish();
    }

    public void setTempUri(Uri fileUri) {
        tempUri = fileUri;
    }

    @Override
    public void onClick(View v) {
        Log.d("DDF", "imageClick");
        int index = -1;
        ArrayList<Uri> uris = new ArrayList<>();
        for (int i = 0; i < imageUris.size(); i++) {
            Pair p = (Pair) imageUris.get(i);
            if (p.first == v) {
                //the correct imageView was found
                index = i;
            }

            uris.add((Uri) p.second);
        }

        if (index < 0) {
            //none of the imageViews matched
            return;
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
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            //itemFragment.onActivityResult(requestCode, resultCode, data);
            Log.d("updateImage", "Result");

            if (resultCode == Activity.RESULT_OK) {
                item.addToAddedPictures(tempUri);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture

            } else {
                // Image capture failed, advise user
                Toast.makeText(this, "Der opstod en fejl under brug af kameraet", Toast.LENGTH_LONG).show();
            }

            setTempUri(null);

        } else if(requestCode == ItemActivity.IMAGEVIEWER_REQUEST_CODE){
            if(resultCode == AppCompatActivity.RESULT_OK){
                ArrayList<Uri> remainingURIs = data.getParcelableArrayListExtra("remainingURIs");

                Log.d("updateImage", "remaining URIs: " + remainingURIs.size());

                //no change
                if(remainingURIs.size() == imageUris.size()){
                    return;
                }

                if(item != null){
                    item.setPicturesChanged(true);
                }

                for(int i = 0; i<imageUris.size(); i++){
                    Pair listItem = (Pair) imageUris.get(i);

                    if(!remainingURIs.contains(listItem.second)){
                        //image has been removed

                        if(item.getAddedPictures() !=  null){//image may have been added during this registration
                            if(!item.getAddedPictures().contains(listItem.second)){
                                item.addDeletedPicture((Uri) listItem.second);
                            } else{
                                item.removeFromAddedPicture((Uri) listItem.second);
                            }
                        }
                        else{//image was taken during earlier registration
                            item.addDeletedPicture((Uri) listItem.second);
                        }

                        //remove picture from list of local images
                        item.removeFromPictures((Uri) listItem.second);
                    }
                }
            }
        } else if(requestCode == ItemActivity.AUDIORECORDING_REQUEST_CODE){
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
        if (App.hasRecordAudioPermission(this)){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        ItemActivity.RECORD_PERMISSION_REQUEST);

            }
            else{
                Toast.makeText(this, "Funktionen kræver adgang til mikrofonen. Gå til app indstillinger for at give adgang.", Toast.LENGTH_LONG).show();
            }
        }
        else{
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

        if(item.hasContent() && isNewRegistration){
            //save draft
            Log.d("draft", "Saving Draft");
            (new SaveDraftTask()).execute();
        }
    }

    private class SaveDraftTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                FileOutputStream fos = openFileOutput("draft", Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                oos.writeObject(item);
                oos.flush();
                oos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File file = new File(getFilesDir().getPath() + "/" + "draft");

            Log.d("draft", "draft saved: " + file.exists());

            return null;
        }
    }
}
