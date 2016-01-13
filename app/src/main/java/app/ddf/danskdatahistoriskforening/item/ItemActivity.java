package app.ddf.danskdatahistoriskforening.item;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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

import app.ddf.danskdatahistoriskforening.Model;
import app.ddf.danskdatahistoriskforening.R;
import app.ddf.danskdatahistoriskforening.dal.Item;
import app.ddf.danskdatahistoriskforening.helper.BitmapEncoder;
import app.ddf.danskdatahistoriskforening.helper.PagerSlidingTabStrip;
import app.ddf.danskdatahistoriskforening.image.ImageviewerDeleteActivity;

public class ItemActivity extends AppCompatActivity implements View.OnClickListener {
    //TODO calculate acceptable thumbnail dimensions based on screensize or available space
    private final int MAX_THUMBNAIL_WIDTH = 150;
    private final int MAX_THUMBNAIL_HEIGHT = 250;

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int IMAGEVIEWER_REQUEST_CODE = 200;

    private Item item;
    private ArrayList<Pair<ImageView, Uri>> imageUris;
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
        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tab_strip);
        pagerSlidingTabStrip.setViewPager(viewPager);


        if (savedInstanceState == null) {

            item = new Item();
            Intent intent = getIntent();
            if (intent.hasExtra("item")) {
                item = intent.getParcelableExtra("item");
            }

        } else {
            item = savedInstanceState.getParcelable("item");
            tempUri = savedInstanceState.getParcelable("tempUri");

        }

        //     viewPager.setPageTransformer(false, new ZoomOutPageTransformer());
        //    ((LinearLayout.LayoutParams) viewPager.getLayoutParams()).weight = 1;

    }

    @Override
    protected void onResume() {
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

    }


    private void generateImagePair(Uri uri) {
        Pair<ImageView, Uri> uriImagePair = new Pair<>(new ImageView(this), uri);
        LinearLayout.LayoutParams sizeParameters = new LinearLayout.LayoutParams(MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
        uriImagePair.first.setLayoutParams(sizeParameters);

        BitmapEncoder.loadBitmapFromURI(uriImagePair.first, uriImagePair.second, MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);

        uriImagePair.first.setOnClickListener(this);

        imageUris.add(uriImagePair);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("item", item);
        outState.putInt("index", viewPager.getCurrentItem());
        outState.putParcelable("tempUri", tempUri);
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


        //item.setItemHeadline(itemFragment.getItemTitle());

        for (Pair<ImageView, Uri> pair : imageUris) {
            item.addToPictures(pair.second);
        }

        /*
        HUSK
        HUSK
        HUSK
        HUSK
        HUSK
        item.setRecordings(itemFragment.audioUris);*/


        /*item.setDonator(detailsFragment.donator == null ? null : detailsFragment.donator.getText().toString());
        item.setProducer(detailsFragment.producer == null ? null : detailsFragment.producer.getText().toString());
        item.setItemDescription(descriptionFragment.getItemDescription());*/


        if (item.getItemId() > 0) {
            if (!Model.isConnected()) {
                Toast.makeText(this, "Genstanden kan ikke ændres uden internet", Toast.LENGTH_SHORT).show();
            }
            /*try{
                if(detailsFragment.dateReceive != null && detailsFragment.dateReceive.getText() != null && !detailsFragment.dateReceive.getText().toString().equals(""))
                    item.setItemRecieved(Model.getFormatter().parse(detailsFragment.dateReceive.getText().toString()));
                if(detailsFragment.dateFrom != null && detailsFragment.dateFrom.getText() != null && !detailsFragment.dateFrom.getText().toString().equals("") )
                    item.setItemDatingFrom(Model.getFormatter().parse(detailsFragment.dateFrom.getText().toString()));
                if(detailsFragment.dateTo != null && detailsFragment.dateTo.getText() != null && !detailsFragment.dateTo.getText().toString().equals("")  )
                    item.setItemDatingTo(Model.getFormatter().parse(detailsFragment.dateTo.getText().toString()));
            } catch(ParseException e){
                e.printStackTrace();
            }*/
            new AsyncTask<Item, Void, Integer>() {
                @Override
                protected Integer doInBackground(Item... params) {
                    return Model.getDAO().updateItem(ItemActivity.this, params[0]);
                }

                @Override
                protected void onPostExecute(Integer response) {
                    checkForErrors(response);
                }
            }.execute(item);
        } else {
            /*try{
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
            }*/
            new AsyncTask<Item, Void, Integer>() {
                @Override
                protected Integer doInBackground(Item... params) {
                    return Model.getDAO().saveItemToDB(ItemActivity.this, params[0]);
                }

                @Override
                protected void onPostExecute(Integer response) {
                    checkForErrors(response);
                }
            }.execute(item);
        }


    }

    private void checkForErrors(int responseCode) {
        switch (responseCode) {
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

                //no change
                if(remainingURIs.size() == imageUris.size()){
                    return;
                }

                if(item != null){
                    item.setPicturesChanged(true);
                }

         //       ArrayList temp = new ArrayList<>(imageUris);
                for(int i = 0; i<imageUris.size(); i++){
                    Pair listItem = (Pair) imageUris.get(i);

                    if(!remainingURIs.contains(listItem.second)){
                        //image has been removed

                        if(item.getAddedPictures() !=  null){
                            if(!item.getAddedPictures().contains(listItem.second)){
                                item.addDeletedPicture((Uri) listItem.second);
                            } else{
                                item.removeFromAddedPicture((Uri) listItem.second);
                            }
                        }
              //          temp.remove(listItem);
                    }
                    else{

                    }
                }
            }
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
}
