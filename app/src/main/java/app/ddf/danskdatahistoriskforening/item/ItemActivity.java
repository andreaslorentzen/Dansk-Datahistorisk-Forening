package app.ddf.danskdatahistoriskforening.item;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;

import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.helper.BitmapEncoder;
import app.ddf.danskdatahistoriskforening.helper.PagerSlidingTabStrip;
import app.ddf.danskdatahistoriskforening.R;

public class ItemActivity extends AppCompatActivity{
    //TODO calculate acceptable thumbnail dimensions based on screensize or available space
    private final int MAX_THUMBNAIL_WIDTH = 150;
    private final int MAX_THUMBNAIL_HEIGHT = 250;

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int IMAGEVIEWER_REQUEST_CODE = 200;

    private Toolbar registerToolbar;
    private TextView internetBar;

    private Item item;
    private ArrayList<Pair<ImageView,Uri>> imageUris;

    public Item getItem() {
        return item;
    }

    public ArrayList<Pair<ImageView,Uri>> getImageUris(){
        return imageUris;
    }

    public void setImageUris(ArrayList<Pair<ImageView,Uri>> imageUris){
        this.imageUris = imageUris;
    }

    /**
     * http://developer.android.com/training/animation/screen-slide.html
     */

    private ViewPager viewPager;
    private PagerAdapter mPagerAdapter;


    private ItemFragment itemFragment;
    private ItemDetailsFragment detailsFragment;
    private ItemDescriptionFragment descriptionFragment;

    private WeakReference<ItemFragment> itemFragmentWeakReference;
    private WeakReference<ItemDetailsFragment> itemDetailsFragmentWeakReference;
    private WeakReference<ItemDescriptionFragment> itemDescriptionFragmentWeakReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Model.setCurrentActivity(this);
        internetBar = (TextView) findViewById(R.id.internetConnBar);
        setContentView(R.layout.activity_register);

        registerToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        registerToolbar.setTitle("Registrer genstand");
        registerToolbar.setTitleTextColor(-1); // #FFF
        registerToolbar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(registerToolbar);

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (ViewPager) findViewById(R.id.pager);



        if(savedInstanceState == null) {
            itemFragment = new ItemFragment();
            detailsFragment = new ItemDetailsFragment();
            descriptionFragment = new ItemDescriptionFragment();

            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(mPagerAdapter);

            PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tab_strip);
            pagerSlidingTabStrip.setViewPager(viewPager);

