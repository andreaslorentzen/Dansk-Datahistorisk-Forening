package app.ddf.danskdatahistoriskforening;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity{

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;


    private Toolbar registerToolbar;
    private Item item;

    private IDAO dao;

    /**
     * http://developer.android.com/training/animation/screen-slide.html
     */

    private ViewPager viewPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        registerToolbar.setTitle("Registrer genstand");
        registerToolbar.setTitleTextColor(-1); // #FFF
        registerToolbar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(registerToolbar);

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);

        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tab_strip);
        pagerSlidingTabStrip.setViewPager(viewPager);

        item = new Item();
        Intent intent = getIntent();
        if(intent.hasExtra("item")){
            item = intent.getParcelableExtra("item");

            itemFragment.setItemTitle(item.getItemHeadline());
            //TODO indsæt billeder, lyd og lokation også

            detailsFragment.setDateFrom(item.getItemDatingFrom());
            detailsFragment.setDateTo(item.getItemDatingTo());
            detailsFragment.setDateReceive(item.getItemRecieved());
            detailsFragment.setDonator(item.getDonator());
            detailsFragment.setProducer(item.getProducer());

            descriptionFragment.setItemDescription(item.getItemDescription());
        }

   //     viewPager.setPageTransformer(false, new ZoomOutPageTransformer());
   //    ((LinearLayout.LayoutParams) viewPager.getLayoutParams()).weight = 1;

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
        //TODO store textfield content in item
        Log.d("fejl", "start save activity");

        item.setItemHeadline(itemFragment.getItemTitle());
        item.setItemDescription(descriptionFragment.getItemDescription());

        Log.d("fejl", item.getItemHeadline());

        int resultCode;

        if(item.getItemId() > 0){
            new AsyncTask<Item, Void, Integer>(){
                @Override
                protected Integer doInBackground(Item... params){
                    return Model.getDAO().saveItemToDB(RegisterActivity.this, params[0]);
                }

                @Override
                protected void onPostExecute(Integer response){
                    checkForErrors(response);
                }
            }.execute(item);
        }
        else{
            new AsyncTask<Item, Void, Integer>(){
                @Override
                protected Integer doInBackground(Item... params){
                    return Model.getDAO().saveItemToDB(RegisterActivity.this, params[0]);
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
            default:
                Toast.makeText(this, "Noget gik galt", Toast.LENGTH_LONG).show();
        }
    }

    private void prompt(){
        finish();
    }

    private RegisterItemFragment itemFragment = new RegisterItemFragment();
    private RegisterDetailsFragment detailsFragment = new RegisterDetailsFragment();
    private RegisterDescriptionFragment descriptionFragment = new RegisterDescriptionFragment();

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private Fragment[] fragments = new Fragment[3];

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            fragments[0] = itemFragment;
            fragments[1] = detailsFragment;
            fragments[2] = descriptionFragment;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];

        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Genstand";
                case 1:
                    return "Oplysninger";
                case 2:
                    return "Beskrivelse";
            }

            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            itemFragment.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void takePic(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

}