            item = new Item();
            Intent intent = getIntent();
            if (intent.hasExtra("item")) {
                item = intent.getParcelableExtra("item");
            }

        }
        else {
            item = savedInstanceState.getParcelable("item");
        }

        imageUris = new ArrayList<>();
        ArrayList<Uri> uris = item.getPictures();
        if(uris != null){
            for(int i = 0; i<uris.size(); i++){
                Pair<ImageView, Uri> uriImagePair = new Pair(new ImageView(this), uris.get(i));
                LinearLayout.LayoutParams sizeParameters = new LinearLayout.LayoutParams(MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
                uriImagePair.first.setLayoutParams(sizeParameters);

                imageUris.add(uriImagePair);

                BitmapEncoder.loadBitmapFromURI(uriImagePair.first, uriImagePair.second, MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
            }
        }
        else{
            Log.d("updateImage", "no uris");
        }


   //     viewPager.setPageTransformer(false, new ZoomOutPageTransformer());
   //    ((LinearLayout.LayoutParams) viewPager.getLayoutParams()).weight = 1;

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        itemFragment = new ItemFragment();
        detailsFragment = new ItemDetailsFragment();
        descriptionFragment = new ItemDescriptionFragment();

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);

        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tab_strip);
        pagerSlidingTabStrip.setViewPager(viewPager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<Uri> uris = new ArrayList<>();
        for(int i=0; i<imageUris.size(); i++){
            Pair p = (Pair) imageUris.get(i);
            uris.add((Uri) p.second);
        }
        item.setPictures(uris);

        outState.putParcelable("item", item);
        outState.putInt("index", viewPager.getCurrentItem());
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


        //item.setItemHeadline(itemFragment.getItemTitle());

        for(Pair<ImageView, Uri> pair : imageUris) {
            item.addToPictures(pair.second);
        }

        item.setRecordings(itemFragment.audioUris);


        /*item.setDonator(detailsFragment.donator == null ? null : detailsFragment.donator.getText().toString());
        item.setProducer(detailsFragment.producer == null ? null : detailsFragment.producer.getText().toString());
        item.setItemDescription(descriptionFragment.getItemDescription());*/


        if(item.getItemId() > 0){
            if(!Model.isConnected()){
                Toast.makeText(this, "Genstanden kan ikke ændres uden internet", Toast.LENGTH_SHORT).show();
            }
            try{
                if(detailsFragment.dateReceive != null && detailsFragment.dateReceive.getText() != null && !detailsFragment.dateReceive.getText().toString().equals(""))
                    item.setItemRecieved(Model.getFormatter().parse(detailsFragment.dateReceive.getText().toString()));
                if(detailsFragment.dateFrom != null && detailsFragment.dateFrom.getText() != null && !detailsFragment.dateFrom.getText().toString().equals("") )
                    item.setItemDatingFrom(Model.getFormatter().parse(detailsFragment.dateFrom.getText().toString()));
                if(detailsFragment.dateTo != null && detailsFragment.dateTo.getText() != null && !detailsFragment.dateTo.getText().toString().equals("")  )
                    item.setItemDatingTo(Model.getFormatter().parse(detailsFragment.dateTo.getText().toString()));
            } catch(ParseException e){
                e.printStackTrace();
            }
            new AsyncTask<Item, Void, Integer>(){
                @Override
                protected Integer doInBackground(Item... params){
                    return Model.getDAO().updateItem(ItemActivity.this, params[0]);
                }

                @Override
                protected void onPostExecute(Integer response){
                    checkForErrors(response);
                }
            }.execute(item);
        }
        else{
            try{
                System.out.println(detailsFragment.hasReceiveChanged());
                if(detailsFragment.hasReceiveChanged())
                    item.setItemRecieved(Model.getFormatter().parse(detailsFragment.dateReceive.getText().toString()));
                else
                    item.setItemRecieved(null);
                if(detailsFragment.hasDateFromChanged())
                    item.setItemDatingFrom(Model.getFormatter().parse(detailsFragment.dateFrom.getText().toString()));
                else
                    item.setItemDatingFrom(null);
                if(detailsFragment.hasDateToChanged())
                    item.setItemDatingTo(Model.getFormatter().parse(detailsFragment.dateTo.getText().toString()));
                else
                    item.setItemDatingTo(null);
            } catch(ParseException e){
                e.printStackTrace();
            }
            new AsyncTask<Item, Void, Integer>(){
                @Override
                protected Integer doInBackground(Item... params){
                    return Model.getDAO().saveItemToDB(ItemActivity.this, params[0]);
                }

                @Override
                protected void onPostExecute(Integer response){
                   checkForErrors(response);
                }
            }.execute(item);
        }



    }

    private void checkForErrors(int responseCode){
        switch(responseCode){
            case -1:
                Model.setListUpdated(false);
                finish();
                break;
            case 1:
                Toast.makeText(this, "Der er ikke angivet en titel til museumsgenstanden!", Toast.LENGTH_LONG).show();
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

    private void prompt(){
        finish();
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        /*private Pair<String, Fragment>[] fragments = new Pair[3];*/

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
           /* fragments[0] = new Pair<String, Fragment>("Genstand", itemFragment);
            fragments[1] = new Pair<String, Fragment>("Beskrivelse", descriptionFragment);
            fragments[2] = new Pair<String, Fragment>("Oplysninger", detailsFragment);*/
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("ddfstate", "new fragment: " + position);

            Fragment fragment;

            switch (position){
                case 0:
                    fragment = new ItemFragment();
                    itemFragment = (ItemFragment) fragment;
                    break;
                case 1:
                    fragment = new ItemDescriptionFragment();
                    descriptionFragment = (ItemDescriptionFragment) fragment;
                    break;
                case 2:
                    fragment = new ItemDetailsFragment();
                    detailsFragment = (ItemDetailsFragment) fragment;
                    break;
                default:
                    fragment = new ItemFragment();
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

            switch (position){
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
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            itemFragment.onActivityResult(requestCode, resultCode, data);
        }
        else if(requestCode == IMAGEVIEWER_REQUEST_CODE){
            itemFragment.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void takePic(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void updateInternet(boolean isConnected){
        if(internetBar != null){
            if(isConnected)
                internetBar.setVisibility(View.GONE);
            else
                internetBar.setVisibility(View.VISIBLE);
        }
    }
}
